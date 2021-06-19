

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.FileSaver;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class HarrisApp {

	static int nmax = 0;						// number of corners to show
	static int cornerSize = 2;					// size of cross-markers
	static Color cornerColor = Color.green;		// color of cross markers

	ImagePlus im;


	private static boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Alpha", params.alpha, 3);
		dlg.addNumericField("Threshold", params.tH, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		params.alpha = dlg.getNextNumber();
		params.tH = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		nmax = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}	
		return true;
	}

	// Brightens the image ip. May not work with ShortProcessor and FloatProcessor
	@SuppressWarnings("unused")
	private static void brighten(ImageProcessor ip) {	
		int[] lookupTable = new int[256];
		for (int i = 0; i < 256; i++) {
			lookupTable[i] = 128 + (i / 2);
		}
		ip.applyTable(lookupTable); 
	}

	private static void drawCorners(ImageProcessor ip, List<Corner> corners) {
		ip.setColor(cornerColor);
		int n = 0;
		for (Corner c: corners) {
			drawCorner(ip, c);
			n = n + 1;
			if (nmax > 0 && n >= nmax) 
				break;
		}
	}

	private static void drawCorner(ImageProcessor ip, Corner c) {
		int size = cornerSize;
		int x = Math.round(c.getX());
		int y = Math.round(c.getY());
		ip.drawLine(x - size, y, x + size, y);
		ip.drawLine(x, y - size, x, y + size);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}

		ImagePlus iplus = new ImagePlus("D:\\Program Files\\Eclipse\\A170522_Assignment 3\\input\\noise.tif");
		ImageProcessor ip = iplus.getProcessor();

		HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.findCorners();

		System.out.println(corners.size());

		ColorProcessor R = ip.convertToColorProcessor();
		drawCorners(R, corners);
		ImagePlus output = new ImagePlus("Corners from " + iplus.getShortTitle(), R);
		output.show();
		FileSaver a=new FileSaver(output);
		a.saveAsJpeg("D:\\Program Files\\Eclipse\\A170522_Assignment 3\\output\\noise.jpg");

	}

}
