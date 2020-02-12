package de.biovoxxel.toolbox;


import java.awt.Color;
import java.awt.Font;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
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
 * 13-08-15 v1.1 bugfix: speckle analysis was done earlier in the bounding box instead of the real ROI. This behaviour is solved
 * 
 */
public class Speckle_Inspector implements PlugInFilter {
	ImagePlus imp;
	private String version = "v0.0.4";
	private int flags = DOES_ALL;
	private int nPasses, pass;
	
	//Define variables
	private int positive=0;
	//private int negative=0;
	private int less=0;
	private int more=0;
	private int PosPart=0;
	private int NegLess=0;
	private int NegMore=0;
	//define input variables
	private String bigObjects;
	private String smallObjects;
	private String redirectToImage;
	private double minObjectSize;
	private double maxObjectSize;
	private double minObjectCirc;
	private double maxObjectCirc;
	private double minSpeckleNumber;
	private double maxSpeckleNumber;
	private double minSpeckleSize;
	private double maxSpeckleSize;
	private boolean excludeEdge;
	private boolean showRoiManager;
	private boolean showSpeckleList;
	private boolean showStatisticsLog;
	private boolean individualRoiAnalysis;
	private double fontSize;
	
	//Particle Analyzer flag definitions
	private int measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
	private int analyzerOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS;
	private int speckleAnalyzerOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS;
	private RoiManager rm;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}
		
	public void run(ImageProcessor ip) {

		String[] imageNames = getOpenImageNames();
				
		GenericDialog gd = new GenericDialog("Speckle Inspector");
			gd.addChoice("big objects", imageNames, imageNames[0]);
			gd.addChoice("small speckles", imageNames, imageNames[0]);
			gd.addChoice("redirect measurements to", imageNames, imageNames[0]);
			gd.addNumericField("min_Object size: ", 0, 0, 9, "pixel");
			gd.addNumericField("max_Object size: ", Double.POSITIVE_INFINITY, 0, 9, "pixel");
			gd.addNumericField("min_Object_circularity: ", 0.00, 2, 9, "");
			gd.addNumericField("max_Object_circularity: ", 1.00, 2, 9, "");
			
			gd.addNumericField("min_Speckle_number: ", 0, 0, 9, "");
			gd.addNumericField("max_Speckle_number: ", Double.POSITIVE_INFINITY, 0, 9, "");
			gd.addNumericField("min_Speckle_size: ", 0, 0, 9, "pixel");
			gd.addNumericField("max_Speckle_size: ", Double.POSITIVE_INFINITY, 0, 9, "pixel");
			
			gd.addCheckbox("exclude objects on edges", true);
			gd.addCheckbox("roi manager visible", false);
			gd.addCheckbox("speckle list", false);
			gd.addCheckbox("statistic log", false);
			gd.addCheckbox("individual_roi analysis", false);

			gd.addNumericField("font size (label)", 10, 0, 9, "");
			
			gd.addHelp("http://fiji.sc/BioVoxxel_Toolbox#Speckle_Inspector");
			
			gd.showDialog();
			gd.setSmartRecording(true);
			if(gd.wasCanceled()) {
				return;
			}
			bigObjects = gd.getNextChoice();
			smallObjects = gd.getNextChoice();
			redirectToImage = gd.getNextChoice();
			minObjectSize = gd.getNextNumber();
			maxObjectSize = gd.getNextNumber();
			minObjectCirc = gd.getNextNumber();
			maxObjectCirc = gd.getNextNumber();
			minSpeckleNumber = gd.getNextNumber();
			maxSpeckleNumber = gd.getNextNumber();
			minSpeckleSize = gd.getNextNumber();
			maxSpeckleSize = gd.getNextNumber();
			
			excludeEdge = gd.getNextBoolean();
			if(excludeEdge==true) {
				analyzerOptions |= ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			} else {
				analyzerOptions &= ~ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			}
			showRoiManager = gd.getNextBoolean();
			showSpeckleList = gd.getNextBoolean();
			showStatisticsLog = gd.getNextBoolean();
			individualRoiAnalysis = gd.getNextBoolean();
			fontSize = gd.getNextNumber();

			if(gd.invalidNumber()) {
				IJ.error("invalid number entered");
				return;
			}
		
		if(bigObjects.equals(smallObjects)) {
			IJ.error("images need to be different");
			return;
		}
		ImagePlus bigObjectImp = WindowManager.getImage(bigObjects);
		bigObjectImp.killRoi();
		ImageProcessor bigObjectIP = bigObjectImp.getProcessor();
		ImagePlus smallObjectImp = WindowManager.getImage(smallObjects);
		smallObjectImp.killRoi();
		ImageProcessor smallObjectIP = smallObjectImp.getProcessor();

		if(!bigObjectIP.isBinary() || !smallObjectIP.isBinary()) {
			IJ.error("works with 8-bit binary images only");
			return;
		}
		
		if((bigObjectImp.getWidth()!=smallObjectImp.getWidth()) || bigObjectImp.getHeight()!=smallObjectImp.getHeight()) {
			IJ.error("images need to be of the same size");
			return;
		}
		
		ResultsTable rt = new ResultsTable();
		
		ParticleAnalyzer analyzeObjects = new ParticleAnalyzer(analyzerOptions, measurementFlags, rt, minObjectSize, maxObjectSize, minObjectCirc, maxObjectCirc);
		analyzeObjects.analyze(bigObjectImp);
		int objectNumber = rt.getCounter();

		//close an existing RoiManager before instantiating a new one for this analysis
		RoiManager oldRM = RoiManager.getInstance2();
		if(oldRM!=null) {
			oldRM.close();
		}
		if(showRoiManager) {
			rm = new RoiManager();
		} else {
			rm = new RoiManager(true);
		}
		

		int[] x = new int[objectNumber];
		int[] y = new int[objectNumber];
		int[] cX = new int[objectNumber];
		int[] cY = new int[objectNumber];

		Calibration cal = bigObjectImp.getCalibration();
		Recorder rec = Recorder.getInstance();
		if(rec!=null) {
			 Recorder.record = false;
		}
		
		for(int i=0; i<objectNumber; i++) {
			x[i] = (int) rt.getValue("XStart", i);
			y[i] = (int) rt.getValue("YStart", i);
			cX[i] = (int) cal.getRawX(rt.getValue("X", i));
			cY[i] = (int) cal.getRawY(rt.getValue("Y", i));
			IJ.doWand(bigObjectImp, x[i], y[i], 0.0, "8-connected");
			bigObjectImp.getRoi().setName(Integer.toString(i+1));
			rm.addRoi(bigObjectImp.getRoi());
			
		}
		//Roi[] objectRoi = rm.getRoisAsArray();  //this  created bounding boxes instead of the original ROIs

		if(rec!=null) {
			 Recorder.record = true;
		}
		
		//analyze speckles
		rt.reset();
		int[] specklesPerObject = new int[objectNumber];
		
		if(!redirectToImage.equalsIgnoreCase("None")) {
			Analyzer.setRedirectImage(WindowManager.getImage(redirectToImage));
		}
		
		for(int o=0; o<objectNumber; o++) {
			ParticleAnalyzer analyzeSpeckles = new ParticleAnalyzer(speckleAnalyzerOptions, measurementFlags, rt, minSpeckleSize, maxSpeckleSize);
			//smallObjectImp.killRoi();
			smallObjectImp.setRoi(rm.getRoi(o), false);
			analyzeSpeckles.analyze(smallObjectImp, smallObjectIP);
			specklesPerObject[o] = rt.getCounter();
			//IJ.log(""+specklesPerObject[o]);
			
			rt.reset();
			analyzeSpeckles = null;
		}
		
		//create output image
		bigObjectImp.killRoi();
		ImagePlus outputImp = bigObjectImp.duplicate();
		outputImp.setTitle(WindowManager.getUniqueName("Inspector of " + bigObjects));
		ImageConverter outputImgConverter = new ImageConverter(outputImp);
		outputImgConverter.convertToRGB();
		ImageProcessor outputIP = outputImp.getProcessor();

		Font font = new Font("Sans Serif", java.awt.Font.BOLD, (int) fontSize);
		outputIP.setFont(font);
		
		FloodFiller outputFloodFiller = new FloodFiller(outputIP);
		
		for(int c=0; c<objectNumber; c++) {
			if(specklesPerObject[c]>=minSpeckleNumber && specklesPerObject[c]<=maxSpeckleNumber) {
				outputIP.setColor(Color.magenta);
				outputFloodFiller.fill8(x[c], y[c]);
				positive=positive+1;
				PosPart=PosPart+specklesPerObject[c];
			} else if(specklesPerObject[c]<minSpeckleNumber) {
				outputIP.setColor(Color.blue);
				outputFloodFiller.fill8(x[c], y[c]);
				less=less+1;
				NegLess=NegLess+specklesPerObject[c];
			} else if(specklesPerObject[c]>maxSpeckleNumber) {
				Color drawColor = new Color(0,93,0);
				outputIP.setColor(drawColor);
				outputFloodFiller.fill8(x[c], y[c]);
				more=more+1;
				NegMore=NegMore+specklesPerObject[c];
			}
			outputIP.setColor(Color.white);
			outputIP.drawString("" + (c+1) + "-(" + specklesPerObject[c] + ")", cX[c]-((int)(fontSize*1.5)), cY[c]+((int)(fontSize/2)));
		}
		outputFloodFiller = null;
		outputImp.show();
		//outputImp.updateAndDraw();

		//write speckle counts in speckle list (a results table)
		if(showSpeckleList==true) {
			ResultsTable speckleList = new ResultsTable();
			speckleList.setPrecision(0);
			speckleList.setValue("Object", 0, 1);
			speckleList.setValue("Speckles", 0, specklesPerObject[0]);
			speckleList.incrementCounter();
			for(int r=1; r<objectNumber; r++) {
				speckleList.addValue("Object", r+1);
				speckleList.setValue("Speckles", r, specklesPerObject[r]);
				if(r<objectNumber-1) {
					speckleList.incrementCounter();
				}
			}
			speckleList.showRowNumbers(false);
			speckleList.show("Speckle List " + bigObjects);
		}

		//calculate statistics and IJ.log to log window
		if(objectNumber==0) {
			IJ.log("No Objects detected");
		} else if((PosPart+NegLess+NegMore)==0) {
			IJ.log("No Speckles detected");
		} else if(showStatisticsLog) {
			IJ.log("Object size limit min: " + minObjectSize + " / max: " + maxObjectSize);
			IJ.log("Circularity limit min: " + minObjectCirc + " / max: " + maxObjectCirc);
			IJ.log("Speckle no. limit min: " + minSpeckleNumber + " / max: " + maxSpeckleNumber);
			IJ.log("Speckle size limit min: " + minSpeckleSize + " / max: " + maxSpeckleSize);
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
			IJ.log(" Speckle Inspector plugin by BioVoxxel/Dr. Jan Brocher, 2014, " + version);
		}

		if(individualRoiAnalysis) {
			analyzeRoiSet(rm, smallObjectImp);
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
			ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.RECORD_STARTS, measurementFlags, roiResults, minSpeckleSize, maxSpeckleSize); //original setup, only kept if current code not functional
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
