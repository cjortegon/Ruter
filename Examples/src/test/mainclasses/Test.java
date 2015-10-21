package test.mainclasses;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import ruter.map.Map;
import ruter.map.MapException;
import ruter.map.PathSection;
import ruter.simulator.Simulation;
import ruter.simulator.Vehicle;
import test.components.ContinueDriver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.Painter;

import ruter.network.AdhocNetwork;
import visualkey.KCanvas;

/**
 *
 * @author camilo
 */
public class Test extends KCanvas implements KeyListener {

	private static final int[] MAP_DIMENSION = {500, 300};
	private static final long FRAME_TIME = Simulation.ACCURACY_HIGH;
	private static final int MAX_PPM = 10;
	private static final int MIN_PPM = 1;
	private static final int NUMBER_OF_VEHICLES = 150;

	public static double ppm = 5; // Pixels per meter

	private Map map;
	private PathSection firstSection;
//	private ContinueDriver driver;
	private Simulation simulation;

	public static void main(String args[]) {
		Test test = new Test();
		test.setDefaultCloseOperation(test.EXIT_ON_CLOSE);
		test.setVisible(true);
	}

	public Test() {
		super(new Dimension((int) (MAP_DIMENSION[0] * ppm + 10), (int) (MAP_DIMENSION[1] * ppm + 20)));
		createMainInstances();
		addVehicles();
		addKeyListener(this);
		simulation.start();
		updateTitle(false);
	}

	public void createMainInstances() {

		// Map
		map = new Map(MAP_DIMENSION[0], MAP_DIMENSION[1], 10);
		designMap();

		// Simulation
		simulation = new Simulation(this, map, null, (int) (MAP_DIMENSION[0]*ppm), (int) (MAP_DIMENSION[1]*ppm), FRAME_TIME);
	}
	
	private void designMap() {
		
		try {
			firstSection = map.makePath(80, 10, 2.5, 2, 3);
			map.makePath(150, 10, 2.5, 2, 3);
			map.makePath(160, 10, 2.5, 2, 3);
			map.makePath(180, 20, 2.5, 2, 3);
			map.makePath(190, 40, 2.5, 2, 3);
			map.makePath(190, 60, 2.5, 2, 3);
			map.makePath(180, 80, 2.5, 2, 3);
			map.makePath(160, 90, 2.5, 2, 3);
			map.makePath(140, 90, 2.5, 2, 3);
			map.makePath(120, 80, 2.5, 2, 3);
			map.makePath(110, 60, 2.5, 2, 3);
			map.makePath(110, 40, 2.5, 2, 3);
			map.makePath(120, 20, 2.5, 2, 3);
			map.makePath(140, 10, 2.5, 2, 3);
			map.makePath(150, 10, 2.5, 2, 3);
			map.finishMap();
		} catch (MapException e) {}
	}

	private void addVehicles() {
		Color colors[] = {Color.blue, Color.cyan, Color.red, Color.black, Color.white, Color.green, Color.yellow, Color.pink};
		Random ale = new Random();
		for (int i = 0; i < NUMBER_OF_VEHICLES; i++) {
			Vehicle v = new Vehicle((i % 10)*2.5 + 5, 2.5 * (i/10) + 5, 2, 1, 1.5, 0);
//			Vehicle v = new Vehicle(i*2.5, 10 + 5, 2, 1, 1.5, 180);
//			driver = new ContinueDriver(map, firstSection, FRAME_TIME/1000d, simulation);
			v.putDriver(new ContinueDriver(map, firstSection, FRAME_TIME/1000d, simulation));
			simulation.addVehicle(v, colors[ale.nextInt(12345) % colors.length]);
		}
	}

	@Override
	protected void paintCanvas(Graphics g) {
		simulation.repaint(g, ppm);
		g.setColor(Color.black);
//		g.fillOval((int) (driver.nextX*ppm) - 4, (int) (driver.nextY*ppm) - 4, 8, 8);
	}

	public void updateTitle(boolean requestScreenAdjust) {
		super.setTitle("Ruter - PPM: " + ppm + " - speed: " + (simulation.getSimulationSpeed() > 1 ? "1/" + (int) simulation.getSimulationSpeed() + "x" : (int) (1 / simulation.getSimulationSpeed()) + "x"));
		if (requestScreenAdjust) {
			modifyCanvasSize(new Dimension((int) (MAP_DIMENSION[0] * ppm + 10), (int) (MAP_DIMENSION[1] * ppm + 20)));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			ppm *= 1.125;
			updateTitle(true);
			repaint();
			break;
		case KeyEvent.VK_DOWN:
			ppm /= 1.125;
			updateTitle(true);
			repaint();
			break;
		case KeyEvent.VK_RIGHT:
			simulation.increaseSimulationSpeed();
			updateTitle(false);
			break;
		case KeyEvent.VK_LEFT:
			simulation.decreaseSimulationSpeed();
			updateTitle(false);
			break;
		}

		switch (e.getKeyChar()) {

		case 'a':
			break;

		case 'p':
			simulation.pauseSimulation();
			break;

		case 'r':
			simulation.resumeSimulation();
			break;

		case 's':
			simulation.stopSimulation();
			break;

		case 't':
			System.out.println("Time: "+simulation.getRealTime());
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
