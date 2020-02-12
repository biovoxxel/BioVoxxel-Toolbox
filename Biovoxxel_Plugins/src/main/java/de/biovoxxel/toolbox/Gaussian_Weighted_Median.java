package de.biovoxxel.toolbox;



import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
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

/** This plugin implements a gaussian weighted median filter.
 *  This preserves edges better compared to the standard median filter but is more calculation intensive
 *  The radius is given in pixel. Filter size is (radius*2+1)*(radius*2+1). 
 *  For this size an approximate 2D gaussian integer matrix is calculated.
 *  This integer matrix defines the weight (count) the individual pixel values
 *  are give during the calculation of the median value.  
 *  Maximal radius size is 20 to reduce processing costs.
 *  Preview enables processing on the fly.
 *  Filter plugin also works in ROIs only.
 *  Supports only 8-bit and 16-bit images so far.
 *
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
 */


public class Gaussian_Weighted_Median implements ExtendedPlugInFilter, DialogListener {
	
	private PlugInFilterRunner pfr;
	private int nPasses = 1;
	private int pass;
	private int radius = 2;
	int flags = DOES_8G|DOES_16|DOES_STACKS|KEEP_PREVIEW|PARALLELIZE_STACKS;
		
	public int setup(String arg, ImagePlus imp) {
        	return flags;
    	}


	public void run(ImageProcessor orig) {
	        gaussianWeightedMedian(orig, radius);
	        if (IJ.escapePressed()) {
	        	orig.reset();
	        }
	}
	        
	public void gaussianWeightedMedian(ImageProcessor orig, int radius) {
	        //check for existing ROI and get bounds
	        Rectangle roi = orig.getRoi();
	        int startX = roi.x;
            	int startY = roi.y;
            	int w = roi.width;
            	int h = roi.height;
            	ImageProcessor copy = orig.duplicate();
		
		final float sigma = (float) (radius*Math.exp(-0.5));
		final int filterEdge = (radius*2)+1;
		final int filterSize = (filterEdge*filterEdge);
	
	        //Pixel array to hold pixel values over the filter size
	        int[] P = new int[filterSize];	
	        
	        //initiation of weighted kernel to hold gaussian weighted values
		int[][] weightKernel = new int[filterEdge][filterEdge];
		int sum = 0;
		
		//calculate gaussian weight matrix for specific radius
		for (int j = -radius; j <= radius; j++) {
			for (int i = -radius; i <= radius; i++) {
				weightKernel[radius+j][radius+i] = (int) (10*(Math.exp(-((j*j)+(i*i))/(2*(sigma*sigma)))));
				sum = sum + weightKernel[radius+j][radius+i];
				//IJ.log(""+weightKernel[radius+j][radius+i] + " sum: " + sum); //print weight matrix
	              	} 
		}


		int[] Pweighted = new int[sum];
		int yFilterPos = 0;
		int xFilterPos = 0;

		//filter position over image
		for (int v = startY; v < (startY+h); v++) {
			/*show Progress still does not work (why so ever, no idea)
			 
			if(v%20==0) {
				showProgress((double) v/(double) h);
			}
			*/
			
			for (int u = startX; u < (startX+w); u++) {
	                
               			 //fill pixel array P for filter position (u,v)
		                int k = 0;
				for (int yFilter = -radius; yFilter <= radius; yFilter++) {
					if((v+yFilter) < 0) {
						yFilterPos = -v;
					} else if((v+yFilter) > startY+h-1) {
						yFilterPos = (startY+h-1)-v;
					} else {
						yFilterPos = yFilter;
					}
					for (int xFilter = -radius; xFilter <= radius; xFilter++) {
						if((u+xFilter) < 0) {
							xFilterPos = -u;
						} else if((u+xFilter) > startX+w-1) {
							xFilterPos = (startX+w-1)-u;
						} else {
							xFilterPos = xFilter;
						}
						
						P[k] = copy.get(u + xFilterPos, v + yFilterPos);
		                       		k++;
		                       		
		                       	}
				}
			
		                int oldIndex=0;
		                int newIndex=0;
		                for(int yIndex=0; yIndex<filterEdge; yIndex++) {
		                	for(int xIndex=0; xIndex<filterEdge; xIndex++) {
		                		int weightIndex=0;
		                		while (weightIndex<weightKernel[yIndex][xIndex]) {
		                			Pweighted[newIndex] = P[oldIndex];
		                			newIndex++;
		                			weightIndex++;
		                		}
		                		oldIndex++;
		                	}
		                	
		                }
				
				Arrays.sort(Pweighted);
				orig.set(u, v, (Pweighted[(Pweighted.length)/2] + Pweighted[(Pweighted.length+2)/2])/2);
			}
	   	}
	   	pass++;
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("Gaussian Weighted Median");
			gd.addNumericField("Radius:", radius, 0, 5, "pixel");
	                gd.addPreviewCheckbox(pfr);	// passing pfr makes the filter ready for preview
	                gd.addDialogListener(this);	// the DialogItemChanged method will be called on user input
	                gd.showDialog();		// display the dialog; preview runs in the background now
	                if (gd.wasCanceled()) {
	                	return DONE;
	                }
	               	IJ.register(this.getClass());	// protect static class variables (filter parameters) from garbage collection
			//radius = (int) gd.getNextNumber();
	        this.pfr = pfr;
	        return IJ.setupDialog(imp, flags); // ask whether to process all slices of stack (if a stack)
	}


	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		radius = (int) gd.getNextNumber();
		if (gd.invalidNumber() || radius<=0 || radius>20) {
			return false;
		} else {
			return true;
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
