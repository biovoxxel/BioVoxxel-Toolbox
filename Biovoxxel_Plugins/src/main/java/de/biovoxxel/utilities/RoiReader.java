package de.biovoxxel.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

@Plugin(type = Command.class, menuPath = "Plugins>BioVoxxel>Utilities>Leica ROI Reader")
public class RoiReader implements Command {
	
	@Parameter(label = "Select (Leica) ROI file", style = "file")
	File roiFile;
	

	@Override
	public void run() {
		
		Vector<Float> x = null;
		Vector<Float> y = null;
		Vector<Roi> roiVector = new Vector<Roi>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(roiFile));
			
			String line = br.readLine();
		
			while (line != null) {
				System.out.println(line);
				
				
				if (line.contains("<Attachment ")) {
					
					line = br.readLine();
					
					x = new Vector<Float>();
					y = new Vector<Float>();

					while (line.contains("P")) {
						
						System.out.println("Point line = " + line);
						
						x.add(Float.parseFloat(line.substring(line.indexOf("\"")+1, line.indexOf("Y")-2)));
						y.add(Float.parseFloat(line.substring(line.indexOf("Y=\"")+3, line.lastIndexOf("\""))));
						
						line = br.readLine();
					}

					float[] xCoord = new float[x.size()];
					float[] yCoord = new float[y.size()];
					
					for (int v = 0; v < x.size(); v++) {
						xCoord[v] = x.get(v);
						yCoord[v] = y.get(v);
					}
					
					Roi roi = new PolygonRoi(xCoord, yCoord, Roi.POLYGON);
					//Roi roi = new Roi(x[0], y[0], x[1]-x[0], y[2]-y[0]);	//for pure rectangular Rois
					roiVector.add(roi);
				}
				
				line = br.readLine();
			}
				
			br.close();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		if (roiVector.size() > 0) {
			RoiManager rm = new RoiManager();
			
			for (int r = 0; r < roiVector.size(); r++) {
				Roi roi = roiVector.get(r);
				roi.setName("ROI-" + r);
				rm.add(roi, -1);
				
			}
			rm.setVisible(true);
		}
		
	}
	
}
