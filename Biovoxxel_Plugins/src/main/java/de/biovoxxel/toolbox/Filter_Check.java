package de.biovoxxel.toolbox;


import java.awt.Rectangle;
import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.util.Tools;

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

public class Filter_Check implements PlugInFilter {
	ImagePlus imp;
	ImagePlus currentFilteredImp;
	ImageProcessor currentFilteredIP;
	private int flags = DOES_8G|DOES_16;
	

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return flags;
	}

	public void run(ImageProcessor ip) {
		//imp.lock();
		String originalTitle = imp.getTitle();
		String filterListString = "Gaussian Blur...,Median...,Gaussian Weighted Median,Mean...,Minimum...,Maximum...,Unsharp Mask...,Variance...,Linear Kuwahara,Bilateral Filter";
		File pluginsDir = new File(IJ.getDirectory("plugins"));
		if(pluginsDir==null) {
			IJ.error("No plugins folder detected");
			return;
		} else {
			File[] parentFileList = pluginsDir.listFiles();
			File[] subfolderList = new File[parentFileList.length];
			int subfolderCount = 0;
			for(int l=0; l<parentFileList.length; l++) {
				//IJ.log(""+parentFileList[l].getName());
				if(parentFileList[l].isDirectory()) {
					subfolderList[subfolderCount] = parentFileList[l];
					subfolderCount++;
				} else {
					if(parentFileList[l].getName().equals("Mean_Shift.class")) {
						filterListString = filterListString + ",Mean Shift";
					} else if(parentFileList[l].getName().equals("Mexican_Hat_Filter.class")) {
						filterListString = filterListString + ",Mexican Hat Filter";
					}
				}
			}
			
			for(int s=0; s<subfolderList.length; s++) {
				if(subfolderList[s]!=null) {
					//IJ.log(""+subfolderList[s]);
					File[] subfolderFiles = subfolderList[s].listFiles();
					for(int sfl=0; sfl<subfolderFiles.length; sfl++) {
						//IJ.log(""+subfolderFiles[sfl].getName());
						if(subfolderFiles[sfl].getName().equals("Mean_Shift.class")) {
							filterListString = filterListString + ",Mean Shift";
						} else if(subfolderFiles[sfl].getName().equals("Mexican_Hat_Filter.class")) {
							filterListString = filterListString + ",Mexican Hat Filter";
						}
					}
				}
			}
		}
		//check for existing ROI and get bounds
		int imageWidth = ip.getWidth();
		int imageHeight = ip.getHeight();
		Rectangle roi = ip.getRoi();
		int w = roi.width;
		int h = roi.height;
		
		String[] filterList = Tools.split(filterListString, ",");
		
		GenericDialog gd = new GenericDialog("Filter Choice and Setup");
			gd.addChoice("Filter", filterList, "Gaussian Blur...");
			gd.addNumericField("start radius", 1.0, 0);
			gd.addNumericField("stop radius", 1.0, 0);
			gd.addNumericField("parameter", 0.0, 1);
			gd.showDialog();
			if(gd.wasCanceled()) {
				return;
			}
			
			String filterMethod = gd.getNextChoice();
			double startR = gd.getNextNumber();
			double stopR = gd.getNextNumber();
			double filterParameter = gd.getNextNumber();
			int filterRange = (int)stopR-(int)startR;
			
			if(gd.invalidNumber()) {
				IJ.error("invalid number entered");
				return;
			} else if(filterRange>30) {
				IJ.showMessage("radius range <30 necessary");
				return;
			} else if(filterMethod.equals("Gaussian Weighted Median") && ((w==imageWidth && h==imageHeight) || filterRange>10 || (int)stopR>20)) {
				IJ.showMessage("select a small ROI\na filter range < 10 and\na stop radius < 25");
				return;
			}
		
		//controlling parameter settings
		if(filterMethod.equals("Unsharp Mask...") && (filterParameter<0.1 || filterParameter>0.9)) {
			IJ.error("parameter settings: 0.1-0.9");
			return;
		} else if(filterMethod.equals("Linear Kuwahara") && (filterParameter%2==0)) {
			IJ.error("parameter needs to be an odd number");
			return;
		}
		
		//preparing the images for processing
		
		//ImageStack filterRangeStack = new ImageStack(w, h, filterRange+1); //original
		ImagePlus selectedROI = imp.duplicate();
		ImageProcessor selectedIP = selectedROI.getProcessor();
		ImageStack filterRangeStack = new ImageStack(w, h, 1);
		filterRangeStack.setProcessor(selectedIP, 1);
		ImagePlus outputStack = new ImagePlus(filterMethod+"("+originalTitle+")", filterRangeStack);
				
		for(int f=0; f<=filterRange; f++) {
			if(!filterMethod.equals("Bilateral Filter") && !filterMethod.equals("Gaussian Weighted Median")) {
				filterRangeStack.addSlice(""+(startR+f), selectedIP.duplicate());
				if(filterMethod.equals("Mean Shift")) {
					outputStack.show();
				}
				//IJ.wait(200);
				outputStack.setSlice(f+2);
			}
			
			//WindowManager.setCurrentWindow(outputStack.getWindow());
			
			if(filterMethod.equals("Mean Shift")) {
				IJ.run(outputStack, "Mean Shift", "spatial="+(startR+f)+" color="+filterParameter+" slice");
			} else if(filterMethod.equals("Unsharp Mask...")) {
				IJ.run(outputStack, "Unsharp Mask...", "radius="+(startR+f)+" mask="+filterParameter+" slice");
			} else if(filterMethod.equals("Linear Kuwahara")) {
				IJ.run(outputStack, "Linear Kuwahara", "number_of_angles="+(startR+f)+" line_length="+filterParameter+" criterion=Variance");
			} else if(filterMethod.equals("Bilateral Filter")) {
				ImagePlus currentFilteredImp = imp.duplicate();
				IJ.run(currentFilteredImp, "Bilateral Filter", "spatial="+(startR+f)+" range="+filterParameter);
				currentFilteredImp = WindowManager.getCurrentImage();
				ImageProcessor currentFilteredIP = currentFilteredImp.getProcessor();
				filterRangeStack.addSlice(""+(startR+f)+"/"+filterParameter, currentFilteredIP);
				currentFilteredImp.changes = false;
				currentFilteredImp.close();
			} else if(filterMethod.equals("Gaussian Weighted Median")) {
				ImagePlus currentFilteredImp = imp.duplicate();
				currentFilteredImp.show();
				IJ.run(currentFilteredImp, "Gaussian Weighted Median", "radius="+(startR+f));
				ImageProcessor currentFilteredIP = currentFilteredImp.getProcessor();
				filterRangeStack.addSlice(""+(startR+f)+"/"+filterParameter, currentFilteredIP);
				currentFilteredImp.changes = false;
				currentFilteredImp.close();
			} else if(filterMethod.equals("Gaussian Blur...")){
				IJ.run(outputStack, filterMethod, "sigma="+(startR+f)+" slice");
			} else {
				IJ.run(outputStack, filterMethod, "radius="+(startR+f)+" slice");
			}
			
			if(!filterMethod.equals("Bilateral Filter")) {
				filterRangeStack.setSliceLabel(("r="+(startR+f)+"/"+filterParameter), f+2);
			}
		}
		outputStack.updateAndDraw();
		outputStack.setSlice(1);
		outputStack.show();
				
		imp.unlock();
	}

}
