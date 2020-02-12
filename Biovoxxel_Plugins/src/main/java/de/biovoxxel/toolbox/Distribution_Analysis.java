package de.biovoxxel.toolbox;

import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.EDM;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
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

public class Distribution_Analysis implements PlugInFilter {
	ImagePlus imp;
	private int flags = DOES_8G;
	private String version = "v0.0.1";
	private double minSize, maxSize, minCirc, maxCirc;
	private String neighborMethod, statisticalMethod, chosenCI;
	private boolean includeHoles, excludeEdges;
	private int paOptions = ParticleAnalyzer.CLEAR_WORKSHEET | ParticleAnalyzer.RECORD_STARTS;
	private int paMeasurements = Measurements.AREA | Measurements.CENTROID;
	private double[] criticalF = new double[10];
	private double[] criticalT = new double[10];
	private double[] criticalF95 = {2.978237016, 1.840871688, 1.53431418, 1.391719552, 1.158655374, 1.109688288, 1.076352036, 1.061912029, 1.053397886, 1.047627319};
	private double[] criticalT95 = {1.812461102, 1.697260851, 1.670648865, 1.660234327, 1.647906854, 1.646378818, 1.645615867, 1.645361708, 1.645234659, 1.645158438};
	private double[] criticalF99 = {4.849146802, 2.385967353, 1.836259361, 1.597669125, 1.231664935, 1.158625448, 1.109682472, 1.088680123, 1.076352997, 1.068021936};
	private double[] criticalT99 = {2.763769458, 2.457261531, 2.390119457, 2.364217356, 2.333828914, 2.330082625, 2.328213787, 2.327591515, 2.32728048, 2.327093897};
	private double[] criticalF999 = {8.753866276, 3.217090322, 2.252265545, 1.867401382, 1.319136791, 1.216098723, 1.148287469, 1.1194961, 1.102684079, 1.091358502};
	private double[] criticalT999 = {4.143700493, 3.385184866, 3.231709121, 3.173739481, 3.106611618, 3.098402156, 3.09431229, 3.092951196, 3.092271061, 3.091863111};
	private double pListed = 0.0d;
	

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}

	public void run(ImageProcessor ip) {
		
		String[] neighborDeterminationMethod = {"centroid NND", "average NND"};
		String[] statisticalMethodArray = {"mean", "median"};
		String[] confidenceInterval = {"95%", "99%", "99.9%"};

		GenericDialog gd = new GenericDialog("Distribution Analysis " + version);
			gd.addNumericField("min_size", 0.0, 1);
			gd.addNumericField("max_size", Double.POSITIVE_INFINITY, 0);
			gd.addNumericField("min_circularity", 0.00, 2);
			gd.addNumericField("max_circularity", 1.00, 2);
			gd.addCheckbox("include holes", false);
			gd.addCheckbox("exclude edges", false);
			gd.addRadioButtonGroup("neighbor determination", neighborDeterminationMethod, 1, 2, "centroid NND");
			gd.addRadioButtonGroup("statistical method", statisticalMethodArray, 1, 2, "median");
			gd.addRadioButtonGroup("conficence interval (CI)", confidenceInterval, 1, 3, "95%");
			gd.showDialog();
			
			minSize = gd.getNextNumber();
			maxSize = gd.getNextNumber();
			minCirc = gd.getNextNumber();
			maxCirc = gd.getNextNumber();
			includeHoles = gd.getNextBoolean();
			excludeEdges = gd.getNextBoolean();
			neighborMethod = gd.getNextRadioButton();
			statisticalMethod = gd.getNextRadioButton();
			chosenCI = gd.getNextRadioButton();
			
		
		if(Double.isNaN(maxSize) || gd.invalidNumber() || minCirc<0 || maxCirc>1.0) {
			IJ.error("invalid number");
			return;
		}
		
		if(includeHoles) {
			paOptions |= ParticleAnalyzer.INCLUDE_HOLES;
		} else {
			paOptions &= ~ParticleAnalyzer.INCLUDE_HOLES;
		}
		
		if(excludeEdges) {
			paOptions |= ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		} else {
			paOptions &= ~ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES; 
		}
		
		//determination of critical F- and t-values
		if(chosenCI=="95%") {
			criticalF = criticalF95;
			criticalT = criticalT95;
			pListed = 0.05d;
		} else if(chosenCI=="99%") {
			criticalF = criticalF99;
			criticalT = criticalT99;
			pListed = 0.01d;
		} else if(chosenCI=="99.9%") {
			criticalF = criticalF999;
			criticalT = criticalT999;
			pListed = 0.001d;
		}
		
		//particle analysis
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer pa = new ParticleAnalyzer(paOptions, paMeasurements, rt, minSize, maxSize, minCirc, maxCirc);
		pa.analyze(imp);
		int particleNumber = rt.getCounter();
		
		double imageArea = imp.getWidth() * imp.getHeight();;
		if(neighborMethod=="average NND") {
			double particleAreaSum = 0;
			for(int ia=0; ia<particleNumber; ia++) {
				particleAreaSum = particleAreaSum + rt.getValue("Area", ia);
			}
			imageArea = imageArea - particleAreaSum;
		}
		double theoreticalRandomNND = 0.5 * Math.sqrt(imageArea / particleNumber);
		double stdDevTheoreticalRandomNND = Math.sqrt(theoreticalRandomNND);
		
		IJ.log("Theoretical random nearest neighbor distance = " + theoreticalRandomNND);
		IJ.log("Variance = " + theoreticalRandomNND);
		IJ.log("StdDev = " + stdDevTheoreticalRandomNND);

		
		double[] nearestNeighborDistanceArray = new double[particleNumber];
		double nearestNeighborSum = 0;
		//int comparisonCounter = 0;
		
		if(neighborMethod=="average NND") {
			nearestNeighborDistanceArray = getAverageND(imp, rt);
			for(int s=0; s<particleNumber; s++) {
				nearestNeighborSum = nearestNeighborSum + nearestNeighborDistanceArray[s];
			}
		} else {
			
			double currentDistance = 0;
			
			for(int fixParticle=0; fixParticle<particleNumber; fixParticle++) {
				for(int varParticle=0; varParticle<particleNumber; varParticle++) {
					
					currentDistance = Math.sqrt(Math.pow((rt.getValue("X", fixParticle)-rt.getValue("X", varParticle)), 2) + Math.pow((rt.getValue("Y", fixParticle)-rt.getValue("Y", varParticle)), 2));
					if((fixParticle==0 && varParticle==1) || (fixParticle!=0 && varParticle==0)) {
						nearestNeighborDistanceArray[fixParticle] = currentDistance;
					} else if(fixParticle!=varParticle && nearestNeighborDistanceArray[fixParticle] > currentDistance) {
						nearestNeighborDistanceArray[fixParticle] = currentDistance;

					}
					//comparisonCounter++;
				}
			
			}
			
			for(int s=0; s<particleNumber; s++) {
				nearestNeighborSum = nearestNeighborSum + nearestNeighborDistanceArray[s];
			}
		}
		
		double nearestNeighborMean = nearestNeighborSum / particleNumber;

		//determine the median nearest neighbor distance
		Arrays.sort(nearestNeighborDistanceArray);
				
		double nearestNeighborMedian = 0;
		if(particleNumber % 2 == 0) {
			nearestNeighborMedian = ((nearestNeighborDistanceArray[particleNumber/2] + nearestNeighborDistanceArray[(particleNumber/2)-1])/2);
		} else {
			nearestNeighborMedian = nearestNeighborDistanceArray[(particleNumber/2)];
		}
		
		//determine the sum of the differences
		double differenceSumMean = 0;
		double differenceSumMedian = 0;
		
		for(int v=0; v<particleNumber; v++) {
			differenceSumMean = differenceSumMean + Math.pow((nearestNeighborDistanceArray[v]-nearestNeighborMean), 2);
			differenceSumMedian = differenceSumMedian + Math.pow((nearestNeighborDistanceArray[v]-nearestNeighborMedian), 2);
		}
		
		//determine variances and SDs
		double varianceMean = differenceSumMean/particleNumber;
		double SDMean = Math.sqrt(varianceMean);
		double varianceMedian = differenceSumMedian/particleNumber;
		double SDMedian = Math.sqrt(varianceMedian);

		//show intermediate output
		IJ.log("Measured average nearest neighbor distance = " + nearestNeighborMean);
		IJ.log("Variance (mean) = " + varianceMean);
		IJ.log("StdDev (mean) = " + SDMean);
		IJ.log("Measured median nearest neighbor distance = " + nearestNeighborMedian);
		IJ.log("Variance (median) = " + varianceMedian);
		IJ.log("StdDev (median) = " + SDMedian);
		IJ.log("--------------------------------------------------------------------------");
		//IJ.log("Comparisons = " + comparisonCounter);
		IJ.log("Sample size n = " + particleNumber);

		double testValue = 0;
		double testVariance = 0;
		
		if(statisticalMethod=="median") {
			testValue = nearestNeighborMedian;
			testVariance = varianceMedian;
		} else if(statisticalMethod=="mean") {
			testValue = nearestNeighborMean;
			testVariance = varianceMean;
		}
		
		//Fisher's F-test
		double F = Math.max(theoreticalRandomNND, testVariance) / Math.min(theoreticalRandomNND, testVariance);
		double Tvalue = 0;
		double df = 0;
		
		if((particleNumber>=11 && particleNumber<31 && F>=criticalF[0]) || (particleNumber>=31 && particleNumber<61 && F>=criticalF[1]) || (particleNumber>=61 && particleNumber<101 && F>=criticalF[2]) || (particleNumber>=101 && particleNumber<501 && F>=criticalF[3]) || (particleNumber>=501 && particleNumber<1001 && F>=criticalF[4]) || (particleNumber>=1001 && particleNumber<2001 && F>=criticalF[5]) || (particleNumber>=2001 && particleNumber<3001 && F>=criticalF[6]) || (particleNumber>=3001 && particleNumber<4001 && F>=criticalF[7]) || (particleNumber>=4001 && particleNumber<5001 && F>=criticalF[8]) || (particleNumber>=5001 && F>=criticalF[9])) {
			//Welch Test
			Tvalue = Math.abs(theoreticalRandomNND-testValue) / (Math.sqrt((theoreticalRandomNND/particleNumber) + (testVariance/particleNumber)));
			df = Math.floor((Math.pow(((theoreticalRandomNND/particleNumber) + (testVariance/particleNumber)),2)) / ((Math.pow((theoreticalRandomNND/particleNumber),2)/(particleNumber-1)) + (Math.pow((testVariance/particleNumber),2)/(particleNumber-1))));
			IJ.log("d.f. = " + df);
			IJ.log("t = " + Tvalue + " (Welch's t-test)");
		} else {
			//Student's t-Test
			Tvalue = (Math.abs(theoreticalRandomNND-testValue)) / (Math.sqrt(0.5*(theoreticalRandomNND+testVariance)) * Math.sqrt(2/particleNumber));
			df = (2*particleNumber) - 2;
			IJ.log("d.f.: " + df);
			IJ.log("t = " + Tvalue + " (Student's t-test)");
		}
		
		//critical t-values for alpha=0.01 (two-tailed)
		
		double criticalTValue = 0;
		if(df>=10 && df<30) {
			criticalTValue=criticalT[0];
		} else if(df>=30 && df<60) {
			criticalTValue=criticalT[1];
		} else if(df>=60 && df<100) {
			criticalTValue=criticalT[2];
		} else if(df>=100 && df<500) {
			criticalTValue=criticalT[3];
		} else if(df>=500 && df<1000) {
			criticalTValue=criticalT[4];
		} else if(df>=1000 && df<2000) {
			criticalTValue=criticalT[5];
		} else if(df>=2000 && df<3000) {
			criticalTValue=criticalT[6];
		} else if(df>=3000 && df<4000) {
			criticalTValue=criticalT[7];
		} else if(df>=4000 && df<5000) {
			criticalTValue=criticalT[8];
		} else if(df>=5000) {
			criticalTValue=criticalT[9];
		}

		IJ.log("critical t-value = " + criticalTValue);
		IJ.log("confidence interval = " + chosenCI);
		IJ.log("--------------------------------------------------------------------------");
		
		if(Tvalue >= criticalTValue) {
			if(testValue < theoreticalRandomNND) {
				IJ.log("  --->   clustering particles");
			} else if(testValue > theoreticalRandomNND) {
				IJ.log("  --->   self-avoiding particles");
			}
			IJ.log("significant different from random distribution with p < " + pListed);
		} else {
			IJ.log("  --->   random particle distribution");
			IJ.log("no significant difference to random distribution (p > "+pListed+")");
		}
		IJ.log("according to " + statisticalMethod + " of the " + neighborMethod + " distance");
		IJ.log("--------------------------------------------------------------------------");



	}

	
	public double[] getAverageND(ImagePlus imageIMP, ResultsTable resultsTable) {
	
		ImageProcessor imageIP = imageIMP.getProcessor();
		int particleNumber = resultsTable.getCounter();
		
		//create intensity coded voronoi
		Prefs.blackBackground = true;
		EDM edm = new EDM();
		EDM.setOutputType(EDM.FLOAT);
		edm.setup("voronoi", imageIMP);
		edm.run(imageIP);
		edm.setup("final", imageIMP);
		
		//create an invisible voronoi image for further processing
		ImagePlus intermediateVoronoiImp = WindowManager.getCurrentImage();
		ImagePlus voronoiImp = intermediateVoronoiImp.duplicate();
		intermediateVoronoiImp.close();
		
		double[] averageNeighborDistance = new double[particleNumber];
		int[] startX = new int[particleNumber];
		int[] startY = new int[particleNumber];
		
		IJ.showStatus("Evaluating average NND");
		Calibration cal = new Calibration(voronoiImp);
		cal.pixelWidth = 1.0;
		cal.pixelHeight = 1.0;
		double size = cal.pixelWidth;
		
		for(int i=0; i<particleNumber; i++) {
			startX[i] = (int) Math.round(resultsTable.getValue("XStart", i));
			startY[i] = (int) Math.round(resultsTable.getValue("YStart", i));
						
			IJ.doWand(voronoiImp, startX[i], startY[i], 0.0, "8-connected");
			IJ.run(voronoiImp, "Make Band...", "band=" + size);
			
			ImageStatistics voronoiBandSelectionStats = voronoiImp.getStatistics();
			averageNeighborDistance[i] = (2 * voronoiBandSelectionStats.min);
			voronoiImp.killRoi();
			voronoiBandSelectionStats = null;
			IJ.showProgress((double)i / (double)particleNumber);
		}
		
		return averageNeighborDistance;
	}
}
