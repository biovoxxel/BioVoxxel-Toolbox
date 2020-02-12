package de.biovoxxel.toolbox;



import java.awt.AWTEvent;
import java.awt.Checkbox;
import java.awt.Color;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.EDM;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

/*
 *	Copyright (C), Jan Brocher / BioVoxxel. All rights reserved.
 *
 *	All Macros/Plugins were written by Jan Brocher/BioVoxxel.
 *
 *	Redistribution and use in source and binary forms of all plugins and macros, with or without modification, 
 *	are permitted provided that the following conditions are met:
 *
 *	1.) Redistributions of source code must retain the above copyright notice, 
 *	this list of conditions and the following disclaimer.
 *	2.) Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *	and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3.) Neither the name of BioVoxxel nor the names of its contributors may be used to endorse or promote 
 *  products derived from this software without specific prior written permission.
 *	
 *	DISCLAIMER:
 *
 *	THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ?AS IS? AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 *	INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *	DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *	SERVICES;  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 *	WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 *	USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
 
/* 
 * May 04, 2015: 		Bug fix: Now correctly works with stacks (Thorsten Wagner, wagner@biomedical-imaging.de)
 * May 21, 2015: 		Now uses Jan's EDM based erosion and adds convexity based watershed which is scale invariant 
 * August 23rd, 2015: 	Separator size definition as tuning parameter (including error handling) added
 */

public class Watershed_Irregular_Features implements ExtendedPlugInFilter, DialogListener {
	ImagePlus imp;
	private double erosions = 1;
	private double convexityThreshold = 0;
	private String separatorRange = "0-Infinity";
	private boolean excludeRange = false;
	private PlugInFilterRunner pfr;
	private String label = "Preview";
	private int nPasses = 1;
	private int pass;
	private double AreaMin = 0.0;
	private double AreaMax = Double.POSITIVE_INFINITY;
	private int options = ParticleAnalyzer.RECORD_STARTS;
	private int measurements = Measurements.CENTROID;
	private int flags = DOES_8G|KEEP_PREVIEW|SNAPSHOT|DOES_STACKS;
	
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		
		if(!imp.getProcessor().isBinary()) {
			IJ.error("works only on 8-bit binary images");
			return DONE;
		}
		
