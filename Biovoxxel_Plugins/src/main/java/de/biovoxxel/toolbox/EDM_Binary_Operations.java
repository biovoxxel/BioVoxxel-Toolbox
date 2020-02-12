package de.biovoxxel.toolbox;


import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.EDM;
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
 
 
 public class EDM_Binary_Operations implements ExtendedPlugInFilter, DialogListener {
	ImagePlus imp;
	private int flags = DOES_8G|KEEP_PREVIEW|SNAPSHOT;
	private PlugInFilterRunner pfr;
	private int nPasses = 1;
	private int pass;
	private int iterationChoice = 1;
	private String operationChoice;
	private double previousIterations = Prefs.get("bvtb.EDM.iterations", 1);
	private String previousOperation = Prefs.get("bvtb.EDM.operation", "erosion");
	private String[] operation = {"erode", "dilate", "open", "close"};
	
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		if(!imp.getProcessor().isBinary()) {
			IJ.error("works only on 8-bit binary images");
			return DONE;
		} else {
			return DOES_8G | DOES_STACKS;
		}
	}
		
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog("EDM Binary Morphology");
			gd.addNumericField("iterations:", previousIterations, 0);
			gd.addRadioButtonGroup("operation", operation, 2, 2, previousOperation);
			gd.addPreviewCheckbox(pfr);	// passing pfr makes the filter ready for preview
			gd.addDialogListener(this);	// the DialogItemChanged method will be called on user input
			gd.addHelp("http://fiji.sc/BioVoxxel_Toolbox#EDM_Erosion_and_EDM_Dilation");
			gd.showDialog();		// display the dialog; preview runs in the background now
			if (gd.wasCanceled()) {
				return DONE;
			}
			IJ.register(this.getClass());	// protect static class variables (filter parameters) from garbage collection
		this.pfr = pfr;
		return IJ.setupDialog(imp, flags); // ask whether to process all slices of stack (if a stack)
	}
	
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		iterationChoice = (int)gd.getNextNumber();
		operationChoice = gd.getNextRadioButton();
		if (iterationChoice<0) {
			IJ.error("invalid number");
			return false;
		} else {
			return true;
		}
	}
	
	public void run(ImageProcessor ip) {
		Prefs.blackBackground = true;
		boolean invertedLut = ip.isInvertedLut();
		if(invertedLut) {
			ip.invertLut();
		}
		
		if(operationChoice.equals("erode")) {
			doErosion(ip, iterationChoice, 255);
		} else if(operationChoice.equals("dilate")) {
			doDilation(ip, iterationChoice, 255);
		} else if(operationChoice.equals("open")) {
			doErosion(ip, iterationChoice, 255);
			doDilation(ip, iterationChoice, 255);
		} else if(operationChoice.equals("close")) {
			doDilation(ip, iterationChoice, 255);
			doErosion(ip, iterationChoice, 255);
		}
		imp.updateAndDraw();
	}
	
	
	public void doErosion(ImageProcessor ip, int min, int max) {
		new EDM().toEDM(ip);
		ip.applyTable(getThresholdLUT(min, max));
	}
	
	public void doDilation(ImageProcessor ip, int min, int max) {
		ip.invert();
		new EDM().toEDM(ip);
		ip.applyTable(getThresholdLUT(min, max));
		ip.invert();
	}
	
	public int[] getThresholdLUT(int min, int max) {
		int[] lut = new int[256];
		for(int i=0; i<256; i++) {
			if(i<=min) {
				lut[i] = 0;
			} else {
				lut[i] = 255;
			}
		}
		return lut;
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