package de.biovoxxel.toolbox;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
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

/** 
 * MoLiM and DiLiM are two binarization algorithms which initially limit the image histogram
 * and thereafter take the new resulting mean value as a threshold to devide the image features
 * into foreground and background partitions
 *
 * See also publication: 
 * Qualitative and Quantitative Evaluation of Two New Histogram Limiting Binarization Algorithms
 * J. Brocher, Int. J. Image Process. 8(2), 2014 pp. 30-48
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
 * Please cite: 
 * 
 * Qualitative and Quantitative Evaluation of Two New Histogram Limiting Binarization Algorithms
 * J. Brocher, Int. J. Image Process. 8(2), 2014 pp. 30-48
 * 
 * Thank you
 */

public class Limited_Mean implements PlugInFilter {

	private boolean differential;
	private boolean force;
	//private int histMax = 255;
	private int newMedian;
	private double modal;
	private double median;
	private double mean;
	public int setup(String arg, ImagePlus img) {
		if(WindowManager.getImageCount()==0) {
			IJ.showMessage("No images open");
			return DONE;
		} else {
			String title = img.getTitle();
			return DOES_8G;
		}
	}

	public void run(ImageProcessor orig) {
		orig.resetRoi();
		ByteProcessor origCopy = (ByteProcessor) orig.duplicate();
		ImageStatistics stats = origCopy.getStatistics();
		modal = stats.dmode;
		mean = stats.mean;
		
		//choice between the mode limited histogram or the differential limited histogram
		//the differential method might take the mode, an intermediate mean or the median
		//depending on the histogram
		GenericDialog gd = new GenericDialog("MoLiM & DiLiM");
			gd.addCheckbox("differential limitation", true);
			if(modal!=0 && modal!=255) {
				gd.addCheckbox("force to smaller partition", false);
			}
			gd.showDialog();
			differential = gd.getNextBoolean();
			if(modal!=0 && modal!=255) {
				force = gd.getNextBoolean();
			}
			if (gd.wasCanceled()) {
	                	return;
	                }
	                
		int[] hist = origCopy.getHistogram();
		/*
		if((modal==0 || modal==255) && force==true) {
			force=false;
			ij.IJ.log("force command not possible");
			
		} else 
		*/
		if((modal!=0 || modal!=255) && force==true) {
			//ij.IJ.log("forced to small partition");
		}
		
		
		
		if((modal<=mean && force==false) || (modal>mean && force==true)) {
			median = getMedian(hist);

			//ij.IJ.log("old modal: "+modal);
			//ij.IJ.log("old median: "+median);
			//ij.IJ.log("old mean: "+mean);
						
		} else if((modal>mean && force==false) || (modal<=mean && force==true)) {
			orig.invert();
			origCopy.invert();
			hist = origCopy.getHistogram();
			stats = origCopy.getStatistics();
			modal = stats.dmode;
			mean = stats.mean;
			median = getMedian(hist);

			//ij.IJ.log("old modal (inverted): "+modal);
			//ij.IJ.log("old median (inverted): "+median);
			//ij.IJ.log("old mean (inverted)"+mean);
		}
		
		

		if(modal==0.0 && median==0.0 && differential==true) {
			hist[0] = 0;
			double intermediateMean = getMean(hist);
			//ij.IJ.log("Limit: intermediate mean="+intermediateMean);
			
			for(int mod = 0; mod<=intermediateMean; mod++) {
				hist[mod] = 0;
			}
			
				
		} else if(modal==0.0 && median>0.0 && differential==true) {
			for(int mod = 0; mod<=median; mod++) {
				hist[mod] = 0;
			}
			//ij.IJ.log("Limit: median");			
		} else {
			if(Math.abs(median-modal) < Math.abs(mean-median) && differential==true) {
				for(int mod = 0; mod<=median; mod++) {
					hist[mod] = 0;
				}
				//ij.IJ.log("Limit: median: "+Math.abs(median-modal)+ " / "+Math.abs(Math.round(mean-median)));				
			} else {
				for(int mod = 0; mod<=modal; mod++) {
					hist[mod] = 0;
				}
				//ij.IJ.log("Limit modal: "+Math.abs(median-modal)+ " / "+Math.abs(Math.round(mean-median)));
			}
		}
		
		double newMean = getMean(hist);
		newMedian = getMedian(hist);
		
		//ij.IJ.log("new median:"+newMedian);
		//ij.IJ.log("new mean:"+newMean);
		//ij.IJ.log("--------------------------------------------");
		
		if(modal>mean && force==false) {
			orig.invert();
		}
		//IJ.run(img, "Grays", "");
		//orig.setThreshold(newMean, histMax, 1);
		orig.threshold((int)newMean);				
	}

	public double getMean(int[] array) {
		double counts = 0;
		double sum = 0;
		for(int m=0; m<array.length; m++) {
			sum = sum + array[m] * m;
			counts = counts + array[m];
		}
		double newMean = sum/counts;
		return newMean; 
	}
	
	public int getMedian(int[] array) {
		int[] cumHist = new int[256];
		int totalArea = 0;

		for(int tA=0; tA<256; tA++) {
			totalArea = totalArea + array[tA];
		}
		//ij.IJ.log("totalArea:"+totalArea);
		
		for(int cH=0; cH<256; cH++) {
			if(cH==0) {
				cumHist[0] = array[0];
			} else {
				cumHist[cH] = cumHist[cH-1] + array[cH];
			}
		}
		int medianValue = (int) Math.ceil(totalArea/2);
		//ij.IJ.log("medianValue:"+medianValue);

		for(int med=0; med<256; med++) {
			if(cumHist[med]<=medianValue) {
				newMedian = med;
			}
		}
		if(newMedian==0) {
			newMedian=0;
		} else {
			newMedian=newMedian+1;
		}

		return newMedian;
	}
		
}