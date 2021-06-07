
//--------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------
// first release date: 09/30/2015
// latest release date: 06/07/2021
//--------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------

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

requires("1.53c");

// BioVoxxel Macro Menu

	var filemenu = newMenu("BioVoxxel Menu Tool", newArray("Extended Particle Analyzer", "Field-of-view measure correction", "Shape Descriptor Maps", "Binary Feature Extractor", "Speckle Inspector", "Watershed Irregular Features", "EDM Binary Operations", "-", "Threshold Check", "-", "Filter Check", "-", "Flat-field correction", "Pseudo flat-field correction", "Convoluted Background Subtraction", "-", "Gaussian weighted Median", "Difference of Gaussian", "Difference from Median", "Adaptive Filter", "Recursive Filters", "-", "Hyperstack Color Coding", "-", "SSIDC Cluster Indicator", "-", "Neighbor Analysis", "2D Particle Distribution", "Cluster Indicator", "-", "About"));
	
	macro "BioVoxxel Menu Tool - C000C010C020C030C040C050Df9C050D62C050D75C060D87C060D76C060D61C060Db8C060D63C060D74Da8Dc8C060D50De9C060D81C060De7C060D91Dc2C060D77Dd4Dd8C060De8C070D71D97Dd3C070D73D78C070D51D60De6C070D06D5aD69Db2C070D4bD86Dd5C070D3dD98Da7C070D64Da2C070D3cD72D82D83D84D85D92D93D94D95D96Da1Da3Da4Da5Da6Db3Db4Db5Db6Db7Dc3Dc4Dc5Dc6Dc7Dd6Dd7C070D2dC070Dd9C070C080D2cC080D15D19Dd2C080D88C080D18C080D24DafC080D33C080D2bC080D42D65C080De5C080D52C080D4cC080D17D41D68C080C090D2aD59C090D9fDb1Dc9C090D32D4aD5bC090D1aD3bC090D16D53D66C090D25D29D5eD6eDf8C090D26D27D28D34D35D36D37D38D39D3aD43D44D45D46D47D48D49D54D55D56D57D58D67DeaC090D6aC090DbeC090D07C090D23DdbC090D70D7eDcdC090D79DccC0a0D4eDb9C0a0DbdDdcC0a0D14C0a0D8eDaeC0a0D4dD8fC0a0D05Da9C0a0DebC0b0Dc1DfaC0b0D9eC0b0D5cD5dD99DdaC0b0D6bD6cD6dD7aD7bD7cD7dD89D8aD8bD8cD8dD9aD9bD9cD9dDaaDabDacDadDbaDbbDbcDcaDcbC0b0De4C0b0D1bD3eC0b0D80C0b0C0c0Df7C0c0D08C0c0D7fC0c0C0d0D2eC0d0Dd1C0d0D6fC0d0C0e0C0f0D5fC0f0"{

		BVCmd = getArgument();
		if (BVCmd!="-") {
			if (BVCmd=="Extended Particle Analyzer") { run("Extended Particle Analyzer"); }
			else if (BVCmd=="Field-of-view measure correction") { CountCorrection(); }
			else if (BVCmd=="Shape Descriptor Maps") { ShapeDescriptorMaps(); }
			else if (BVCmd=="Binary Feature Extractor") { run("Binary Feature Extractor"); }
			else if (BVCmd=="Speckle Inspector") { run("Speckle Inspector"); }
			else if (BVCmd=="Watershed Irregular Features") { run("Watershed Irregular Features"); }
			else if (BVCmd=="EDM Binary Operations") { run("EDM Binary Operations"); }
			else if (BVCmd=="Auto Binary Masking") { AutoMasking(); }
			else if (BVCmd=="Threshold Check") { ThresholdCheck(); }
			else if (BVCmd=="Filter Check") { run("Filter Check"); }
			else if (BVCmd=="Flat-field correction") { FFBackgroundCorrection(); }
			else if (BVCmd=="Pseudo flat-field correction") { run("Pseudo flat field correction"); }
			else if (BVCmd=="Median Background Subtraction") { MedianBackgroundSubtraction(); }
			else if (BVCmd=="Convoluted Background Subtraction") { run("Convoluted Background Subtraction"); }
			else if (BVCmd=="Scaled Intensity Plots") { ScaledIntensityPlots(); }
			else if (BVCmd=="Stack Line Plots") { StackLinePlots(); }
			//else if (BVCmd=="Combine Plots") { CombinePlots(); }
			else if (BVCmd=="Gaussian weighted Median") { run("Gaussian Weighted Median"); }
			else if (BVCmd=="Difference of Gaussian") { DifferenceOfGaussian(); }
			else if (BVCmd=="Difference from Median") { DifferenceOfMedian(); }
			else if (BVCmd=="Adaptive Filter") { run("Adaptive Filter"); }
			else if (BVCmd=="Recursive Filters") { run("Recursive Filters"); }
			else if (BVCmd=="Hyperstack Color Coding") { HyperstackColorCoding(); }
			else if (BVCmd=="SSIDC Cluster Indicator") {run("SSIDC Cluster Indicator");}
			else if (BVCmd=="Neighbor Analysis") { NeighborAnalysis(); }
			else if (BVCmd=="2D Particle Distribution") { run("Particle Distribution (2D)"); }
			else if (BVCmd=="Cluster Indicator") { run("Cluster Indicator"); }
			else if (BVCmd=="Particle Length (via Skeleton)") { CorrectedSkeletonLength(); }
			else if (BVCmd=="About") { About(); }
		
		//deprecated links (not present in the menu above, but could still be included (if needed by individual users)
			else if (BVCmd=="Advanced Particle Analyzer (deprecated)") { AdvancedParticleAnalyzer(); }
			else if (BVCmd=="Contrast Detection (deprecated)") { ContrastDetection(); }
			else if (BVCmd=="Cluster Indicator (deprecated macro)") { ClusterIndicator(); }
		}
	}



//------------------------------------------------------------------------------------------
//Analyze Particles according to shape discriptor limitations
//Author: Jan Brocher/BioVoxxel
//First version released: 02/05/2013
//version 0.3 (03/05/2013), bug-fixed analyses, works with selections
//version 0.4 (08/05/2013), bug-fix to work with scaled images and improved performance
//version 0.5 Image selection changed to drop-down menu (14/05/2013)
//version 0.6 Added field of view count correction by eliminating edge particles on 2 frame borders (31/05/2013)
//version 0.7 added th euser choice to use scaled units or pixels for particle analysis (07/06/2013)
//version 0.9 added Extent, Feret's AR and Compactness ; including a bugfix to enable adding to ROI Manager (07/06/2013)
//version 1.0 "memory of keyed-in parameters added, restore default also possible (08/07/2013)
//version 1.1 Thanks to a suggestion from Sidnei Paciornik added redirection to and usage of original grayscale image 
//	      --> calculation of and exclusion by coefficient of variance (COV) (17/02/2014)
//version 1.2 Thanks to suggestions from Gabriel Landini and Jan Eglinger improved performance using floodFill command
//version 1.3 made Area, Perimeter and Max. Feret sensitive to scaling and enable decision to take units or pixel values
//version 1.4 solved problems with object elimination due to accidental scaling of XStart and YStart
//------------------------------------------------------------------------------------------

function AdvancedParticleAnalyzer() {

	originalImg=getTitle();
	getDimensions(width, height, channels, slices, frames);
	getVoxelSize(Vw, Vh, Vdepth, Vunit);
	
	//run("Select None");
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	if(is("binary")==false || slices>1) {
		exit("works only on individual 8-bit binary images");
	}
	openImages=nImages;
	imageNames = newArray(nImages+1);
	imageNames[0] = "None";
	for (i=0; i<openImages; i++){
	        selectImage(i+1);
	        imageNames[i+1] = getTitle();
	}
	selectWindow(originalImg);

	//define variables
	var previousArea = call("ij.Prefs.get", "advPartAnal.area", "0-Infinity");
	var previousExtent = call("ij.Prefs.get", "advPartAnal.Extent", "0.00-1.00");
	var previousPerim = call("ij.Prefs.get", "advPartAnal.perimeter", "0-Infinity");
	var previousCirc = call("ij.Prefs.get", "advPartAnal.circularity", "0.00-1.00");
	var previousRound = call("ij.Prefs.get", "advPartAnal.roundness", "0.00-1.00");
	var previousSolidity = call("ij.Prefs.get", "advPartAnal.solidity", "0.00-1.00");
	var previousCompactness = call("ij.Prefs.get", "advPartAnal.compactness", "0.00-1.00");
	var previousAR = call("ij.Prefs.get", "advPartAnal.AR", "0.00-Infinity");
	var previousFAR = call("ij.Prefs.get", "advPartAnal.FAR", "0.00-Infinity");
	var previousAngle = call("ij.Prefs.get", "advPartAnal.angle", "0-180");
	var previousMaxFeret = call("ij.Prefs.get", "advPartAnal.max.feret", "0-Infinity");
	var previousFeretAngle = call("ij.Prefs.get", "advPartAnal.feret.angle", "0-180");
	var previousCOV = call("ij.Prefs.get", "advPartAnal.variation.coefficient", "0.00-1.00");
	var previousShow = call("ij.Prefs.get", "advPartAnal.show", "Masks");
	var previousReset = call("ij.Prefs.get", "advPartAnal.reset", 0);

	
	
	//Setup including shape descriptors
	Dialog.create("Advanced Particle Analyzer");
		Dialog.addString("Area ("+Vunit+"^2)", previousArea);
		if(Vunit!="pixels") {
			Dialog.addCheckbox("Pixel units", false);
		}
		Dialog.addString("Extent", previousExtent);
		Dialog.addString("Perimeter (pixel)", previousPerim);
		Dialog.addString("Circularity", previousCirc);
		Dialog.addString("Roundness (IJ)", previousRound);
		Dialog.addString("Solidity", previousSolidity);
		Dialog.addString("Compactness", previousCompactness);
		Dialog.addString("Aspect ratio (AR)", previousAR);
		Dialog.addString("Feret's AR", previousFAR);
		Dialog.addString("Ellipsoid angle (degree)", previousAngle);
		Dialog.addString("Max. Feret", previousMaxFeret);
		Dialog.addString("Feret's angle (degree)", previousFeretAngle);
		Dialog.addString("Coefficient of variation", previousCOV);
		Dialog.addChoice("Show", newArray("Nothing", "Outlines", "Bare Outlines", "Ellipses", "Masks", "Count Masks", "Overlay Outlines", "Overlay Masks"), previousShow);
		Dialog.addChoice("Redirect to", imageNames, "None");
		Dialog.addCheckbox("Use default values", previousReset);
		Dialog.addCheckboxGroup(3, 2, newArray("Display results", "Summarize", "Add to Manager", "Exclude edges", "Include holes", "Frame count correction"), newArray(true, false, false, false, false, false));
		Dialog.addHelp("www.biovoxxel.de/macros.html");
		Dialog.show();
		Area=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.area", Area);
		if(Vunit!="pixels" || Vunit!="Pixels") {
			usePixel=Dialog.getCheckbox();
			call("ij.Prefs.set", "advPartAnal.unit", usePixel);
		}
		Extent=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.Extent", Extent);
		Perimeter=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.perimeter", Perimeter);
		Circularity=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.circularity", Circularity);
		Roundness=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.roundness", Roundness);
		Solidity=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.solidity", Solidity);
		Compactness=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.compactness", Compactness);
		AR=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.AR", AR);
		FeretAR=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.FAR", FeretAR);
		EllipsoidAngle=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.angle", EllipsoidAngle);
		MaxFeret=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.max.feret", MaxFeret);
		FeretAngle=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.feret.angle", FeretAngle);
		COV=Dialog.getString();
		call("ij.Prefs.set", "advPartAnal.variation.coefficient", COV);	
		Output=Dialog.getChoice();
		call("ij.Prefs.set", "advPartAnal.show", Output);
		Redirect=Dialog.getChoice();
		Reset=Dialog.getCheckbox();
		call("ij.Prefs.set", "advPartAnal.reset", Reset);
		DisplayResults=Dialog.getCheckbox();
		if(DisplayResults==true) {DisplayResults=" display";} else {DisplayResults="";}
		Summarize=Dialog.getCheckbox();
		if(Summarize==true) {Summarize=" summarize";} else {Summarize="";}
		AddToManager=Dialog.getCheckbox();
		if(AddToManager==true) {
			AddToManager=" add";
			roiManager("reset");
		} else {AddToManager="";}
		ExcludeEdges=Dialog.getCheckbox();
		if(ExcludeEdges==true) {ExcludeEdges=" exclude";} else {ExcludeEdges="";}
		IncludeHoles=Dialog.getCheckbox();
		if(IncludeHoles==true) {IncludeHoles=" include";} else {IncludeHoles="";}
		Correction=Dialog.getCheckbox();

	setBatchMode(true);
	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction redirect="+Redirect+" decimal=3");
	setForegroundColor(0,0,0);

	//Default value definition
	if(Reset==1) {
		Area="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.area", Area);
		Extent="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.Extent", Extent);
		Perimeter="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.perimeter", Perimeter);
		Circularity="0.00-1.00";
		call("ij.Prefs.set", "advPartAnal.circularity", Circularity);
		Roundness="0.00-1.00";
		call("ij.Prefs.set", "advPartAnal.roundness", Roundness);
		Solidity="0.00-1.00";
		call("ij.Prefs.set", "advPartAnal.solidity", Solidity);
		Compactness="0.00-1.00";
		call("ij.Prefs.set", "advPartAnal.compactness", Compactness);
		AR="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.AR", AR);
		FeretAR="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.FAR", FeretAR);
		EllipsoidAngle="0-180";
		call("ij.Prefs.set", "advPartAnal.angle", EllipsoidAngle);
		MaxFeret="0-Infinity";
		call("ij.Prefs.set", "advPartAnal.max.feret", MaxFeret);
		FeretAngle="0-180";
		call("ij.Prefs.set", "advPartAnal.feret.angle", FeretAngle);
		COV="0.00-1.00";
		call("ij.Prefs.set", "advPartAnal.variation.coefficient", COV);	
		Output="Masks";
		call("ij.Prefs.set", "advPartAnal.show", Output);
		Reset=0;
		call("ij.Prefs.set", "advPartAnal.reset", Reset);
	}
	
	if(Correction==true) {
		

	//---------------------------------------------------------------------------------------
	//Eliminate particles touching 2 image edges for correct counting
	//Developed by Jan Brocher / BioVoxxel, 2013
	//first release: 29.05.2013 (v0.1)
	//v0.2, corrected edge particle elimination in contrast to v0.1
	//NO WARRANTY OF FUNCTIONALITY AND NO LIABILITY FOR ANY DAMAGE OR CHANGES TO IMAGES; SOFTWARE AND HARDWARE
	//---------------------------------------------------------------------------------------
	
	
		run("Wand Tool...", "mode=8-connected tolerance=0");
		setBackgroundColor(255,255,255);
		run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
	
		selectWindow(originalImg);
		run("Duplicate...", "title=duplicate");
		duplicate=getTitle();
		openImages=nImages;
		bit=bitDepth();
		binary=is("binary");
		
		if(bit!=8 || binary==false) {
			exit("Only works with 8-bit binary images");
		}
	
		Dialog.create("Setup");
			Dialog.addChoice("keep edges", newArray("Top-Left", "Top-Right", "Bottom-Left", "Bottom-Right"), "Top-Left");
			Dialog.show();
			imgPos=Dialog.getChoice();
		
		selectWindow(duplicate);
		run("Select None");
		run("Canvas Size...", "width="+(width+1)+" height="+(height+1)+" position="+imgPos);
		getDimensions(w2, h2, ch2, slices2, frames2);
		setBackgroundColor(0, 0, 0);
		for(i=1; i<=slices; i++) {
			setSlice(i);
			if(imgPos=="Top-Left" || imgPos=="Top-Right" || imgPos=="Bottom-Left") {
				doWand(width, height);
			} else if(imgPos=="Bottom-Right") {
				doWand(0, 0);
			}
			run("Clear", "slice");
			run("Select None");
		}
		if(slices>1) {
			setSlice(1);
		}
		run("Canvas Size...", "width="+(width)+" height="+(height)+" position="+imgPos);
		selectWindow(duplicate);
	}
	original=getTitle();
	selectWindow(original);
	if(Vunit!="pixels" || Vunit!="Pixels") {
		if(usePixel==true) {
			usedUnit=" pixel";
		} else {
			usedUnit="";
		}
	} else {
		usedUnit="";
	}
	run("Analyze Particles...", "size=" + Area + usedUnit + " circularity="+Circularity+" show=Masks" + ExcludeEdges + " clear" + IncludeHoles + " record");
	temp=getTitle();
	initList=nResults;
	
	//Calculate additional values
	compactness=newArray(initList);
	FAR=newArray(initList);
	extent=newArray(initList);
	cov=newArray(initList);
	for(calc=0; calc<initList; calc++) {
		FAR[calc]=((getResult("Feret", calc))/(getResult("MinFeret", calc)));
		compactness[calc]=(sqrt((4/PI)*getResult("Area", calc))/getResult("Major", calc));
		extent[calc]=(getResult("Area", calc)/((getResult("Width", calc))*(getResult("Height", calc))));
		cov[calc]=((getResult("StdDev", calc))/(getResult("Mean", calc)));
	}

	selectWindow(temp);
	run("Invert LUT");
	
	X=newArray(initList);
	Y=newArray(initList);
	
