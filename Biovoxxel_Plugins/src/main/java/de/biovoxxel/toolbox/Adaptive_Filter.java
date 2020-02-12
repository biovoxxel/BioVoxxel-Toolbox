package de.biovoxxel.toolbox;


import java.util.Arrays;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
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
/** Adaptive Filter

 * This plugin filter creates a square grid of checkboxes to enable the user
 * to create a customized 'median' or 'mean' filter with arbitrary size controlled
 * by the radius keyed in initially. Several preset grids can be chosen.
 * Only pixel intensity values under activated filter grid positions are taken
 * into account for the calculations of the new pixel value
 * 
 * So far can be used for 8-bit and 16-bit grayscale images only.
 * <br>
 * <br>Please cite BioVoxxel and Jan Brocher when you publish results 
 * obtained by usage of this plugin or a modified version of it
 * 
 * Thank you
 * 
 */

public class Adaptive_Filter implements PlugInFilter {
	
	ImagePlus imp;

	String[] choices = new String[] {"median", "mean" };
	String[] shapes = new String[] {"none", "all", "x shaped", "/ shaped", "\\ shaped", "+ shaped", "horizontal", "vertical", "star shaped"};
	int filterPosX = 0;
	int filterPosY = 0;
	int newPixelValue = 0;
	private int positive=0;
	private int centerPixelValue;
	private double testValue1;
	private double testValue2;
	
	public int setup(String arg, ImagePlus imp) {
        	this.imp = imp;
        	return DOES_8G+DOES_16;
        	//implement also for stacks, ROIs and RGB images
    	}

