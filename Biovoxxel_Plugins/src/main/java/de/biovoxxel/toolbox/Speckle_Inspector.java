package de.biovoxxel.toolbox;


import java.awt.Color;
import java.awt.Font;

import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.FloodFiller;
import ij.process.ImageConverter;
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

/**
 * 
 * Copyright (C), 2014, Jan Brocher / BioVoxxel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * IN NO EVENT WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES 
 * AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, 
 * INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING 
 * OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO 
 * LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR 
 * THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), 
 * EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF 
 * SUCH DAMAGES.
 * 
 * Please cite BioVoxxel and Jan Brocher when you publish results 
 * obtained by usage of this plugin or a modified version of it
 * 
 * Thank you
 *
 * 
 */
public class Speckle_Inspector implements PlugInFilter {
	ImagePlus imp;
	private int flags = DOES_ALL;
	private int nPasses, pass;
	
	private boolean oldMacro = false;
	
	//Define variables
	private int positive=0;
	//private int negative=0;
	private int less=0;
	private int more=0;
	private int PosPart=0;
	private int NegLess=0;
	private int NegMore=0;
	//define input variables
	private String primaryObjects;
	private String secondaryObjects;
	private String redirectToImage;
	private double minPrimarySize;
	private double maxPrimarySize;
	private double minPrimaryCirc;
	private double maxPrimaryCirc;
	private double minSecondaryNumber;
	private double maxSecondaryNumber;
	private double minSecondarySize;
	private double maxSecondarySize;
	private double minSecondaryCircularity;
	private double maxSecondaryCircularity;
	private boolean excludeEdge;
	private String showRoiManager;
	private boolean showSpeckleList;
	private boolean showStatisticsLog;
	private boolean secondaryObjectAnalysis;
	private double fontSize;
	
