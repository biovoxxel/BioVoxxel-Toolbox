package de.biovoxxel.toolbox;


import java.awt.AWTEvent;
import java.awt.Checkbox;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilterRunner;
import ij.plugin.filter.RankFilters;
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
 
 public class Convoluted_Background_Subtraction implements ExtendedPlugInFilter, DialogListener {
	ImagePlus imp;
	private int flags = DOES_8G|DOES_16|KEEP_PREVIEW|SNAPSHOT;
	private PlugInFilterRunner pfr;
	private int nPasses = 1;
	private int pass;
	private String[] filter = {"Gaussian", "Median", "Mean"};
	private String previousFilter = Prefs.get("bvtb.CBS.filter", "Gaussian");
	private String convolutionFilterChoice;
	private double filterRadius;
	
			
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}
	
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("Convoluted Background Subtraction");
			gd.setInsets(5, 0, 3);
			gd.addRadioButtonGroup("convolution filter:", filter, 1, 3, previousFilter);
			gd.addNumericField("radius:", 1.0, 1);
			gd.addPreviewCheckbox(pfr);	// passing pfr makes the filter ready for preview
			gd.addDialogListener(this);	// the DialogItemChanged method will be called on user input
			gd.addHelp("http://imagej.net/BioVoxxel_Toolbox");
			gd.showDialog();		// display the dialog; preview runs in the background now
			if (gd.wasCanceled()) {
				return DONE;
			}
			//IJ.register(this.getClass());	// protect static class variables (filter parameters) from garbage collection
			this.pfr = pfr;
			return IJ.setupDialog(imp, flags); // ask whether to process all slices of stack (if a stack)
	}
	
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		convolutionFilterChoice = gd.getNextRadioButton();
		filterRadius = gd.getNextNumber();
		Checkbox previewCheckbox = (Checkbox) gd.getCheckboxes().firstElement();
		previewCheckbox.setSize(130, 25);
		if (filterRadius<0 || gd.invalidNumber() || (convolutionFilterChoice.equals("Median") && filterRadius>100)) {
			if((convolutionFilterChoice.equals("Median") && filterRadius>100)) {
				previewCheckbox.setLabel("max. radius 100");
			} else {
				previewCheckbox.setLabel("Invalid number");
			}
			imp.getProcessor().reset();
			return false;
		} else {
			previewCheckbox.setLabel("Preview");
			return true;
		}
	}
	
	public void run(ImageProcessor ip) {
		
		ImageProcessor duplicateIP = ip.duplicate();
		ImagePlus duplicatedIMP = new ImagePlus("duplicatedIMP", duplicateIP);
		
		if(convolutionFilterChoice.equals("Gaussian")) {
			GaussianBlur gb = new GaussianBlur();
			gb.blurGaussian(duplicateIP, filterRadius, filterRadius, 0.001);
			RankFilters rf = new RankFilters();
			rf.rank(duplicateIP, java.lang.Math.floor(filterRadius/10)*1.5, RankFilters.MAX); // counteracts the size reduction of features during median convolution filtering
		} else if(convolutionFilterChoice.equals("Median")) {
			RankFilters rf = new RankFilters();
			rf.rank(duplicateIP, filterRadius, RankFilters.MEDIAN);
			rf.rank(duplicateIP, java.lang.Math.floor(filterRadius/10)*1.5, RankFilters.MAX); // counteracts the size reduction of features during median convolution filtering
		} else if(convolutionFilterChoice.equals("Mean")) {
			RankFilters rf = new RankFilters();
			rf.rank(duplicateIP, filterRadius, RankFilters.MEAN);
			rf.rank(duplicateIP, java.lang.Math.floor(filterRadius/10)*1.5, RankFilters.MAX); // counteracts the size reduction of features during median convolution filtering
		}
		
		ImageCalculator ic = new ImageCalculator();
		ImagePlus cbgsImp = ic.run("Subtract create", imp, duplicatedIMP);
		ImageProcessor cbgsIP = cbgsImp.getProcessor();
		
		for(int y=0; y<imp.getHeight(); y++) {
			for (int x=0; x<imp.getWidth(); x++) {
				ip.putPixel(x, y, cbgsIP.getPixel(x, y));
			}
		}
		//imp.updateAndDraw();
	}
	
	
	
	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
		pass = 0;
	}
	 
 }