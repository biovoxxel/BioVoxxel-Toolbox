package de.biovoxxel.toolbox;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.ThresholdToSelection;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class SSIDC_ implements PlugInFilter {
	
	private ImagePlus imp;
	
	
	//general global parameters
	private int paOptions = ParticleAnalyzer.CLEAR_WORKSHEET | ParticleAnalyzer.RECORD_STARTS;
	private int paMeasurements = Measurements.AREA|Measurements.CENTROID|Measurements.AREA_FRACTION|Measurements.LIMIT|Measurements.LABELS;

	@Override
	public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
    	return DOES_8G;
    	
	}
	
	
	public void run(ImageProcessor ip) {
		
		int minDensity = 3;
		double epsilon = 0.0;
		
		GenericDialog gd = new GenericDialog("SSIDC Setup");
			gd.addNumericField("distance (epsilon)", 5.0, 0, 8, "pixel");
			gd.addNumericField("minDensity", 3.0, 0, 8, "particles");
			gd.showDialog();
			if(gd.wasCanceled()) {
				return;
			}
			epsilon = gd.getNextNumber();
			minDensity = (int)gd.getNextNumber();
			
			if(gd.invalidNumber() || minDensity<3.0) {
				IJ.error("invalid number");
				return;
			}
		
		Recorder.record = false;
		//setup ImagePlus and ImageProcessors for intermediate calculations
		ImagePlus visitedPointsImp = imp.duplicate();
		ImageProcessor intermediateVisitedPointsIP = visitedPointsImp.getProcessor();
		ByteProcessor visitedPointsIP = intermediateVisitedPointsIP.convertToByteProcessor();
		visitedPointsIP.setValue(0.0d);
		visitedPointsImp.setTitle("Visited");
		//visitedPointsImp.show();	//keep for test visualization
		
		ImagePlus maskImp = imp.duplicate();
		ImageProcessor maskIP = maskImp.getProcessor();
		maskIP.setValue(255.0d);
		maskIP.snapshot();
		maskImp.setTitle("Mask");
		//maskImp.show();	//keep for test visualization
		
		ImagePlus clusterMaskImp = IJ.createImage("ClusterMask", "8-bit black", imp.getWidth(), imp.getHeight(), 1);
		ImageProcessor clusterMaskIP = clusterMaskImp.getProcessor();
		clusterMaskIP.setValue(255.0d);
		clusterMaskIP.snapshot();
		//clusterMaskImp.show();	//keep for test visualization
		
		//analyze the entire image to get the starting position of all particles
		ResultsTable particleRT = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(paOptions, paMeasurements, particleRT, 0.0, Double.POSITIVE_INFINITY);
		pa.analyze(imp);
		int pointCount = particleRT.getCounter();
		int[] x = new int[pointCount];
		int[] y = new int[pointCount];
		
		for(int i=0; i<pointCount; i++) {
			x[i] = (int)particleRT.getValue("XStart", i);
			y[i] = (int)particleRT.getValue("YStart", i);
		}
		
		Overlay noise = new Overlay();
		
		boolean terminateCluster = false;
		
		Roi[] clusterRoi = new Roi[pointCount];
		int clusterCount = 0;
		
		for(int screen=0; screen<pointCount; screen++) {
			terminateCluster = false;
			//IJ.log("particle " + screen + " - exists: " + visitedPointsIP.get(x[screen], y[screen]));
			if(visitedPointsIP.get(x[screen], y[screen])==255) {
				//mark the current fixed center particle as visited
				IJ.doWand(visitedPointsImp, x[screen], y[screen], 0.0, "4-connected");
				Roi visitedCenterParticleRoi = visitedPointsImp.getRoi();
				visitedPointsIP.fill(visitedCenterParticleRoi);
				//visitedPointsIP.resetRoi();
				//visitedCenterParticleRoi = null;
				visitedPointsImp.updateAndDraw();
				double oldRoiArea = 0;
				double newRoiArea = 0;
				
				Roi enlargedRoi = getEnlargedRoi(maskImp, x[screen], y[screen], epsilon);
				Roi neighborhoodRoi = getNeighborhood(maskImp, maskIP, enlargedRoi, x[screen], y[screen]);
				ResultsTable neighborhoodRT = getNeighborhoodAnalysis(visitedPointsImp, neighborhoodRoi);
				int neighborhoodPointCount = neighborhoodRT.getCounter();
													
				
				if(neighborhoodPointCount>=minDensity) {
					
					boolean internalLoop = false;
					while(!terminateCluster) {
						if(internalLoop) {
							//clusterMaskImp.updateAndDraw();
							/*
							IJ.doWand(clusterMaskImp, x[screen], y[screen], 0.0, "4-connected");
							enlargedRoi = clusterMaskImp.getRoi();
							*/
							
							int threshold = 255;
							clusterMaskIP.setThreshold(threshold, threshold, ImageProcessor.NO_LUT_UPDATE);
							enlargedRoi = ThresholdToSelection.run(clusterMaskImp);
							clusterMaskIP.resetThreshold();						
							
							neighborhoodRoi = getNeighborhood(maskImp, maskIP, enlargedRoi, x[screen], y[screen]);
							neighborhoodRT = getNeighborhoodAnalysis(visitedPointsImp, neighborhoodRoi);
							neighborhoodPointCount = neighborhoodRT.getCounter();
						} else {
							internalLoop = true;
						}
						
						int[] neighborX = new int[neighborhoodPointCount];
						int[] neighborY = new int[neighborhoodPointCount];
								
						for(int i=0; i<neighborhoodPointCount; i++) {
							neighborX[i] = (int)neighborhoodRT.getValue("XStart", i);
							neighborY[i] = (int)neighborhoodRT.getValue("YStart", i);	
						}
						clusterMaskIP.fill(enlargedRoi);
						
						for(int neighbor=0; neighbor<neighborhoodPointCount; neighbor++) {
							if(visitedPointsIP.get(neighborX[neighbor],neighborY[neighbor])==255) {
								Roi enlargedNeighborRoi = getEnlargedRoi(maskImp, neighborX[neighbor], neighborY[neighbor], epsilon);
								Roi neighborNeighborhoodRoi = getNeighborhood(maskImp, maskIP, enlargedNeighborRoi, neighborX[neighbor], neighborY[neighbor]);
								ResultsTable neighborNeighborhoodRT = getNeighborhoodAnalysis(visitedPointsImp, neighborNeighborhoodRoi);
								int neighborNeighborhoodPointCount = neighborNeighborhoodRT.getCounter();
								
								
								IJ.doWand(visitedPointsImp, neighborX[neighbor], neighborY[neighbor], 0.0, "4-connected");
								Roi visitedNeighborParticleRoi = visitedPointsImp.getRoi();
								visitedPointsIP.fill(visitedNeighborParticleRoi);
								
								if(neighborNeighborhoodPointCount>=minDensity) {
									clusterMaskIP.fill(enlargedNeighborRoi);
								} else {

								}
							} else {

							}
						}
						oldRoiArea = newRoiArea;
						newRoiArea = getRoiArea(clusterMaskImp);
						if(newRoiArea==oldRoiArea) {
							terminateCluster = true;
						}
					}
					
					//clusterManager.addRoi(enlargedRoi);
					clusterRoi[clusterCount] = enlargedRoi;
					clusterCount++;
					clusterMaskIP.reset();
					
				} else {
					noise.add(enlargedRoi);
				}
			} else {

			}
			IJ.showProgress((double)screen / (double)pointCount);
		}
		//clusterMaskImp.show(); //keep for test reasons to display intermediat cluster mask image
		
		//add all rois to the RoiManager to store and display them on the image
		if(clusterCount==0) {
			//IJ.showMessage("No clusters with the\ncurrent parameters found");
			IJ.log("No clusters with the current parameters found in image " + imp.getTitle());
		} else {
			RoiManager managerTest = RoiManager.getInstance2();
			if(managerTest!=null) {
				managerTest.close();
			}
			RoiManager clusterManager = new RoiManager();
			for(int c=0; c<clusterCount; c++) {
				clusterManager.addRoi(clusterRoi[c]);
			}
			System.out.println(clusterCount + " clusters found and displayed");
			clusterManager.runCommand("Show all without labels");
		}
		Recorder.record = true;
	}
		
	
	private Roi getEnlargedRoi(ImagePlus analysisImp, int x, int y, double epsilon) {
		//creates a roi around  the current particle wit a distance = epsilon from the particles outline
		analysisImp.killRoi();
		IJ.doWand(analysisImp, x, y, 0.0, "4-connected");
		IJ.run(analysisImp, "Enlarge...", "enlarge=" + epsilon + " pixel");
		Roi enlargedRoi = analysisImp.getRoi();
		
		return enlargedRoi;
	}
	
	private Roi getNeighborhood(ImagePlus maskImp, ImageProcessor maskIP, Roi enlargedRoi, int x, int y) {
		//create neighborhood of particle with start point x,y and hand over the neighborhood roi
		maskIP.fill(enlargedRoi);
		IJ.doWand(maskImp, x, y, 0.0, "4-connected");
		Roi	particleNeighborhood = maskImp.getRoi();
		maskIP.reset();
		
		return particleNeighborhood;
	}
	
	private ResultsTable getNeighborhoodAnalysis(ImagePlus analysisImp, Roi neighborhoodRoi) {
		//analyze neighborhood of particle with start point x,y and hand over all reachable particles
		analysisImp.setRoi(neighborhoodRoi, false);
		ResultsTable roiRT = new ResultsTable();
		ParticleAnalyzer paRoi = new ParticleAnalyzer(paOptions, paMeasurements, roiRT, 0.0, Double.POSITIVE_INFINITY);
		paRoi.analyze(analysisImp);
		
		return roiRT;
	}
	
	private double getRoiArea(ImagePlus measureImp) {
		//measureImp.setRoi(currentRoi);
		ResultsTable roiMeasureRT = new ResultsTable();
		Analyzer roiAnalyzer = new Analyzer(measureImp, Measurements.AREA, roiMeasureRT);
		roiAnalyzer.measure();
		double roiArea = roiMeasureRT.getValue("Area", 0);
		roiMeasureRT = null;
		
		return roiArea;
	}


	
}



