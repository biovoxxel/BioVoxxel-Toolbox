package de.biovoxxel.toolbox;


import java.awt.AWTEvent;
import java.awt.Checkbox;
import ij.*;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.plugin.filter.RankFilters;

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
 * @author BioVoxxel
 * 
 * This plugin runs the standard image filters Gaussian Blur..., Mean and Median Filter
 * recursively over the image using the specified 0 &lt; radius &lt;= 3 for a maximum number of
 * iterations which can be specified in the respective field. Maximal iteration number is 500.
 * Thus, the filter leads to a better edge preservation when using the median filter and generally
 * allows a better homogenisation of the image with a lower amount of feature degradation.
 * 
 */

public class Basic_Recursive_Filters implements ExtendedPlugInFilter, DialogListener {
	private ImagePlus imp;
	private String originalImageTitle;
	private int flags = DOES_8G|DOES_16|DOES_RGB|KEEP_PREVIEW|SNAPSHOT;	//TODO make it work with stacks
	private String[] filter = {"Median", "Mean", "Gaussian"};
		
	private String chosenFilter;
	private int chosenIteration;
	private double chosenRadius;
	
	private int runs = 0;
	
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("Recursive filtering");
			gd.addChoice("filter", filter, "Median");
			gd.addNumericField("radius", 1, 0, 10, "max.: 3");
            gd.addNumericField("max_iterations", 200, 0, 10, "max.: 500");
            			
			gd.addPreviewCheckbox(pfr);	// passing pfr makes the filter ready for preview
            gd.addDialogListener(this);	// the DialogItemChanged method will be called on user input
            gd.showDialog();		// display the dialog; preview runs in the background now
            if (gd.wasCanceled()) {
            	imp.setTitle(originalImageTitle);
            	return DONE;
            }
           	IJ.register(this.getClass());	// protect static class variables (filter parameters) from garbage collection
			return IJ.setupDialog(imp, flags);
	}


	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		chosenFilter = gd.getNextChoice();
		chosenRadius = (double) gd.getNextNumber();
		chosenIteration = (int) gd.getNextNumber();
		
		Checkbox previewCheckbox = (Checkbox) gd.getCheckboxes().get(0);
		if (gd.invalidNumber() || chosenRadius<=0 || chosenRadius>3 || chosenIteration<1 || chosenIteration>500) {
			if (previewCheckbox.getState()) {
				previewCheckbox.setSize(130, 20);
				previewCheckbox.setLabel("Invalid number");
			}
			return false;
		} else {
			return true;
		}
	}

	
	
	public void run(ImageProcessor ip) {
		
		
		ImagePlus duplicateImp = imp.duplicate();
		boolean continueProcessing = true;
		double[] consecutiveMean = new double[2]; 
		ImageCalculator ic = new ImageCalculator();
		runs = 0;
		
		while(continueProcessing) {
			if(chosenFilter.equals("Median")) {
				RankFilters rf = new RankFilters();
				rf.rank(ip, chosenRadius, RankFilters.MEDIAN);
			} else if(chosenFilter.equals("Mean")) {
				RankFilters rf = new RankFilters();
				rf.rank(ip, chosenRadius, RankFilters.MEAN);
			} else if(chosenFilter.equals("Gaussian")){
				GaussianBlur gb = new GaussianBlur();
				gb.blurGaussian(ip, chosenRadius, chosenRadius, 0.01);
			}
			
			ImagePlus differenceImp = ic.run("Difference create", imp, duplicateImp);
			//ImageProcessor differenceIP = differenceImp.getProcessor();
			ImageStatistics imgStat = differenceImp.getStatistics();
			
			if(runs==0) {
				consecutiveMean[0] = imgStat.mean;
				consecutiveMean[1] = Math.pow(2, imp.getBitDepth());
			} else {
				consecutiveMean[0] = consecutiveMean[1];
				consecutiveMean[1] = imgStat.mean;
			}
			runs++;
			
			if(Math.abs(consecutiveMean[0]-consecutiveMean[1])==0) {
				continueProcessing = false;
			} else {
				continueProcessing = true;
			}
			
			
			if(runs>(chosenIteration-1)) {
				continueProcessing = false;
			}
		}
		imp.setTitle(originalImageTitle + "_" + chosenFilter + "_" + runs);
		imp.updateAndDraw();
	}
	
	

	public void setNPasses (int nPasses) {
	}


	public int setup(String arg, ImagePlus imp) {
		try {
			this.imp = imp;
			originalImageTitle = imp.getTitle();
			return flags;
		} catch (Exception e) {
			//e.printStackTrace();
			IJ.error("No image open");
			return DONE;
		}
	}
}