	//Particle Analyzer flag definitions
	private int measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
	private int primaryAnalyzerOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS;
	private int secondaryAnalyzerOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS;
	private RoiManager primaryRoiManager;
	private RoiManager secondaryRoiManager;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}
		
	public void run(ImageProcessor ip) {

		String[] imageNames = getOpenImageNames();
		String[] roiManagerList = {"none", "primary", "secondary"};
		
		//legacy macro compatibility regarding object naming
		String macroParameters = Macro.getOptions();
		if (macroParameters != null && macroParameters.indexOf("big=") >= 0 && macroParameters.indexOf("small=") >= 0) {
			oldMacro = true;
			String modernMacroParameters = macroParameters.replace("big=", "primary=");
			modernMacroParameters = modernMacroParameters.replace("small=", "secondary=");
			modernMacroParameters = modernMacroParameters.replace("min_object=", "min_primary_size=");
			modernMacroParameters = modernMacroParameters.replace("max_object=", "max_primary_size=");
			modernMacroParameters = modernMacroParameters.replace("min_object_circularity=", "min_primary_circularity=");
			modernMacroParameters = modernMacroParameters.replace("max_object_circularity=", "max_primary_circularity=");
			modernMacroParameters = modernMacroParameters.replace("min_speckle_number=", "lower_secondary_count=");
			modernMacroParameters = modernMacroParameters.replace("max_speckle_number=", "upper_secondary_count=");
			modernMacroParameters = modernMacroParameters.replace("min_speckle_size=", "min_secondary_size=");
			modernMacroParameters = modernMacroParameters.replace("max_speckle_size=", "max_secondary_size=");
			modernMacroParameters = modernMacroParameters.replace(" roi ", " show=primary ");
			modernMacroParameters = modernMacroParameters.replace("individual_roi", "secondary_object");
			
			Macro.setOptions(modernMacroParameters);			
		}
		
		GenericDialog gd = new GenericDialog("Speckle Inspector");
			gd.addChoice("Primary objects (binary)", imageNames, imageNames[0]);
			gd.addChoice("Secondary objects (binary)", imageNames, imageNames[0]);
			gd.addChoice("Redirect measurements to", imageNames, imageNames[0]);
			
			gd.addNumericField("min_primary_size: ", 0, 0, 9, "pixel");
			gd.addNumericField("max_primary_size: ", Double.POSITIVE_INFINITY, 0, 9, "pixel");
			gd.addNumericField("min_primary_circularity: ", 0.00, 2, 9, "");
			gd.addNumericField("max_primary_circularity: ", 1.00, 2, 9, "");
			
			gd.addNumericField("lower_secondary_count: ", 0, 0, 9, "");
			gd.addNumericField("upper_secondary_count: ", Double.POSITIVE_INFINITY, 0, 9, "");
			
			gd.addNumericField("min_secondary_size: ", 0, 0, 9, "pixel");
			gd.addNumericField("max_secondary_size: ", Double.POSITIVE_INFINITY, 0, 9, "pixel");
			gd.addNumericField("min_secondary_circularity", 0.00, 2, 9, "");
			gd.addNumericField("max_secondary_circularity", 1.00, 2, 9, "");
			
			gd.addChoice("show ROI Manager", roiManagerList, roiManagerList[0]);
			gd.addCheckbox("exclude objects on edges", true);
			//gd.addCheckbox("roi manager visible", false);
			gd.addCheckbox("speckle list", false);
			gd.addCheckbox("statistic log", false);
			gd.addCheckbox("secondary_object analysis", false);

			gd.addNumericField("font size (label)", 10, 0, 9, "");
			
			gd.addHelp("http://fiji.sc/BioVoxxel_Toolbox#Speckle_Inspector");
			
			gd.showDialog();
			gd.setSmartRecording(true);
			if(gd.wasCanceled()) {
				return;
			}
			primaryObjects = gd.getNextChoice();
			secondaryObjects = gd.getNextChoice();
			redirectToImage = gd.getNextChoice();
			minPrimarySize = gd.getNextNumber();
			maxPrimarySize = gd.getNextNumber();
			minPrimaryCirc = gd.getNextNumber();
			maxPrimaryCirc = gd.getNextNumber();
			minSecondaryNumber = gd.getNextNumber();
			maxSecondaryNumber = gd.getNextNumber();
			minSecondarySize = gd.getNextNumber();
			maxSecondarySize = gd.getNextNumber();
			minSecondaryCircularity = gd.getNextNumber();
			maxSecondaryCircularity = gd.getNextNumber();
			
			showRoiManager = gd.getNextChoice();
			excludeEdge = gd.getNextBoolean();
			if(excludeEdge==true) {
				primaryAnalyzerOptions |= ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			} else {
				primaryAnalyzerOptions &= ~ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			}
			showSpeckleList = gd.getNextBoolean();
			showStatisticsLog = gd.getNextBoolean();
			secondaryObjectAnalysis = gd.getNextBoolean();
			fontSize = gd.getNextNumber();

			if(gd.invalidNumber()) {
				IJ.error("invalid number entered");
				return;
			}
		
		if(primaryObjects.equals(secondaryObjects)) {
			IJ.error("images need to be different");
			return;
		}
		ImagePlus primaryObjectImp = WindowManager.getImage(primaryObjects);
		primaryObjectImp.killRoi();
		ImageProcessor bigObjectIP = primaryObjectImp.getProcessor();
		ImagePlus secondaryObjectImp = WindowManager.getImage(secondaryObjects);
		secondaryObjectImp.killRoi();
		ImageProcessor secondaryObjectIP = secondaryObjectImp.getProcessor();

		if(!bigObjectIP.isBinary() || !secondaryObjectIP.isBinary()) {
			IJ.error("works with 8-bit binary images only");
			return;
		}
		
		if((primaryObjectImp.getWidth()!=secondaryObjectImp.getWidth()) || primaryObjectImp.getHeight()!=secondaryObjectImp.getHeight()) {
			IJ.error("images need to be of the same size");
			return;
		}
		
		ResultsTable primaryResultsTable = new ResultsTable();
		
		ParticleAnalyzer analyzeObjects = new ParticleAnalyzer(primaryAnalyzerOptions, measurementFlags, primaryResultsTable, minPrimarySize, maxPrimarySize, minPrimaryCirc, maxPrimaryCirc);
		analyzeObjects.analyze(primaryObjectImp);
		int objectNumber = primaryResultsTable.getCounter();

		//clear an existing RoiManager before instantiating a new one for this analysis
		RoiManager oldRM = RoiManager.getInstance2();
		if(oldRM!=null) {
			oldRM.close();
		}
		
		
		if (showRoiManager.equals("none")) {
			primaryRoiManager = new RoiManager(true);
			secondaryRoiManager = new RoiManager(true);
		} else if (showRoiManager.equals("primary")) {
			primaryRoiManager = new RoiManager();
			secondaryRoiManager = new RoiManager(true);
		} else if (showRoiManager.equals("secondary")) {
			primaryRoiManager = new RoiManager(true);
			secondaryRoiManager = new RoiManager();
		}
		

		int[] x = new int[objectNumber];
		int[] y = new int[objectNumber];
		int[] cX = new int[objectNumber];
		int[] cY = new int[objectNumber];

		Calibration cal = primaryObjectImp.getCalibration();
		Recorder rec = Recorder.getInstance();
		if(rec!=null) {
			 Recorder.record = false;
		}
		
		for(int po=0; po<objectNumber; po++) {
			x[po] = (int) primaryResultsTable.getValue("XStart", po);
			y[po] = (int) primaryResultsTable.getValue("YStart", po);
			cX[po] = (int) cal.getRawX(primaryResultsTable.getValue("X", po));
			cY[po] = (int) cal.getRawY(primaryResultsTable.getValue("Y", po));
			IJ.doWand(primaryObjectImp, x[po], y[po], 0.0, "8-connected");
			primaryObjectImp.getRoi().setName(Integer.toString(po+1));
			primaryRoiManager.addRoi(primaryObjectImp.getRoi());
			
		}
		
		if(rec!=null) {
			 Recorder.record = true;
		}
		
		//analyze speckles
		//primaryResultsTable.reset();
		int[] secondaryCountPerPrimaryObject = new int[objectNumber];
		
		if(!redirectToImage.equalsIgnoreCase("None")) {
			Analyzer.setRedirectImage(WindowManager.getImage(redirectToImage));
		}
		
		ResultsTable secondaryResultsTable = new ResultsTable();
		int secondaryX;
		int secondaryY;
		//int[] secondaryCenterX = new int[objectNumber];
		//int[] secondaryCenterY = new int[objectNumber];
				
		for(int primaryObjectIndex=0; primaryObjectIndex < objectNumber; primaryObjectIndex++) {
			ParticleAnalyzer analyzeSpeckles = new ParticleAnalyzer(secondaryAnalyzerOptions, measurementFlags, secondaryResultsTable, minSecondarySize, maxSecondarySize, minSecondaryCircularity, maxSecondaryCircularity);

			secondaryObjectImp.setRoi(primaryRoiManager.getRoi(primaryObjectIndex), false);
			analyzeSpeckles.analyze(secondaryObjectImp, secondaryObjectIP);
			secondaryCountPerPrimaryObject[primaryObjectIndex] = secondaryResultsTable.getCounter();
			secondaryObjectImp.killRoi();
			//IJ.log(""+specklesPerObject[o]);

			
			if (showRoiManager.equals("secondary")) {
				for (int so = 0; so < secondaryCountPerPrimaryObject[primaryObjectIndex]; so++) {
					secondaryX = (int) secondaryResultsTable.getValue("XStart", so);
					secondaryY = (int) secondaryResultsTable.getValue("YStart", so);
					//System.out.println(secondaryX + " / " + secondaryY);
					//secondaryCenterX[so] = (int) cal.getRawX(secondaryResultsTable.getValue("X", so));
					//secondaryCenterY[so] = (int) cal.getRawY(secondaryResultsTable.getValue("Y", so));
					IJ.doWand(secondaryObjectImp, secondaryX, secondaryY, 0.0, "8-connected");
					Roi secondaryObjectRoi = secondaryObjectImp.getRoi();
					secondaryObjectRoi.setName((primaryObjectIndex+1) + "-" + (so+1));
					secondaryRoiManager.addRoi(secondaryObjectRoi);
					secondaryObjectImp.killRoi();
				}				
			}
			
			secondaryResultsTable.reset();
			analyzeSpeckles = null;
		}
		
		//create output image
		primaryObjectImp.killRoi();
		ImagePlus outputImp = primaryObjectImp.duplicate();
		outputImp.setTitle(WindowManager.getUniqueName("Inspector of " + primaryObjects));
		ImageConverter outputImgConverter = new ImageConverter(outputImp);
		outputImgConverter.convertToRGB();
		ImageProcessor outputIP = outputImp.getProcessor();

		Font font = new Font("Sans Serif", java.awt.Font.BOLD, (int) fontSize);
		outputIP.setFont(font);
		
		FloodFiller outputFloodFiller = new FloodFiller(outputIP);
		
		for(int c=0; c<objectNumber; c++) {
			if(secondaryCountPerPrimaryObject[c]>=minSecondaryNumber && secondaryCountPerPrimaryObject[c]<=maxSecondaryNumber) {
				outputIP.setColor(Color.magenta);
				outputFloodFiller.fill8(x[c], y[c]);
				positive=positive+1;
				PosPart=PosPart+secondaryCountPerPrimaryObject[c];
			} else if(secondaryCountPerPrimaryObject[c]<minSecondaryNumber) {
				outputIP.setColor(Color.blue);
				outputFloodFiller.fill8(x[c], y[c]);
				less=less+1;
				NegLess=NegLess+secondaryCountPerPrimaryObject[c];
			} else if(secondaryCountPerPrimaryObject[c]>maxSecondaryNumber) {
				Color drawColor = new Color(0,93,0);
				outputIP.setColor(drawColor);
				outputFloodFiller.fill8(x[c], y[c]);
				more=more+1;
				NegMore=NegMore+secondaryCountPerPrimaryObject[c];
			}
			outputIP.setColor(Color.white);
			outputIP.drawString("" + (c+1) + "-(" + secondaryCountPerPrimaryObject[c] + ")", cX[c]-((int)(fontSize*1.5)), cY[c]+((int)(fontSize/2)));
		}
		outputFloodFiller = null;
		outputImp.show();
		//outputImp.updateAndDraw();

		if(showRoiManager.equals("primary")) {
			primaryRoiManager.setVisible(true);
		} else if (showRoiManager.equals("secondary")) {
			secondaryRoiManager.setVisible(true);
		}
		
		
		
		//write speckle counts in speckle list (a results table)
		if(showSpeckleList==true) {
			ResultsTable speckleList = new ResultsTable();
			speckleList.setPrecision(0);
			speckleList.setValue("Object", 0, 1);
			speckleList.setValue("Speckles", 0, secondaryCountPerPrimaryObject[0]);
			speckleList.incrementCounter(); //TODO
			for(int r=1; r<objectNumber; r++) {
				speckleList.addValue("Object", r+1);
				speckleList.setValue("Speckles", r, secondaryCountPerPrimaryObject[r]);
				if(r<objectNumber-1) {
					speckleList.incrementCounter();
				}
			}
			speckleList.showRowNumbers(false);
			speckleList.show("Speckle List " + primaryObjects);
		}

		//calculate statistics and IJ.log to log window
		if(objectNumber==0) {
			IJ.log("No Objects detected in " + primaryObjectImp.getTitle());
		} else if((PosPart+NegLess+NegMore)==0) {
			IJ.log("No Speckles detected in " + secondaryObjectImp.getTitle());
		} else if(showStatisticsLog) {
			IJ.log("Object size limit min: " + minPrimarySize + " / max: " + maxPrimarySize);
			IJ.log("Circularity limit min: " + minPrimaryCirc + " / max: " + maxPrimaryCirc);
			IJ.log("Speckle no. limit min: " + minSecondaryNumber + " / max: " + maxSecondaryNumber);
			IJ.log("Speckle size limit min: " + minSecondarySize + " / max: " + maxSecondarySize);
			IJ.log("----------------------------------------------------");
			IJ.log("White features are excluded from the analysis");
			IJ.log("All Objects: " + objectNumber);
			IJ.log("All Speckles: " + (PosPart+NegLess+NegMore));
			IJ.log("Aver. speckle no/feature (all): " + ((PosPart+NegLess+NegMore)/objectNumber));
			IJ.log("----------------------------------------------------");
			IJ.log("Selected Features (magenta): " + positive);
			IJ.log("   All speckles in pos. features: " + PosPart);
			if(positive!=0) {
				IJ.log("   Aver. speckle no/pos feature: " + (PosPart/positive));
			}
			IJ.log("");
			IJ.log("Features with speckle no. smaller min (blue): " + less);
			IJ.log("   All speckles: " + NegLess);
			if(less!=0) {
				IJ.log("   Aver. speckle no/feature: " + (NegLess/less));
			}
			IJ.log("");
			IJ.log("Features with speckle no. higher max (green): " + more);
			IJ.log("   All speckles: " + NegMore);
			if(more!=0) {
				IJ.log("   Aver. speckle no/feature: " + (NegMore/more));
			}
			IJ.log("----------------------------------------------------");
			//IJ.log(" Speckle Inspector plugin by BioVoxxel/Dr. Jan Brocher, 2014, " + version);
		}

		if(secondaryObjectAnalysis) {
			analyzeRoiSet(primaryRoiManager, secondaryObjectImp);
		}

		analyzeObjects = null;
		//rm = null;
		
		Analyzer.setRedirectImage(null);

	}
	
	public String[] getOpenImageNames() {
		int[] imageIDList = WindowManager.getIDList();
		String[] imageNames = new String[imageIDList.length+1];
		imageNames[0] = "None";
		
		if(!imageIDList.equals(null)) {
			for(int i=0; i<imageIDList.length; i++){
				   ImagePlus tempIMP = WindowManager.getImage(imageIDList[i]);
				   imageNames[i+1] = tempIMP.getTitle();
			}
			return imageNames;
			} else {
				IJ.error("no open images detected");
				return imageNames;
		}
	}


	public void analyzeRoiSet(RoiManager rm, ImagePlus roiAnalysisImp) {
		
		//IJ.run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction stack display redirect=None decimal=3");
		int counter = rm.getCount();
		ImageProcessor roiAnalysisIP = roiAnalysisImp.getProcessor();
		ResultsTable roiResults = new ResultsTable();
		//ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.RECORD_STARTS, measurementFlags, roiResults, minSpeckleSize, maxSpeckleSize);
		for(int n=0; n<counter;n++) {
			//IJ.log(""+n+". "+roiArray[n]);
			roiAnalysisImp.killRoi();
			roiAnalysisImp.setRoi(rm.getRoi(n), false);
			ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.RECORD_STARTS, measurementFlags, roiResults, minSecondarySize, maxSecondarySize, minSecondaryCircularity, maxSecondaryCircularity); //original setup, only kept if current code not functional
			roiAnalyzer.analyze(roiAnalysisImp, roiAnalysisIP);
			//prepare this Particle Analyzer for garbage collection
			roiAnalyzer = null;
		}
		
		roiResults.show("Roi Analysis");
		
	}


	/*
	public void setNPasses(int nPasses) {
		this.nPasses = nPasses;
		pass = 0;
	}
	*/
}
