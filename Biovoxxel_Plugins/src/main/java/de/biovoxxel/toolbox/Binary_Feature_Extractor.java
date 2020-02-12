package de.biovoxxel.toolbox;


import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
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

public class Binary_Feature_Extractor implements PlugInFilter {
	ImagePlus imp;
	ResultsTable countTable;
	private int measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
	private int analyzerOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS|ParticleAnalyzer.ADD_TO_MANAGER;
	private int finalCount = 0;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		String[] imageNames = getOpenImageNames();
		if(imageNames[0]=="None") {
			IJ.error("need at least 2 binary open images");
			return;
		}
		double previousMinOverlap = Prefs.get("BVTB.BinaryFeatureExtractor.minOverlap", 0);
		boolean previousCombine = Prefs.get("BVTB.BinaryFeatureExtractor.combine", false);

		GenericDialog gd = new GenericDialog("Binary Feature Extractor");
			gd.addChoice("Objects image", imageNames, imageNames[0]);
			gd.addChoice("Selector image", imageNames, imageNames[1]);
			gd.addNumericField("Object_overlap in % (0=off)", previousMinOverlap, 0, 9, "");
			gd.addCheckbox("Combine objects and selectors", previousCombine);
			gd.addCheckbox("Count output", true);
			gd.addCheckbox("Analysis tables", false);
			gd.showDialog();
			if(gd.wasCanceled()) {
				return;
			}
			String objectsImgTitle = gd.getNextChoice();
			String selectorsImgTitle = gd.getNextChoice();
			double minOverlap = gd.getNextNumber();
			boolean combineImages = gd.getNextBoolean();
			boolean showCountOutput = gd.getNextBoolean();
			boolean showAnalysis = gd.getNextBoolean();
			if(gd.invalidNumber() || minOverlap<0 || minOverlap>100) {
				IJ.error("invalid number");
				return;
			}
			Prefs.set("BVTB.BinaryFeatureExtractor.minOverlap", minOverlap);
			Prefs.set("BVTB.BinaryFeatureExtractor.combine", combineImages);


		if(objectsImgTitle.equals(selectorsImgTitle)) {
			IJ.error("images need to be different");
			return;
		}

		ImagePlus objectsImp = WindowManager.getImage(objectsImgTitle);
		ImageProcessor objectsIP = objectsImp.getProcessor();
		ImagePlus selectorsImp = WindowManager.getImage(selectorsImgTitle);
		ImageProcessor selectorsIP = selectorsImp.getProcessor();

		if(!objectsIP.isBinary() || !selectorsIP.isBinary()) {
			IJ.error("works with 8-bit binary images only");
			return;
		}

		if((objectsImp.getWidth()!=selectorsImp.getWidth()) || objectsImp.getHeight()!=selectorsImp.getHeight()) {
			IJ.error("images need to be of the same size");
			return;
		}

		//close any existing RoiManager before instantiating a new one for this analysis
		RoiManager oldRM = RoiManager.getInstance2();
		if(oldRM!=null) {
			oldRM.close();
		}

		RoiManager objectsRM = new RoiManager(true);
		ResultsTable objectsRT = new ResultsTable();
		ParticleAnalyzer analyzeObjects = new ParticleAnalyzer(analyzerOptions, measurementFlags, objectsRT, 0.0, Double.POSITIVE_INFINITY);
		ParticleAnalyzer.setRoiManager(objectsRM);
		
		analyzeObjects.analyze(objectsImp);
		objectsRM.runCommand("Show None");
		int objectNumber = objectsRT.getCounter();
		
		Roi[] objectRoi = objectsRM.getRoisAsArray();
		
		ResultsTable measureSelectorsRT = new ResultsTable();
		Analyzer overlapAnalyzer = new Analyzer(selectorsImp, measurementFlags, measureSelectorsRT);

		ImagePlus outputImp = IJ.createImage("output", "8-bit black", objectsImp.getWidth(), objectsImp.getHeight(), 1);
		ImageProcessor outputIP = outputImp.getProcessor();
		
		double[] measuredOverlap = new double[objectNumber];

		outputIP.setValue(255.0);
		for(int o=0; o<objectNumber; o++) {
			selectorsImp.killRoi();
			selectorsImp.setRoi(objectRoi[o]);
			overlapAnalyzer.measure();
			measuredOverlap[o] = measureSelectorsRT.getValue("%Area", o);
			if(minOverlap!=0.0 && measuredOverlap[o]>=minOverlap) {
				outputIP.fill(objectRoi[o]);
				finalCount++;
			} else if(minOverlap==0.0 && measuredOverlap[o]>0.0) {
				outputIP.fill(objectRoi[o]);
				finalCount++;
			}
		}
		//measureSelectorsRT.show("Objects");				
		