setForegroundColor(0,0,0);

	for(coord=0; coord<initList; coord++) {
		X[coord]=getResult("XStart", coord);
		Y[coord]=getResult("YStart", coord);
	}

	//elimination process of particles
	for(n=0; n<initList; n++) {
		end=false;
		selectWindow(temp);

		if(Extent!="0.00-1.00" && end==false) {
				ExtentMin=substring(Extent, 0, (indexOf(Extent, "-")));
				ExtentMax=substring(Extent, (indexOf(Extent, "-")+1));
				if(extent[n]<ExtentMin || extent[n]>ExtentMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}
		
		if(Perimeter!="0-Infinity" && end==false) {
				PerimeterMin=substring(Perimeter, 0, (indexOf(Perimeter, "-")));
				PerimeterMax=substring(Perimeter, (indexOf(Perimeter, "-")+1));
				
				if(PerimeterMax=="Infinity") { 
					PerimeterMax=999999999999;
				}
				currentPerimeter = getResult("Perim.", n);
				if(Vunit!="pixels" || Vunit!="Pixels") {
					if(usePixel==true) {
						toUnscaled(currentPerimeter);
					}
				}
				if(currentPerimeter<PerimeterMin || currentPerimeter>PerimeterMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
					
		}

		if(Roundness!="0.00-1.00" && end==false) {
				RoundnessMin=substring(Roundness, 0, (indexOf(Roundness, "-")));
				RoundnessMax=substring(Roundness, (indexOf(Roundness, "-")+1));
				if(getResult("Round", n)<RoundnessMin || getResult("Round", n)>RoundnessMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}
			
		if(Solidity!="0.00-1.00" && end==false) {
				SolidityMin=substring(Solidity, 0, (indexOf(Solidity, "-")));
				SolidityMax=substring(Solidity, (indexOf(Solidity, "-")+1));
				if(getResult("Solidity", n)<SolidityMin || getResult("Solidity", n)>SolidityMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}

		if(Compactness!="0.00-1.00" && end==false) {
				CompactnessMin=substring(Compactness, 0, (indexOf(Compactness, "-")));
				CompactnessMax=substring(Compactness, (indexOf(Compactness, "-")+1));
				if(compactness[n]<CompactnessMin || compactness[n]>CompactnessMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}
			
		if(AR!="0.00-Infinity" && end==false) {
				ARMin=substring(AR, 0, (indexOf(AR, "-")));
				ARMax=substring(AR, (indexOf(AR, "-")+1));
				if(ARMax=="Infinity") {
					ARMax=999999999999;
				}
				if(getResult("AR", n)<ARMin || getResult("AR", n)>ARMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}

		if(FeretAR!="0.00-Infinity" && end==false) {
				FARMin=substring(FeretAR, 0, (indexOf(FeretAR, "-")));
				FARMax=substring(FeretAR, (indexOf(FeretAR, "-")+1));
				if(FARMax=="Infinity") {
					FARMax=999999999999;
				}
				if(FAR[n]<FARMin || FAR[n]>FARMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
		}
		
		if(EllipsoidAngle!="0-180" && end==false) {
				EllipsoidAngleMin=substring(EllipsoidAngle, 0, (indexOf(EllipsoidAngle, "-")));
				EllipsoidAngleMax=substring(EllipsoidAngle, (indexOf(EllipsoidAngle, "-")+1));
				if(getResult("Angle", n)<EllipsoidAngleMin || getResult("Angle", n)>EllipsoidAngleMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
				
		}
		
		if(MaxFeret!="0.00-Infinity" && end==false) {
				MaxFeretMin=substring(MaxFeret, 0, (indexOf(MaxFeret, "-")));
				MaxFeretMax=substring(MaxFeret, (indexOf(MaxFeret, "-")+1));
				if(MaxFeretMax=="Infinity") {
					MaxFeretMax=999999999999;
				}
				currentMaxFeret = getResult("Feret", n);
				if(Vunit!="pixels" || Vunit!="Pixels") {
					if(usePixel==true) {
						toUnscaled(currentMaxFeret);
					}
				}
				if(currentMaxFeret<MaxFeretMin || currentMaxFeret>MaxFeretMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
				
		}
			
		if(FeretAngle!="0-180" && end==false) {
				FeretAngleMin=substring(FeretAngle, 0, (indexOf(FeretAngle, "-")));
				FeretAngleMax=substring(FeretAngle, (indexOf(FeretAngle, "-")+1));
				if(getResult("FeretAngle", n)<FeretAngleMin || getResult("FeretAngle", n)>FeretAngleMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
				
		}

		if(COV!="0.00-1.00" && end==false) {
				COVMin=substring(COV, 0, (indexOf(COV, "-")));
				COVMax=substring(COV, (indexOf(COV, "-")+1));
				if(cov[n]<COVMin || cov[n]>COVMax) {
					floodFill(X[n],Y[n], "8-connected");
					end=true;
				}
				
		}
	}

	selectWindow(temp);
	if(Vunit!="pixels") {
		if(usePixel==false) {
			setVoxelSize(Vw, Vh, Vdepth, Vunit);
		} else {
			setVoxelSize(1, 1, 1, "pixels");
		}
	}
	
	run("Analyze Particles...", "size=0-Infinity" + usedUnit + " circularity=0.00-1.00 show=["+Output+"]" + DisplayResults + ExcludeEdges + " clear" + IncludeHoles + Summarize + " record" + AddToManager);
	if(Output!="Nothing") {
		close(temp);
	}
 else {
		rename("PartAnal_"+originalImg);
	}
	finalImg=getTitle();

	finalList=nResults;
	nFAR=newArray(finalList);
	nCompactness=newArray(finalList);
	nExtent=newArray(finalList);
	nCov=newArray(finalList);
	for(newSD=0; newSD<finalList; newSD++) {
		nFAR[newSD]=((getResult("Feret", newSD))/(getResult("MinFeret", newSD)));
		nCompactness[newSD]=(sqrt((4/PI)*getResult("Area", newSD))/getResult("Major", newSD));
		nExtent[newSD]=(getResult("Area", newSD)/((getResult("Width", newSD))*(getResult("Height", newSD))));
		nCov[newSD]=((getResult("StdDev", newSD))/(getResult("Mean", newSD)));
	}

	//potentially include convexity calculation here
	
	if(DisplayResults==" display") {
		for(writeNew=0; writeNew<finalList; writeNew++) {
			setResult("FeretAR", writeNew, nFAR[writeNew]);
			setResult("Compact", writeNew, nCompactness[writeNew]);
			setResult("Extent", writeNew, nExtent[writeNew]);
			setResult("COV", writeNew, nCov[writeNew]);
		}
	}
		
	if(Output=="Masks") {
		run("Invert LUT");
	} else if (Output=="Bare Outlines") {
		run("Invert");
	}
	
	if(Output!="Nothing") {
		selectWindow(finalImg);
	}
	
	setBatchMode(false);
	exit();
}


//---------------------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------
//Count correction counts particles after exclusion of edge particles and corrects for this bias
//Developed by Jan Brocher / BioVoxxel, 2013
//first release: 31.05.2013
//v0.1
//NO WARRANTY OF FUNCTIONALITY AND NO LIABILITY FOR ANY DAMAGE OR CHANGES TO IMAGES; SOFTWARE AND HARDWARE
//--------------------------------------------------------------------------------------------------------

function CountCorrection() {

	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction display redirect=None decimal=8");
	original=getTitle();
	getDimensions(width, height, channels, slices, frames);
	
	bit=bitDepth();
	binary=is("binary");
	
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	if(bit!=8 || binary==false || slices>1 || channels>1 || frames>1) {
		exit("Only works with individual 8-bit binary images");
	}
	
	Dialog.create("Analyze Particles");
		Dialog.addString("Size (pixel^2)", "0-Infinity");
		Dialog.addString("Circularity", "0.00-1.00");
		Dialog.addChoice("Show", newArray("Nothing", "Outlines", "Bare Outlines", "Ellipses", "Masks", "Count Masks", "Overlay Outlines", "Overlay Masks"), "Masks");
		Dialog.addCheckbox("Include holes", false);
		Dialog.addHelp("www.biovoxxel.de/macros.html");
		Dialog.show();
		Size=Dialog.getString();
		Circularity=Dialog.getString();
		Output=Dialog.getChoice();
	
		IncludeHoles=Dialog.getCheckbox();
		if(IncludeHoles==true) {IncludeHoles=" include";} else {IncludeHoles=""; }

	setBatchMode(true);
	selectWindow(original);
	run("Analyze Particles...", "size="+Size+" circularity="+Circularity+" show=["+Output+"] clear slice exclude" + IncludeHoles);
	if(Output=="Masks") {
		run("Invert LUT");
	}
	results=nResults;
	correctionFactor=newArray(results);
	boundWidth=newArray(results);
	boundHeight=newArray(results);
	originalArea=newArray(results);
	finalCount=0;
	originalAreaSum=0;
	correctedAreaSum=0;
	
	for(i=0; i<results; i++) {
	
		boundWidth[i]=getResult("Width", i);
		boundHeight[i]=getResult("Height", i);
		originalArea[i]=getResult("Area", i);
		correctionFactor[i]=((width*height)/((width-boundWidth[i])*(height-boundHeight[i])));
		finalCount=finalCount+correctionFactor[i];
		originalAreaSum=(originalAreaSum+originalArea[i]);
		correctedAreaSum=(correctedAreaSum+(originalArea[i]*correctionFactor[i]));
	}
	originalMeanArea=originalAreaSum/results;
	correctedMeanArea=correctedAreaSum/results;
	
	print("original count: "+results);
	print("corrected count: "+finalCount);
	print(" ");
	print("original mean area: "+originalMeanArea);
	print("corrected mean area: "+correctedMeanArea);
	print("-----------------------------------------");

	setBatchMode(false);
}

//------------------------------------------------------------------------------------------
//Color coded shape discriptor maps
//Author: Jan Brocher/BioVoxxel
//First version 0.1 (04/05/2013)
//version 0.2 (08/05/2013), fixed a bug leading to problems with scaled images
//version 0.3 (21/05/2013), fixed missing watershed function
//version 0.4 (27/05/2013), included distribution plots for shape descriptors
//version 0.5 (07/02/2014), included Compactness and Extent and improved performance
//version 0.6 (23/02/2014), exchanged distribution plots for interactive plots
//version 0.7 (06/02/2015), Thanks to Gunnar Schley "Roundness" was added to the list of shape descriptors
//version 0.8 (07/06/2021), thanks to Jan Valečka added complete LUT support
//------------------------------------------------------------------------------------------

function ShapeDescriptorMaps() {
	showValues = false;
	original=getTitle();
	lutPanel = getList("LUTs");
	previouslyChosenLut = call("ij.Prefs.get", "shape.descriptor.maps.lut", "Fire");
	type=is("binary");
	if(type==false) { exit("works only with 8-bit binary images"); }
	getDimensions(width, height, channels, slices, frames);
	run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	
	//Setup
	Dialog.create("Analysis Setup");
		Dialog.addChoice("Particle color", newArray("white", "black"), "white");
		Dialog.addCheckbox("do watershed first", false);
		Dialog.addCheckbox("exclude edge particles", false);
		Dialog.addCheckbox("include holes", false);
		Dialog.addCheckbox("show calibration bar", true);
		Dialog.addCheckbox("enable interactive plots", true);
		Dialog.addNumber("Data plot bins (0=auto)", 0);
		Dialog.addChoice("LUT", lutPanel, previouslyChosenLut);
		Dialog.addMessage("if interactive plots are active \npress 'Esc' to finally stop macro!\nResults table will then appear.");
		Dialog.addHelp("www.biovoxxel.de/macros.html");
		Dialog.show();
		color=Dialog.getChoice();
		watershed=Dialog.getCheckbox();
		excludeEdges=Dialog.getCheckbox();
		includeHoles=Dialog.getCheckbox();
		calibrationbar=Dialog.getCheckbox();
		distributionPlot=Dialog.getCheckbox();
		binning = Dialog.getNumber();
		LUT=Dialog.getChoice();
			call("ij.Prefs.set", "shape.descriptor.maps.lut", LUT);


	setBatchMode(true);
	//prepare original image for analysis
	selectWindow(original);
	run("Select None");
	if(color=="black") {
		run("Invert");
	}
	if(watershed==true) {
		run("Watershed");
	}
	if(excludeEdges==true) {
		edges="exclude";
	} else {
		edges="";	
	}
	if(includeHoles==true) {
		holes="include";
	} else {
		holes="";	
	}
	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction redirect=None decimal=3");
	run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Masks "+edges+" clear "+holes+" record");
	run("Invert LUT");
	rename("Input");
	input=getTitle();
	run("Duplicate...", "title=ShapeDescr_"+original);
	result=getTitle();
		
	allParticles=nResults;
	X=newArray(nResults);
	Y=newArray(nResults);
	Area=newArray(allParticles);
	Peri=newArray(allParticles);
	Feret=newArray(allParticles);
	Angle=newArray(allParticles);
	FeretAngle=newArray(allParticles);
	Circ=newArray(allParticles);
	Roundness=newArray(allParticles);
	AR=newArray(allParticles);
	Solidity=newArray(allParticles);
	Compactness=newArray(allParticles);
	Extent=newArray(allParticles);

	biggestArea=0;
	biggestPeri=0;
	biggestFeret=0;
	biggestAngle=0;
	biggestFeretAngle=0;
	biggestCirc=0;
	biggestRoundness=0;
	biggestAR=0;
	biggestSolidity=0;
	biggestCompactness=0;
	biggestExtent=0;
	
	for(i=0; i<allParticles; i++) {
		//read in positional information
		X[i]=getResult("XStart", i);
		Y[i]=getResult("YStart", i);
		//toUnscaled(X[i], Y[i]); //bug-fix in version 0.2
		//read in shape descriptors 
		Area[i]=getResult("Area", i);
		Peri[i]=getResult("Perim.", i);
		Feret[i]=getResult("Feret", i);
		Angle[i]=getResult("Angle", i);
		FeretAngle[i]=getResult("FeretAngle", i);
		Circ[i]=getResult("Circ.", i);
		Roundness[i]=getResult("Round", i);
		AR[i]=getResult("AR", i);
		Solidity[i]=getResult("Solidity", i);
		Compactness[i]=(sqrt((4/PI)*getResult("Area", i))/getResult("Major", i));
		setResult("Compactness", i, Compactness[i]);
		Extent[i]=(getResult("Area", i)/((getResult("Width", i))*(getResult("Height", i))));
		setResult("Extent", i, Extent[i]);

		if(i>0) {
			if(Area[i]>biggestArea) { biggestArea=Area[i]; }
			if(Peri[i]>biggestPeri) { biggestPeri=Peri[i]; }
			if(Feret[i]>biggestFeret) { biggestFeret=Feret[i]; }
			if(Angle[i]>biggestAngle) { biggestAngle=Angle[i]; }
			if(FeretAngle[i]>biggestFeretAngle) { biggestFeretAngle=FeretAngle[i]; }
			if(Circ[i]>biggestCirc) { biggestCirc=Circ[i]; }
			if(Roundness[i]>biggestRoundness) { biggestRoundness=Roundness[i]; }
			if(AR[i]>biggestAR) { biggestAR=AR[i]; }
			if(Solidity[i]>biggestSolidity) { biggestSolidity=Solidity[i]; }
			if(Compactness[i]>biggestCompactness) { biggestCompactness=Compactness[i]; }
			if(Extent[i]>biggestExtent) { biggestExtent=Extent[i]; }
		}
		biggestValue=newArray("biggestArea", "biggestPeri", "biggestFeret", "biggestAngle", "biggestFeretAngle", "biggestCirc", "biggestRounsness", "biggestAR", "biggestSolidity", "biggestCompactness", "biggestExtent");
	}
	
	
	if(showValues==true) {
	 	//print biggest values in the Log winsow
		print("highest values for individual shape descriptors");
		print("Area:   "+biggestArea);
		print("Perimeter:   "+biggestPeri);
		print("Max. Feret:   "+biggestFeret);
		print("Angle:   "+biggestAngle);
		print("Feret Angle:   "+biggestFeretAngle);
		print("Circularity:   "+biggestCirc);
		print("Roundness:   "+biggestRoundness);
		print("Aspect Ratio:   "+biggestAR);
		print("Solidity:   "+biggestSolidity);
		print("Compactness:   "+biggestCompactness);
		print("Extent:   "+biggestExtent);
	}
	
	
	//*******************************************************************
	

	setPasteMode("Copy");
	selectWindow(result);
	setBatchMode("hide");
	for(nS=1; nS<12; nS++) {
		run("Add Slice"); 
	}
	run("Select None");
	
	//run voronoi on particles
	selectWindow(input);
	run("Voronoi");
	setThreshold(1, 255);
	setOption("BlackBackground", true);
	run("Convert to Mask");
	run("Invert");

	//color code shape descriptor maps
	shapeDescriptors=newArray("Area", "Perim.", "Feret", "Angle", "FeretAngle", "Circ.", "Round", "AR", "Solidity", "Compactness", "Extent");
	mapNames=newArray("Area", "Perimeter", "Max Feret", "Angle", "Feret Angle", "Circularity", "Roundness", "Aspect Ratio", "Solidity", "Compactness", "Extent");
	for(m=0; m<shapeDescriptors.length; m++) {
		selectWindow(input);
		run("Duplicate...", "title=["+mapNames[m]+"]");
		map=getTitle();
		selectWindow(map);
		for(i=0; i<allParticles; i++) {
			doWand(X[i],Y[i], 0.0, "8-connected");
			if(shapeDescriptors[m]=="Area") {
				value=round(255/biggestArea*Area[i]);
			}
			if(shapeDescriptors[m]=="Perim.") {
				value=round(255/biggestPeri*Peri[i]);
			}
			if(shapeDescriptors[m]=="Feret") {
				value=round(255/biggestFeret*Feret[i]);
			}
			if(shapeDescriptors[m]=="Angle") {
				value=round(255/biggestAngle*Angle[i]);
			}
			if(shapeDescriptors[m]=="FeretAngle") {
				value=round(255/biggestFeretAngle*FeretAngle[i]);
			}
			if(shapeDescriptors[m]=="Circ.") {
				value=round(255/biggestCirc*Circ[i]);
			}
			if(shapeDescriptors[m]=="Round") {
				value=round(255/biggestRoundness*Roundness[i]);
			}
			if(shapeDescriptors[m]=="AR") {
				value=round(255/biggestAR*AR[i]);
			}
			if(shapeDescriptors[m]=="Solidity") {
				value=round(255/biggestSolidity*Solidity[i]);
			}
			if(shapeDescriptors[m]=="Compactness") {
				value=round(255/biggestCompactness*Compactness[i]);
			}
			if(shapeDescriptors[m]=="Extent") {
				value=round(255/biggestExtent*Extent[i]);
			}
			//print(X[i] + "/" + Y[i] + "/" + value);
			setForegroundColor(value, value, value);
			run("Fill");
		}
		showProgress(((m+1)*i)/(allParticles*10));
		run("Select All");
		run("Copy");
		selectWindow(result);
		setSlice(m+2);
		run("Paste");
		run("Select None");
		close(map);
		
	}
	selectWindow(result);
	currentID = getImageID();
	setSlice(1);
	run("Select All");
	run("Copy");
	setPasteMode("Transparent-white");
	selectWindow(result);
	for(mask=2; mask<=12; mask++) {
		setSlice(mask);
		run("Paste");
		if(mask>1) {
			setMetadata("Label", mapNames[mask-2]);
		}
	}
	setSlice(1);
	close(input);
	run(LUT);
	setBatchMode("show");
	run("Select None");	

	if(calibrationbar==true) {
		newImage("Calibration Bar", "8-bit Ramp", 256, 30, 1);
		run(LUT);
		run("RGB Color");
		setForegroundColor(255, 255, 255);
		setBackgroundColor(0, 0, 0);
		run("Canvas Size...", "width=256 height=80 position=Top-Center");
		setFont("SansSerif", 11, "Bold");
		drawString("smallest (0)    (Area, Perimeter, AR)     biggest", 5, 45);
		drawString("0 deg             (Angle, Feret Angle)         180 deg", 5, 60);
		drawString("0    (Circ., Round, Solid., Compact., Ext.)    max", 5, 75);
		setBatchMode("show");
	}

	//updateResults();
	
	setBatchMode(false);
	if(distributionPlot==true) {
		leftButton=16;
		while(isOpen(result)) {
			getCursorLoc(x, y, z, clicked);
			wait(250);
			if(clicked&leftButton!=0 && isActive(currentID) && z>0) {
				resultEntries = nResults;
				
				selectedData = Table.getColumn(shapeDescriptors[z-1]);
				selectedData = Array.concat(newArray(1), selectedData);
				
				//bins = selectedData.length / 10;
				
				Array.getStatistics(selectedData, dataMin, dataMax, dataMean, DataStdDev);
				Plot.create("SDM-"+getMetadata("Label"), "Particle", getMetadata("Label"));
				if(binning == 0) {
					Plot.addHistogram(selectedData, 0, dataMean);
				} else {
					Plot.addHistogram(selectedData, dataMax/binning, dataMean);
				}
				Plot.setStyle(0, "black,#a0a0ff,1.0,Separate Bars");
				Plot.show();
				selectWindow(result);
			}
			clicked = -1;
		}
	}	
}



//----------------------------------------------------------------------------

/*
//------------------------------------------------------------------------------
// EDM Erosion
//------------------------------------------------------------------------------

function EDMerosion() {
	title=getTitle();
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	selectWindow(title);
	
	getLocationAndSize(x, y, width, height);
	Dialog.create("EDM Erosion");
		Dialog.addNumber("Pixels to erode:", 1);
		Dialog.show();
		erosion=Dialog.getNumber();
	
	run("Options...", "iterations=1 count=1 black edm=8-bit do=Nothing");
	
	run("Distance Map");
	rename("Eroded " + title);
	//run("Threshold...");
	setThreshold(1+erosion, 255);
	run("Convert to Mask");
	setLocation(x+50, y+50);
	exit();
}

//------------------------------------------------------------------------------
// EDM Dilation
//------------------------------------------------------------------------------

function EDMdilate() {
	title=getTitle();
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	selectWindow(title);
	
	getLocationAndSize(x, y, width, height);
	Dialog.create("EDM Dilate");
		Dialog.addNumber("Pixels to dilate:", 1);
		Dialog.show();
		dilate=Dialog.getNumber();
	
		
	run("Options...", "iterations=1 count=1 black edm=8-bit do=Nothing");
	
	selectWindow(title);
	run("Invert");
	run("Distance Map");
	setLocation(x+50, y+50);
	rename("Dilated " + title);
	//run("Threshold...");
	setThreshold(0, dilate);
	run("Convert to Mask");
	selectWindow(title);
	run("Invert");
	selectWindow("Dilated " + title);
	exit();
}
*/
/*
//-----------------------------------------------------------------------------
// Speckle Inspector; developed by Jan Brocher/BioVoxxel, 2013
// Future versions will work on stacks and inside ROIs
// v1.2 Image selection changed to drop-down menu
//-----------------------------------------------------------------------------

function SpeckleInspector() {

	//Define variables
	
	positive=0;
	negative=0;
	less=0;
	more=0;
	PosPart=0;
	NegLess=0;
	NegMore=0;
	sizemax="infinity";
	list=0;
	
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }

	openImages=nImages;
	if(openImages==0) {
		exit("No open images found");
	}
	
	imageNames = newArray(nImages);
	for (i=0; i<imageNames.length; i++){
	        selectImage(i+1);
	        imageNames[i] = getTitle();
	}
	
	//Define Preferences!!!
	Dialog.create("Setup");
		Dialog.addChoice("small (internal) features:", imageNames);
		Dialog.addChoice("bigger (external) objects:", imageNames);
		Dialog.addNumber("Feature no. min: ", 0);
		Dialog.addNumber("Feature no. max: ", 99999);
		Dialog.addNumber("Feature size min: ", 0);
		Dialog.addNumber("Feature size max: ", 99999);
		Dialog.addNumber("Object size min: ", 0);
		Dialog.addNumber("Object size max: ", 99999);
		Dialog.addNumber("Object circularity min: ", 0.00);
		Dialog.addNumber("Object circularity max: ", 1.00);
		Dialog.addCheckbox("exclude objects on edges", true);
		Dialog.addCheckbox("show speckle list", false);
		Dialog.addHelp("www.biovoxxel.de/macros.html");
		Dialog.show();

		target=Dialog.getChoice();
		reference=Dialog.getChoice();
		min=Dialog.getNumber();
		max=Dialog.getNumber();
		SpeckleMin=Dialog.getNumber();
		SpeckleMax=Dialog.getNumber();
		sizemin=Dialog.getNumber();
		sizemaximum=Dialog.getNumber();
		if (sizemaximum==99999) { sizemax="Infinity"; }
		CircMin=Dialog.getNumber();
		CircMax=Dialog.getNumber();
		edge=Dialog.getCheckbox();
		SpeckleList=Dialog.getCheckbox();
		
	
	// "choose image containing the small features (speckles)" -----------------------------------------------------
		
	selectWindow(target);
	getDimensions(width1, height1, channels1, slices1, frames1);
	binary1 = is("binary");
	if (binary1!=1) { exit("works only with binary images"); }
	run("Set Scale...", "distance=0 known=0 pixel=1 unit=pixel");
	run("Select None");
	
	
	
	// "choose image containing the big features enclosing the speckles" ---------------------------------------------------------------------------------------cell	
	
	selectWindow(reference);
	getDimensions(width2, height2, channels2, slices2, frames2);
	binary1 = is("binary");
	if (binary1!=1) { exit("works only with binary images"); }
	run("Set Scale...", "distance=0 known=0 pixel=1 unit=pixel");
	run("Select None");
	
	if (width1!=width2 || height1!=height2) { 
		exit("images need to be of same size");
	} else {
	
	run("Set Measurements...", "area bounding centroid center redirect=None decimal=3");
	
	selectWindow(reference);
	
	//analyze center points of features
	List.clear;
	roiManager("reset");
	if (edge==0) {
		run("Analyze Particles...", "size=sizemin-sizemax circularity=CircMin-CircMax show=Nothing display clear record");
		}
	else if (edge==1) {
		run("Analyze Particles...", "size=sizemin-sizemax circularity=CircMin-CircMax show=Nothing display exclude clear record");
		}
	rows=nResults();
	
	//get center point positions
	selectWindow(reference);
	for (i=0;i<rows;i++) {
		x=getResult("X",i);
		y=getResult("Y",i);
		toUnscaled(x, y);
		doWand(x, y);
		roiManager("Add");
		roiManager("Select", i);
		roiManager("Rename",(i+1));
		}
	
	IJ.renameResults("CentroidList");
	if (SpeckleList==1) {run("Table...", "name=Speckles width=300 height=500");}
	
	//analyze particle number in target image
	List.clear
	selectWindow(target);
	for (t=0;t<rows;t++) {
		roiManager("Select", t);
		run("Analyze Particles...", "size=SpeckleMin-SpeckleMax circularity=0.00-1.00 show=Nothing display clear record");
		particles=nResults();
		List.set(t, particles);
		if (SpeckleList==1) { print("[Speckles]","Feature: "+(t+1)+" Speckle no.: "+particles); }
		
	}
	
	
	//mark reference slide
	selectWindow(reference);
	run("Select None");
	run("Duplicate...", "title=Output");
	selectWindow("Output");
	run("RGB Color");
	
	for (RefObj=0;RefObj<rows;RefObj++) {
		PartAnal=List.getValue(RefObj);
		roiManager("Select", RefObj);
		//included objects
		if (PartAnal>=min && PartAnal<=max) {
			setForegroundColor(255,0,255);
			roiManager("Fill");
			positive=positive+1;
			PosPart=PosPart+PartAnal;
		} 
	
		//excluded objects
		else if (PartAnal<min) {
			setForegroundColor(0,0,255);
			roiManager("Fill");
			less=less+1;
			NegLess=NegLess+PartAnal;			
		}
		else if (PartAnal>max) {
			setForegroundColor(0,93,0);
			roiManager("Fill");
			more=more+1;
			NegMore=NegMore+PartAnal;			
		}
	}
	
	
	selectWindow("Results"); run("Close");
	
	//result
	
	selectWindow("Output");
	end=roiManager("count");
	
	IJ.renameResults("CentroidList","Results")
	
	selectWindow("Output");
	for (label=0;label<end;label++) {
		CX=getResult("X",label);
		CY=getResult("Y",label);
		PartNum=List.getValue(label);
		setColor(255,255,255);
		setFont("SansSerif", 10, "bold");
		drawString((label+1)+"-("+PartNum+")",CX-12,CY+7);
		
	}
	
	selectWindow("Results"); run("Close");
	
	
	//Statistics output
	print("Speckle no. limit min: " + min + " / max: " + max);
	print("Speckle size limit min: " + SpeckleMin + " / max: " + SpeckleMax);
	print("Feature size limit min: " + sizemin + " / max: " + sizemaximum);
	print("Circularity limit min: " + CircMin + " / max: " + CircMax);
	print("----------------------------------------------------");
	print("White features are excluded from the analysis");
	print("All Features: " + rows);
	print("All Speckles: " + (PosPart+NegLess+NegMore));
	print("Aver. speckle no/feature (all): " + ((PosPart+NegLess+NegMore)/rows));
	print("----------------------------------------------------");
	print("Selected Features (magenta): " + positive);
	print("   All speckles in pos. features: " + PosPart);
	print("   Aver. speckle no/pos feature: " + (PosPart/positive));
	print("");
	print("Features with speckle no. smaller min (blue): " + less);
	print("   All speckles: " + NegLess);
	print("   Aver. speckle no/feature: " + (NegLess/less));
	print("");
	print("Features with speckle no. higher max (green): " + more);
	print("   All speckles: " + NegMore);
	print("   Aver. speckle no/feature: " + (NegMore/more));
	print("----------------------------------------------------");
	print(" Speckle Inspector developed by BioVoxxel/Dr. Jan Brocher, 2013 (v1.2)");
	selectWindow("Output");
	
	
	}
}
*/
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
// Difference of Gaussian, by Jan Brocher/BioVoxxel 2012
// Developed after a description from J. Russ (The Image Processing Handbook, 6th Ed.) 
//------------------------------------------------------------------------------

function DifferenceOfGaussian() {

	title=getTitle();
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	selectWindow(title);
	newtitle="DoG of " + title;
	
	Dialog.create("Gaussian Blur");
		Dialog.addMessage("Choose Gaussian Blur settings");
		Dialog.addNumber("smaller sigma:", 1);
		Dialog.addNumber("greater sigma (3-6x higher):", 3);
		Dialog.show();
		sig1 = Dialog.getNumber();
		sig2 = Dialog.getNumber();
		//factor = Dialog.getChoice();
	
	setBatchMode(true);
	run("Duplicate...", "title=DoG");
	rename(newtitle);
	run("Duplicate...", "title=intermediate");
	selectWindow("intermediate");
	run("Gaussian Blur...", "sigma=sig2");
	run("Select All");
	run("Copy");
	selectWindow(newtitle);
	run("Gaussian Blur...", "sigma=sig1");
	setPasteMode("Subtract");
	setBatchMode(false);
	run("Paste");
	exit();

}


//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//Difference of Median, by Jan Brocher 2012
//------------------------------------------------------------------------------

function DifferenceOfMedian() {

	title=getTitle();
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	selectWindow(title);
	
	Dialog.create("Median Blur");
		Dialog.addMessage("Choose Median Blur settings");
		Dialog.addNumber("sigma:", 2);
		sig = Dialog.getNumber();
		Dialog.show();
		
	setBatchMode(true);
	selectWindow(title);	
	run("Duplicate...", "title=DoMed");
	newtitle="DoMed of " + title;
	rename(newtitle);
	run("Median...", "sigma=sig");
	selectWindow(title);
	run("Select All");
	run("Copy");
	selectWindow(newtitle);
	setPasteMode("Difference");
	setBatchMode(false);
	run("Paste");
	exit();

}


//--------------------------------------------------------------------------------------------------
// The macro masks images and stacks with binary counterparts
// developed by Jan Brocher/BioVoxxel 2013
// first version 0.2 (26/04/2013)
// v0.3 Image selection changed to drop-down menu (14/05/2013)
//--------------------------------------------------------------------------------------------------

function AutoMasking() {
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }

	openImages=nImages;
	if(openImages==0) {
		exit("No open images found");
	}

	imageNames = newArray(nImages);
	for (i=0; i<imageNames.length; i++){
	        selectImage(i+1);
	        imageNames[i] = getTitle();
	}
	
	Dialog.create("Masking Setup");
		Dialog.addChoice("Select original image:", imageNames);
		Dialog.addChoice("Select mask image:", imageNames);
		Dialog.addChoice("Set transparent", newArray("white", "black"), "white");
		Dialog.show();
		name1=Dialog.getChoice();
		name2=Dialog.getChoice();
		transparence=Dialog.getChoice();
	
	setBatchMode(true);
	
	// "choose first image"	---------------------------------------------------------------------------------------

	selectWindow(name1);
	type1 = bitDepth();
	getDimensions(width1, height1, channels1, slices1, frames1);
	if(Stack.isHyperstack==true) { exit("does not work on hyperstacks"); }
	
	// "choose second image" ---------------------------------------------------------------------------------------	
	
	selectWindow(name2);
	dir = getDirectory("image");
	type2 = bitDepth();
	getDimensions(width2, height2, channels2, slices2, frames2);
	if(Stack.isHyperstack==true) { exit("does not work on hyperstacks"); }
	
	if (width1!=width2 || height1!=height2 || channels1!=channels2 || slices1!=slices2 || frames1!=frames2) { exit("Stacks need to have the same dimensions"); }
	else {
		n = 1;
		selectWindow(name1);
		run("Select None");
		run("Duplicate...", "title=["+name1+"-masked] duplicate channels=1-channels1 slices=1-slices1 frames=1-frames1");
		
		//try to make it work with hyperstacks
		//can use image calculator on hyperstacks but only transparent-zero available
		
		while (n<=slices1) {
			selectWindow(name1);
			if (type1!=24) { run("RGB Color"); }
			setSlice(n);
			setPasteMode("Copy");
			run("Select All");
			run("Copy");
			selectWindow(name1+"-masked");
			setSlice(n);
			run("Select All");
			run("Paste");
	
			selectWindow(name2);
			if (type2!=24) { run("RGB Color"); }
			setSlice(n);
			if (transparence=="white") {
				setPasteMode("Transparent-white");
			} else if (transparence=="black"){
				setPasteMode("Transparent-zero");
			}
			run("Select All");
			run("Copy");
			selectWindow(name1+"-masked");
			setSlice(n);
			run("Select All");
			run("Paste");
			n = n + 1;
		}
		selectWindow(name1+"-masked");
		setSlice(1);	
		setBatchMode(false);
	}
}

/**
 * ---------------------------------------------------------------------------------------
 * Automatic Threshold Check, developed by Jan Brocher/BioVoxxel 2013
 * v1.0 24-08-2012: first release Auto threshold plugin needs to be run separately
 * v1.1 08-05-2013: auto-read-in of montage size (thanks, Jerome), increased colors in output for better visibility
 * v1.2 14-05-2013: Image selection changed to drop-down menu
 * v2.0 27-10-2013: completely rewritten version, does all Auto Thresholds and Auto Local Thresholds automatically
 * and checks the thresholds quantitatively indicating the best threshold choices according
 * to a user defined definition of the darkest/brightest intensity value which still
 * should be recognized by the auto thresholds
 * v2.1 06-12-2013 includes also 3 new auto local thresholds from Gabriel Landini (18-11-2013)
 * v2.2 05-03-2014 improved performance during quantification
 * ---------------------------------------------------------------------------------------
 */

function ThresholdCheck() {
	//preparing macro and reading in data from original image
	requires("1.48h");
	openImages=nImages;
	if(openImages==0) {
		exit("No open images found");
	}
	original = getTitle();
	type = bitDepth();
	run("Select None");
	getDimensions(width, height, channels, slices, frames);
	getRawStatistics(area, mean, min, max, std, histogram);
	getLocationAndSize(LocX, LocY, LocW, LocH);
	if(channels>1 || slices>1 || frames>1) {
		exit("works only with single images");
	}
	if(type!=8) {
		exit("works only with 8-bit images");
	}
	if(is("Inverting LUT")) {
		showMessageWithCancel("Caution inverted LUT", "The image has an inverted LUT\nLUT will be automatically inverted");
		selectWindow(original);
		run("Invert LUT");
	}
		
	run("Select None");
	run("Overlay Options...", "stroke=black width=1 set");
	run("Conversions...", "scale weighted");

	//Threshold name definition array
	ThrNames = newArray("Default", "Huang", "Huang2", "Intermodes", "IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen", "Bernsen", "Contrast", "Mean (local)", "Median", "MidGrey", "Niblack", "Otsu (local)", "Phansalkar", "Sauvola");

	//initial dialog to choose thresholds and parameters
	var prevWhiteObj = call("ij.Prefs.get", "threshold.check.objects", true);
	var prevIgnoreBlack = call("ij.Prefs.get", "threshold.check.ignoreBlack", false);
	var prevIgnoreWhite = call("ij.Prefs.get", "threshold.check.ignoreWhite", false);
	var prevIncludeLocal = call("ij.Prefs.get", "threshold.check.local", false);
	var prevRadius = call("ij.Prefs.get", "threshold.check.radius", 15);
	var prevParam1 = call("ij.Prefs.get", "threshold.check.param1", 0);
	var prevParam2 = call("ij.Prefs.get", "threshold.check.param2", 0);
	var prevQuant = call("ij.Prefs.get", "threshold.check.quant", false);
	var prevExtendedQuant = call("ij.Prefs.get", "threshold.check.extendedQuant", false);
	var prevMontage = call("ij.Prefs.get", "threshold.check.montage", false);
	
	Dialog.create("Threshold Check v2.1 by BioVoxxel");
		Dialog.addCheckbox("bright objects on dark background", prevWhiteObj);
		Dialog.addCheckbox("ignore black (default=off)", prevIgnoreBlack);
		Dialog.addCheckbox("ignore white (default=off)", prevIgnoreWhite);
		Dialog.addMessage("_________________________________");
		Dialog.addCheckbox("include local thresholds", prevIncludeLocal);
		Dialog.setInsets(0, 20, 0);
		Dialog.addNumber("Radius (default=15)", prevRadius);
		Dialog.setInsets(0, 20, 0);
		Dialog.addNumber("Parameter 1 (default=0)", prevParam1);
		Dialog.setInsets(0, 20, 0);
		Dialog.addNumber("Parameter 2 (default=0)", prevParam2);
		Dialog.addMessage("_________________________________");
		Dialog.addCheckbox("Quantification (relative)", prevQuant);
		Dialog.addCheckbox("Extended quality measures", prevExtendedQuant);
		Dialog.addCheckbox("Test watershed-ability", false);
		Dialog.addCheckbox("Montage output (optional)", prevMontage);
		Dialog.addCheckbox("Remember current dialog settings", false);
		Dialog.show();
		whiteObj = Dialog.getCheckbox();
		ignoreBlack = Dialog.getCheckbox();
		ignoreWhite = Dialog.getCheckbox();
		includeLocal = Dialog.getCheckbox();
		radius = Dialog.getNumber();
		param1 = Dialog.getNumber();
		param2 = Dialog.getNumber();
		quant = Dialog.getCheckbox();
		extendedQuant = Dialog.getCheckbox();
		doWatershed = Dialog.getCheckbox();
		montage = Dialog.getCheckbox();
		settings = Dialog.getCheckbox();
		

	if(settings==true) {
		call("ij.Prefs.set", "threshold.check.objects", whiteObj);
		call("ij.Prefs.set", "threshold.check.ignoreBlack", ignoreBlack);
		call("ij.Prefs.set", "threshold.check.ignoreWhite", ignoreWhite);
		call("ij.Prefs.set", "threshold.check.local", includeLocal);
		call("ij.Prefs.set", "threshold.check.radius", radius);
		call("ij.Prefs.set", "threshold.check.param1", param1);
		call("ij.Prefs.set", "threshold.check.param2", param2);
		call("ij.Prefs.set", "threshold.check.quant", quant);
		call("ij.Prefs.set", "threshold.check.extendedQuant", extendedQuant);
		call("ij.Prefs.set", "threshold.check.montage", montage);
	}

	if(isOpen("Results")==1) {
		selectWindow("Results");
		run("Close"); 
	}
	//Define settings for bright/dark objects
	if(whiteObj==true) {
		white = " white";
		clickIntensity = "lowest";
	} else {
		white = "";
		clickIntensity = "highest";
	}
	
	if(includeLocal==true) {
		rounds = 26;
	} else {
		rounds = 17;
	}

	if(ignoreBlack==true) {
		ignoreBlack = " ignore_black";
	} else {
		ignoreBlack = "";
	}

	if(ignoreWhite==true) {
		ignoreWhite = " ignore_white";
	} else {
		ignoreWhite = "";
	}

	saturation = 1;
	
	if(quant==true) {
		selectWindow(original);
		run("Duplicate...", "title=Sensitivity definition");
		sensitivityDefinition = getTitle();
		setLocation(LocX, LocY, LocW, LocH);
		run("Enhance Contrast...", "saturated="+saturation+" normalize");
		setTool("point");
		waitForUser("make 1 (!) point selection in an area\nwith the "+clickIntensity+" intensity which\nshould still be recognized by the threshold\n\nthen press Ok");
				
		if(selectionType()!=10) {
			close(sensitivityDefinition);
			exit("need 1 point selection");
		} else {
			getSelectionBounds(sensitivityDefinitionX, sensitivityDefinitionY, widthSel, heightSel);
			close(sensitivityDefinition);
			setTool(0);
		}
	}
	setBatchMode(true);
	//Auto Threshold
	selectWindow(original);
	run("Auto Threshold", "method=[Try all]" + ignoreBlack + ignoreWhite + white);
	if(isOpen("Log")==1) {
		selectWindow("Log"); 
		run("Close");
	}
	selectWindow("Montage");
	run("Montage to Stack...", "images_per_row=5 images_per_column=4 border=1");
	rename("ThresholdCheck_"+original);
	AutoThreshold = getTitle();
	setSlice(20);
	run("Delete Slice");
	run("Delete Slice");
	run("Delete Slice");
	run("Canvas Size...", "width="+width+" height="+height+" position=Top-Left zero");
	close("Montage");
	selectWindow(AutoThreshold);
	//setBatchMode("show");
	
	if(includeLocal==true) {
		//Auto Local Threshold
		selectWindow(original);
		run("Auto Local Threshold", "method=[Try all] radius=" + radius + " parameter_1=" + param1 + " parameter_2=" + param2 + white);
		run("Montage to Stack...", "images_per_row=3 images_per_column=3 border=1");
		rename("AutoLocalThreshold");
		AutoLocalThreshold = getTitle();
		run("Canvas Size...", "width="+width+" height="+height+" position=Top-Left zero");
		close("Montage");
		
		run("Concatenate...", "  title=[ThresholdCheck_"+original+"] image1=["+AutoThreshold+"] image2=["+AutoLocalThreshold+"] image3=[-- None --]");
		//setBatchMode("show");
	}
	ThresholdStack = getTitle();
	
	if(doWatershed) {
		run("Watershed", "stack");
	}
	//produce color coded stack
	selectWindow(ThresholdStack);
	
	run("Select None");
	run("HiLo");
	run("RGB Color");

	selectWindow(original);
	run("Duplicate...", "title=[dup_"+original+"]");
	duplicate=getTitle();
	if(whiteObj==false) {
		run("Invert");
	}
	run("Green");
	run("Enhance Contrast...", "saturated="+saturation+" normalize");
	run("Select All");
	run("Copy");
	setPasteMode("Add"); 
	selectWindow(ThresholdStack);
	run("Select All");
	for (i=1;i<=rounds;i++) {
		setSlice(i);
		run("Paste");
	}

	
	if(quant==true) {
		//get sensitivity for over-thresholding		
		newImage("red", "8-bit white", width, height, 1);
		run("Red");
		run("RGB Color");
		run("Select All");
		run("Paste");
		run("HSB Stack");
		setSlice(1);
		makeRectangle(sensitivityDefinitionX-1, sensitivityDefinitionY-1, 3, 3); 
		getRawStatistics(nPixels, overSensitivityDefinition);
		close("red");

		//get sensitivity for under-thresholding
		newImage("blue", "8-bit white", width, height, 1);
		run("Blue");
		run("RGB Color");
		run("Select All");
		run("Paste");
		run("HSB Stack");
		setSlice(1);
		makeRectangle(sensitivityDefinitionX-1, sensitivityDefinitionY-1, 3, 3);  
		getRawStatistics(nPixels, underSensitivityDefinition);
		close("blue");

		close(duplicate);
				
		//run the quantification function
		ThresholdQuant(ThresholdStack);
	}

		
	selectWindow(ThresholdStack);
	fontSize = round(height/20);
	if(fontSize<10) {
		fontSize=10;
	}
	setFont("SansSerif", fontSize, "bold");
	setColor(255,255,255);
	for(o=0; o<nSlices; o++) {
		setSlice(o+1);
		drawString(ThrNames[o], 10, 10+fontSize);
	}
	setSlice(1);
	
	if(montage==true && includeLocal==true) {
		selectWindow(ThresholdStack);
		run("Make Montage...", "columns=5 rows=6 scale=1 first=1 last="+rounds+" increment=1 border=3 font=12");
		setBatchMode("show");
	} else if(montage==true && includeLocal==false) {
		selectWindow(ThresholdStack);
		run("Make Montage...", "columns=5 rows=4 scale=1 first=1 last="+rounds+" increment=1 border=3 font=12");
		setBatchMode("show");
	}
	selectWindow(AutoThreshold);
	setBatchMode(false);
	
	if(quant==true) {
		updateResults();
		IJ.renameResults("ThresholdCheck_"+original);
	}
	exit();
	
	function ThresholdQuant(colorCodedStack) {
		run("Select None");
		run("Options...", "iterations=1 count=1 black edm=Overwrite");
		run("Set Measurements...", "  area_fraction display redirect=None decimal=3");
		selectWindow(colorCodedStack);
		highestQuality = 0;
		rounds = nSlices;
		
		ThrNames = newArray("Default", "Huang", "Huang2", "Intermodes", "IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen", "Bernsen", "Contrast", "Mean (local)", "Median", "MidGrey", "Niblack", "Otsu (local)", "Phansalkar", "Sauvola");
		
		for(runs=1; runs<=rounds; runs++) {
			selectWindow(colorCodedStack);
			setSlice(runs);
			run("Duplicate...", "title=copyCodedStack");
			copyColorCoded = getTitle();
			selectWindow(copyColorCoded);
			run("HSB Stack");
			setSlice(3);
			run("Delete Slice");
			setSlice(2);
			run("Delete Slice");		
							
		// background
			selectWindow(copyColorCoded);
			setThreshold(round(underSensitivityDefinition), 170);
			run("Measure");
			setResult("Label", nResults-1, ThrNames[runs-1] + " (background):");
			
		// -------------
		
		// under
			//selectWindow(copyColorCoded);
			setThreshold(127, round(underSensitivityDefinition)-1);
			run("Measure");
			setResult("Label", nResults-1, ThrNames[runs-1] + " (under):");
		// -------------
		
		
		// positive
			//selectWindow(copyColorCoded);
			setThreshold(round(overSensitivityDefinition), 42);
			run("Measure");
			setResult("Label", nResults-1, ThrNames[runs-1] + " (positive):");
		// -------------
		
		// over
			//selectWindow(copyColorCoded);
			setThreshold(0, round(overSensitivityDefinition)-1);
			run("Measure");
			setResult("Label", nResults-1, ThrNames[runs-1] + " (over):");
		// -------------

			close(copyColorCoded);

			updateResults();
			valueBackground = getResult("%Area", nResults-4);
			valueUnder = getResult("%Area", nResults-3);
			valuePositive = getResult("%Area", nResults-2);
			valueOver = getResult("%Area", nResults-1);

			quality = (100*valuePositive)/(valueUnder + valuePositive + valueOver);
			
			if(extendedQuant) {
				sensitivity = (100*valuePositive)/(valuePositive+valueUnder);
				specificity = (100*valueBackground)/(valueBackground+valueOver);
				accuracy = (100*(valuePositive+valueBackground))/(valuePositive+valueBackground+valueOver+valueUnder);
				
				print(ThrNames[runs-1]);
				print("sensitivity: " + sensitivity);
				print("specificity: " + specificity);
				print("accuracy: " + accuracy);
				print("------------------------------------");
			}

			if(quality>highestQuality) {
				bestThreshold = ThrNames[runs-1];
				highestQuality = quality;
				bestUnder = valueUnder;
				bestOver = valueOver;
				bestThresholdIndex = runs;
			}

						
			//updateResults();
			setResult("Label", nResults, "quality =");
			setResult("%Area", nResults-1, quality);
			//updateResults();
			setResult("Label", nResults, "-----------------------------------");
			setResult("%Area", nResults-1, "---------------");
			updateResults();
	
		}
		
		setResult("Label", nResults, "bright objects:");
		setResult("%Area", nResults-1, whiteObj);
		setResult("Label", nResults, "saturation:");
		setResult("%Area", nResults-1, saturation);
		setResult("Label", nResults, "sensitivity definition location:");
		setResult("%Area", nResults-1, ""+round(sensitivityDefinitionX)+" / "+round(sensitivityDefinitionY));
		setResult("Label", nResults, "hue cut-off (under):");
		setResult("%Area", nResults-1, underSensitivityDefinition);
		setResult("Label", nResults, "hue cut-off (over):");
		setResult("%Area", nResults-1, overSensitivityDefinition);
		setResult("Label", nResults, "------------------------------");
		setResult("%Area", nResults-1, "---------------");
		setResult("Label", nResults, "best thresholds:");
		setResult("%Area", nResults-1, bestThreshold);
		for(bT=1; bT<=rounds; bT++) {
			
			testQuality = getResult("%Area", (bT*6)-2);
			if(testQuality == highestQuality && bT!=bestThresholdIndex) {
				setResult("Label", nResults, "best thresholds");
				setResult("%Area", nResults-1, ThrNames[bT-1]);
			}	
		}
		updateResults();
		setResult("Label", nResults, "------------------------------");
		setResult("%Area", nResults-1, "---------------");
	}
	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction stack display redirect=None decimal=3");
}




//---------------------------------------------------------------------------------------
// Flat-field background correction, jan Brocher/BioVoxxel 2013
//---------------------------------------------------------------------------------------

function FFBackgroundCorrection() {
	setBatchMode(true);
	openImages=nImages;
	if(openImages==0) {
		exit("No open images found");
	}

	imageNames = newArray(nImages);
	for (i=0; i<imageNames.length; i++){
	        selectImage(i+1);
	        imageNames[i] = getTitle();
	}

	Dialog.create("Setup");
		Dialog.addChoice("Select original image", imageNames);
		Dialog.addChoice("Select flat-field image:", imageNames);
		Dialog.show();
		original=Dialog.getChoice();
		flatField=Dialog.getChoice();
	
	// "choose original image"---------------------------------------------------------------------------------------
		
	selectWindow(original);
	type1 = bitDepth();
	getDimensions(width1, height1, channels1, slices1, frames1);
	getPixelSize(unit1, pixelWidth1, pixelHeight1);
	run("Select None");
	
	// "choose flat-field image" ---------------------------------------------------------------------------------------	
	
	selectWindow(flatField);
	type2 = bitDepth();
	getDimensions(width2, height2, channels2, slices2, frames2);
	getPixelSize(unit2, pixelWidth2, pixelHeight2);
	run("Select None");
	
	if (width1!=width2 || height1!=height2) { exit("images need to be of same dimensions"); }
	if (type1!=type2) { exit("images need to be of the same type"); }
	
	if(type1!=24) {
		setPasteMode("Copy");
		selectWindow(original);
		getRawStatistics(nPixels, mean, min, max, std, histogram);
		run("Calculator Plus", "i1=["+original+"] i2=["+flatField+"] operation=[Divide: i2 = (i1/i2) x k1 + k2] k1=mean k2=0 create");
		rename("FFCorr_"+original);
		//setBatchMode("show");
		setBatchMode(false);
		exit();
	} else if (type1==24) {
		selectWindow(original);
		run("Duplicate...", "title=[dup_"+original+"]");
		HSBOrig=getTitle();
		run("HSB Stack");
		setSlice(3);
		run("Duplicate...", "title=brightnessOrig");
		brightnessOrig = getTitle();
		getRawStatistics(nPixels, mean, min, max, std, histogram);
		
		selectWindow(flatField);
		run("Duplicate...", "title=[dup_"+flatField+"]");
		HSBFF=getTitle();
		run("HSB Stack");
		setSlice(3);
		run("Duplicate...", "title=brightnessFF");
		brightnessFF = getTitle();
		
		run("Calculator Plus", "i1=["+brightnessOrig+"] i2=["+brightnessFF+"] operation=[Divide: i2 = (i1/i2) x k1 + k2] k1=mean k2=0 create");
		result=getTitle();
		run("Select All");
		run("Copy");
		selectWindow(HSBOrig);
		setSlice(3);
		run("Select All");
		setPasteMode("Copy");
		run("Paste");
		run("RGB Color");
		rename("FFCorr_"+original);
		run("Select None");
		setBatchMode(false);
		exit();
	}
}

//---------------------------------------------------------------------------------------
// Pseudo flat-field background correction, jan Brocher/BioVoxxel 2014
//---------------------------------------------------------------------------------------

/*
function PseudoFFCorrection() {
	getDimensions(width, height, channels, slices, frames);
	type = bitDepth();
	setBatchMode(true);
	if(type!=24) {
		setPasteMode("Copy");
		original=getTitle();
		getStatistics(area, mean, min, max, std, histogram);
		run("Duplicate...", "title=Background");
		bckgr=getTitle();
		setBatchMode("show");
		run("Gaussian Blur...");
		run("Calculator Plus", "i1=["+original+"] i2=["+bckgr+"] operation=[Divide: i2 = (i1/i2) x k1 + k2] k1=mean k2=0 create");
		rename("BGCorr_"+original);
		close(bckgr);
		setBatchMode(false);
		exit();
	} else if (type==24) {
		original=getTitle();
		run("Duplicate...", "title=[dup_"+original+"]");
		HSB=getTitle();
		run("HSB Stack");
		setSlice(3);
		run("Duplicate...", "title=brightness");
		brightness = getTitle();
		getStatistics(area, mean, min, max, std, histogram);
		selectWindow(HSB);
		setSlice(3);
		run("Duplicate...", "title=background");
		bckgr = getTitle();
		setBatchMode("show");
		run("Gaussian Blur...");
		run("Calculator Plus", "i1=["+brightness+"] i2=["+bckgr+"] operation=[Divide: i2 = (i1/i2) x k1 + k2] k1=mean k2=0 create");
		result=getTitle();
		run("Select All");
		run("Copy");
		selectWindow(HSB);
		setSlice(3);
		run("Select All");
		setPasteMode("Copy");
		run("Paste");
		run("RGB Color");
		rename("BGCorr_"+original);
		run("Select None");
		close(bckgr);
		close(brightness);
		setBatchMode(false);
		exit();
	}
}

*/
//---------------------------------------------------------------------------------------
// Median Background Subtraction, jan Brocher/BioVoxxel 2014
// according to a description in Dunn et al., 2011, AJP Cell Physiology
// radius of the median filter should be chosen around size of largest non-background object
// first release v0.2 14-02-2014
//---------------------------------------------------------------------------------------

function MedianBackgroundSubtraction() {
	original=getTitle();
	getLocationAndSize(x, y, width, height);
	getPixelSize(unit, pixelWidth, pixelHeight);
	type = bitDepth();
	existingSelection = selectionType();
	if(type==24 || type==48) {
		exit("works only with grayscale images");
	}
	
	/* 
	// optional
	if(type==8) {
		initialOffset = 10;
	} else if(type==16) {
		initialOffset = 20;
	} else if(type==32) {
		initialOffset = 0.05;
	}
	*/
	
	Dialog.create("setup");
		Dialog.addNumber("median offset", 0);
		Dialog.addSlider("dilate", 0, 5, 0); 
		if(existingSelection==5) {
			Dialog.addCheckbox("subtraction plot", true);
		} else {
			Dialog.addCheckbox("subtraction plot", false);	
		}
		Dialog.addCheckbox("dark objects", false);
		Dialog.addCheckbox("keep original image", false);
		Dialog.show();
		medianOffset = Dialog.getNumber();
		dilation = Dialog.getNumber();
		showPlot = Dialog.getCheckbox();
		darkObjects = Dialog.getCheckbox();
		keepOriginal = Dialog.getCheckbox();

	if(darkObjects==true) {
		selectWindow(original);
		run("Invert");
		run("Restore Selection");
	}
	
	if(keepOriginal==true) {
		selectWindow(original);
		run("Duplicate...", "title=" + original + "-1");
		setLocation(x+(width/2), y, width, height);
		original = getTitle();
		if(existingSelection==5) {
			run("Restore Selection");
		}
	}
	
	if(showPlot==true) {
		setTool(4);
		if(selectionType()!=5) {
			waitForUser("make a straight line selection");
		}
		if(selectionType!=5) {
			exit("needs a straight line selection");
		} else {
			setPasteMode("Copy");
			selectWindow(original);
			originalPlot = getProfile();
			Array.getStatistics(originalPlot, originalPlotMin, originalPlotMax);
			toScaled(originalPlotMax);
			getStatistics(area, mean, min, max, std, histogram);
			lineLength = originalPlot.length;
			run("Duplicate...", "title=Background");
			bckgr=getTitle();
			setLocation(x+(width/2), y, width, height);
			run("Median...");
			run("Restore Selection");
			medianPlot = getProfile();
			
			if(dilation>0) {
				run("Maximum...", "radius=" + dilation);
			}
			
			if(medianOffset!=0) {
				run("Subtract...", "value=" + medianOffset);
			}
			medianPlotWithOffset = getProfile();
			
			imageCalculator("subtract", original, bckgr);
			run("Restore Selection");
			finalPlot = getProfile();
			close(bckgr);
		}
		Plot.create("MedSubBG_"+ original, "Distance (" + unit + ")", "Intensity", originalPlot);
		toUnscaled(originalPlotMax);
		Plot.setLimits(0, lineLength, 0, originalPlotMax);
		Plot.setColor("red");
		Plot.add("line", medianPlot);
		Plot.setColor("blue");
		Plot.add("line", finalPlot);
		
		if(medianOffset!=0 || dilation>0) {
			Plot.setColor("green");
			Plot.add("line", medianPlotWithOffset);
		}
		
		Plot.setColor("darkGray");
		Plot.show();
	} else {
		setPasteMode("Copy");
		selectWindow(original);
		run("Select None");
		run("Duplicate...", "title=Background");
		bckgr=getTitle();
		setLocation(x+(width/2), y, width, height);
		run("Median...");
		if(dilation>0) {
				run("Maximum...", "radius=" + dilation);
			}
		if(medianOffset > 0) {
			run("Subtract...", "value=" + medianOffset);
		}
		run("Restore Selection");
		imageCalculator("subtract", original, bckgr);
		close(bckgr);
	}
}

//---------------------------------------------------------------------------------------
// Scaled Intensity Plots, by Jan Brocher
// first release: v0.1, 14-02-2014
//---------------------------------------------------------------------------------------

function ScaledIntensityPlots() {

	original = getTitle();
	type=bitDepth();
	getRawStatistics(length, mean, intMin, intMax);
	getPixelSize(unit, pixelWidth, pixelHeight);
	getSelectionBounds(xSelection, ySelection, widthSelection, heightSelection);
	if(selectionType()==0 || selectionType()==5 || selectionType()==6 || selectionType()==7) {

		if(selectionType()==0) {
			rectSelection = "pos";
		} else {
			rectSelection = "neg";
		}
		if(type==8 || type==24) {
			initialYmax = 255;
		} else if(type==16) {
			initialYmax = 65535;
		} else if(type==32 && intMax > 1) {
			initialYmax = intMax;
		} else {
			initialYmax = 1;
		}
		
		openImages=nImages;
		plotImages=nImages;
		plotArray=newArray(nImages);
		p = 0;
		if(openImages==0) {
			exit("No open images found");
		}
		for (i=0; i<openImages; i++){
			selectImage(i+1);
			if(getInfo("window.type")!="Plot") {
				plotImages = plotImages - 1;
			} else {
				plotArray[p] = getImageID();
				p = p + 1;
			}
		}
		
		if(plotImages>0) {
			imageNames = newArray(plotImages+1);
			imageNames[0] = "No";
			for (n=1; n<plotImages+1; n++){
			        selectImage(plotArray[n-1]);
			        imageNames[n] = getTitle();
			}
		} else {
			addToExistingPlot = "No";
		}
		
		var prevDirection = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevDirection", "horizontal");
		var prevWidth = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevWidth", 450);
		var prevHeight = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevHeight", 200);
		var prevMinLimit = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevMinLimit", 0);
		var prevMaxLimit = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevMaxLimit", 255);
		var prevGridLines = call("ij.Prefs.get", "BVTB.scaledIntensityPlots.prevGridLines", true);
		
		Dialog.create("Plot normalizer");
			Dialog.setInsets(0, 5, 0);
			Dialog.addMessage("Image: " + original);
			if(rectSelection=="pos") {
				Dialog.addRadioButtonGroup("plotting direction", newArray("horizontal", "vertical"), 1, 2, prevDirection);
			}
			Dialog.addNumber("Window width", prevWidth);
			Dialog.addNumber("Window height", prevHeight);
			Dialog.addNumber("Min. int. limit", prevMinLimit);
			Dialog.addNumber("Max. int. limit", prevMaxLimit);
			Dialog.setInsets(0, 5, 0);
			Dialog.addMessage("Max. int. value: " + intMax);
			Dialog.addChoice("Color", newArray("black", "red", "green", "blue", "cyan", "magenta", "yellow", "lightGray", "gray", "darkGray", "orange", "pink"), "black");
			Dialog.addChoice("Look", newArray("line", "circles", "boxes", "triangles", "crosses", "dots", "x", "error bars"), "line");
			if(plotImages>0) {
				Dialog.addChoice("add to existing plot", imageNames, "No"); 
			}
			Dialog.addCheckbox("Draw grid lines", prevGridLines);
			Dialog.addCheckbox("reset to default values after run", false);
			Dialog.show();
			if(rectSelection=="pos") {
				direction = Dialog.getRadioButton();
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevDirection", direction);
			}
			windowWidth = Dialog.getNumber();
			call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevWidth", windowWidth);
			windowHeight = Dialog.getNumber();
			call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevHeight", windowHeight);
			yMin = Dialog.getNumber();
			call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevMinLimit", yMin);
			yMax = Dialog.getNumber();
			call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevMaxLimit", yMax);
			color = Dialog.getChoice();
			look = Dialog.getChoice();
			if(plotImages>0) {
				addToExistingPlot = Dialog.getChoice();
			}
			grid = Dialog.getCheckbox();
			call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevGridLines", grid);
			default = Dialog.getCheckbox();

			if(default==true) {
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevDirection", "horizontal");
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevWidth", 450);
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevHeight", 200);
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevMinLimit", 0);
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevMaxLimit", initialYmax);
				call("ij.Prefs.set", "BVTB.scaledIntensityPlots.prevGridLines", true);				
			}

		if(grid==true) {
				setGrid = "draw ";
			} else {
				setGrid = "";
			}

		if(rectSelection=="pos") {
			if(direction=="horizontal") {
				selectWindow(original);
				intensityValues = getProfile();
			} else if(direction=="vertical") {
				selectWindow(original);
				setKeyDown("alt"); 
				intensityValues = getProfile();
				setKeyDown("none");
			}
		} else {
			selectWindow(original);
			intensityValues = getProfile();
		}	
		
		if(plotImages>0) {
			if(addToExistingPlot!="No") {
				selectWindow(addToExistingPlot);
				Plot.getValues(existingX, existingY);
				Array.getStatistics(existingX, existingMin, existingMax);
				metadata = getMetadata("Info");
				if(metadata=="") {
					exit("destination plot was not created\nwith scaled intensity plots tool.\nnecessary metadata not existing");
				} else if(substring(metadata, 6, 7)=="1") {
					setGrid = "draw";
				} else if(substring(metadata, 6, 7)=="0") {
					setGrid = "";
				}
			} else {
				existingMax = 5000000000;
			}
		} else {
			existingMax = 5000000000;
		}
		run("Profile Plot Options...", "width="+windowWidth+" height="+windowHeight+" minimum=0 maximum=0 fixed interpolate "+setGrid+" sub-pixel");
		if(plotImages==0) {
			Plot.create("Plot of "+original, "Distance (pixel)", "Intensity");
		} else if(plotImages>0 && addToExistingPlot=="No") {
			addSuffix = 0;
			for(n=0; n<=plotImages; n++) {
				if(imageNames[n]=="Plot of "+original) {
					addSuffix = addSuffix + 1;
				}
				for(epn=0; epn<=plotImages; epn++) {
					if(endsWith(imageNames[n], addSuffix)==true) {
						addSuffix = addSuffix + 1;
					}
				}
			}
			newPlotName = "Plot of "+original+"-"+addSuffix;
				
			Plot.create(newPlotName, "Distance (pixel)", "Intensity");
		} else {
			Plot.create("TransientPlot", "", "");
		}
		
		if(rectSelection=="neg") {
			if(length>existingMax && addToExistingPlot!="No") {
				length = existingMax+1;
			}
			Plot.setLimits(0, length, yMin, yMax);
		} else if(rectSelection=="pos" && direction=="horizontal") {
			if(widthSelection>existingMax && addToExistingPlot!="No") {
				widthSelection = existingMax+1;
			}
			Plot.setLimits(0, widthSelection, yMin, yMax);
		} else if(rectSelection=="pos" && direction=="vertical") {
			if(heightSelection>existingMax && addToExistingPlot!="No") {
				heightSelection = existingMax+1;
			}
			Plot.setLimits(0, heightSelection, yMin, yMax);
		}
		Plot.setFrameSize(windowWidth, windowHeight);
		Plot.setColor(color);
		Plot.add(look, intensityValues);
		Plot.show();
		currentPlot = getTitle();
		//set plot metadata
		setMetadata("Info", "grid: "+grid+"\nwindow_width: "+windowWidth+"\nwindow_height: "+windowHeight+"\nmin. int. limit: "+yMin+"\nmax. int. limit: "+yMax);

		if(addToExistingPlot!="No") {
			selectWindow(currentPlot);
			run("Select All");
			run("Copy");
			run("Select None");
			selectWindow(addToExistingPlot);
			run("RGB Color");
			setPasteMode("Transparent-white");
			run("Select All");
			run("Paste");
			run("Select None");
			setPasteMode("Copy");
			close(currentPlot);
		}
		
	} else {
		exit("line or rectangular selection needed");
	}
}


/**
 * Stack Line Plots,  developed by Jan Brocher/BioVoxxel 2014
 * v0.1 first release (05-03-14)
 * 
 **/

function StackLinePlots() {
	
	if(isKeyDown("shift")) {
		alt= true;
	} else {
		alt = false;
	}
	setBatchMode(true);
	original = getTitle();
	getLocationAndSize(x, y, width, height);
	type = bitDepth();
	if(type>16) {
		exit("works only with 8- and 16-bit images");
	}
	
	getDimensions(width, height, channels, slices, frames);
	
	if(slices>1 && frames>1) {
		Dialog.create("Multi plot setup");
			Dialog.addChoice("select dimension", newArray("slices", "frames"), "slices");
			Dialog.show();
			dim = Dialog.getChoice();
			if(dim=="slices") {
				dimension = slices;
			} else {
				dimension = frames;
			}
	} else if(slices>1 && frames<2) {
		dimension = slices;
		dim = "slices";
	}  else if(slices<2 && frames>1) {
		dimension = frames;
		dim = "frames";
	}
	
	
	
	if(slices<2) {
		exit("Stack needed");
	}
	selection = Roi.getType;
	
	if(selection!="freeline" && selection!="line" && selection!="polyline") {
		exit("line selection needed");
	}
	getSelectionCoordinates(xpoints, ypoints);
	toUnscaled(xpoints, ypoints);
	
	yValues = newArray(xpoints.length);
	
	selectWindow(original);
	setBatchMode("Hide");
	
	if(alt==false) {
		yMax=0;
		for(s=1; s<=dimension; s++) {
			if(dim=="slices") {
				Stack.setSlice(s);
			} else {
				Stack.setFrame(s);
			}
			yValues = getProfile();
			Array.getStatistics(yValues, min, max, mean, stdDev);
			if(max>yMax) {
				yMax=max;
			}
		}
	} else if(alt==true) {
		yMax = pow(2, type) - 1;
	}
	
	for(s=1; s<=dimension; s++) {
		selectWindow(original);
		if(dim=="slices") {
			Stack.setSlice(s);
		} else {
			Stack.setFrame(s);
		}
		yValues = getProfile();
		Plot.create("Plot of " + original, "line length", "pixel intensity", yValues);
		Plot.setLimits(0, yValues.length, 0, yMax);
		Plot.show();
	}
	
	run("Images to Stack", "name=[PlotStack_"+original+"] title=[Plot of] use");
	setBatchMode("exit and display");
	selectWindow(original);
	setLocation(x, y);

}


/*
//-------------------------------------------------------------------------------------------------------------------------------------
// Contrast Detection, developed by Jan Brocher/BioVoxxel 2013
// deprecated
//-------------------------------------------------------------------------------------------------------------------------------------


function ContrastDetection() {
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	setPasteMode("Copy");
	original=getTitle();
	getDimensions(width, height, channels, slices, frames);
	type=bitDepth();
	
	if(slices>1) {
		exit("works only with individual images so far");
	}
	
	Dialog.create("Contrast Setup");
		Dialog.addNumber("radius", 1);
		Dialog.addCheckbox("enhance edges in original", false);
		Dialog.addCheckbox("enhance contrast", true);
		Dialog.addNumber("contrast saturation", 1);
		Dialog.addCheckbox("final maxima filter", false);
		Dialog.addCheckbox("inverse", false);
		Dialog.show();
		r=Dialog.getNumber();
		sharpen=Dialog.getCheckbox();
		contrast=Dialog.getCheckbox();
		satur=Dialog.getNumber();
		finalFilter=Dialog.getCheckbox();
		inverse=Dialog.getCheckbox();
		
	setBatchMode(true);
	run("Duplicate...", "title=Minimum");
	run("Conversions...", "scale weighted");
	if(type==24) {
		run("8-bit");	
	}
	Minimum=getTitle();
	run("Duplicate...", "title=Maximum");
	if(type==24) {
		run("8-bit");	
	}
	Maximum=getTitle();
	selectWindow(Minimum);
	run("Minimum...", "radius=r");
	selectWindow(Maximum);
	run("Maximum...", "radius=r");
	setPasteMode("Difference");
	selectWindow(original);

	run("Select All");
	run("Copy");
	selectWindow(Minimum);
	run("Paste");
	selectWindow(Maximum);
	run("Paste");

	setPasteMode("Subtract");
	if(inverse==true || sharpen==true) {
		selectWindow(Minimum);
	} else {
		selectWindow(Maximum);
	}
	run("Select All");
	run("Copy");
	if(inverse==true || sharpen==true) {
		selectWindow(Maximum);
		rename(original + "_Minima_r("+r+")");
	} else {
		selectWindow(Minimum);
		rename(original + "_Maxima_r("+r+")");
	}
	resultWindow=getTitle();
	run("Paste");
	if(sharpen==true) {
		selectWindow(resultWindow);
		run("Select All");
		run("Copy");
		selectWindow(original);
		run("Duplicate...", "title=Enhanced_"+original);
		run("Select All");
		run("Paste");
	}
	if(contrast==true && sharpen==false) {
		selectWindow(resultWindow);
		run("Enhance Contrast...", "saturated=satur normalize");
	}
	if(finalFilter==true) {
		run("Minimum...", "radius=0.5");
	}
	setBatchMode(false);

}
*/

//-----------------------------------------------------------------------------------------------------------------------------------------
//Hyperstack Color-coding
//Based on the idea of the temporal color coding from the macro of Kota Miura and Johannes Schindelin
//Offers the choice to code time or volume in a hyperstack
//Keeps the color coded Hyperstack in addition to a color coded Z-projection stack. Z-Projection type can be selected
//Author: Jan Brocher/BioVoxxel, 2013
//Version: 0.1 (25/04/2013)
//version 0.2 (07/06/2021), thanks to Jan Valečka added complete LUT support
//-----------------------------------------------------------------------------------------------------------------------------------------

function HyperstackColorCoding() {
	//read input
	original=getTitle();
	type=Stack.isHyperstack;
	BitDepth=bitDepth();
	lutPanel = getList("LUTs");
	previouslyChosenLut = call("ij.Prefs.get", "hyperstack.color.coding.lut", "Fire");
	run("Select None");
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	if (BitDepth!=8) {
		exit("works only with 8-bit images");
	}
	getLut(r, g, b);
	getDimensions(width, height, channels, slices, frames);
	if(channels>1) {
		exit("does not work with multi-channel stacks");
	}
	setPasteMode("Copy");
	
	//Setup dialog
	Dialog.create("Dimension Choice");
		if(type==true) {
			Dialog.addChoice("Color code", newArray("Time", "Volume"), "Time");
		} else if (slices==1) {
			Dialog.addChoice("Color code", newArray("Time"));
		} else if (frames==1) {
			Dialog.addChoice("Color code", newArray("Volume"));
		}
		Dialog.addChoice("LUT", lutPanel, previouslyChosenLut);
		if(slices>1) {
			Dialog.addCheckbox("Z-Projection", true);
			Dialog.addChoice("Projection type", newArray("Average Intensity", "Max Intensity", "Min Intensity", "Sum Slices", "Standard Deviation", "Median"), "Max Intensity");
			Dialog.addCheckbox("All time frames", true);
		}
		Dialog.addCheckbox("Calibration bar", false);
		Dialog.show();
		Dimension=Dialog.getChoice();
		LUT=Dialog.getChoice();
			call("ij.Prefs.set", "hyperstack.color.coding.lut", LUT);
			
		if(slices>1) {
			ZProject=Dialog.getCheckbox();
			ProjectType=Dialog.getChoice();
			ProjectionFrames=Dialog.getCheckbox();
		} else {
			ZProject=false;
		}
		CalBar=Dialog.getCheckbox();
	
	//creation of output stack
	run("Hyperstack...", "title=Colored_"+Dimension+"_"+original+" type=RGB display=Composite width="+width+" height="+height+" channels="+channels+" slices="+slices+" frames="+frames);
	output=getTitle();
	
	selectWindow(original);
	run(LUT);
	getLut(Oreds, Ogreens, Oblues);
	
	//calling the color coding function
	ColorCoding();
	if (CalBar==true) {
		CalibrationBar();
	}
	exit();
	
	function ColorCoding() {
		
		if (Dimension=="Time") {
			n=frames;
			runs=slices;
		} else if (Dimension=="Volume") {
			n=slices;
			runs=frames;
		}
	
		reds=newArray(256);
		greens=newArray(256);
		blues=newArray(256);
		setBatchMode(true);
		for (hyper=1; hyper<=runs; hyper++) {
		
			for (i=1; i<=n; i++) {
				if (Dimension=="Time") {
					Stack.setSlice(hyper);
					Stack.setFrame(i);
				} else if (Dimension=="Volume") {
					Stack.setFrame(hyper);
					Stack.setSlice(i);
				}
				
				color=floor((255/n)*i);
				for (loop=0; loop<256; loop++) {
					f=(loop/255);
					reds[loop]=round(Oreds[color]*f);
					greens[loop]=round(Ogreens[color]*f);
					blues[loop]=round(Oblues[color]*f);
				}
				
				selectWindow(original);
				if (Dimension=="Time") {
					Stack.setSlice(hyper);
					Stack.setFrame(i);
				} else if (Dimension=="Volume") {
					Stack.setFrame(hyper);
					Stack.setSlice(i);
				}
				setLut(reds, greens, blues);
				run("Select All");
				run("Copy");
				selectWindow(output);
				if (Dimension=="Time") {
					Stack.setSlice(hyper);
					Stack.setFrame(i);
				} else if (Dimension=="Volume") {
					Stack.setFrame(hyper);
					Stack.setSlice(i);
				}
				run("Paste");
			}
		}
		Stack.setFrame(1);
		Stack.setSlice(1);
		if (ZProject==true) {
			selectWindow(output);
			if(ProjectionFrames==true) {
				run("Z Project...", "start=1 stop="+slices+" projection=["+ProjectType+"] all");
			} else {
				run("Z Project...", "start=1 stop="+slices+" projection=["+ProjectType+"]");
			}
		}
		setBatchMode(false);
		selectWindow(original);
		setLut(r, g, b);
		Stack.setFrame(1);
		Stack.setSlice(1);
	}
	
	
	function CalibrationBar() {
		if (Dimension=="Time") {
			n=frames;
		} else if (Dimension=="Volume") {
			n=slices;
		}
		newImage("Calibration Bar", "8-bit Ramp", 256, 35, 1);
		run(LUT);
		setBackgroundColor(255, 255, 255);
		run("RGB Color");
		run("Canvas Size...", "width=256 height=50 position=Top-Center");
		setFont("SansSerif", 12, "bold");
		setColor(0, 0, 0);
		if (Dimension=="Time") {
			drawString("1", 2, 49);
			if(n<100) {
				drawString(n, 240, 49);
			} else {
				drawString(n, 230, 49);
			}
		} else if (Dimension=="Volume") {
			run("Rotate 90 Degrees Right");
			drawString("1", 1, 13);
			drawString(n, 1, 255);
			
		}
		
	}
}

//-----------------------------------------------------------------------------------------------------------------------------------------
// Neighbor Analysis
// Gives a color coded output regarding the number of neighbors of each particle in a binary image
// Author: Jan Brocher/BioVoxxel, 2013
// Version: 0.4 (31/01/2014)
// version: 0.5 (04/04/2014) Thanks to Gabriel Landini improved time performance using the floodFill command
// version: 0.6 (02/11/2016) thanks to Dennis Defoe, edge particles can be visually eliminated but still be included in the analysis as neighbors
// version: 0.7 (20/09/2019) Thanks to "mkemperl" bugfix for non-showing zero-neighbor particles
//-----------------------------------------------------------------------------------------------------------------------------------------

function NeighborAnalysis() {
	
	original=getTitle();
	type=is("binary");
	if(type==false) { exit("works only with 8-bit binary images"); }
	getDimensions(width, height, channels, slices, frames);
	run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	
	//Setup
	Dialog.create("Analysis Setup");
		Dialog.addChoice("Particle color", newArray("white", "black"), "white");
		Dialog.addChoice("Analysis method", newArray("Voronoi", "UEP Voronoi", "Centroid Neighborhood", "Particle Neighborhood"), "Voronoi");
		Dialog.addNumber("Neighborhood radius (pixel)", 0);
		Dialog.addCheckbox("do watershed first", false);
		Dialog.addString("Size (pixel^2)", "0-Infinity");
		Dialog.addString("Circularity", "0.00-1.00");
		Dialog.addCheckbox("visually exclude edge particles", false);
		Dialog.addCheckbox("exclude edge particles", false);
		Dialog.addCheckbox("exclude inclusion particles", false);
		Dialog.addCheckbox("produce calibration bar", true);
		Dialog.addCheckbox("plot neighbor distribution", true);
		Dialog.addHelp("www.biovoxxel.de/macros.html");
		Dialog.show();
		color=Dialog.getChoice();
		method=Dialog.getChoice();
		hoodRadius=Dialog.getNumber();
		watershed=Dialog.getCheckbox();
		size=Dialog.getString();
		circularity=Dialog.getString();
		dontVisualizeEdges=Dialog.getCheckbox();
		excludeEdges=Dialog.getCheckbox();
		excludeInclusionParticles=Dialog.getCheckbox();
		calibrationbar=Dialog.getCheckbox();
		createPlot=Dialog.getCheckbox();
	
	selectWindow(original);
	run("Select None");
	if(color=="black") {
		run("Invert");
	}
	if(excludeEdges==true) {
		edges="exclude";
	} else {
		edges="";	
	} 
	//prepare original image for analysis
	run("Analyze Particles...", "size="+size+" circularity="+circularity+" show=Masks "+edges+" clear");
	run("Invert LUT");
	rename(original+"-1");
	original=getTitle();
	
	//*******************************************************************
	setBatchMode(true);
	
	if(method=="Voronoi") {
		run("Duplicate...", "title=[V-Map_"+original+"]");
		voronoi=getTitle();
	} else if(method=="UEP Voronoi") {
		run("Duplicate...", "title=[UEP-V-Map_"+original+"]");
		voronoi=getTitle();
	} else if(method=="Centroid Neighborhood" || method=="Particle Neighborhood") {
		run("Duplicate...", "title=[NbHood_"+original+"]");
		neighborhood=getTitle();
	}
	
	//initial particle watershed if activated
	if(watershed==true) {
		run("Watershed");
	}
	
	//if method==Voronoi
	if(method=="Voronoi") {
		//Analyze voronoi particle number
		selectWindow(voronoi);
		run("Set Measurements...", "  centroid redirect=None decimal=3");
		run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
		//define variables
		initialParticles=nResults;
		X=newArray(nResults);
		Y=newArray(nResults);
		neighborArray=newArray(nResults);
		neighbors=0;
		mostNeighbors=0;
		//run voronoi
		run("Voronoi");
		setThreshold(1, 255);
		setOption("BlackBackground", true);
		run("Convert to Mask");
		run("Invert");
		//retveive particle coordinates
		for(l=0; l<initialParticles; l++) {
			X[l]=getResult("XStart", l);
			Y[l]=getResult("YStart", l);
		}
	
		//set measurements
		run("Set Measurements...", " redirect=None decimal=3");
		
		//analyze neighbors
		selectWindow(voronoi);	
		for(i=0; i<initialParticles; i++) {
			doWand(X[i],Y[i], 0, "8-connected");
			run("Enlarge...", "enlarge=2 pixel");
			run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
			neighbors = nResults-1;
			neighborArray[i]=neighbors;
			if(neighbors>mostNeighbors) {
				mostNeighbors=neighbors;
			}
		}
	}
	
	if(method=="UEP Voronoi") {
		//create ultimate points
		selectWindow(voronoi);
		run("Ultimate Points");
		setThreshold(1, 255);
		setOption("BlackBackground", true);
		run("Convert to Mask");
		//analyze UEP number
		run("Set Measurements...", "  centroid redirect=None decimal=3");
		run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
		//define variables
		initialParticles=nResults;
		X=newArray(nResults);
		Y=newArray(nResults);
		neighborArray=newArray(nResults);
		neighbors=0;
		mostNeighbors=0;
		//run voronoi
		run("Voronoi");
		setThreshold(1, 255);
		setOption("BlackBackground", true);
		run("Convert to Mask");
		run("Invert");
		//retveive particle coordinates
		for(l=0; l<initialParticles; l++) {
			X[l]=getResult("XStart", l);
			Y[l]=getResult("YStart", l);
		}
	
		//set measurements
		run("Set Measurements...", "  redirect=None decimal=3");
		
		//analyze neighbors
		selectWindow(voronoi);	
		for(i=0; i<initialParticles; i++) {
			doWand(X[i],Y[i], 0, "8-connected");
			run("Enlarge...", "enlarge=2 pixel");
			run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
			neighbors = nResults-1;
			neighborArray[i]=neighbors;
			if(neighbors>mostNeighbors) {
				mostNeighbors=neighbors;
			}
		}
	}
	
	//if method==centroid neighborhood
	if(method=="Centroid Neighborhood") {
		selectWindow(neighborhood);
		run("Set Measurements...", "  centroid redirect=None decimal=3");
		run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing display clear record");
		//define variables
		initialParticles=nResults;
		X=newArray(nResults);
		Y=newArray(nResults);
		centroidX=newArray(nResults);
		centroidY=newArray(nResults);

		neighborArray=newArray(nResults);
		neighbors=0;
		mostNeighbors=0;
		//retveive particle coordinates
		for(l=0; l<initialParticles; l++) {
			X[l]=getResult("XStart", l);
			Y[l]=getResult("YStart", l);
			centroidX[l]=getResult("X", l);
			centroidY[l]=getResult("Y", l);

			toUnscaled(X[l], Y[l]);
			toUnscaled(centroidX[l], centroidY[l]);
		}
		//prepare selector image
		setForegroundColor(255, 255, 255);
		setBackgroundColor(0, 0, 0);
		run("Set Measurements...", " centroid redirect=None decimal=3");
		//run("Wand Tool...", "mode=8-connected tolerance=0");
		run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
			
		for(hood=0; hood<initialParticles; hood++) {
			//create selector neighborhood
			selectWindow(neighborhood);
			run("Select None");
			run("Duplicate...", "title=[Selector_"+original+"]");
			selector=getTitle();
			fillOval(centroidX[hood]-hoodRadius, centroidY[hood]-hoodRadius, (hoodRadius*2), (hoodRadius*2));
			run("Select None");
			doWand(X[hood], Y[hood], 0, "8-connected");
			selectWindow(neighborhood);
			run("Restore Selection");
			run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
			neighbors = nResults-1;
			neighborArray[hood]=neighbors;
			if(neighbors>mostNeighbors) {
				mostNeighbors=neighbors;
			}
			
			close(selector);
		}
		
	}
	
	
	//if method==particle neighborhood
	if(method=="Particle Neighborhood") {
		selectWindow(neighborhood);
		run("Set Measurements...", "  centroid redirect=None decimal=3");
		run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
		//define variables
		initialParticles=nResults;
		X=newArray(nResults);
		Y=newArray(nResults);
		neighborArray=newArray(nResults);
		neighbors=0;
		mostNeighbors=0;
		//retveive particle coordinates
		for(l=0; l<initialParticles; l++) {
			X[l]=getResult("XStart", l);
			Y[l]=getResult("YStart", l);
		}
		//prepare selector image
		setForegroundColor(255, 255, 255);
		setBackgroundColor(0, 0, 0);
		run("Set Measurements...", " centroid redirect=None decimal=3");
		run("Wand Tool...", "mode=8-connected tolerance=0");
		run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
			
		if (excludeInclusionParticles) {	
			selectWindow(neighborhood);
			run("Select None");
			run("Duplicate...", "title=[FilledHoles_"+original+"]");
			filesHoles = getTitle();
			run("Fill Holes");
		}

		
		for(hood=0; hood<initialParticles; hood++) {
			//create selector neighborhood
			selectWindow(neighborhood);
			run("Select None");
			run("Duplicate...", "title=[Selector_"+original+"]");
			selector=getTitle();
			doWand(X[hood], Y[hood], 0, "8-connected");
			//print(hood + "(" + X[hood]+"/"+ Y[hood] + ")");
			run("Enlarge...", "enlarge="+hoodRadius + " pixel");
			run("Fill");
			run("Select None");
			doWand(X[hood], Y[hood], 0, "8-connected");

			if (excludeInclusionParticles) {
				selectWindow(filesHoles);
			} else {
				selectWindow(neighborhood);
			}
			
			run("Restore Selection");
			run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
			neighbors = nResults-1;
			neighborArray[hood]=neighbors;
			if(neighbors>mostNeighbors) {
				mostNeighbors=neighbors;
			}
			close(selector);
		}
		
	}

	if(mostNeighbors==0) {
		exit("no neighbors detected\ndid you choose the correct particle color?");
	}
	//*******************************************************************
	
	
	//Color coded original features
	

	selectWindow(original);
	if(method=="Voronoi") {
		run("Duplicate...", "title=[Voronoi_"+original+"]");
	} else if(method=="UEP Voronoi") {
		run("Duplicate...", "title=[UEP-V_"+original+"]");
	} else if(method=="Centroid Neighborhood") {
		run("Duplicate...", "title=[C-NbHood_"+hoodRadius+"_"+original+"]");
	} else if(method=="Particle Neighborhood") {
		run("Duplicate...", "title=[P-NbHood_"+hoodRadius+"_"+original+"]");
	}
	particles=getTitle();
	if(watershed==true) {
		run("Watershed");
	}
	selectWindow(particles);
	
	for(mark=0; mark<initialParticles; mark++) {
		markValue=neighborArray[mark];
		if(markValue==0) {
			doWand(X[mark],Y[mark], 0, "8-connected");
			Roi.setStrokeColor(0);
			run("Add Selection...");
			run("Select None");
		}
		setForegroundColor(markValue, markValue, markValue);
		floodFill(X[mark],Y[mark], "8-connected");
		
	}
	
	run("Select None");		
	run("glasbey");
	setBatchMode("show");
		
	//visually eliminate edge particles (but count them as neighbors)
	if(dontVisualizeEdges) {
		selectWindow(original);
		run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing exclude clear record");
		visibleParticleNumber = nResults;
	}
	

	//create distribution plot
	if(createPlot==true) {
		if(dontVisualizeEdges) {
			selectWindow(original);
			run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 pixel show=Masks exclude clear record add");
			roiManager("Show None");
			masksWithoutEdgeParticles = getTitle();
			run("Select All");
			run("Copy");
			selectWindow(particles);
			setPasteMode("Transparent-white");
			run("Paste");
			setPasteMode("Copy");
			colorIndex = 0;
			neighborList = newArray(mostNeighbors+1);
			for(roi=0; roi<roiManager("Count"); roi++) {
				selectWindow(particles);
				roiManager("Select", roi);
				getStatistics(areaNotNeeded, colorIndex);
				neighborList[colorIndex] = neighborList[colorIndex] + 1;
			}
			particleCount = roiManager("Count");
		} else {
			neighborList = newArray(mostNeighbors+1);
			Array.fill(neighborList, 0);
			for(num=0; num<initialParticles; num++) {
				nextNeighbor = neighborArray[num];
				if(nextNeighbor>0) {
					neighborList[nextNeighbor] += 1;
				} else {
					neighborList[0] += 1;

				}
			}
			particleCount = initialParticles;
		}
		
		
		Plot.create("Distribution: " + particles, "neighbors", "count", neighborList);
		Plot.addText("particles (total) = " + particleCount, 0.01, 0.1);
		setBatchMode("show");
	}
	
	close(original);
	
	//Calibration Bar
	if(calibrationbar==true) {
		stepsize=floor(256/mostNeighbors);
		newImage("Calibration_"+original, "8-bit Black", (stepsize*mostNeighbors+stepsize), 30, 1);
		w=getWidth();
		step=0;
		for(c=0; c<=mostNeighbors+1; c++) {
			makeRectangle(step, 0, step+stepsize, 30);
			setForegroundColor(c, c, c);
			run("Fill");
			step=step+stepsize;
		}
		run("Select None");
		run("glasbey");
		run("RGB Color");
		setForegroundColor(255, 255, 255);
		setBackgroundColor(0, 0, 0);
		run("Canvas Size...", "width="+w+" height=50 position=Top-Center");
		if(mostNeighbors>9) { 
			offset=15;
		} else {
			offset=10;
		}
		drawString("0", 2, 48);
		drawString(mostNeighbors, w-offset, 48);
	}
	setBatchMode(false);
	exit();

}

//-----------------------------------------------------------------------------------------------------------------------------------------

/*
function ParticleDistribution2D() {
	original = getTitle();
	getSelectionBounds(xPos, yPos, width, height);
	run("Options...", "iterations=1 count=1 black edm=Overwrite");
	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction redirect=None decimal=3");
	
	Dialog.create("Setup");
		Dialog.addString("Size:", "0-Infinity");
		Dialog.addString("Circularity:", "0.00-1.00");
		Dialog.addCheckbox("include holes", false);
		Dialog.addCheckbox("exclude edges", false);
		Dialog.addCheckbox("show ultimate points", false);
		Dialog.addRadioButtonGroup("evaluate by", newArray("mean", "median"), 1, 2, "median");
		Dialog.addRadioButtonGroup("conficence interval (CI)", newArray("95%", "99%", "99.9%"), 1, 3, "95%");
		Dialog.show();
		size = Dialog.getString();
		circ = Dialog.getString();
		holes = Dialog.getCheckbox();
		edges = Dialog.getCheckbox();
		uep = Dialog.getCheckbox();
		take = Dialog.getRadioButton();
		confidence = Dialog.getRadioButton();
		if(holes==true) {
			hole = "include ";
		} else {
			hole = "";
		}
		if(edges==true) {
			edge = "exclude ";
		} else {
			edge = "";
		}
		if(uep==true) {
			output = "Masks ";
		} else {
			output = "Nothing ";
		}
	
	//determination of critical F- and t-values
	if(confidence=="95%") {
		criticalF = newArray(2.978237016, 1.840871688, 1.53431418, 1.391719552, 1.158655374, 1.109688288, 1.076352036, 1.061912029, 1.053397886, 1.047627319);
		criticalT = newArray(1.812461102, 1.697260851, 1.670648865, 1.660234327, 1.647906854, 1.646378818, 1.645615867, 1.645361708, 1.645234659, 1.645158438);
		pListed = 0.05;
	} else if(confidence=="99%") {
		criticalF = newArray(4.849146802, 2.385967353, 1.836259361, 1.597669125, 1.231664935, 1.158625448, 1.109682472, 1.088680123, 1.076352997, 1.068021936);
		criticalT = newArray(2.763769458, 2.457261531, 2.390119457, 2.364217356, 2.333828914, 2.330082625, 2.328213787, 2.327591515, 2.32728048, 2.327093897);
		pListed = 0.01;
	} else if(confidence=="99.9%") {
		criticalF = newArray(8.753866276, 3.217090322, 2.252265545, 1.867401382, 1.319136791, 1.216098723, 1.148287469, 1.1194961, 1.102684079, 1.091358502);
		criticalT = newArray(4.143700493, 3.385184866, 3.231709121, 3.173739481, 3.106611618, 3.098402156, 3.09431229, 3.092951196, 3.092271061, 3.091863111);
		pListed = 0.001;
	}
	
	//particle analysis
	run("Analyze Particles...", "size="+size+" circularity="+circ+" show="+output+""+edge+"clear "+hole+"record");
	if(uep==true) {
		run("Invert LUT");
		run("Ultimate Points");
		setThreshold(1, 255);
		setOption("BlackBackground", true);
		run("Convert to Mask");
		UEP = getTitle();
	}
	
	particles = nResults;
	area = width * height;
	randomPart = 0.5 * sqrt(area/particles);
	stdDevRandom = sqrt(randomPart);
	print("Theoretical random nearest neighbor distance = "+randomPart);
	print("Variance = "+randomPart);
	print("StdDev = "+stdDevRandom);
	
	x = newArray(particles);
	y = newArray(particles);
	
	for(c=0; c<particles; c++) {
		x[c] = getResult("X", c);
		y[c] = getResult("Y", c);
	}
	
	count = 0;
	nearestNeighbor = newArray(particles);
	dist = newArray((particles*particles)-particles);
	nearestNeighborSum = 0;
	
	for(fix=0; fix<particles; fix++) {
		for(rest=0; rest<particles; rest++) {
			if(fix!=rest) {
				dist[count] = sqrt(pow((x[fix]-x[rest]), 2) + pow((y[fix]-y[rest]), 2));
				if(rest==0 || (fix==0 && rest==1)) {
					nearestNeighbor[fix] = dist[count];
				} else if(rest>0) {
					if (dist[count]<nearestNeighbor[fix]) {
						nearestNeighbor[fix] = dist [count];	
					} 
				}
			count++;
			}
		}
		nearestNeighborSum = nearestNeighborSum + nearestNeighbor[fix];
	}
	
	nearestNeighborMean = nearestNeighborSum / particles;
	
	//determine the median nearest neighbor distance
	Array.sort(nearestNeighbor);
	arraySize = nearestNeighbor.length;
	if(arraySize % 2 == 0) {
		nearestNeighborMedian = ((nearestNeighbor[arraySize/2] + nearestNeighbor[(arraySize/2)-1])/2);
	} else {
		nearestNeighborMedian = nearestNeighbor[floor(arraySize/2)];
	}
	
	
	//determine the sum of the differences
	differenceSumMean = 0;
	differenceSumMedian = 0;
	
	for(v=0; v<particles; v++) {
		differenceSumMean = differenceSumMean + pow((nearestNeighbor[v]-nearestNeighborMean), 2);
		differenceSumMedian = differenceSumMedian + pow((nearestNeighbor[v]-nearestNeighborMedian), 2);
	}
	
	//determine variances and SDs
	varianceMean = differenceSumMean/particles;
	SDMean = sqrt(varianceMean);
	varianceMedian = differenceSumMedian/particles;
	SDMedian = sqrt(varianceMedian);
	
	print("Measured average nearest neighbor distance = "+nearestNeighborMean);
	print("Variance (mean) = "+varianceMean);
	print("StdDev (mean) = "+SDMean);
	print("Measured median nearest neighbor distance = "+nearestNeighborMedian);
	print("Variance (median) = "+varianceMedian);
	print("StdDev (median) = "+SDMedian);
	print("--------------------------------------------------------------------------");
	print("Comparisons = "+count);
	print("Sample size n = " + particles);
	
	if(take=="median") {
		testValue = nearestNeighborMedian;
		variance = varianceMedian;
	} else if(take=="mean") {
		testValue = nearestNeighborMean;
		variance = varianceMean;
	}
	
	//Fisher's F-test
	F = maxOf(randomPart, variance) / minOf(randomPart, variance);
	
	if((particles>=11 && particles<31 && F>=criticalF[0]) || (particles>=31 && particles<61 && F>=criticalF[1]) || (particles>=61 && particles<101 && F>=criticalF[2]) || (particles>=101 && particles<501 && F>=criticalF[3]) || (particles>=501 && particles<1001 && F>=criticalF[4]) || (particles>=1001 && particles<2001 && F>=criticalF[5]) || (particles>=2001 && particles<3001 && F>=criticalF[6]) || (particles>=3001 && particles<4001 && F>=criticalF[7]) || (particles>=4001 && particles<5001 && F>=criticalF[8]) || (particles>=5001 && F>=criticalF[9])) {
		//Welch Test
		Tvalue = abs(randomPart-testValue) / (sqrt((randomPart/particles) + (variance/particles)));
		df = floor((pow(((randomPart/particles) + (variance/particles)),2)) / ((pow((randomPart/particles),2)/(particles-1)) + (pow((variance/particles),2)/(particles-1))));
		print("d.f. = " + df);
		print("t = " + Tvalue + " (Welch's t-test)");
	} else {
		//Student's t-Test
		Tvalue = (abs(randomPart-testValue)) / (sqrt(0.5*(randomPart+variance)) * sqrt(2/particles));
		df = (2*particles) - 2;
		print("d.f.: " + df);
		print("t = " + Tvalue + " (Student's t-test)");
	}
	
	//critical t-values for alpha=0.01 (two-tailed)
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
	print("critical t-value = " + criticalTValue);
	print("confidence interval = " + confidence);
	print("--------------------------------------------------------------------------");
	
	if(Tvalue >= criticalTValue) {
		if(testValue < randomPart) {
			print("  --->   clustering particles");
		} else if(testValue > randomPart) {
			print("  --->   self-avoiding particles");
		}
		print("significant different from random distribution with p < " + pListed);
	} else {
		print("  --->   random particle distribution");
		print("no significant difference to random distribution (p > "+pListed+")");
	}
	print("according to " + take + " nearest neighbor distance");
	print("--------------------------------------------------------------------------");

}

*/

//-----------------------------------------------------------------------------------------------------------------------------------------
// Cluster Finder, Jan Brocher/BioVoxxel 2014
// v0.1 first release
// v0.2 detection methods changed (UEP and voronoi distance based) (04-03-14)
//-----------------------------------------------------------------------------------------------------------------------------------------

function  ClusterIndicator() {
	
	original = getTitle();
	run("Remove Overlay");
	if(is("binary")==false) {
		exit("works only on 8-bit binary images");
	}
	run("Select None");
	getDimensions(width, height, channels, slices, frames);
	run("Overlay Options...", "stroke=blue width=1 fill=none set");
	run("Set Measurements...", "area centroid center bounding area_fraction redirect=None decimal=3");
	
	Dialog.create("Cluster Finder");
		Dialog.addNumber("estimated cluster diameter (pixel)", 50);
		Dialog.addNumber("density (x-fold of total image)", 2);
		Dialog.addNumber("max. iterations", 25);
		Dialog.addRadioButtonGroup("method", newArray("average ND", "UEP NND"), 1, 2, "average ND");
		Dialog.addCheckbox("fuse center-cluster overlaps", true);
		Dialog.addCheckbox("show ROI manager", false);
		Dialog.addCheckbox("show terminated iterations", false);
		Dialog.addCheckbox("show calculation image", false);
		Dialog.show();
		clusterSize = Dialog.getNumber();
		clusterRadius = clusterSize/2;
		density = Dialog.getNumber();
		maxIterations = Dialog.getNumber();
		method = Dialog.getRadioButton();
		showOverlap = Dialog.getCheckbox();
		manager = Dialog.getCheckbox();
		termIterations = Dialog.getCheckbox();
		showCalcImg = Dialog.getCheckbox();


	if(manager==true) {
		roiManager("reset");
		setBatchMode(true);
	} else {
		setBatchMode(true);
		roiManager("reset");
	}

	selectWindow(original);
	run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record");
	//individualArea = newArray(nResults);
	u = newArray(nResults);
	v = newArray(nResults);

	if(method=="UEP NND") {
		//create intensity coded ultimate point
		
		distances = newArray(nResults*nResults);
		NND = newArray(nResults);
		
		count = 0;
		for(a=0; a<nResults; a++) {
			u[a] = getResult("X", a);
			v[a] = getResult("Y", a);
			for(n=a+1; n<nResults; n++) {
				distances[count] = sqrt(pow(abs(getResult("X", a)-getResult("X", n)), 2) + pow(abs(getResult("Y", a)-getResult("Y", n)), 2));
				if(a==0 || n==a+1) {
					NND[a] = distances[count];
				} else {
					if(NND[a] > distances[count]) {
						NND[a] = distances[count];
					}
				}
				count++;
			}
		}
		countLast = 0;
		for(b=0; b<nResults-1; b++) {
			distances[countLast] = sqrt(pow(abs(getResult("X", b)-getResult("X", nResults-1)), 2) + pow(abs(getResult("Y", b)-getResult("Y", nResults-1)), 2));
			if(b==0) {
				NND[nResults-1] = distances[countLast];
			} else if(NND[nResults-1] > distances[countLast]) {
				NND[nResults-1] = distances[countLast];
			}
			countLast++;
		}
		
		newImage(original+"-UEP_NND", "32-bit black", width, height, 1);
		evaluation = getTitle();
		selectWindow(evaluation);
		
		for(norm=0; norm<nResults; norm++) {
			setPixel(u[norm], v[norm], 1/NND[norm]);
		}
		
		
		
	} else if(method=="average ND") {
		
		//create intensity coded voronoi
		run("Options...", "iterations=1 count=1 black edm=32-bit do=Nothing");
		setBackgroundColor(0,0,0);
		selectWindow(original);
		run("Voronoi");
		voronoi = getTitle();
		selectWindow(voronoi);
		getRawStatistics(voronoiArea, voronoiMean, voronoiMin, voronoiMax);
		//setThreshold(1, voronoiMax);
		//run("Create Selection");
		//run("Invert");
		//updateDisplay();
		//resetThreshold();
		run("Select None");
		run("Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
		
		toUnscaled(u, v);
		averDistanceValue = newArray(nResults+1);
		selectWindow(voronoi);
		for(s=0; s<nResults; s++) {
			u[s] = getResult("X", s);
			v[s] = getResult("Y", s);
			doWand(u[s], v[s]);
			run("Make Band...", "radius=1");
			getRawStatistics(selectionArea, averDistanceValue[s]);
			showProgress(s/nResults);
		}
		
		newImage(original+"-averND", "32-bit black", width, height, 1);
		evaluation = getTitle();
				
		selectWindow(evaluation);
		for(d=0; d<nResults; d++) {
			setPixel(u[d], v[d], (1.0-(averDistanceValue[s]/voronoiMax)));
			showStatus("Preparing point map...");
		}
		
	}

	selectWindow(evaluation);
	run("Select None");
	List.clear();
	List.setMeasurements;
	
	totalDensity = List.getValue("%Area");
	totalBrightness = List.getValue("Mean");
	limit  = totalDensity*totalBrightness;

	multiplicatorX = round(((width+clusterRadius)*2)/clusterSize);
	multiplicatorY = round(((height+clusterRadius)*2)/clusterSize);
	//print(multiplicatorX);
	//print(multiplicatorY);
	
	counter = 0;
	excluded = 0;
	totalCount = 0;
	addToManager = 0;
	duplicatedDetection = 0;
	sumClusterDensity = 0;
	terminatedIterations = 0;
	xClusterPos = newArray(((multiplicatorX)*(multiplicatorY))+1);
	yClusterPos = newArray(((multiplicatorX)*(multiplicatorY))+1);
		
	Xold = -1;
	Yold = -1;
	
	for(rY=0; rY<multiplicatorY; rY++) {
		for(rX=0; rX<multiplicatorX; rX++) {
			selectWindow(evaluation);
			makeOval(0+(rX*clusterRadius)-clusterRadius, 0+(rY*clusterRadius)-clusterRadius, clusterSize, clusterSize);
			totalCount = totalCount + 1;
			showStatus("Cluster detection " + ((100*totalCount)/(multiplicatorX*multiplicatorY)) + " %");
			showProgress(totalCount/(multiplicatorX*multiplicatorY));
			i=0;
			
			while(i<maxIterations) {
				List.setMeasurements;
				X = List.getValue("X");
				Y = List.getValue("Y");
				XM = List.getValue("XM");
				YM = List.getValue("YM");
										
				if(Xold==X && Yold==Y) {
					t=0;
					localBrightness = List.getValue("Mean");
					localDensity = List.getValue("%Area");
					localValue = localBrightness * localDensity;
								
					while(t<=counter) {
						if(X!=xClusterPos[t] && Y!=yClusterPos[t]) {
							//print("new: ("+xClusterPos[t]+")/("+yClusterPos[t]+") - "+ t); //control output
							addToManager = 1;
							t++;
						} else if(X==xClusterPos[t] && Y==yClusterPos[t]) {
							addToManager = 0;
							duplicatedDetection = duplicatedDetection + 1;
							//print("exists: ("+xClusterPos[t]+")/("+yClusterPos[t]+") - "+ t); //control output
							Xold = X;
							Yold = Y;
							t = counter+1;
						} else {
							t++;
						}
					}
					
					if(addToManager==1 && (localValue >= (limit*density))) {
						sumClusterDensity = sumClusterDensity + localValue;
						roiManager("add");
						//print("in: "+X+"("+xClusterPos[counter]+")/("+yClusterPos[counter]+")"+Y+" - counter: "+ counter); //control output
						xClusterPos[counter] = X;
						yClusterPos[counter] = Y;
						Xold = X;
						Yold = Y;
						counter = counter + 1;
					} else if(addToManager==1 && (localValue < (limit*density))) {
						excluded = excluded + 1;
						//print("excluded: "+X+"/"+Y);  //control output
					}		
					i=maxIterations;
					List.clear();
				} else {
					nX = XM;
					nY = YM;
					Xold = X;
					Yold = Y;
					//print(nY+" / "+X);  //control output
					makeOval((nX-clusterRadius), (nY-clusterRadius), clusterSize, clusterSize);
					i++;
									 
					if(i==maxIterations) {
						terminatedIterations = terminatedIterations + 1;
						if(termIterations==true) {
							selectWindow(original);
							makeOval((nX-clusterRadius), (nY-clusterRadius), clusterSize, clusterSize);
							run("Add Selection...");
							selectWindow(evaluation);
						}
					}
				}
			}
		}
	}

	fusion = 0;
	count = roiManager("count");
	if(showOverlap==true) {
		showStatus("Cluster fusion");
		selectWindow(original);
		for(point=0; point<count; point++) {
			roiManager("select", point);
			getSelectionBounds(boundX, boundY, w, h);
			centerX = boundX+clusterRadius;
			centerY = boundY+clusterRadius;
			selection = 0;
			while(selection<count) {
				roiManager("select", selection);
				if(selection!=point && selectionContains(centerX, centerY)) {
					roiManager("select", newArray(point, selection));
					roiManager("OR");
					roiManager("Add");
					roiManager("select", newArray(point, selection));
					roiManager("Delete");
					fusion = fusion + 1;
					count = roiManager("count");
					selection = count;
					point = point-1;
				}
				selection++;
			}
		}	
	} else {
		selectWindow(original);
	}

	roiManager("Show all without labels");

	if(showCalcImg==true) {
		selectWindow(evaluation);
		run("Select None");
		//run("Maximum...", "radius=1");
		run("Enhance Contrast...", "saturated=0 normalize");
		run("Fire");
		roiManager("Show all without labels");
		setBatchMode("show");
		
	}
		
	// output
	print("_____________________________________________");
	print("ROI diameter: "+clusterSize+" pixel");
	print("average image density: "+limit+" %");
	print("minimal density limit: "+(limit*density)+" % ("+density+"-fold)");
	print("max. iterations: " + maxIterations);
	print("initiated ROIs in total: " + (totalCount));
	print("--------------------------------------------------");
	print(count + " clusters accepted (density (ROI) > "+(totalBrightness*density)+" %) ("+((100*count)/totalCount)+" %)");
	//print("average density: " + sumClusterDensity/count + " % or ROI area");
	print("--------------------------------------------------");
	print(excluded + " clusters excluded due to low density ("+((100*excluded)/totalCount) + " %)");
	print(duplicatedDetection + " duplicate clusters ("+((100*duplicatedDetection)/totalCount) + " %)");
	print(fusion + " ROIs fused due to overlap ("+((100*fusion)/totalCount) + " %)");
	print(terminatedIterations + " terminated iterations ("+((100*terminatedIterations)/totalCount) + " %) (blue ROIs)");
	print("_____________________________________________");

	run("Options...", "iterations=1 count=1 black edm=8-bit do=Nothing");
	run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction stack display redirect=None decimal=3");
	setBatchMode(false);
	showStatus("Done");
	exit();
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------------


function CorrectedSkeletonLength() {

	setOption("BlackBackground", true);
	original = getTitle();
	if (is("binary")==false) {
		exit("8-bit binary image necessary"); 
	}
	//setup dialog
	Dialog.create("Setup");
			Dialog.addString("Size:", "0-Infinity");
			Dialog.addString("Circularity:", "0.00-1.00");
			Dialog.addChoice("Show", newArray("Nothing", "Outlines", "Bare Outlines", "Ellipses", "Masks", "Count Masks", "Overlay Outlines", "Overlay Masks"), "Nothing");
			Dialog.addCheckbox("clear results", false);
			Dialog.addCheckbox("include holes", false);
			Dialog.addCheckbox("exclude edges", false);
			Dialog.addCheckbox("mininmal results", true);
			Dialog.show();
			size = Dialog.getString();
			circ = Dialog.getString();
			show = Dialog.getChoice();
			clear = Dialog.getCheckbox();
			holes = Dialog.getCheckbox();
			edges = Dialog.getCheckbox();
			display = Dialog.getCheckbox();
			if(clear==true) {
				clear = "clear ";
			} else {
				clear = "";
			}
			if(holes==true) {
				hole = "include ";
			} else {
				hole = "";
			}
			if(edges==true) {
				edge = "exclude ";
			} else {
				edge = "";
			}
	
	setBatchMode(true);
	
	//create distance map of particles
	selectWindow(original);
	run("Duplicate...", "title=distance");
	distance = getTitle();
	run("Distance Map");
	
	//create particle skeleton
	selectWindow(original);
	run("Duplicate...", "title=skeleton");
	skeleton = getTitle();
	run("Skeletonize");
	
	//create endpoint erosded skeleton
	run("Duplicate...", "title=eroded");
	eroded = getTitle();
	run("Options...", "iterations=1 count=7 black edm=Overwrite do=Erode");
	
	//create binary endpoint image
	run("Calculator Plus", "i1="+skeleton+" i2="+eroded+" operation=[Subtract: i2 = (i1-i2) x k1 + k2] k1=1 k2=0 create");
	endpoints = getTitle();
	
	//create image with endpoint intensities 
	run("Select All");
	run("Copy");
	selectWindow(distance);
	run("Select All");
	setPasteMode("Transparent-white");
	run("Paste");
	run("Select None");
	
	correction = getTitle();
	
	
	//results display mode and redirect to the endpoint intensity image 
	if(display==true) {
		run("Set Measurements...", "area centroid integrated redirect=["+correction+"] decimal=3");
	} else {
		run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction redirect=["+correction+"] decimal=3");
	}
	
	//analysis
	selectWindow(skeleton);
	run("Analyze Particles...", "size="+size+" circularity="+circ+" show=["+show+"] display " + edge + clear + hole +" record");
	setBatchMode("show");
	//correct the length by addition of skeleton area to endpoint intensities
	for(n=0; n<nResults;n++) {
		setResult("Corr. Length", n, ((getResult("Area", n))+(getResult("RawIntDen", n))));
	}
	//close intermediate images
	close(distance);
	close(skeleton);
	close(eroded);
	close(endpoints);
	close(correction);
	setBatchMode(false);
}

//-----------------------------------------------------------------------------------------------------------------------------------------

function About() {

	if(isOpen("Log")==1) { selectWindow("Log"); run("Close"); }
	
	  Dialog.create("About");
	  Dialog.addMessage("All Macros/Plugins in this BioVoxxel Menu were written by Jan Brocher/BioVoxxel.\n \nCopyright (C) Jan Brocher.\n \nRedistribution and use in source and binary forms of all plugins and macros,\nwith or without modification, are permitted provided that the following conditions are met:\nRedistributions of source code must retain the above copyright notice, this list of conditions\nand the following disclaimer.\n Redistributions in binary form must reproduce the above copyright notice,\nthis list of conditions and the following disclaimer in the documentation\nand/or other materials provided with the distribution.\n \nDISCLAIMER:\n \nTHIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS �AS IS�\nAND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,\nBUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY\nAND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE\nFOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,\nOR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT\nOF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;\nOR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,\nWHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE\nOR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,\nEVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.");
	  Dialog.addHelp("http://fiji.sc/BioVoxxel_Toolbox");
	  Dialog.show();
}
