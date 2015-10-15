package ruter.visual;

import java.awt.Graphics;

import ruter.simulator.Simulation;
import ruter.simulator.Updatable;
import ruter.visual.Drawable;

public class PathRecorder implements Updatable, Drawable {

	private double xPath[], yPath[];
	private int size;

	public PathRecorder(int recordSize) {
		xPath = new double[recordSize];
		yPath = new double[recordSize];
	}

	@Override
	public void update(double x, double y) {
		if(size < xPath.length)
			size ++;
		if(size > 0) {
			for (int i = size-1; i > 0; i--) {
				xPath[i] = xPath[i-1];
				yPath[i] = yPath[i-1];
			}
		}
		xPath[0] = x;
		yPath[0] = y;
	}

	@Override
	public void update(Simulation simulation) {
	}

	@Override
	public void draw(Graphics g, double ppm) {
		for (int i = 1; i < size; i++) {
			g.drawLine((int)(xPath[i]*ppm), (int)(yPath[i]*ppm), (int)(xPath[i-1]*ppm), (int)(yPath[i-1]*ppm));
		}
	}

}
