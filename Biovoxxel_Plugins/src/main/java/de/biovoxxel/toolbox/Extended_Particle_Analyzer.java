package de.biovoxxel.toolbox;



import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.CanvasResizer;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

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
 	Copyright (C), Jan Brocher / BioVoxxel.

	All Macros/Plugins were written by Jan Brocher/BioVoxxel.

	Redistribution and use in source and binary forms of all plugins and macros, with or without modification, are permitted provided that the following conditions are met:

	1.) Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
	2.) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
	
	DISCLAIMER:

	THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ?AS IS? AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
	@author Jan Brocher/BioVoxxel
	@version 0.2.5
 
	version history:
	0.2.5: improved exception handling and parameter control / redirecting to original image correctly analyzes Mean, Skew and Kurtosis
	0.2.6: measurements from a redirected intensity based images are correctly placed in the results table

 */

public class Extended_Particle_Analyzer implements PlugInFilter {

	public ImagePlus inputImp, redirectedImg, outputImg;
	//ResultsTable finalResultsTable = new ResultsTable();
	private RoiManager redirectRoiManager = new RoiManager(true);
	private int nPasses, pass;
	private int flags = DOES_8G;
	
	//variable for image titles
	private int originalImgID;
	public String originalImgTitle;
	private int outputImgID;
	public String outputImgTitle;
	
	//image dimension variables
	private int width, height, slices;
	private int[] imgDimensions = new int[5];
	
	//calibration variables
	private double pixelWidth, pixelHeight, squaredPixel;

		
	//Dialog and restriction parameter definition
	public String Area, Extent, Perimeter, Circularity, Roundness, Solidity, Compactness, AR, FeretAR, EllipsoidAngle, MaxFeret, MinFeret, FeretAngle, COV;
	public String Output, Redirect, Correction;
	public String unit;
	public String reset;
	public boolean usePixel, usePixelForOutput, Reset, DisplayResults, ClearResults, Summarize, AddToManager, ExcludeEdges, IncludeHoles; //checkbox variable
	public int displayResults, summarize, addtoManager, excludeEdges, includeHoles; //checkbox result variables
	public int currentPAOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS|ParticleAnalyzer.SHOW_MASKS;
	public int measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
	private int outputOptions = ParticleAnalyzer.RECORD_STARTS;
	public double AreaMin = 0.0;
	public double AreaMax = Double.POSITIVE_INFINITY;
	public double CircularityMin = 0.0;
	public double CircularityMax = 1.0;
		
	//measurement variables
	private int[] X, Y;
	private int[] keptResults;
	private Calibration calibImg;
	private String[] imageNames;
	public ResultsTable outputResultsTable = null;

	
	public Extended_Particle_Analyzer() {
		
	}
	
	//------------------------------------------------------------------------------------------------------------------------	
	public int setup(String arg, ImagePlus imp1) {
    		this.inputImp = imp1;
    		return flags;
	}
	
