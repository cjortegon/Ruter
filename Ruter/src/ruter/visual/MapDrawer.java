/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import ruter.exceptions.MapException;
import ruter.map.Map;
import ruter.map.PathSection;

/**
 * This class is used to handle the map functions.
 * @author Camilo Ortegon
 */
public class MapDrawer {

	public Map map;
	private double lastPpm;
	public int xWindow, yWindow;
	private Color streetColor = Color.gray;
	private Color laneColor = Color.lightGray;

	/**
	 * Canvas to paint
	 */
	private BufferedImage canvas;
	private Graphics graphics;

	/**
	 * Static part of the canvas. For example the streets.
	 */
	private BufferedImage staticPartCanvas;
	private Graphics staticPartGraphics;

	public MapDrawer(Map map) {
		this.map = map;
	}

	private void startCanvas(int width, int height) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = canvas.getGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, width, height);
	}

	private void startStaticPartCanvas(int width, int height) {
		staticPartCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		staticPartGraphics = staticPartCanvas.getGraphics();
		staticPartGraphics.setColor(Color.white);
		staticPartGraphics.fillRect(0, 0, width, height);
	}

	public void move(int xMove, int yMove) {
		this.xWindow += xMove;
		this.yWindow += yMove;
	}

	public void paint(Graphics g) {
		g.drawImage(canvas, 0, 0, null);
	}

	private void repaintStaticPartOfTheCanvas(int width, int height, double ppm) {

		startStaticPartCanvas(width, height);

		for (int i = 0; i < map.pathsections.size(); i++) {
			PathSection p = map.pathsections.get(i);
			int street[][] = checkStreetSquare(p, xWindow, yWindow, width, height, ppm);
			if(street != null) {
				staticPartGraphics.setColor(streetColor);
				staticPartGraphics.fillPolygon(street[0], street[1], 4);
				// Drawing roads
				staticPartGraphics.setColor(laneColor);
//				for (int r = 0; r < p.getNumberOfRoads(); r++) {
//					staticPartGraphics.drawLine((int)(p.getLaneStartX(r)*ppm), (int)(p.getLaneStartY(r)*ppm),
//							(int)(p.getLaneEndX(r)*ppm), (int)(p.getLaneEndY(r)*ppm));
//				}
				double[][][] lanes = p.getPaintingLanes();
				for (int r = 0; r < lanes[0][0].length; r++) {
					staticPartGraphics.drawLine((int)(lanes[0][0][r]*ppm), (int)(lanes[0][1][r]*ppm),
							(int)(lanes[1][0][r]*ppm), (int)(lanes[1][1][r]*ppm));
				}
			}
		}
	}

	public void repaint(Graphics g, int width, int height, int x, int y, double ppm) {
		if(ppm != lastPpm) {
			repaintStaticPartOfTheCanvas(width, height, ppm);
			lastPpm = ppm;
		}
		g.drawImage(staticPartCanvas, -x, -y, null);
	}

	private int[][] checkStreetSquare(PathSection p, int x, int y, int width, int height, double ppm) {

		int street[][] = new int[2][4];
		double border[][] = p.getBorder();

		for (int j = 0; j < 4; j++) {
			street[PathSection.X][j] = (int)(border[PathSection.X][j]*ppm-x);
			street[PathSection.Y][j] = (int)(border[PathSection.Y][j]*ppm-y);
		}

		int minX = street[0][0];
		if(street[0][1] < minX)
			minX = street[0][1];
		if(street[0][2] < minX)
			minX = street[0][2];
		if(street[0][3] < minX)
			minX = street[0][3];

		int minY = street[1][0];
		if(street[1][1] < minY)
			minY = street[1][1];
		if(street[1][2] < minY)
			minY = street[1][2];
		if(street[1][3] < minY)
			minY = street[1][3];

		if(minX < width && minY < height)
			return street;
		else
			return null;
	}

}
