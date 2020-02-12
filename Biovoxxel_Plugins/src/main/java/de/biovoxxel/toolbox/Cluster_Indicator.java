package de.biovoxxel.toolbox;


import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.EDM;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

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


public class Cluster_Indicator implements PlugInFilter {
	ImagePlus imp;
	private static String version = "v0.1.1";
	private int flags = DOES_8G;
	private int nResults = 0;
	private double clusterDiameter, clusterDensity, maxIterations;
	private String neighborDistanceDeterminationMethod;
	private boolean fuseOverlappingClusters, filledRoiOverlays, showTerminatedIterations, showNeighborDistanceCalculationImage, showLogWindow;
		private RoiManager clusterRoiManager;
	private Overlay finalOverlay = new Overlay();



	public int setup(String arg, ImagePlus imp) {
		if(imp.isLocked()) {
			imp.unlock();
		}
		this.imp = imp;
		return flags;
	}

	public void run(ImageProcessor ip) {
				Calibration cal = imp.getCalibration();
		cal.pixelWidth = 1.0;
		cal.pixelHeight = 1.0;
		cal.pixelDepth = 1.0;

		if(!ip.isBinary()) {
			IJ.error("Image type not supported", "works only with 8-bit binary images");
			return;
		}

		imp.killRoi();
		Overlay existingOverlay = imp.getOverlay();
		if(existingOverlay!=null) {
			existingOverlay.clear();
			existingOverlay=null;
			imp.updateAndDraw();
		}
		ip.resetRoi();

		String[] radioButtons = {"average NND", "centroid NND"};
		
		{
		GenericDialog gd = new GenericDialog("Cluster Indicator " + version);
			gd.addNumericField("cluster_diameter (pixel)", 50.0, 0);
			gd.addNumericField("density (x-fold of overall density)", 2.0, 1);
			gd.addNumericField("iterations", 25.0, 0);
			gd.addRadioButtonGroup("method", radioButtons, 1, 2, "average NND");
			gd.addCheckbox("fuse center-cluster overlaps", true);
			gd.addCheckbox("filled Roi overlays", false);
			//gd.addCheckbox("show ROI manager", false); //not implemented in the code yet
			gd.addCheckbox("terminated iterations", false);
			gd.addCheckbox("calculation image", false);
			gd.addCheckbox("log window", true);
			gd.showDialog();
			if (gd.wasCanceled()) {
				return;
			}

			clusterDiameter = gd.getNextNumber();
			clusterDensity = gd.getNextNumber();
			maxIterations = gd.getNextNumber();
			neighborDistanceDeterminationMethod = gd.getNextRadioButton();
			fuseOverlappingClusters = gd.getNextBoolean();
			filledRoiOverlays = gd.getNextBoolean();
			//showROIManager = gd.getNextBoolean(); //not implemented in the code yet
			showTerminatedIterations = gd.getNextBoolean();
			showNeighborDistanceCalculationImage = gd.getNextBoolean();
			showLogWindow = gd.getNextBoolean();


			if(gd.invalidNumber() || maxIterations<1 || clusterDiameter<1 || (clusterDiameter - Math.round(clusterDiameter))!=0 || (maxIterations - Math.round(maxIterations))!=0) {
				IJ.error("Invalid number");
				return;
			}
		}

		Recorder rec = Recorder.getInstance();
		if(rec!=null) {
			 Recorder.record = false;
		}
		
		//prepare analysis tools for getting center points of all binary particles
		ImagePlus evaluationImp = NewImage.createFloatImage(WindowManager.getUniqueName("evaluation_" + imp.getTitle()), imp.getWidth(), imp.getHeight(), 1, NewImage.FILL_BLACK);;
		ImageProcessor evaluationIP = evaluationImp.getProcessor();
		
		int paOptions = ParticleAnalyzer.CLEAR_WORKSHEET|ParticleAnalyzer.RECORD_STARTS;
		int paMeasurements = Measurements.CENTROID;
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(paOptions, paMeasurements, rt, 0.0, Double.POSITIVE_INFINITY);
		
		pa.analyze(imp, ip);
		nResults = rt.getCounter();
		
		int[] x = new int[nResults];
		int[] y = new int[nResults];
		
		//centroid nearest neighbor distance is faster but ignores particle size and shape
		if(neighborDistanceDeterminationMethod.equals("centroid NND")) {
			double currentDistance;
			double[] nearestNeighborDistance = new double[nResults];
						
			for(int i=0; i<nResults; i++) {
				x[i] = (int) Math.round(rt.getValue("X", i));
				y[i] = (int) Math.round(rt.getValue("Y", i));
				
				for(int nd=0; nd<nResults; nd++) {
					currentDistance = Math.sqrt(Math.pow((rt.getValue("X", i)-rt.getValue("X", nd)), 2) + Math.pow((rt.getValue("Y", i)-rt.getValue("Y", nd)), 2));
					if((i==0 && nd==1) || (i!=0 && nd==0)) {
						nearestNeighborDistance[i] = currentDistance;
					} else if(i!=nd && nearestNeighborDistance[i] > currentDistance) {
						nearestNeighborDistance[i] = currentDistance;
					} else {
						//not determined (not needed, yet)
					}
				}
			}
			
			for(int p=0; p<nResults; p++) {
				evaluationIP.putPixelValue(x[p], y[p], (1.0d/nearestNeighborDistance[p]));
			}
			evaluationImp.updateAndDraw();
			
		//average nearest neighbor distance takes particle size and shape into account but is more calculation intensive 
		} else if(neighborDistanceDeterminationMethod.equals("average NND")) {
			//create intensity coded voronoi
			Prefs.blackBackground = true;
			EDM edm = new EDM();
			EDM.setOutputType(EDM.FLOAT);
			edm.setup("voronoi", imp);
			edm.run(ip);
			edm.setup("final", imp);
			
			//create an invisible voronoi image for further processing
			ImagePlus intermediateVoronoiImp = WindowManager.getCurrentImage();
			ImagePlus voronoiImp = intermediateVoronoiImp.duplicate();
			intermediateVoronoiImp.close();
			ImageStatistics voronoiImpStats = voronoiImp.getStatistics();

			double[] averageNeighborDistance = new double[nResults];
			int[] startX = new int[nResults];
			int[] startY = new int[nResults];
						IJ.showStatus("Evaluating average NND");
			
			for(int i=0; i<nResults; i++) {
				startX[i] = (int) Math.round(rt.getValue("XStart", i));
				startY[i] = (int) Math.round(rt.getValue("YStart", i));
				x[i] = (int) Math.round(rt.getValue("X", i));
				y[i] = (int) Math.round(rt.getValue("Y", i));
				
				IJ.doWand(voronoiImp, startX[i], startY[i], 0.0, "8-connected");
				//voronoiImp.updateAndDraw();
				double size = cal.pixelWidth;
				IJ.run(voronoiImp, "Make Band...", "band=" + size);
				
				ImageStatistics voronoiBandSelectionStats = voronoiImp.getStatistics();
				averageNeighborDistance[i] = (2 * voronoiBandSelectionStats.min);
				voronoiImp.killRoi();
				IJ.showProgress(i, nResults);
			}
		

			for(int p=0; p<nResults; p++) {
				evaluationIP.putPixelValue(x[p], y[p], (1.0d - (averageNeighborDistance[p])/voronoiImpStats.max));
				IJ.showStatus("Preparing point map...");
			}
			evaluationImp.updateAndDraw();
		}
		
		//cluster analysis
		ResultsTable evaluationRT = new ResultsTable();
		int measurementFlags = Measurements.AREA|Measurements.MEAN|Measurements.STD_DEV|Measurements.MODE|Measurements.MIN_MAX|Measurements.CENTROID|Measurements.CENTER_OF_MASS|Measurements.PERIMETER|Measurements.RECT|Measurements.ELLIPSE|Measurements.SHAPE_DESCRIPTORS|Measurements.FERET|Measurements.INTEGRATED_DENSITY|Measurements.MEDIAN|Measurements.SKEWNESS|Measurements.KURTOSIS|Measurements.AREA_FRACTION|Measurements.STACK_POSITION|Measurements.LIMIT|Measurements.LABELS;
		
		Analyzer evaluationAnalyzer = new Analyzer(evaluationImp, measurementFlags, evaluationRT);
		evaluationAnalyzer.measure();
		
		double totalDensity = evaluationRT.getValue("%Area", 0);
		double totalBrightness = evaluationRT.getValue("Mean", 0);
		double limit = totalDensity*totalBrightness;
		double clusterRadius = clusterDiameter/2;

		int i = 0;
		//int t = 0;
		
		int multiplicatorX = (int)Math.round(((imp.getWidth() + clusterRadius)*2)/clusterDiameter);
		int multiplicatorY = (int)Math.round(((imp.getHeight() + clusterRadius)*2)/clusterDiameter);

		int counter = 0;
		int excludedClusters = 0;
		int totalClusterNumber = 0;
		int addToManager = 0;
		int duplicatedCluster = 0;
		double sumClusterDensity = 0;
		int terminatedIterations = 0;
		int[] xClusterPos = new int[((multiplicatorX)*(multiplicatorY))+1];
		int[] yClusterPos = new int[((multiplicatorX)*(multiplicatorY))+1];
		
		int centroidX, centroidY, centerOfMassX, centerOfMassY, newX, newY;
		double localBrightness, localDensity, localValue;
		
		int Xold = -1;
		int Yold = -1;

		int currentMaxIterationNumber = 0;
		
		clusterRoiManager = new RoiManager(true);

		Color terminatedStrokeColor = new Color(0.0f, 0.0f, 1.0f, 1.0f);
		Color terminatedFillColor = new Color(0.0f, 0.0f, 1.0f, 0.3f);


				
		for(int rY=0; rY<multiplicatorY; rY++) {
			for(int rX=0; rX<multiplicatorX; rX++) {
				Roi clusterROI = new OvalRoi(0+(rX*clusterRadius)-clusterRadius, 0+(rY*clusterRadius)-clusterRadius, clusterDiameter, clusterDiameter);
				evaluationImp.setRoi(clusterROI);
				totalClusterNumber = totalClusterNumber + 1;
				IJ.showStatus("Cluster detection " + ((100*totalClusterNumber)/(multiplicatorX*multiplicatorY)) + " %");
				IJ.showProgress((double)totalClusterNumber/(double)(multiplicatorX*multiplicatorY));
				
				i = 0;
				//t = 0;
				
				while(i<(int) maxIterations) {
					//IJ.log("max.: "+maxIterations + "/" + i); //control output
					ResultsTable clusterRoiRT = new ResultsTable();
					Analyzer clusterRoiAnalyzer = new Analyzer(evaluationImp, measurementFlags, clusterRoiRT);
					clusterRoiAnalyzer.measure();
					centroidX = (int) Math.round(clusterRoiRT.getValue("X", 0));
					centroidY = (int) Math.round(clusterRoiRT.getValue("Y", 0));
					centerOfMassX = (int) Math.round(clusterRoiRT.getValue("XM", 0));
					centerOfMassY = (int) Math.round(clusterRoiRT.getValue("YM", 0));

					if(Xold==centroidX && Yold==centroidY) {
						int t = 0;
						localBrightness = clusterRoiRT.getValue("Mean", 0);
						localDensity = clusterRoiRT.getValue("%Area", 0);
						localValue = localBrightness * localDensity;
						
						while(t<=counter) {
							if(centroidX!=xClusterPos[t] || centroidY!=yClusterPos[t]) {
								addToManager = 1;
								t++;
							} else if(centroidX==xClusterPos[t] && centroidY==yClusterPos[t]) {
								addToManager = 0;
								duplicatedCluster = duplicatedCluster + 1;
								Xold = centroidX;
								Yold = centroidY;
								t = counter + 1;
							} else {
								t++;
							}
						}
						
						if(addToManager==1 && (localValue >= (limit*clusterDensity))) {
							sumClusterDensity = sumClusterDensity + localValue;
							
							clusterRoiManager.addRoi(clusterROI);
							
							xClusterPos[counter] = centroidX;
							yClusterPos[counter] = centroidY;
							Xold = centroidX;
							Yold = centroidY;
							counter = counter + 1;
						} else if(addToManager==1 && (localValue < (limit*clusterDensity))) {
							excludedClusters = excludedClusters + 1;
						} else {
							//not determined yet
						}
						i = (int) maxIterations;
						
						clusterRoiAnalyzer = null;
						clusterRoiRT = null;
						
					} else {
						newX = centerOfMassX;
						newY = centerOfMassY;
						Xold = centroidX;
						Yold = centroidY;
						
						evaluationImp.killRoi();
						clusterROI = new OvalRoi((newX-clusterRadius), (newY-clusterRadius), clusterDiameter, clusterDiameter);
						evaluationImp.setRoi(clusterROI, false);
						i++;

						if(i>currentMaxIterationNumber) {
							currentMaxIterationNumber = i;
						}
						
						if(i>=(int) maxIterations) {
							terminatedIterations = terminatedIterations + 1;
							
							if(showTerminatedIterations) {
								
								clusterROI.setStrokeColor(terminatedStrokeColor);
								if(filledRoiOverlays) {
									clusterROI.setFillColor(terminatedFillColor);
								}
																
								finalOverlay.add(clusterROI);
							}
						}
					}					
				}
			}
		}
		
		if(totalClusterNumber==excludedClusters) {
			IJ.error("all clusters excluded\ndue to low density\ndensity < " + clusterDensity);
			return;
		}
		int roiCount = clusterRoiManager.getCount();
		Roi[] allRoisInManager = clusterRoiManager.getRoisAsArray();
		if(allRoisInManager==null || allRoisInManager.length<1) {
			IJ.error("no clusters found/accepted\n \ntry to modify parameters");
			return;
		}
		int remainingClusters = 0;
		Color roiStrokeColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
		Color roiFillColorOpaque = new Color(1.0f, 0.0f, 0.0f, 0.3f);
		double averageAreaOfFusedClusters = 0;
		
		if(fuseOverlappingClusters && allRoisInManager.length>1) {
			IJ.showStatus("Cluster fusion");
			
			int[] selectedIndexes = new int[roiCount];			
			for(int indexRun=0; indexRun<roiCount; indexRun++) {
				selectedIndexes[indexRun] = indexRun;
			}
										
			clusterRoiManager.setSelectedIndexes(selectedIndexes);
			clusterRoiManager.runCommand("Combine");
			clusterRoiManager.runCommand("Add");
			clusterRoiManager.setSelectedIndexes(selectedIndexes);
			clusterRoiManager.runCommand("Delete");
			clusterRoiManager.select(imp, 0);
			Roi fusedRoi = imp.getRoi();
			
			fusedRoi.setStrokeColor(roiStrokeColor);
			if(filledRoiOverlays) {
				fusedRoi.setFillColor(roiFillColorOpaque);
			}
			finalOverlay.add(fusedRoi);
			imp.setOverlay(finalOverlay);
			imp.updateAndDraw();
			
			//produce a mask image to get the final number of fused ROIs (and potentially their area if desired)
			ImageProcessor fusedRoisIP = fusedRoi.getMask();
			ImagePlus fusedRoisImp = new ImagePlus("fusedRoiAnalysis", fusedRoisIP);
			ResultsTable fusedRoiRT = new ResultsTable();
			ParticleAnalyzer fusedRoiAnalysis = new ParticleAnalyzer(ParticleAnalyzer.CLEAR_WORKSHEET, Measurements.AREA, fusedRoiRT, 0.0, Double.POSITIVE_INFINITY);
			fusedRoiAnalysis.analyze(fusedRoisImp);
			remainingClusters = fusedRoiRT.getCounter();

			//determine the average area
			double summedAreaOfFusedClusters = 0;
			for(int a = 0; a<remainingClusters; a++) {
				summedAreaOfFusedClusters = summedAreaOfFusedClusters + fusedRoiRT.getValue("Area", a);
			}
			averageAreaOfFusedClusters = (summedAreaOfFusedClusters / remainingClusters);
			
			
			if(showNeighborDistanceCalculationImage) {
				ContrastEnhancer normHist = new ContrastEnhancer();
				normHist.stretchHistogram(evaluationIP, 0.0d);
				IJ.run(evaluationImp, "Fire", "");
	
				evaluationImp.setOverlay(finalOverlay);
				evaluationImp.show();
				evaluationImp.killRoi();
			}

			
		} else {
			
			for(int roiToImage = 0; roiToImage<clusterRoiManager.getCount(); roiToImage++) {
				allRoisInManager[roiToImage].setStrokeColor(roiStrokeColor);
				if(filledRoiOverlays) {
					allRoisInManager[roiToImage].setFillColor(roiFillColorOpaque);
				}
				finalOverlay.add(allRoisInManager[roiToImage]);
			}
			
			imp.setOverlay(finalOverlay);
			imp.updateAndDraw();
			if(showNeighborDistanceCalculationImage) {
				ContrastEnhancer normHist = new ContrastEnhancer();
				normHist.stretchHistogram(evaluationIP, 0.0d);
				IJ.run(evaluationImp, "Fire", "");

				evaluationImp.setOverlay(finalOverlay);
				evaluationImp.killRoi();
				evaluationImp.show();
			}

		}

		imp.killRoi();
			if(showLogWindow) {
			// output
			IJ.log("_____________________________________________");
			IJ.log("ROI diameter: "+clusterDiameter+" pixel");
			//IJ.log("average image density: "+limit+" %");
			//IJ.log("minimal density limit: "+(limit*clusterDensity)+" % ("+clusterDensity+"-fold)");
			IJ.log("min. individual cluster density: " + clusterDensity);
			IJ.log("max. iterations: " + maxIterations);
			IJ.log("initiated ROIs in total: " + (totalClusterNumber));
			IJ.log("--------------------------------------------------");
			IJ.log(roiCount + " clusters accepted (~"+ IJ.d2s((100 * (double)roiCount) / (double)totalClusterNumber)+" %)");
			if(fuseOverlappingClusters) {
				IJ.log(remainingClusters + " individual clusters remaining after fusion");
				IJ.log((roiCount - remainingClusters) + " ROIs fused due to overlap");
				IJ.log("average fused cluster area: " + averageAreaOfFusedClusters + " pixel");
			}
			//IJ.log("average density: " + sumClusterDensity/remainingRoiCount + " % or ROI area");
			IJ.log("--------------------------------------------------");
			IJ.log(excludedClusters + " clusters excluded due to low density (~"+ IJ.d2s((100 * (double)excludedClusters) / (double)totalClusterNumber) + " %)");
			IJ.log(duplicatedCluster + " duplicate clusters (~"+ IJ.d2s((100*(double)duplicatedCluster) / (double)totalClusterNumber) + " %)");
			IJ.log("max. iteration number reached: " + currentMaxIterationNumber);
			IJ.log(terminatedIterations + " terminated iterations (~"+ IJ.d2s((100 * (double)terminatedIterations)/(double)totalClusterNumber) + " %) (blue ROIs)");
			IJ.log("_____________________________________________");
			IJ.showStatus("Done");
	
			evaluationRT = null;
			evaluationAnalyzer = null;
			
			if(rec!=null) {
				 Recorder.record = false;
			}
		}
	}
}
