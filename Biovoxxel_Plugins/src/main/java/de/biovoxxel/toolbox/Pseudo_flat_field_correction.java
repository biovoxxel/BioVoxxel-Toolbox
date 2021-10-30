package de.biovoxxel.toolbox;


import java.awt.AWTEvent;
import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.GaussianBlur;
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
 * 
 * Please cite BioVoxxel and Jan Brocher when you publish results 
 * obtained by usage of this plugin or a modified version of it
 * 
 * Thank you
 * 
 */

public class Pseudo_flat_field_correction implements ExtendedPlugInFilter, DialogListener {
	
	private PlugInFilterRunner pfr;
	ImagePlus imp;
	ImageProcessor visualizedBlur;
	ImagePlus updatedBlurImage = new ImagePlus();
	ImagePlus existingPreviewImage = null;
	int flags = DOES_8G|DOES_16|DOES_RGB|KEEP_PREVIEW|DOES_STACKS;
	private int nPasses = 1;
	private int pass; 
	private double radius = 50.0d;
	private boolean previousHideBackground = Prefs.getBoolean("pseudo.flatfield.correction.visibility", true);
	public static boolean hideBackground;
	
	public int setup(String arg, ImagePlus imp) {
		if(WindowManager.getImageCount()==0) {
			IJ.showMessage("No Images open");
			return DONE;
		}
		
		if(imp.isComposite()) {
			IJ.error("Image type not supported", "does not work with composite images");
			return DONE;
		}
		
		if(imp.isLocked()) {
			imp.unlock();
		}
		
		this.imp = imp;
		return flags;
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("Pseudo flat-field correction");
			gd.addNumericField("Blurring radius", radius, 1, 6, "Pixels");
			gd.addCheckbox("hide background", previousHideBackground);
			gd.addPreviewCheckbox(pfr); 
			gd.addDialogListener(this);
			gd.showDialog();
			if (gd.wasCanceled()) {
				return DONE;
			}
		IJ.register(this.getClass());
		this.pfr = pfr;
		return IJ.setupDialog(imp, flags);
		
	}

	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		radius = gd.getNextNumber();
		hideBackground = gd.getNextBoolean();
		if (gd.invalidNumber() || radius<0.5) {
			return false;
		}
		Prefs.set("pseudo.flatfield.correction.visibility", hideBackground);
		return true;
	}

	public void run(ImageProcessor ip) {
		int[] imageDimensions = imp.getDimensions();
		int width = imageDimensions[0];
		int height = imageDimensions[1];
		//int channels = imageDimensions[2];;
		//int slices = imageDimensions[3];
		//int frames = imageDimensions[4]; 

		int imageSize = width * height;
		int[] rgbValues = new int[3];
		float[] hsbValues = new float[3];
		float[] hue = new float[imageSize];
		float[] saturation = new float[imageSize];
		float[] brightness = new float[imageSize];
		//int[] roundedBrightness = new int[imageSize];
		
		
		ImageProcessor duplicatedIp = ip.createProcessor(width, height);
		if(imp.getBitDepth()==24) {
			duplicatedIp = duplicatedIp.convertToFloatProcessor();
		}
				
		if(imp.getBitDepth()==24) {
			int n = 0;
			//float brightnessValueSum = 0;
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					ip.getPixel(x, y, rgbValues);
					Color.RGBtoHSB(rgbValues[0], rgbValues[1], rgbValues[2], hsbValues);
					hue[n] = hsbValues[0];
					saturation[n] = hsbValues[1];
					brightness[n] = hsbValues[2];

					//brightnessValueSum = brightnessValueSum + brightness[n];
					duplicatedIp.putPixelValue(x, y, (double)brightness[n]);
					n++;	
				}
			}
			
			visualizedBlur = duplicatedIp.duplicate();
		
		} else {
			duplicatedIp = ip.duplicate();
			visualizedBlur = ip.duplicate();
		}

		ImagePlus duplicatedImp = new ImagePlus();
		duplicatedImp.setProcessor(duplicatedIp);
		
		GaussianBlur blurredBackground = new GaussianBlur();
		blurredBackground.blurGaussian(duplicatedIp, radius, radius, 0.02);
		float backgroundMeanIntensity = (float)duplicatedIp.getStatistics().mean;
			
		String originalTitle = imp.getTitle();
		updatedBlurImage.setTitle(originalTitle+"_background");
		String updatedBlurImageTitle = updatedBlurImage.getTitle();

		existingPreviewImage = WindowManager.getImage(updatedBlurImageTitle);
		//this part visualizes a background image to be able to determine a sufficiently big Gaussian Blur
		if (!hideBackground) {
			if(existingPreviewImage!=null) {
				updatedBlurImage.setProcessor(visualizedBlur);
				blurredBackground.blurGaussian(visualizedBlur, radius, radius, 0.02);
				updatedBlurImage.updateAndDraw();
			} else {
				updatedBlurImage.setProcessor(visualizedBlur);
				blurredBackground.blurGaussian(visualizedBlur, radius, radius, 0.02);
				updatedBlurImage.show();
			}
		} else if(hideBackground && existingPreviewImage != null) {
			existingPreviewImage.close();
		}

		if(imp.getBitDepth()==24) {
			
			float[] newBrightnessValue = new float[imageSize];
			float highestBackgroundPixel = 0.0f;
			int m = 0;
						
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					if(duplicatedIp.getPixelValue(x, y)!=0) {
						newBrightnessValue[m] =  ((brightness[m] * backgroundMeanIntensity) / (duplicatedIp.getPixelValue(x, y)));
						if(newBrightnessValue[m] > highestBackgroundPixel) {
							highestBackgroundPixel = newBrightnessValue[m];
						}
						//IJ.log(""+m+".) "+hue[m]+" / "+saturation[m]+" / "+newBrightnessValue[m]);
					}
					
					m++;
				}
			}

			int z = 0;
			float scaledBrightnessValue = 0.0f;
			int newPixel = 0;
			
			for(int v=0; v<height; v++) {
				for(int u=0; u<width; u++) {
					if(duplicatedIp.getPixelValue(u, v)==0) {
						newPixel = ip.getPixel(u, v);
					} else {
						scaledBrightnessValue = newBrightnessValue[z] / highestBackgroundPixel;
						//IJ.log(""+z+". > "+ scaledBrightnessValue);
						newPixel = Color.HSBtoRGB(hue[z], saturation[z], scaledBrightnessValue);
						ip.putPixel(u, v, newPixel);
						z++;
					}
				}
			}

			
			//IJ.log("HB --> "+highestBackgroundPixel);
			ip.multiply((double)highestBackgroundPixel);
						
			//imp.updateAndDraw();
			
		} else {
			double newPixel;
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					if(duplicatedIp.getPixel(x, y)==0) {
						newPixel = ip.getPixel(x, y);
					} else {
						newPixel = ((ip.getPixel(x, y) * (int)backgroundMeanIntensity) / duplicatedIp.getPixel(x, y));
					}
					ip.putPixelValue(x, y, newPixel);
				}
			}		
			//imp.updateAndDraw();
		}
	}
/*
	public void printIntArray(int[] array) {
		for(int i=0; i<array.length; i++) {
			IJ.log(""+i+".) "+array[i]);
		}
	}
*/

	public void setNPasses (int nPasses) {
	        this.nPasses = nPasses;
	        pass = 0;
	}
	
	void showProgress(double percent) {
		percent = (double)(pass-1)/nPasses + percent/nPasses;
		IJ.showProgress(percent);
	}
}
