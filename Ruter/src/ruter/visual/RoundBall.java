package ruter.visual;

import java.awt.Graphics;

import ruter.simulator.Simulation;
import ruter.simulator.Updatable;

public class RoundBall implements Updatable, Drawable {

	private double x, y, radius;

	public RoundBall(double radius) {
		this.radius = radius;
	}

	@Override
	public void draw(Graphics g, double ppm) {
		g.drawOval((int) ((x-radius)*ppm), (int) ((y-radius)*ppm), (int) (radius*2*ppm), (int) (radius*2*ppm));
	}

	@Override
	public void update(Simulation simulation) {
	}

	@Override
	public void update(double x, double y) {
		this.x = x;
		this.y = y;
	}

}
