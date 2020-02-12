package de.biovoxxel.toolbox;


import java.awt.AWTEvent;
import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
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

/**
 * Enhance Truecolor Contrast
 * 
 * This plugin enables to increase the contrast of truecolor images
 * in the HSB color space to avoid any changes in hue and saturation
 * The method uses a high precision calculation approach to avoid
 * a conversion between RGB and HSB (since the latter suffers from
 * loss of precision and image quality!).
 * 
 * The saturation field can be used equivalent to the one in the 
 * normal enhance contrast function in ImageJ
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

public class Enhance_True_Color_Contrast implements ExtendedPlugInFilter, DialogListener {
	ImagePlus imp;
	float[] hsv = new float[3];
	String originalTitle;
	float lowestBrightness = 0f;
	float highestBrightness = 1f;
	PlugInFilterRunner pfr;
	private int nPasses = 1;
	private int pass;
	private float saturatedPixelPercentage = 0.0f;
	private int flags = DOES_RGB|PARALLELIZE_STACKS|SUPPORTS_MASKING|KEEP_PREVIEW;
	
	public int setup(String arg, ImagePlus imp) {
		if(WindowManager.getImageCount()==0) {
			IJ.showMessage("No Images open");
			return DONE;
		} else {
			this.imp = imp;
			originalTitle = imp.getTitle();
			return flags;
		}
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("True-color contrast setup");
			gd.addNumericField("saturated pixels:", 0.0, 1, 4, "%");
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
		saturatedPixelPercentage = (float) gd.getNextNumber();
		if (gd.invalidNumber() || saturatedPixelPercentage<0 || saturatedPixelPercentage>100) {
			return false;
		} else {
			return true;
		}
	}

	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int imageSize = width * height;
		int[] rgbValues = new int[3];
		float[] hsbValues = new float[3];
		float[] hue = new float[imageSize];
		float[] saturation = new float[imageSize];
		float[] brightness = new float[imageSize];
		int[] brightnessHistogram = new int[256];
		int[] cumulativeHistogram = new int[256];
		int newRGBValue;
		float quantile = saturatedPixelPercentage/100;
		
		
				
		int n = 0;
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				ip.getPixel(x, y, rgbValues);
				Color.RGBtoHSB(rgbValues[0], rgbValues[1], rgbValues[2], hsbValues);
				hue[n] = hsbValues[0];
				saturation[n] = hsbValues[1];
				brightness[n] = hsbValues[2];
				//histogram of brightness channel with 256 bins
				brightnessHistogram[Math.round(255*brightness[n])]++;
				n++;
				
			}
		}

		
		float minBrightness = 1f;


		boolean foundLowest = false;
		boolean foundHighest = false;

		int saturatedPixelNumber = Math.round((imageSize * quantile));
	
		//calculate the cumulative histogram of the brightness channel
		cumulativeHistogram[0] = brightnessHistogram[0];
		for(int cH=0; cH<256; cH++) {
			if(cH > 0) {
				cumulativeHistogram[cH] = cumulativeHistogram[cH - 1] + brightnessHistogram[cH];
			}
			if(cumulativeHistogram[cH] >= (minBrightness + (float) saturatedPixelNumber) && foundLowest==false) {
				lowestBrightness = ((float)cH / 255f);
				foundLowest = true;
				//IJ.log("low: "+lowestBrightness);
			}
			if(cumulativeHistogram[cH] >= (imageSize - (float) saturatedPixelNumber) && foundHighest==false) {
				highestBrightness = ((float)cH / 255f);
				foundHighest = true;
				//IJ.log("high: "+highestBrightness);
			}
		}

		//brightness channel normalization
		float[] newBrightnessValue = new float[imageSize];
		
		for(int pixelCount=0; pixelCount<imageSize; pixelCount++) {
			if(brightness[pixelCount] <= lowestBrightness) {
				newBrightnessValue[pixelCount] = 0;
			} else if(brightness[pixelCount] >= highestBrightness) {
				newBrightnessValue[pixelCount] = 1;
			} else {
				newBrightnessValue[pixelCount] = ((brightness[pixelCount] - lowestBrightness) / (highestBrightness - lowestBrightness));
			}
		}
		

		ImageProcessor output = ip.duplicate();
		
		int m = 0;
		for(int v=0; v<height; v++) {
			for(int u=0; u<width; u++) {
				newRGBValue = Color.HSBtoRGB(hue[m], saturation[m], newBrightnessValue[m]);
				ip.putPixel(u, v, newRGBValue);
				m++;
			}
		}
	}

	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
		pass = 0;
	}


}