		selectorsImp.killRoi();
		RoiManager selectorRM = new RoiManager(true);
		ResultsTable selectorRT = new ResultsTable();
		ParticleAnalyzer.setRoiManager(selectorRM);
		ParticleAnalyzer analyzeSelectors = new ParticleAnalyzer(analyzerOptions, measurementFlags, selectorRT, 0.0, Double.POSITIVE_INFINITY);
		analyzeSelectors.analyze(selectorsImp);
		selectorRM.runCommand("Show None");
		int selectorNumber = selectorRT.getCounter();


		if(combineImages) {
			outputImp.updateAndDraw();
			Roi[] selectorRoi = selectorRM.getRoisAsArray();
	
			ResultsTable measureObjectsRT = new ResultsTable();
			Analyzer selectorAnalyzer = new Analyzer(outputImp, measurementFlags, measureObjectsRT);
					
			double[] selectorOverlap = new double[selectorNumber];
			outputIP.setValue(255.0);
			for(int s=0; s<selectorNumber; s++) {
				outputImp.killRoi();
				outputImp.setRoi(selectorRoi[s]);
				selectorAnalyzer.measure();
				selectorOverlap[s] = measureObjectsRT.getValue("%Area", s);
				if(selectorOverlap[s]>0.0d) {
					outputIP.fill(selectorRoi[s]);
				}
			}
			selectorRoi = null;
			selectorAnalyzer = null;
			measureObjectsRT = null;
		}
		//selectorRT.show("Selectors");
		outputImp.killRoi();
		String outputImageTitle = WindowManager.getUniqueName("Extracted_" + objectsImgTitle);
		outputImp.setTitle(outputImageTitle);
		outputImp.show();
		outputImp.changes = true;
		
		if(showCountOutput) {
			String [] openTextWindows = WindowManager.getNonImageTitles();
			boolean makeNewTable = true;
			for(int w = 0; w < openTextWindows.length; w++) {
				if(openTextWindows[w].equals("BFE_Results")) {
					makeNewTable = false;
				} 		
			}
			
			if(makeNewTable) {
				countTable = new ResultsTable();
				countTable.setPrecision(0);
				countTable.setValue("Image", 0, outputImageTitle);
				countTable.setValue("Objects", 0, objectNumber);
				countTable.setValue("Selectors", 0, selectorNumber);
				countTable.setValue("Extracted", 0, finalCount);
				countTable.show("BFE_Results");
			} else {
				IJ.renameResults("BFE_Results", "Results");
				countTable = ResultsTable.getResultsTable();
				countTable.setPrecision(0);
				countTable.incrementCounter();
				countTable.addValue("Image", outputImageTitle);
				countTable.addValue("Objects", objectNumber);
				countTable.addValue("Selectors", selectorNumber);
				countTable.addValue("Extracted", finalCount);
				IJ.renameResults("Results", "BFE_Results");
				countTable.show("BFE_Results");
			}
		}
		
		if(showAnalysis) {
			ResultsTable extractedRT = new ResultsTable();
			ParticleAnalyzer analyzeExtracted = new ParticleAnalyzer(ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS, measurementFlags, extractedRT, 0.0, Double.POSITIVE_INFINITY);
			analyzeExtracted.analyze(outputImp);
			objectsRT.show("Objects");
			selectorRT.show("Selectors");
			extractedRT.show("Extracted");
		} else {
			objectsRT = null;
			selectorRT = null;
		}
				
		objectsRM = null; 		
		measureSelectorsRT = null; 	
		analyzeObjects = null;
		overlapAnalyzer = null;
		objectRoi = null;
		selectorRM = null;
				
		objectsImp.killRoi();
		objectsImp.changes = false;
		selectorsImp.changes = false;

	}

	public String[] getOpenImageNames() {
		int[] imageIDList = WindowManager.getIDList();
		String[] imageNames = new String[imageIDList.length];
		//imageNames[0] = "None";

		if(!imageIDList.equals(null) && imageIDList.length>1) {
			for(int i=0; i<imageIDList.length; i++){
				   ImagePlus tempIMP = WindowManager.getImage(imageIDList[i]);
				   imageNames[i] = tempIMP.getTitle();
			}
			return imageNames;
		} else {
			imageNames[0] = "None";
			return imageNames;
		}
	}
}