	public void run(ImageProcessor orig) {
	        int width = orig.getWidth();
	        int height = orig.getHeight();

		GenericDialog init = new GenericDialog("Filter setup");
			init.addNumericField("Radius (pixels):", 1, 0);
	                init.addChoice("Filter", choices, "median");
	                init.addChoice("Shape", shapes, "all");
	                init.addNumericField("Tolerance 0.0-0.9", 0.0, 1);
				 if(imp.getNSlices()>1) {
					init.addCheckbox("stack processing", false);
				 }
	                init.showDialog();
	                if (init.wasCanceled()) {
	                	return;
	                }
	                int radius = (int) init.getNextNumber();
	                String filterMethod = init.getNextChoice();
	                String startingShape = init.getNextChoice();
	                double tolerance = init.getNextNumber();
			boolean processCompleteStack = false;
			if(imp.getNSlices()>1) {
				processCompleteStack = init.getNextBoolean();
			}
		
		int filterEdge = (radius*2)+1;
		int filterSize = filterEdge*filterEdge;
		String[] gridLabels = new String[filterSize];
		boolean [] defaults = new boolean[filterSize];
		boolean[] filterArray = new boolean[filterSize];
		int n=0;

		//grid labeling according to columns and rows
		int fn = 0;
		for(int fy=1; fy<=filterEdge; fy++) {
			for(int fx=1; fx<=filterEdge; fx++) {
				gridLabels[fn] = (""+ fy + "/" + fx);
				fn++;
			}
		}

		/*
		//continuas labeling
		for(int f=0; f<filterSize; f++) {
			gridLabels[f] = Integer.toString(f);
		}
		*/
		
		//Arrays.fill(gridLabels, " ");
		if(startingShape=="none") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(n==(filterSize-1)/2) {
						defaults[n]=true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="all") {
			Arrays.fill(defaults, true);		
		} else if (startingShape=="x shaped") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridY==gridX || gridX==((filterEdge-1)-gridY)) {
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
					
				}
			}
		} else if (startingShape=="/ shaped") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridX==((filterEdge-1)-gridY)) {
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="\\ shaped") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridY==gridX) {
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="+ shaped") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridX==(filterEdge-1)/2 || gridY==(filterEdge-1)/2) { 
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="horizontal") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridY==(filterEdge-1)/2) { 
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="vertical") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridX==(filterEdge-1)/2) { 
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		} else if (startingShape=="star shaped") {
			for (int gridY=0; gridY<filterEdge; gridY++) {
				for (int gridX=0; gridX<filterEdge; gridX++) {
					if(gridX==(filterEdge-1)/2 || gridY==(filterEdge-1)/2 || gridY==gridX || gridX==((filterEdge-1)-gridY)) { 
						defaults[n] = true;
					} else {
						defaults[n] = false;
					}
					n++;
				}
			}
		}

		GenericDialog grid = new GenericDialog("adapt filter");
			grid.addCheckboxGroup(filterEdge, filterEdge, gridLabels, defaults);
			grid.showDialog();
			if (grid.wasCanceled()) {
	                	return;
	                }
			for(int filter=0; filter<filterSize; filter++) {
				filterArray[filter] = grid.getNextBoolean();
				if(filterArray[filter]==true) {
					positive++;
				}
			}
		
		int processOneOrAll = 1;
		
		if(processCompleteStack){
			processOneOrAll = imp.getNSlices();
		}

		ImagePlus copyImp = imp.duplicate();
		ImageProcessor copy = copyImp.getProcessor();

		for(int slice=1; slice<=processOneOrAll; slice++) {
			imp.setSliceWithoutUpdate(slice);
			copyImp.setSliceWithoutUpdate(slice);
			
			//define grid defined array size
			int[] originalPixelValues = new int[positive];
			
			for(int ImageY=0; ImageY<height; ImageY++) {
				for(int ImageX=0; ImageX<width; ImageX++) {

					centerPixelValue = copy.getPixel(ImageX, ImageY);

					//define position inside filter
					int k=0;
					int index=0;
					for(int filterY=-radius; filterY<=radius; filterY++) {
						//horizontal border padding 
						if((ImageY+filterY) < 0) {
							filterPosY = -ImageY;
						} else if((ImageY+filterY) > height-1) {
							filterPosY = (height-1)-ImageY;
						} else {
							filterPosY = filterY;
						}
						
						for(int filterX=-radius; filterX<=radius; filterX++) {
							//vertical border padding
							if((ImageX+filterX) < 0) {
								filterPosX = -ImageX;
							} else if((ImageX+filterX) > width-1) {
								filterPosX = (width-1)-ImageX;
							} else {
								filterPosX = filterX;
							}
			
							//reading in original pixel values if pixel position was activated in the adaptive filter matrix
							if(filterArray[index]==true) {
								originalPixelValues[k] = copy.getPixel((ImageX+filterPosX), (ImageY+filterPosY));
								k++;
							}
							index++;
															
						}
					}

					if(filterMethod=="median") {
						Arrays.sort(originalPixelValues);
						if(positive%2==0) {
							newPixelValue=(((originalPixelValues[(positive/2)-1])+(originalPixelValues[(positive/2)]))/2);	
						} else {
							newPixelValue=(originalPixelValues[(positive-1)/2]);
						}
					} else if(filterMethod=="mean") {
						int sum=0;
						for(int m=0; m<positive; m++) {
							sum = sum + originalPixelValues[m];
						}
						newPixelValue = sum/positive;
					}

							
					if(centerPixelValue!=0) {
						//IJ.log("old: "+ (double) centerPixelValue);
						//IJ.log("new "+ (double) newPixelValue);
						testValue1 = (double) newPixelValue/centerPixelValue;
						//IJ.log("1.) "+testValue1);
						testValue2 = (double) centerPixelValue/newPixelValue;
						//IJ.log("2.) "+testValue2);
					}
					
								
					if(tolerance==0.0) {
						orig.putPixel(ImageX, ImageY, newPixelValue);
					} else if(tolerance!=0.0 && centerPixelValue==0) {
						orig.putPixel(ImageX, ImageY, newPixelValue);
					} else if(tolerance!=0.0 && (testValue1 < (1.0-tolerance))) {
						orig.putPixel(ImageX, ImageY, newPixelValue);
					} else if(tolerance!=0.0 && (testValue2 < (1.0-tolerance))) {
						orig.putPixel(ImageX, ImageY, newPixelValue);
					} else {
						orig.putPixel(ImageX, ImageY, centerPixelValue);	
					}

				}
			}
		}
		imp.updateAndDraw();
		copyImp = null;
		copy = null;
	}
}