	public void run(ImageProcessor ip1) {


	//------------------------------------------------------------------------------------------------------------------------

		readInputImageParameters(inputImp);
		
		//define variables
		boolean previousUnit = Prefs.get("advPartAnal.unit", false);
		boolean previousOutputUnit = Prefs.get("advPartAnal.outputUnit", false);

		String previousArea = Prefs.get("advPartAnal.area", "0-Infinity");
		String previousExtent = Prefs.get("advPartAnal.Extent", "0.00-1.00");
		String previousPerim = Prefs.get("advPartAnal.perimeter", "0-Infinity");
		String previousCirc = Prefs.get("advPartAnal.circularity", "0.00-1.00");
		String previousRound = Prefs.get("advPartAnal.roundness", "0.00-1.00");
		String previousSolidity = Prefs.get("advPartAnal.solidity", "0.00-1.00");
		String previousCompactness = Prefs.get("advPartAnal.compactness", "0.00-1.00");
		String previousAR = Prefs.get("advPartAnal.AR", "0.00-Infinity");
		String previousFAR = Prefs.get("advPartAnal.FAR", "0.00-Infinity");
		String previousAngle = Prefs.get("advPartAnal.angle", "0-180");
		String previousMaxFeret = Prefs.get("advPartAnal.max.feret", "0-Infinity");
		String previousMinFeret = Prefs.get("advPartAnal.min.feret", "0-Infinity");
		String previousFeretAngle = Prefs.get("advPartAnal.feret.angle", "0-180");
		String previousCOV = Prefs.get("advPartAnal.Stringiation.coefficient", "0.00-1.00");
		String previousShow = Prefs.get("advPartAnal.show", "Masks");
		String previousCorrection = Prefs.get("advPartAnal.borderCountCorrection", "None");
		String[] checkboxLabels = new String[] {"Display results", "Clear results", "Summarize", "Add to Manager", "Exclude edges", "Include holes", "Reset after analysis"};
		boolean[] previousCheckboxGroup = new boolean[7];
		previousCheckboxGroup[0] = Prefs.get("advPartAnal.CB0", true);
		previousCheckboxGroup[1] = Prefs.get("advPartAnal.CB1", false);
		previousCheckboxGroup[2] = Prefs.get("advPartAnal.CB2", false);
		previousCheckboxGroup[3] = Prefs.get("advPartAnal.CB3", false);
		previousCheckboxGroup[4] = Prefs.get("advPartAnal.CB4", false);
		previousCheckboxGroup[5] = Prefs.get("advPartAnal.CB5", false);
		previousCheckboxGroup[6] = Prefs.get("advPartAnal.CB6", false);
		
		//Setup including shape descriptors
		GenericDialog APAdialog = new GenericDialog("Extended Particle Analyzer");
		
			if(!unit.equalsIgnoreCase("pixel")||!unit.equalsIgnoreCase("pixels")) {
				APAdialog.addCheckbox("Pixel units", previousUnit);
				APAdialog.addCheckbox("Output_in_pixels", previousOutputUnit);				
			}
			APAdialog.addStringField("Area ("+unit+"^2)", previousArea);
			APAdialog.addStringField("Extent", previousExtent);
			APAdialog.addStringField("Perimeter", previousPerim);
			APAdialog.addStringField("Circularity", previousCirc);
			APAdialog.addStringField("Roundness (IJ)", previousRound);
			APAdialog.addStringField("Solidity", previousSolidity);
			APAdialog.addStringField("Compactness", previousCompactness);
			APAdialog.addStringField("Aspect ratio (AR)", previousAR);
			APAdialog.addStringField("Feret_AR", previousFAR);
			APAdialog.addStringField("Ellipsoid_angle (degree)", previousAngle);
			APAdialog.addStringField("Max_Feret", previousMaxFeret);
			APAdialog.addStringField("Min_Feret", previousMinFeret);
			APAdialog.addStringField("Feret_Angle (degree)", previousFeretAngle);
			APAdialog.addStringField("Coefficient of variation", previousCOV);
			APAdialog.addChoice("Show", new String[] {"Nothing", "Masks", "Outlines", "Count Masks", "Overlay Outlines", "Overlay Masks"}, previousShow);
			APAdialog.addChoice("Redirect to", imageNames, "None");
			APAdialog.addChoice("Keep borders (correction)", new String[] {"None", "Top-Left", "Top-Right", "Bottom-Left", "Bottom-Right"}, previousCorrection);
			APAdialog.addCheckboxGroup(4, 2, checkboxLabels, previousCheckboxGroup);
			APAdialog.addHelp("http://imagej.net/BioVoxxel_Toolbox");
			APAdialog.showDialog();
			APAdialog.setSmartRecording(true);
			if(APAdialog.wasCanceled()) {
				return;
			}
			
			if(!unit.equalsIgnoreCase("pixel")||!unit.equalsIgnoreCase("pixels")) {
				usePixel=APAdialog.getNextBoolean();
				Prefs.set("advPartAnal.unit", usePixel);
				
				usePixelForOutput=APAdialog.getNextBoolean();
				Prefs.set("advPartAnal.outputUnit", usePixelForOutput);
			}
			
			Area=APAdialog.getNextString();
			testValidUserInput(Area);
			Prefs.set("advPartAnal.area", Area);
			
			Extent=APAdialog.getNextString();
			testValidUserInput(Extent);
			testValidShapeDescriptor(Extent);
			Prefs.set("advPartAnal.Extent", Extent);
			
			Perimeter=APAdialog.getNextString();
			testValidUserInput(Perimeter);
			Prefs.set("advPartAnal.perimeter", Perimeter);
			
			Circularity=APAdialog.getNextString();
			testValidUserInput(Circularity);
			testValidShapeDescriptor(Circularity);
			Prefs.set("advPartAnal.circularity", Circularity);
			
			Roundness=APAdialog.getNextString();
			testValidUserInput(Roundness);
			testValidShapeDescriptor(Roundness);
			Prefs.set("advPartAnal.roundness", Roundness);
			
			Solidity=APAdialog.getNextString();
			testValidUserInput(Solidity);
			testValidShapeDescriptor(Solidity);
			Prefs.set("advPartAnal.solidity", Solidity);
			
			Compactness=APAdialog.getNextString();
			testValidUserInput(Compactness);
			testValidShapeDescriptor(Compactness);
			Prefs.set("advPartAnal.compactness", Compactness);
			
			AR=APAdialog.getNextString();
			testValidUserInput(AR);
			Prefs.set("advPartAnal.AR", AR);
			
			FeretAR=APAdialog.getNextString();
			testValidUserInput(FeretAR);
			Prefs.set("advPartAnal.FAR", FeretAR);
			
			EllipsoidAngle=APAdialog.getNextString();
			testValidUserInput(EllipsoidAngle);
			testValidAngle(EllipsoidAngle);
			Prefs.set("advPartAnal.angle", EllipsoidAngle);
			
			MaxFeret=APAdialog.getNextString();
			testValidUserInput(MaxFeret);
			Prefs.set("advPartAnal.max.feret", MaxFeret);
			
			MinFeret=APAdialog.getNextString();
			testValidUserInput(MinFeret);
			Prefs.set("advPartAnal.min.feret", MinFeret);
			
			FeretAngle=APAdialog.getNextString();
			testValidUserInput(FeretAngle);
			testValidAngle(FeretAngle);
			Prefs.set("advPartAnal.feret.angle", FeretAngle);
			
			COV=APAdialog.getNextString();
			testValidUserInput(COV);
			Prefs.set("advPartAnal.variation.coefficient", COV);	
			
			Output=APAdialog.getNextChoice();
			Prefs.set("advPartAnal.show", Output);
						
			Redirect=APAdialog.getNextChoice();
			
			Correction=APAdialog.getNextChoice();
			Prefs.set("advPartAnal.borderCountCorrection", Correction);
						
			DisplayResults=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB0", DisplayResults);

			ClearResults=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB1", ClearResults);
			
			Summarize=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB2", Summarize);
			
			AddToManager=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB3", AddToManager);
			
			ExcludeEdges=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB4", ExcludeEdges);
						
			IncludeHoles=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB5", IncludeHoles);
			
			Reset=APAdialog.getNextBoolean();
			Prefs.set("advPartAnal.CB6", Reset);

	//------------------------------------------------------------------------------------------------------------------------

		
	    if(!ip1.isBinary()) {
			IJ.showMessage("works only on individual 8-bit binary images");
			return;
		}

		defineParticleAnalyzers();
		
		ImageProcessor ip2 = ip1.duplicate();
		
		//-------------------------------------------------------------------------------------------
		
		ip2 = borderCountCorrection(ip1, Correction);
		ImagePlus imp2 = new ImagePlus(WindowManager.getUniqueName(originalImgTitle), ip2);		
				
		if(!Redirect.equals("None")) {
			currentPAOptions |= ParticleAnalyzer.ADD_TO_MANAGER;
			ParticleAnalyzer.setRoiManager(redirectRoiManager);
			IJ.selectWindow(Redirect);
			redirectedImg = IJ.getImage();	
		}
		
		
		//-------------------------------------------------------------------------------------------
	     
	     particleAnalysis(ip2, imp2, originalImgTitle);
	     if(IJ.escapePressed()) {
			ip1.reset();
	     }

		if(!Prefs.blackBackground) {
		ip1.invert();
		}
		if(Prefs.blackBackground && ip1.isInvertedLut()) {
			ip1.invertLut();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------

	public void readInputImageParameters(ImagePlus startingImage) {
		if(!startingImage.getProcessor().isBinary()) {
			IJ.error("works with 8-bit binary images only");
			return;
		}

		//getting general dimensional information
		originalImgID = startingImage.getID();
		originalImgTitle = startingImage.getTitle();
			//IJ.log(""+originalImgID);
		width = startingImage.getWidth();
		height = startingImage.getHeight();
		imgDimensions = startingImage.getDimensions();
		slices = imgDimensions[3];
		if(slices>1) {
			IJ.error("does not work with stacks");
			return;
		}

		if(!Prefs.blackBackground) {
			startingImage.getProcessor().invert();
		}
		if(Prefs.blackBackground && startingImage.getProcessor().isInvertedLut()) {
			startingImage.getProcessor().invertLut();
		}

		//reading in calibration information
		calibImg = startingImage.getCalibration();
		
		unit = calibImg.getUnit();
		pixelWidth = calibImg.pixelWidth;
		pixelHeight = calibImg.pixelHeight;
		squaredPixel = pixelWidth * pixelHeight;
		
		//prepare environment and read in names of all open image windows
		imageNames = getImageNames(startingImage);
		
	}

	public void particleAnalysis(ImageProcessor ip, ImagePlus imp2, String originalImageTitle) {

		if(!Area.equalsIgnoreCase("0-Infinity")) {
			AreaMin = Double.parseDouble(Area.substring(0, Area.indexOf("-")));
			String AreaInterMax = Area.substring(Area.indexOf("-")+1);
			if(AreaInterMax.equalsIgnoreCase("Infinity")) { 
				AreaMax = Double.POSITIVE_INFINITY;
			} else {
				AreaMax = Double.parseDouble(Area.substring(Area.indexOf("-")+1));
			}
		} 

		if(!usePixel) {
			AreaMin = AreaMin / squaredPixel;
			AreaMax = AreaMax / squaredPixel;
		}
		

		if(!Circularity.equals("0.00-1.00")) {
			CircularityMin = Double.parseDouble(Circularity.substring(0, Circularity.indexOf("-")));
			CircularityMax = Double.parseDouble(Circularity.substring(Circularity.indexOf("-")+1));
		}
		
		//makes sure that renaming of results tables is not recorded
		Recorder rec = Recorder.getInstance();
		if(rec!=null) {
			 Recorder.record = false;
		}
		//read in existing results table	
		TextWindow existingResultsTableWindow = ResultsTable.getResultsWindow();
		if(existingResultsTableWindow!=null) {
			IJ.renameResults("oldResultsTable");
		}
		ResultsTable initialResultsTable = new ResultsTable();
		
		//define the new particle analyzer
		ParticleAnalyzer initialPA = new ParticleAnalyzer(currentPAOptions, measurementFlags, initialResultsTable, AreaMin, AreaMax, CircularityMin, CircularityMax);
		initialPA.setHideOutputImage(true);
		
		//perform the initial analysis of the image 
		initialPA.analyze(imp2);
		int initialResultNumber = initialResultsTable.getCounter();
			//resultsTable.show("Results"); //keep for test output
			//IJ.renameResults("Results", "initial Results Table");
		
		X = new int[initialResultNumber];
		Y = new int[initialResultNumber];
		keptResults = new int[initialResultNumber];
		
		for(int coord=0; coord<initialResultNumber; coord++) {
			X[coord] = (int) initialResultsTable.getValue("XStart", coord);
			Y[coord] = (int) initialResultsTable.getValue("YStart", coord);
		}
		
		ImagePlus tempImg = initialPA.getOutputImage();
		if(!Redirect.equals("None") && !usePixelForOutput) {
			tempImg.setCalibration(WindowManager.getImage(Redirect).getCalibration());
		} else if(Redirect.equals("None") && !usePixelForOutput) {
			tempImg.setCalibration(calibImg);			
		} else if(usePixelForOutput) {
			Calibration newCal = new Calibration();
			newCal.setUnit("pixels");
			
			tempImg.setCalibration(null);
		}
		//tempImg.show();
		ImageProcessor tempIP = tempImg.getProcessor();
		if(tempIP.isInvertedLut()) {
			tempIP.invertLut();
		}
			//tempImg.updateAndDraw(); //not necessary to display this intermediate image but keep as control output option

		//Read in ROIs and values from the redirected image to be able to analyze coefficient of variance, skewness and kurtosis
		
		if(!Redirect.equals("None")) {
			IJ.selectWindow(Redirect);
			initialResultsTable.reset();
			redirectRoiManager.runCommand("Measure");
			initialResultsTable = Analyzer.getResultsTable();
				//redirectedResultsTable.show("Results");
				//IJ.renameResults("ROIs");
		}
				
		//Calculate additional values not present in original results table from the normal particle analyzer
		double[] compactness = new double[initialResultNumber];
		double[] FAR = new double[initialResultNumber];
		double[] extent = new double[initialResultNumber];
		double[] cov = new double[initialResultNumber];
		double[] originalMeanValue = new double[initialResultNumber];
		double[] originalMedian = new double[initialResultNumber];
		double[] originalMode = new double[initialResultNumber];
		double[] originalStdDev = new double[initialResultNumber];
		double[] originalIntDen = new double[initialResultNumber];
		double[] originalRawIntDen = new double[initialResultNumber];
		double[] originalMin = new double[initialResultNumber];
		double[] originalMax = new double[initialResultNumber];
		double[] originalSkewness = new double[initialResultNumber];
		double[] originalKurtosis = new double[initialResultNumber];
		for(int calc=0; calc<initialResultNumber; calc++) {
			originalMeanValue[calc] = initialResultsTable.getValue("Mean", calc);
			originalMedian[calc] = initialResultsTable.getValue("Median", calc);
			originalMode[calc] = initialResultsTable.getValue("Mode", calc);
			originalStdDev[calc] = initialResultsTable.getValue("StdDev", calc);
			originalIntDen[calc] = initialResultsTable.getValue("IntDen", calc);
			originalRawIntDen[calc] = initialResultsTable.getValue("RawIntDen", calc);
			originalMin[calc] = initialResultsTable.getValue("Min", calc);
			originalMax[calc] = initialResultsTable.getValue("Max", calc);
			originalSkewness[calc] = initialResultsTable.getValue("Skew", calc);
			originalKurtosis[calc] = initialResultsTable.getValue("Kurt", calc);
			FAR[calc]=((initialResultsTable.getValue("Feret", calc))/(initialResultsTable.getValue("MinFeret", calc)));
			compactness[calc]=(Math.sqrt((4/Math.PI)*initialResultsTable.getValue("Area", calc))/initialResultsTable.getValue("Major", calc));
			extent[calc]=(initialResultsTable.getValue("Area", calc)/((initialResultsTable.getValue("Width", calc))*(initialResultsTable.getValue("Height", calc))));
			cov[calc]=((initialResultsTable.getValue("StdDev", calc))/(initialResultsTable.getValue("Mean", calc)));
		}
	
		//elimination process of particles
		FloodFiller filledImage = new FloodFiller(tempIP);
		tempIP.setValue(0.0);

		Boolean continueProcessing;
		int KeptResultsCount = 0;
		for(int n=0; n<initialResultNumber; n++) {
			continueProcessing=true;
			if(!Extent.equals("0.00-1.00") && continueProcessing) {
				double ExtentMin = Double.parseDouble(Extent.substring(0, Extent.indexOf("-")));
				double ExtentMax = Double.parseDouble(Extent.substring(Extent.indexOf("-")+1));
				if(extent[n]<ExtentMin || extent[n]>ExtentMax) {
					filledImage.fill8(X[n], Y[n]);
					continueProcessing=true;
					//IJ.log("Extent");
				}
			}
			
			
			if((!Perimeter.equalsIgnoreCase("0-infinity")) && continueProcessing) {
					double PerimeterMin = Double.parseDouble(Perimeter.substring(0, Perimeter.indexOf("-")));
					double PerimeterMax = Double.POSITIVE_INFINITY;
					String PerimeterInterMax = Perimeter.substring(Perimeter.indexOf("-")+1);
					if(PerimeterInterMax.equalsIgnoreCase("infinity")) { 
						PerimeterMax = Double.POSITIVE_INFINITY;
					} else {
						PerimeterMax = Double.parseDouble(Perimeter.substring(Perimeter.indexOf("-")+1));
					}
					double currentPerimeter = initialResultsTable.getValue("Perim.", n);
					
					if(!usePixel) {
						currentPerimeter = currentPerimeter * pixelWidth;
					}
					
					if(currentPerimeter<PerimeterMin || currentPerimeter>PerimeterMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("Perimeter");
					}
						
			}
	
			if(!Roundness.equals("0.00-1.00") && continueProcessing) {
					double RoundnessMin = Double.parseDouble(Roundness.substring(0, Roundness.indexOf("-")));
					double RoundnessMax = Double.parseDouble(Roundness.substring(Roundness.indexOf("-")+1));
					if(initialResultsTable.getValue("Round", n)<RoundnessMin || initialResultsTable.getValue("Round", n)>RoundnessMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("Roundness");
					}
			}
				
			if(!Solidity.equals("0.00-1.00") && continueProcessing) {
					double SolidityMin = Double.parseDouble(Solidity.substring(0, Solidity.indexOf("-")));
					double SolidityMax = Double.parseDouble(Solidity.substring(Solidity.indexOf("-")+1));
					if(initialResultsTable.getValue("Solidity", n)<SolidityMin || initialResultsTable.getValue("Solidity", n)>SolidityMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("Solidity");
					}
			}
	
			if(!Compactness.equals("0.00-1.00") && continueProcessing) {
					double CompactnessMin = Double.parseDouble(Compactness.substring(0, Compactness.indexOf("-")));
					double CompactnessMax = Double.parseDouble(Compactness.substring(Compactness.indexOf("-")+1));
					if(compactness[n]<CompactnessMin || compactness[n]>CompactnessMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("Compactness");
					}
			}
				
			if((!AR.equalsIgnoreCase("0.00-infinity")) && continueProcessing) {
					double ARMin = Double.parseDouble(AR.substring(0, AR.indexOf("-")));
					double ARMax=999999999;
					String ARInterMax = AR.substring(AR.indexOf("-")+1);
					if(ARInterMax.equalsIgnoreCase("infinity")) {
						ARMax=999999999;
					} else {
						ARMax = Double.parseDouble(AR.substring(AR.indexOf("-")+1));
					}
					if(initialResultsTable.getValue("AR", n)<ARMin || initialResultsTable.getValue("AR", n)>ARMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("AR");
					}
			}
	
			if((!FeretAR.equalsIgnoreCase("0.00-infinity")) && continueProcessing) {
					double FARMin = Double.parseDouble(FeretAR.substring(0, FeretAR.indexOf("-")));
					double FARMax = Double.POSITIVE_INFINITY;
					String FARInterMax = FeretAR.substring(FeretAR.indexOf("-")+1);
					if(FARInterMax.equalsIgnoreCase("infinity")) {
						FARMax=Double.POSITIVE_INFINITY;
					} else {
						FARMax = Double.parseDouble(FeretAR.substring(FeretAR.indexOf("-")+1));
					}
					if(FAR[n]<FARMin || FAR[n]>FARMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("FeretAR");
					}
			}
			
			if(!EllipsoidAngle.equals("0-180") && continueProcessing) {
					double EllipsoidAngleMin = Double.parseDouble(EllipsoidAngle.substring(0, EllipsoidAngle.indexOf("-")));
					double EllipsoidAngleMax = Double.parseDouble(EllipsoidAngle.substring(EllipsoidAngle.indexOf("-")+1));
					if(initialResultsTable.getValue("Angle", n)<EllipsoidAngleMin || initialResultsTable.getValue("Angle", n)>EllipsoidAngleMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("EllipsoidAngle");
					}
			}
			
			if((!MaxFeret.equalsIgnoreCase("0.00-infinity")) && continueProcessing) {
					double MaxFeretMin = Double.parseDouble(MaxFeret.substring(0, MaxFeret.indexOf("-")));
					double MaxFeretMax=Double.POSITIVE_INFINITY;
					String MaxFeretInterMax = MaxFeret.substring(MaxFeret.indexOf("-")+1);
					if(MaxFeretInterMax.equalsIgnoreCase("infinity")) {
						MaxFeretMax=Double.POSITIVE_INFINITY;
					} else {
						MaxFeretMax = Double.parseDouble(MaxFeret.substring(MaxFeret.indexOf("-")+1));
					}
					double currentMaxFeret = initialResultsTable.getValue("Feret", n);
					if(!usePixel) {
						currentMaxFeret = currentMaxFeret * pixelWidth;
					}
					
					if(currentMaxFeret<MaxFeretMin || currentMaxFeret>MaxFeretMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("MaxFeret");
					}
			}
			
			if((!MinFeret.equalsIgnoreCase("0.00-infinity")) && continueProcessing) {
				double MinFeretMin = Double.parseDouble(MinFeret.substring(0, MinFeret.indexOf("-")));
				double MinFeretMax=Double.POSITIVE_INFINITY;
				String MinFeretInterMax = MinFeret.substring(MinFeret.indexOf("-")+1);
				if(MinFeretInterMax.equalsIgnoreCase("infinity")) {
					MinFeretMax=Double.POSITIVE_INFINITY;

				} else {
					MinFeretMax = Double.parseDouble(MinFeret.substring(MinFeret.indexOf("-")+1));
				}
				double currentMinFeret = initialResultsTable.getValue("MinFeret", n);
				
				if(!usePixel) {
					currentMinFeret = currentMinFeret * pixelWidth;
				}
				
				if(currentMinFeret<MinFeretMin || currentMinFeret>MinFeretMax) {
					filledImage.fill8(X[n], Y[n]);
					continueProcessing=true;
					//IJ.log("MinFeret");
				}
		}
				
			if(!FeretAngle.equals("0-180") && continueProcessing) {
					double FeretAngleMin = Double.parseDouble(FeretAngle.substring(0, FeretAngle.indexOf("-")));
					double FeretAngleMax = Double.parseDouble(FeretAngle.substring(FeretAngle.indexOf("-")+1));
					if(initialResultsTable.getValue("FeretAngle", n)<FeretAngleMin || initialResultsTable.getValue("FeretAngle", n)>FeretAngleMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("FeretAngle");
					}
			}
	
			if(!COV.equals("0.00-1.00") && continueProcessing) {
					double COVMin = Double.parseDouble(COV.substring(0, COV.indexOf("-")));
					double COVMax = Double.parseDouble(COV.substring(COV.indexOf("-")+1));
					if(cov[n]<COVMin || cov[n]>COVMax) {
						filledImage.fill8(X[n], Y[n]);
						continueProcessing=true;
						//IJ.log("COV");
					}
			}

			
			if(continueProcessing=true) {
				keptResults[KeptResultsCount] = n;
				KeptResultsCount++;
			}			
		}
		initialResultsTable = null;

		if(existingResultsTableWindow!=null) {
			IJ.renameResults("oldResultsTable", "Results");
			outputResultsTable = ResultsTable.getResultsTable();
		} else {
			outputResultsTable = new ResultsTable();
		}
		
		if(rec!=null) {
			 Recorder.record = true;
		}
		
		int existingResultsCounter = 0;
		ResultsTable resultsTable = new ResultsTable();
		
		int currentResultCount = 0;
	
		if(existingResultsTableWindow!=null && !ClearResults) {
			ParticleAnalyzer outputPA = new ParticleAnalyzer(outputOptions, measurementFlags, resultsTable, AreaMin, AreaMax);
			outputPA.analyze(tempImg);
			if (!Output.equals("Nothing")) {
				outputImg = outputPA.getOutputImage();
				outputImgID = outputImg.getID();				
			}
			currentResultCount = resultsTable.getCounter();
			//int currentColumnCount = resultsTable.getLastColumn();
			String[] tableHeadings = resultsTable.getHeadings();

			existingResultsCounter = outputResultsTable.getCounter();

			for(int row=0; row<currentResultCount; row++) {
				for(int column=0;column<tableHeadings.length; column++) {
					if(column==0) {
						outputResultsTable.incrementCounter();
						//outputResultsTable.addValue(tableHeadings[column], resultsTable.getStringValue(tableHeadings[column], row));
						outputResultsTable.addLabel(originalImageTitle);
						
					} else {
						try {
							outputResultsTable.getStringValue(tableHeadings[column], row);
						}
						catch(Exception e) {
							outputResultsTable.setValue(tableHeadings[column], row, 0);
						}
						
						try {
							outputResultsTable.setValue(tableHeadings[column], (row+existingResultsCounter), resultsTable.getValue(tableHeadings[column], row));
						} catch (Exception e) {
							//e.printStackTrace();
							outputResultsTable.setValue(tableHeadings[column], (row+existingResultsCounter), 0);
						}
					}
					
				}
			}
		} else if(existingResultsTableWindow==null || ClearResults) {
			ParticleAnalyzer outputPA = new ParticleAnalyzer(outputOptions, measurementFlags, outputResultsTable, AreaMin, AreaMax);
			outputPA.analyze(tempImg);
			if (!Output.equals("Nothing")) {
				outputImg = outputPA.getOutputImage();
				outputImgID = outputImg.getID();				
			}
			
			for(int l=0; l<outputResultsTable.getCounter(); l++) {
				outputResultsTable.setLabel(originalImageTitle, l);
			}
			
		}

		if(Output.equals("Nothing")) {
			imp2.close();
		} else if(Output.equals("Overlay Outlines")) {
			IJ.selectWindow(outputImgID);
			IJ.run("Invert");
			IJ.run("Red");
			IJ.selectWindow(originalImgID);
			IJ.run("Add Image...", "image=["+outputImg.getTitle()+"] x=0 y=0 opacity=75 zero");
			outputImg.changes = false;
			outputImg.close();
		} else if(Output.equals("Overlay Masks")) {
			IJ.selectWindow(outputImgID);
			IJ.run("Cyan");
			IJ.selectWindow(originalImgID);
			IJ.run("Add Image...", "image=["+outputImg.getTitle()+"] x=0 y=0 opacity=75 zero");
			outputImg.changes = false;
			outputImg.close();
		} else if(!Output.equals("Overlay Outlines") && !Output.equals("Overlay Masks") && !Output.equals("Nothing")) {
			//ImageProcessor outputIP = outputImg.getProcessor();
			outputImgTitle = outputImg.getTitle();
			outputImgID = outputImg.getID();
			outputImg.updateAndDraw();
		}

		//potentially include convexity calculation here
		
		int finalResultNumber = outputResultsTable.getCounter();
		int keptResultsCounter = 0;
		for(int writeNew=existingResultsCounter; writeNew<finalResultNumber; writeNew++) {
			outputResultsTable.setValue("FeretAR", writeNew, FAR[keptResults[keptResultsCounter]]);
			outputResultsTable.setValue("Compact", writeNew, compactness[keptResults[keptResultsCounter]]);
			outputResultsTable.setValue("Extent", writeNew, extent[keptResults[keptResultsCounter]]);
			if(!Redirect.equals("None")) {
				outputResultsTable.setValue("COV", writeNew, cov[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Mean", writeNew, originalMeanValue[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Median", writeNew, originalMedian[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Mode", writeNew, originalMode[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("StdDev", writeNew, originalStdDev[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("IntDen", writeNew, originalIntDen[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("RawIntDen", writeNew, originalRawIntDen[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Min", writeNew, originalMin[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Max", writeNew, originalMax[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Skew", writeNew, originalSkewness[keptResults[keptResultsCounter]]);
				outputResultsTable.setValue("Kurt", writeNew, originalKurtosis[keptResults[keptResultsCounter]]);	
			}
			keptResultsCounter++;
		}
		
		if(DisplayResults) {
			outputResultsTable.show("Results");
		}

		//Default value definition
		if(Reset) {
			resetDialogEntries();
		}
	}

	//------------------------------------------------------------------------------------------------------------------------

	public void closeLogWindow() {
		if(!IJ.getLog().equals(null)) {
			IJ.selectWindow("Log"); 
			IJ.run("Close"); 
		}
	}

	//------------------------------------------------------------------------------------------------------------------------

	public String[] getImageNames(ImagePlus imp1) {
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

	//------------------------------------------------------------------------------------------------------------------------

	public ImageProcessor borderCountCorrection(ImageProcessor inputIP, String correctionPosition) {
		
		if(!correctionPosition.equals("None")) {
			Prefs.set("resizer.zero", false);
			ij.Prefs.blackBackground = true;
			IJ.setBackgroundColor(255,255,255);
			CanvasResizer resizeCanvas = new CanvasResizer(); 
			int xOff = 0;
			int yOff = 0;
	
			if(correctionPosition.equals("Top-Left")) {
				xOff = 0;
				yOff = 0;
			} else if(correctionPosition.equals("Top-Right")) {
				xOff = 1;
				yOff = 0;
			} else if(correctionPosition.equals("Bottom-Left")) {
				xOff = 0;
				yOff = 1;				
			} else if(correctionPosition.equals("Bottom-Right")) {
				xOff = 1;
				yOff = 1;				
			}

			
			ImageProcessor intermediateIP = resizeCanvas.expandImage(inputIP, (width+1), (height+1), xOff, yOff);
						
			FloodFiller bcFF = new FloodFiller(intermediateIP);
			intermediateIP.setValue(0);
			
			//ImagePlus intermediateIMP = new ImagePlus("test imp", intermediateIP);	//test output
			//intermediateIMP.show();	//test output
			if(correctionPosition.equals("Top-Left") || correctionPosition.equals("Top-Right") || correctionPosition.equals("Bottom-Left")) {
				bcFF.fill8(width, height);
			} else if(correctionPosition.equals("Bottom-Right")) {
				bcFF.fill8(0, 0);
			}
			ImageProcessor ip2 = resizeCanvas.expandImage(intermediateIP, (width), (height), (-xOff), (-yOff));
			return ip2;
		} else {
			ImageProcessor ip2 = inputIP.duplicate();
			return ip2;
		}
	}

	//------------------------------------------------------------------------------------------------------------------------

	public void defineParticleAnalyzers() {
		//set the output options as in the particle analyzer
		//IJ.run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction stack display redirect=None decimal=3");
		if(Output.equals("Nothing")) {
			outputOptions |= ParticleAnalyzer.SHOW_NONE;
		} else if(Output.equals("Outlines")) {
			outputOptions |= ParticleAnalyzer.SHOW_OUTLINES;
		} else if(Output.equals("Masks")) {
			outputOptions |= ParticleAnalyzer.SHOW_MASKS;
		} else if(Output.equals("Count Masks")) {
			outputOptions |= ParticleAnalyzer.SHOW_ROI_MASKS;
		} else if(Output.equals("Overlay Outlines")) {
			outputOptions |= ParticleAnalyzer.SHOW_OUTLINES;
		} else if(Output.equals("Overlay Masks")) {
			outputOptions |= ParticleAnalyzer.SHOW_MASKS;
		}

		if(DisplayResults) {
			outputOptions |= ParticleAnalyzer.SHOW_RESULTS;
		} else {
			outputOptions &= ~ParticleAnalyzer.SHOW_RESULTS;
		}

		if(ClearResults) {
			outputOptions |= ParticleAnalyzer.CLEAR_WORKSHEET;
		} else {
			outputOptions &= ~ParticleAnalyzer.CLEAR_WORKSHEET;
		}

		if(Summarize) {
			outputOptions |= ParticleAnalyzer.DISPLAY_SUMMARY;
		} else {
			outputOptions &= ~ParticleAnalyzer.DISPLAY_SUMMARY;
		}

		if(AddToManager) {
			outputOptions |= ParticleAnalyzer.ADD_TO_MANAGER;
		} else {
			outputOptions &= ~ParticleAnalyzer.ADD_TO_MANAGER;
		}
		
		if(ExcludeEdges) {
			currentPAOptions |= ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			outputOptions |= ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		} else {
			currentPAOptions &= ~ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			outputOptions &= ~ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		}

		if(IncludeHoles) {
			currentPAOptions |= ParticleAnalyzer.INCLUDE_HOLES;
			outputOptions |= ParticleAnalyzer.INCLUDE_HOLES;
		} else {
			currentPAOptions &= ~ParticleAnalyzer.INCLUDE_HOLES;
			outputOptions &= ~ParticleAnalyzer.INCLUDE_HOLES;
		}
	}

	//------------------------------------------------------------------------------------------------------------------------

	public void resetDialogEntries() {
		Area="0-Infinity";
		Prefs.set("advPartAnal.area", Area);
		Extent="0.00-1.00";
		Prefs.set("advPartAnal.Extent", Extent);
		Perimeter="0-Infinity";
		Prefs.set("advPartAnal.perimeter", Perimeter);
		Circularity="0.00-1.00";
		Prefs.set("advPartAnal.circularity", Circularity);
		Roundness="0.00-1.00";
		Prefs.set("advPartAnal.roundness", Roundness);
		Solidity="0.00-1.00";
		Prefs.set("advPartAnal.solidity", Solidity);
		Compactness="0.00-1.00";
		Prefs.set("advPartAnal.compactness", Compactness);
		AR="0-Infinity";
		Prefs.set("advPartAnal.AR", AR);
		FeretAR="0-Infinity";
		Prefs.set("advPartAnal.FAR", FeretAR);
		EllipsoidAngle="0-180";
		Prefs.set("advPartAnal.angle", EllipsoidAngle);
		MaxFeret="0-Infinity";
		Prefs.set("advPartAnal.max.feret", MaxFeret);
		MinFeret="0-Infinity";
		Prefs.set("advPartAnal.min.feret", MinFeret);
		FeretAngle="0-180";
		Prefs.set("advPartAnal.feret.angle", FeretAngle);
		COV="0.00-1.00";
		Prefs.set("advPartAnal.variation.coefficient", COV);	
		Output="Masks";
		Prefs.set("advPartAnal.show", Output);
		Correction="None";
		Prefs.set("advPartAnal.borderCountCorrection", Correction);
		//checkbox default reset
		DisplayResults=true;
		Prefs.set("advPartAnal.CB0", DisplayResults);
		ClearResults = false;
		Prefs.set("advPartAnal.CB1", ClearResults);
		Summarize=false;
		Prefs.set("advPartAnal.CB2", Summarize);
		AddToManager=false;
		Prefs.set("advPartAnal.CB3", AddToManager);
		ExcludeEdges=false;
		Prefs.set("advPartAnal.CB4", ExcludeEdges);
		IncludeHoles=false;
		Prefs.set("advPartAnal.CB5", IncludeHoles);
		Reset=false;
		Prefs.set("advPartAnal.CB6", Reset);
	}
	
	//------------------------------------------------------------------------------------------------------------------------
		
	public void setDefaultParameterFields() {
		Area="0-Infinity";
		Extent="0.00-1.00";
		Perimeter="0-Infinity";
		Circularity="0.00-1.00";
		Roundness="0.00-1.00";
		Solidity="0.00-1.00";
		Compactness="0.00-1.00";
		AR="0-Infinity";
		FeretAR="0-Infinity";
		EllipsoidAngle="0-180";
		MaxFeret="0-Infinity";
		MinFeret="0-Infinity";
		FeretAngle="0-180";
		COV="0.00-1.00";
		Output="Masks";
		Correction="None";
		//checkbox default reset
		DisplayResults=true;
		ClearResults = false;
		Summarize=false;
		AddToManager=false;
		ExcludeEdges=false;
		IncludeHoles=false;
		Reset=false;
		Redirect = "None";
		
		currentPAOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS|ParticleAnalyzer.SHOW_MASKS;
		measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
		outputOptions = ParticleAnalyzer.RECORD_STARTS;
	}

	//------------------------------------------------------------------------------------------------------------------------
	
	public void testValidUserInput(String inputParameter) {
		String lowerInputParameter;
		String higherInputParameter;
		try {
			lowerInputParameter = inputParameter.substring(0, inputParameter.indexOf("-"));
			higherInputParameter = inputParameter.substring(inputParameter.indexOf("-")+1);
		}
		catch(StringIndexOutOfBoundsException sioobe) {
			IJ.error("missing '-' between parameter");
			return;
		}
		
		try {  
			double lowerValue = Double.parseDouble(lowerInputParameter);	
		} 
		catch(NumberFormatException nfe) {
			if(lowerInputParameter.equalsIgnoreCase("infinity")) {
				resetDialogEntries();
				IJ.error("Invalid parameter entry");
				return;
			} else {
				//continue
			}
			 
		}
		
		try {  
			double higherValue = Double.parseDouble(higherInputParameter);	
		} 
		catch(NumberFormatException nfe) {
			if(higherInputParameter.equalsIgnoreCase("infinity")) {
				resetDialogEntries();
				IJ.error("Invalid parameter entry");
				return;
			} else {
				//continue
			}
		}
		
		if(Double.parseDouble(lowerInputParameter) > Double.parseDouble(higherInputParameter)) {
			resetDialogEntries();
			IJ.error("min value bigger than max value");
			return;
		}
		
	}
	
	public void testValidAngle(String userInputAngle) {
		double lowerInputAngle = Double.parseDouble(userInputAngle.substring(0, userInputAngle.indexOf("-")));
		double higherInputAngle = Double.parseDouble(userInputAngle.substring(userInputAngle.indexOf("-")+1));
		if(lowerInputAngle<0.0 || higherInputAngle>180.0) {
			resetDialogEntries();
			IJ.error("Invalid angle entered (range: 0-180)");
			return;
		}
	}
	
	public void testValidShapeDescriptor(String userInputShapeDescriptor) {
		double lowerInputShapeDescriptor = Double.parseDouble(userInputShapeDescriptor.substring(0, userInputShapeDescriptor.indexOf("-")));
		double higherInputShapeDescriptor = Double.parseDouble(userInputShapeDescriptor.substring(userInputShapeDescriptor.indexOf("-")+1));
		if(lowerInputShapeDescriptor<0.0 || higherInputShapeDescriptor>1.0) {
					resetDialogEntries();
					IJ.error("Invalid parameter entry");
					return;
				}
	}
	
	
	public void convertTableToCalibratedResults(ResultsTable rt) {
		String[] tableHeadings = rt.getHeadings();
		for(int r=0; r<rt.getCounter(); r++) {
			//TODO: exchange all pixel based values for calibrated ones
		}
	}
	
	public ResultsTable getResultsTable() {
		return outputResultsTable;
	}
	
	public ImagePlus getOutputImage() {
		return outputImg;
	}
	
	//------------------------------------------------------------------------------------------------------------------------

	public void setNPasses(int nPasses) {
		this.nPasses = nPasses;
		pass = 0;
	}
}