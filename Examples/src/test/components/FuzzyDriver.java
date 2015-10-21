/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import ruter.simulator.Driver;
import ruter.simulator.Simulation;
import ruter.simulator.Vehicle;
import ruter.map.Geometry;
import ruter.map.MapComponent;
import ruter.map.PathSection;
import ruter.network.NetworkPackage;
import ruter.network.Node;
import ruter.map.Map;
import ruter.network.UnavailableInformationException;

/**
 *
 * @author camilo
 */
public class FuzzyDriver implements Driver {

	private static final double MAX_ANGLE_RANGE = Math.PI * 1.5;
	private static final double SPEED_LIMIT = 16;
	private static final double CHANGING_LIMIT = SPEED_LIMIT / 2;
	private static final double ACELERATION = SPEED_LIMIT / 4;
	private static final double TURNING_ANGLE = Math.PI;

	private Vehicle v;
	private Map m;
	private PathSection section;
	private Simulation simulation;
	private static final Random random = new Random();
	public int lane;
	public double nextX, nextY, endX, endY;

	public FuzzyDriver(Map map, PathSection section, double frameDuration, Simulation simulation) {
		this.m = map;
		this.section = section;
		this.nextX = section.getEndX();
		this.nextY = section.getEndY();
		this.endX = nextX;
		this.endY = nextY;
		this.simulation = simulation;
		this.lane = 1;
	}

	@Override
	public void drive(double frameDuration) {
	}

	private void selectNextPosition() {
		if(section.getNext() == null) {
			if(section.adjacents != null) {
				section = section.adjacents.get(random.nextInt(12345) % section.adjacents.size());
			}
		} else
			section = section.getNext();
		nextX = section.getLaneEndX(lane);
		nextY = section.getLaneEndY(lane);
	}

	// ************************ Override methods ************************

	@Override
	public void obtainVehicle(Vehicle v) {
		this.v = v;
		this.v.putMap(m);
	}

}