		GenericDialog gd = new GenericDialog("Watershed Irregular Features");
		gd.addNumericField("erosion cycle number:", 1, 0, 5, "");
		gd.addNumericField("convexity_threshold", 0, 2, 5, "");
		gd.addStringField("separator_size", "0-Infinity");
		gd.addCheckbox("exclude", false);
		gd.addPreviewCheckbox(pfr, label); // passing pfr makes the filter ready for preview
		gd.addDialogListener(this); // the DialogItemChanged method will be called on user input
		gd.addHelp("http://fiji.sc/BioVoxxel_Toolbox#Watershed_Irregular_Features");
		gd.showDialog(); // display the dialog; preview runs in the background now
		if (gd.wasCanceled()) {
			return DONE;
		}
		IJ.register(this.getClass()); // protect static class variables (filter parameters) from garbage collection
		this.pfr = pfr;
		return IJ.setupDialog(imp, flags); // ask whether to process all slices of stack (if a stack)
	}
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		erosions = (int) gd.getNextNumber();
		convexityThreshold = gd.getNextNumber();
		separatorRange = gd.getNextString();
		excludeRange = gd.getNextBoolean();
		
		//read out the separator Range definitions
		try {
			separatorRange.substring(0, separatorRange.indexOf("-"));
		} catch (java.lang.StringIndexOutOfBoundsException oobe) {
			separatorRange = "-";
		}
		
		try {
			AreaMin = Double.parseDouble(separatorRange.substring(0, separatorRange.indexOf("-")));
		} catch (java.lang.NumberFormatException minException) {
			AreaMin = Double.NaN;
		}
		
		String AreaInterMax = separatorRange.substring(separatorRange.indexOf("-")+1);
		if(AreaInterMax.equals("Infinity") || AreaInterMax.equals("infinity")) { 
			AreaMax = Double.POSITIVE_INFINITY;
		} else if(!AreaInterMax.equals("")){
			try {
				AreaMax = Double.parseDouble(separatorRange.substring(separatorRange.indexOf("-")+1));
			} catch (java.lang.NumberFormatException maxException) {
				AreaMax = Double.NaN;
			}
		}
		
		Checkbox previewCheckbox = (Checkbox) gd.getCheckboxes().get(1);
		if(gd.invalidNumber() || erosions<1 || convexityThreshold<0.0 || convexityThreshold>1.0 || separatorRange.equals("-") || separatorRange.substring(0, separatorRange.indexOf("-")).equals("") || separatorRange.substring(separatorRange.indexOf("-")+1).equals("") || Double.isNaN(AreaMin) || Double.isNaN(AreaMax)) {
			if (previewCheckbox.getState()) {
				previewCheckbox.setSize(130, 20);
				previewCheckbox.setLabel("Invalid number");
			}
			return false;
		} else {
			return true;
		}
	}
	public void run(ImageProcessor ip) {
		// ip.snapshot();
		Prefs.blackBackground = true;
		boolean invertedLut = ip.isInvertedLut();
		if(invertedLut) {
			ip.invertLut();
		}
		ImagePlus origImp = new ImagePlus("",ip.duplicate());
		ImageProcessor erosionIP = ip.duplicate();
		ImagePlus erosionImp = new ImagePlus("", erosionIP);
		ImageProcessor watershedIP = ip.duplicate();
		ImagePlus watershedImp = new ImagePlus("", watershedIP);
		
		
		EDM edm = new EDM();
		//separate original objects with the normal watershed algorithm from IJ
		edm.toWatershed(watershedIP);
				
		//first, the watershed separation lines are extracted
		//then, the second image calculation keeps only those separators
		//which overlap with an eroded particle
		
		ImageCalculator calculateImages = new ImageCalculator();
		ImagePlus extractedSeparatorsImp = calculateImages.run("XOR create", watershedImp, origImp);
		ImageProcessor extractedSeparatorsIP = extractedSeparatorsImp.getProcessor();
		
		//the parameters for the modulation of the separation occur in a certain priority
		//highest priotity has the separator size parameter, second priority has the convexity threshold, lowest priority has the erosion cycle
		if(!separatorRange.equals("0-Infinity") && !separatorRange.equals("0-infinity")) {
			ResultsTable separatorTable = new ResultsTable();
			
			if(!excludeRange) {
				ParticleAnalyzer separatorAnalyzer = new ParticleAnalyzer(options, measurements, separatorTable, AreaMin, AreaMax);
				separatorAnalyzer.analyze(extractedSeparatorsImp);
				int xStart, yStart;
				extractedSeparatorsIP.setValue(0.0);
				
				//the remaining separation lines in their original size and orientation
				//are copied into the watersheded image to close undesired separations
				for(int r=0; r<separatorTable.getCounter(); r++) {
					xStart = (int)separatorTable.getValue("XStart", r);
					yStart = (int)separatorTable.getValue("YStart", r);
					IJ.doWand(extractedSeparatorsImp, xStart, yStart, 0.0, "8-connected");
					Roi selectedSeparator = extractedSeparatorsImp.getRoi();
					extractedSeparatorsIP.fill(selectedSeparator);
				}
			} else {
				ParticleAnalyzer separatorAnalyzer1 = new ParticleAnalyzer(options, measurements, separatorTable, 0.0, AreaMin);
				ParticleAnalyzer separatorAnalyzer2 = new ParticleAnalyzer(options, measurements, separatorTable, AreaMax, Double.POSITIVE_INFINITY);
				separatorAnalyzer1.analyze(extractedSeparatorsImp);
				separatorAnalyzer2.analyze(extractedSeparatorsImp);
				int xStart, yStart;
				extractedSeparatorsIP.setValue(0.0);
				
				//the remaining separation lines in their original size and orientation
				//are copied into the watersheded image to close undesired separations
				for(int r=0; r<separatorTable.getCounter(); r++) {
					xStart = (int)separatorTable.getValue("XStart", r);
					yStart = (int)separatorTable.getValue("YStart", r);
					IJ.doWand(extractedSeparatorsImp, xStart, yStart, 0.0, "8-connected");
					Roi selectedSeparator = extractedSeparatorsImp.getRoi();
					extractedSeparatorsIP.fill(selectedSeparator);
				}
			}
			
			
		} else if(convexityThreshold==0 && (separatorRange.equals("0-Infinity") || separatorRange.equals("0-infinity"))) {
			//If the convexity threshold is set to 0 simple erode the image n times.
			edm.toEDM(erosionIP);
			erosionIP.threshold((int)erosions);
		} else {
			//Do a seperate erosion for each connectec component (CC). If
			//a CC has a convexity larger than the convexity threshold stop the erosion
			//for this object
			ArrayList<Blob> objects = new ArrayList<Blob>();
			
			
			erosionImp = new ImagePlus("", erosionIP);
			ManyBlobs mb = new ManyBlobs(erosionImp);
			mb.setBackground(0);
			mb.findConnectedComponents();
			while(mb.size()>0) {
				for (Blob blob : mb) {
					if(blob.getConvexity() >convexityThreshold){
						objects.add(blob);
						erosionIP.setColor(Color.black);
						
						erosionIP.fillPolygon(blob.getOuterContour());
					}
				}
				edm.toEDM(erosionIP);
				erosionIP.threshold(2);
				
				erosionImp = new ImagePlus("", erosionIP);
	
				mb = new ManyBlobs(erosionImp);
				mb.setBackground(0);
				mb.findConnectedComponents();

				
			}
			erosionIP.set(0);
			for (Blob blob : objects) {
				Blob.setDefaultColor(Color.white);
				blob.draw(erosionIP);
			}
		}
		
		ImagePlus remainingSeparatorsImp = calculateImages.run("AND create", extractedSeparatorsImp, erosionImp);
				
		//the remaining separator lines are analyzed to get their starting position
		//for later selection
		ResultsTable resultsTable = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, resultsTable, 0.0, Double.POSITIVE_INFINITY);
		pa.analyze(remainingSeparatorsImp);
		int xStart, yStart;
		watershedIP.setValue(255.0);
		
		//the remaining separation lines in their original size and orientation
		//are copied into the watersheded image to close undesired separations
		for(int r=0; r<resultsTable.getCounter(); r++) {
			xStart = (int)resultsTable.getValue("XStart", r);
			yStart = (int)resultsTable.getValue("YStart", r);
			IJ.doWand(extractedSeparatorsImp, xStart, yStart, 0.0, "8-connected");
			Roi selectedSeparator = extractedSeparatorsImp.getRoi();
			watershedIP.fill(selectedSeparator);
		}
		//watershedImp.show();
		//the corrected watersheded image is copied into the original to be able to
		//undo the watershed if undesired
		//origImp.getProcessor().setPixels(watershedImp.getProcessor().getPixels());
		
		if(invertedLut) {
			ip.invertLut();
		}
		for(int i = 0; i < ip.getWidth(); i++){
			for(int j =0; j < ip.getHeight(); j++){
				ip.putPixel(i, j, watershedImp.getProcessor().getPixel(i, j));
			}
		}

	}
	
	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
		pass = 0;
	}
	
	void showProgress(double percent) {
		percent = (double)(pass-1)/nPasses + percent/nPasses;
		IJ.showProgress(percent);
	}
}
