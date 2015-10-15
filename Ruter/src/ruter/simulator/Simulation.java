/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.simulator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import ruter.map.Map;
import ruter.network.Network;
import ruter.network.Node;
import ruter.network.UnavailableInformationException;
import ruter.visual.MapDrawer;
import ruter.visual.Painter;

/**
 *
 * @author Camilo Ortegon
 */
public class Simulation extends Thread {

	/**
	 * Constants
	 */
	public static long ACCURACY_HIGH = 50;
	public static long ACCURACY_MEDIUM = 100;
	public static long ACCURACY_LOW = 200;

	private long simulationTime;	// In milliseconds
	private int lastSleepTime;		// In milliseconds
	private long paintingTime;		// In milliseconds
	private double frameTime;		// In seconds
	private double simulationSpeed;
	private long lastPaint;
	private long realTime;			// In milliseconds
	
	private int windowWidth, windowHeight;

	private boolean running, pause;

	private Map map;
	private MapDrawer mapDrawer;
	private ArrayList<Vehicle> vehicles;
	private ArrayList<Color> vehicleColors;
	private LinkedList<Updatable> updateElements;
	private Network network;
	private Component canvas;

	// Colors
	private Color borderColor = Color.red;
	private Color networkColor = Color.blue;

	/**
	 * This is class contains the simulation thread.
	 * You can specified at the beginning what do you want to simulate.
	 * Speed of the simulation can be modified on the way.
	 * Start method must be called when you're ready to begging, and after you can only pause, resume or resume. No restart is allowed.
	 * @param canvas the component to paint the simulation, it could be null.
	 * The component should call method repaint(Graphics) from this class to make changes visible.
	 * @param map of the simulation
	 * @param network if you want to simulate the model in a network or null otherwise.
	 * @param simulationTime given in milliseconds.It represents the accuracy of the simulation.
	 * Is recommended to use one of the use (ACCURACY_HIGH, ACCURACY_MEDIUM or ACCURACY_LOW).
	 * Using other value is up to you but it may be not efficient or not accurate.
	 */
	public Simulation(Component canvas, Map map, Network network, int windowWidth, int windowHeight, long simulationTime) {
		this.simulationSpeed = 1;
		this.simulationTime = simulationTime;
		this.paintingTime = simulationTime;
		this.frameTime = simulationTime / 1000d;
		this.canvas = canvas;
		this.map = map;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.mapDrawer = new MapDrawer(map);
		this.network = network;
		this.vehicles = new ArrayList<Vehicle>();
		this.vehicleColors = new ArrayList<Color>();
		resumeSimulation();
	}

	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	public void setPaintingSpeed(long paintingTime) {
		this.paintingTime = paintingTime;
	}

	public Map getMap() {
		return map;
	}

	public Network getNetwork() {
		return network;
	}

	public void addVehicle(Vehicle vehicle, Color color) {
		vehicles.add(vehicle);
		vehicleColors.add(color);
	}

	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}

	public void addUpdatableElementToThread(Updatable element) {
		if(updateElements == null) {
			updateElements = new LinkedList<Updatable>();
		}
		updateElements.add(element);
	}

	public double getSimulationSpeed() {
		return simulationSpeed;
	}

	public void increaseSimulationSpeed() {
		this.simulationSpeed /= 2;
	}

	public void decreaseSimulationSpeed() {
		this.simulationSpeed *= 2;
	}

	public void setRealTimeSimulation() {
		this.simulationSpeed = 1;
	}

	public void setMaximumSimulationSpeed() {
		this.simulationSpeed = -1;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			if(pause) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {}
			}
			realTime += simulationTime;
			long runningTime = System.currentTimeMillis();
			for (int i = 0; i < vehicles.size(); i++) {
				vehicles.get(i).drive(frameTime);
			}
			if(network != null) {
				network.updateConnections(realTime);
			}
			if(updateElements != null) {
				Iterator<Updatable> it = updateElements.iterator();
				while(it.hasNext()) {
					it.next().update(this);
				}
			}
			if(canvas != null)
				repaint();
			if(simulationSpeed > 0) {
				int sleepTime = (int) (simulationTime * simulationSpeed);
				runningTime = System.currentTimeMillis() - runningTime;
				if (runningTime < sleepTime) {
					try {Thread.sleep(sleepTime - runningTime);} catch (InterruptedException ex) {}
				}
				lastSleepTime = (int) (sleepTime - runningTime);
			} else {

			}
		}
	}

	public void pauseSimulation() {
		pause = true;
	}

	public void resumeSimulation() {
		lastPaint = System.currentTimeMillis();
		pause = false;
		synchronized(this) {
			notify();
		}
	}

	public void stopSimulation() {
		running = false;
	}

	private void repaint() {
		long timeSinceLastPainting = System.currentTimeMillis()-lastPaint;
		if(timeSinceLastPainting*1.05 > paintingTime) {
			canvas.repaint();
			lastPaint = System.currentTimeMillis();
		}
	}

	/**
	 * This method must be called in the paintComponent(Graphics) method that you passed in the constructor.
	 * @param graphics
	 * @param ppm is the pixels per meter. Determines the scale of the painting.
	 */
	public void repaint(Graphics graphics, double ppm) {

		// Square surrounding
		graphics.setColor(borderColor);
		graphics.drawRect(0, 0, (int) (map.getXBounds() * ppm), (int) (map.getYBounds() * ppm));
		graphics.drawRect(1, 1, (int) (map.getXBounds() * ppm) - 2, (int) (map.getYBounds() * ppm) - 2);
		
		// Painting the map
		mapDrawer.repaint(graphics, windowWidth, windowHeight, 0, 0, ppm);

		// Painting the connections
		if(network != null) {
			graphics.setColor(networkColor);
			for (int i = 0; i < network.getNodes().size(); i++) {
				Node node = network.getNodes().get(i);
				Iterator<Node> it = network.getConnections(i);
				while (it.hasNext()) {
					try {
						Node neightbour = it.next();
						if (neightbour.getId() > node.getId()) {
							graphics.drawLine((int) (node.getX() * ppm), (int) (node.getY() * ppm), (int) (neightbour.getX() * ppm), (int) (neightbour.getY() * ppm));
						}
					} catch (UnavailableInformationException ex) {
					}
				}
			}
		}

		// Painting the robots
		for (int i = 0; i < vehicles.size(); i++) {
			graphics.setColor(vehicleColors.get(i));
			Painter.paintVehicle(graphics, vehicles.get(i), ppm);
		}
	}

	/**
	 * Simulation class also controls the time since simulation has started but in the simulation scale because it can goes faster or slower than the real life.
	 * @return real time simulation in milliseconds.
	 */
	public long getRealTime() {
		return realTime;
	}

}
