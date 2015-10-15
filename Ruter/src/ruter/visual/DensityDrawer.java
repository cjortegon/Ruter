package ruter.visual;

import java.awt.Color;
import java.awt.Graphics;

import ruter.map.Map;
import ruter.simulator.Simulation;
import ruter.simulator.Updatable;
import ruter.visual.Drawable;

public class DensityDrawer implements Drawable, Updatable {

	private Map map;
	private int accessTime;
	private double changingSpeed;
	private double[][] densification;
	private double maxDensification;
	private boolean logaritmicScale;

	/**
	 * This class draws the density graph of the Map according to the number of instances in each sector.
	 * @param map to track density.
	 * @param changingSpeed changingSpeed value from 0 to 1.
	 * The biggest the value the most importance given to the new state.
	 */
	public DensityDrawer(Map map, double changingSpeed) {
		this.map = map;
		this.changingSpeed = changingSpeed;
		this.densification = map.obtainSectorsDensification();
	}

	/**
	 * This class draws the density graph of the Map according to the number of instances in each sector.
	 * @param map to track density.
	 */
	public DensityDrawer(Map map) {
		this.map = map;
		this.changingSpeed = -1;
		this.densification = map.obtainSectorsDensification();
	}

	/**
	 * Logarithmic scale helps minor changes in graphic be more visible.
	 * @param useLogaritmicScale
	 */
	public void useLogaritmicScale(boolean useLogaritmicScale) {
		this.logaritmicScale = useLogaritmicScale;
	}

	@Override
	public void draw(Graphics g, double ppm) {
		for (int x = 0; x < densification.length; x++) {
			for (int y = 0; y < densification[0].length; y++) {
				double percent;
				if(logaritmicScale) {
					percent = Math.log1p(densification[x][y])/Math.log1p(maxDensification);
				} else {
					percent = densification[x][y]/maxDensification;
				}
				g.setColor(new Color(middleColor(Color.yellow.getRGB(), Color.red.getRGB(), percent)));
				int size = (int) Math.ceil(map.getGridSize()*ppm);
				g.fillRect((int)(x*map.getGridSize()*ppm), (int)(y*map.getGridSize()*ppm), size, size);
			}
		}
	}

	@Override
	public void update(Simulation simulation) {
		if(changingSpeed < 0 || changingSpeed > 1) {
			maxDensification = map.modifySectorsDensification(densification, accessTime);
			accessTime ++;
		} else {
			maxDensification = map.modifySectorsDensification(densification, changingSpeed);
		}
	}

	@Override
	public void update(double x, double y) {
	}

	private int middleColor(int firstColor, int secondColor, double percent) {
		int r1 = ((firstColor & 0xff0000) >> 16);
		int g1 = (firstColor & 0xff00) >> 8;
		int b1 = firstColor & 0xff;
		int r2 = (secondColor & 0xff0000) >> 16;
		int g2 = (secondColor & 0xff00) >> 8;
		int b2 = secondColor & 0xff;
		int r = ((int) (r1*(1-percent) + r2*percent)) << 16;
		int g = ((int) (g1*(1-percent) + g2*percent)) << 8;
		int b = (int) (b1*(1-percent) + b2*percent);
		return 0xff000000 | r | g | b;
	}

	private Color colorWithTransparency(Color color, double transparency) {
		//		int t = ((int)(transparency*255)) >> 24;
		//		int r = ((color & 0xff0000) >> 16);
		//		int g = (color & 0xff00) >> 8;
		//		int b = color & 0xff;
		//		return r | g | b;

		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		return new Color(r, g, b, (int)(transparency*128));
	}

}
