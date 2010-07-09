/*
 * This is a class that contains main and
 * will call the TAGrid simulation, also displaying the results 
 * Graphically in a window
 * TA is for Transit Amplifying
 */


import java.awt.*;
import javax.swing.*;
import java.util.*;
 
public class TAStatic extends JFrame implements Runnable {

    TAGridStatic experiment;
    Random rand = new Random();    
	Thread runner;
	Image backImg1;
	Graphics backGr1;
	Image backImg2;
	Graphics backGr2;
	int scale = 20;//beth: could set to 1. Makes the colour transitions better?
	int iterations;
	int gSize;
    Color[] colours = {Color.black,Color.white,Color.green,Color.blue,Color.yellow,Color.red,Color.pink};

	public TAStatic(int size, int maxC, double frac) {
	    gSize=size;
		experiment = new TAGridStatic(size, maxC, frac);
		setSize(400, 400);//window (Frame) size
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		backImg1 = createImage(scale * size, scale * size);
		backGr1 = backImg1.getGraphics();
		backImg2 = createImage(scale * size, scale * size);
		backGr2 = backImg2.getGraphics();
		iterations = 0;
	}

	public void drawCA() {
		backGr1.setColor(Color.white);
		int a;
		backGr1.fillRect(0, 0, this.getSize().width, this.getSize().height);
		for (TACell c : experiment.tissue){
			a = c.type;
			if(a<7){
				backGr1.setColor(colours[a]);
			}else{
				backGr1.setColor(Color.orange);
			}
			backGr1.fillRect(c.home.x * scale, c.home.y * scale, scale, scale);
		}
        backGr2.drawImage(backImg1, 0, 0, gSize * scale, gSize * scale, 0, 0, scale * gSize, scale * gSize, this);
	    repaint();//beth: calls for a screen repaint asap
	}



	public void paint(Graphics g) {
		if ((backImg2 != null) && (g != null)) {
			g.drawImage(backImg2, 0, 0, this.getSize().width, this.getSize().height, -10, -92, scale * gSize + 10, scale * gSize + 10, this);
			//g.drawImage(backImg2, 0, scale+10, this.getSize().width, scale*2, -10, 0, scale * gSize + 10, scale,this);
		}
	}

	public void start() {
		if (runner == null) {
			runner = new Thread(this);
		}
		runner.start();
	}


	public void run() {
    	while (iterations < 5) {
        	//while (runner == Thread.currentThread()) {
			experiment.iterate();
			drawCA();
			iterations++;
			//if((iterations%5)==0)postscriptPrint("TA"+iterations+".eps");
			// This will produce a postscript output of the tissue
		}
	}


	public void postscriptPrint(String fileName) {
		int xx;
		int yy;
		int state;
		boolean flag;
		try {
			java.io.FileWriter file = new java.io.FileWriter(fileName);
			java.io.BufferedWriter buffer = new java.io.BufferedWriter(file);
			System.out.println(fileName);
			buffer.write("%!PS-Adobe-2.0 EPSF-2.0");
			buffer.newLine();
			buffer.write("%%Title: test.eps");
			buffer.newLine();
			buffer.write("%%Creator: gnuplot 4.2 patchlevel 4");
			buffer.newLine();
			buffer.write("%%CreationDate: Thu Jun  4 14:16:00 2009");
			buffer.newLine();
			buffer.write("%%DocumentFonts: (atend)");
			buffer.newLine();
			buffer.write("%%BoundingBox: 0 0 300 300");
			buffer.newLine();
			buffer.write("%%EndComments");
			buffer.newLine();
			for (TACell c : experiment.tissue){
				if(c.type>0){
					xx = (c.home.x * 4) + 20;
					yy = (c.home.y * 4) + 20;
					if (c.proliferated) {
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc fill\n");
						buffer.write("0 setgray\n");
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc  stroke\n");
					} else {
						buffer.write("0.75 setgray\n");
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc fill\n");
					}
				}
			}
			buffer.write("showpage");
			buffer.newLine();
			buffer.write("%%Trailer");
			buffer.newLine();
			buffer.write("%%DocumentFonts: Helvetica");
			buffer.newLine();
			buffer.close();
		} catch (java.io.IOException e) {
			System.out.println(e.toString());
		}
	}


	public static void main(String args[]) {
		double initalSeed = 0.1;
		if(args.length>0){
			initalSeed = Double.parseDouble(args[0]);
			TAStatic s = new TAStatic(64, 4, initalSeed);
			s.start();
		}else{
			TAStatic s = new TAStatic(64, 4, 0.00);
			s.start();
		}
	}
}

