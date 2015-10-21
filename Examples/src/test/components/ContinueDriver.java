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
public class ContinueDriver implements Driver {

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

	public ContinueDriver(Map map, PathSection section, double frameDuration, Simulation simulation) {
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

		//		randomChangeOfLane(50);
		intelligentChangeOfLane(frameDuration);

		double distance = Geometry.distance(v.x, v.y, nextX, nextY);
		if(distance < CHANGING_LIMIT) {
			if(endX != nextX || endY != endY) {
				nextX = endX;
				nextY = endY;
			} else {
				selectNextPosition();
				increasePosition();
			}
		}
		//		else if(distance < SPEED_LIMIT) {
		//			v.speed -= ACELERATION * frameDuration * 0.5;
		//		}
		v.autoDrive(nextX, nextY, TURNING_ANGLE * frameDuration, MAX_ANGLE_RANGE);
	}

	private void intelligentChangeOfLane(double frameDuration) {
		LinkedList<MapComponent> nearby = m.getNearby(v, (v.speed/ACELERATION)*frameDuration);
		LinkedList<Vehicle> drivers = new LinkedList<Vehicle>();
		Iterator<MapComponent> it = nearby.iterator();
		boolean goFaster = true;
		while(it.hasNext()) {
			Vehicle vehicle = (Vehicle)it.next();
			ContinueDriver driver = (ContinueDriver) vehicle.getDriver();
			if(driver.lane == lane) {
				// Is in my same lane
				if(v.speed > 0 && Geometry.distance(vehicle.getX(), vehicle.getY(), nextX, nextY) < Geometry.distance(v.x, v.y, nextX, nextY)) {
					if(vehicle.speed < v.speed) {
						v.speed -= ACELERATION * frameDuration;
						goFaster = false;
						break;
					} else if(Geometry.distance(vehicle.getX(), vehicle.getY(), v.x, v.y) < 3) {
						v.speed -= ACELERATION * frameDuration;
						goFaster = false;
						break;
					}
				}
			} else if(driver.lane == lane - 1 || driver.lane == lane + 1){
				drivers.add(vehicle);
			}
		}
		if(v.speed < SPEED_LIMIT) {
			if(goFaster) {
				if(v.speed > 0 && random.nextInt(12345) % 3 == 0)
					v.speed -= ACELERATION * frameDuration;
				else
					v.speed += ACELERATION * frameDuration;
			} else if(drivers.size() == 0) {
				randomChangeOfLane(3);
			} else {
				boolean leftFree = lane > 0;
				boolean rightFree = lane < section.getNumberOfRoads() - 1;
				Iterator<Vehicle> vit = drivers.iterator();
				while(leftFree && rightFree && vit.hasNext()) {
					Vehicle vehicle = vit.next();
					if(v.speed > 0 && Geometry.distance(vehicle.getX(), vehicle.getY(), nextX, nextY) < Geometry.distance(v.x, v.y, nextX, nextY)) {
						if(vehicle.speed < v.speed) {
							if(((ContinueDriver)vehicle.getDriver()).lane > lane) {
								rightFree = false;
							} else {
								leftFree = false;
							}
						} else if(Geometry.distance(vehicle.getX(), vehicle.getY(), v.x, v.y) < 3) {
							if(((ContinueDriver)vehicle.getDriver()).lane > lane) {
								rightFree = false;
							} else {
								leftFree = false;
							}
						}
					}
				}
				if(leftFree) {
					if(rightFree) {
						randomChangeOfLane(2);
					} else {
						double angle = setNextInFrontOfMe();
						lane --;
						angle += Math.PI/2;
						saveNextPointAndChangeActual(angle);
					}
				} else if(rightFree) {
					double angle = setNextInFrontOfMe();
					lane ++;
					angle += Math.PI/2;
					saveNextPointAndChangeActual(angle);
				}
			}
		}
		//		System.out.println("speed: "+v.speed);
	}

	private void randomChangeOfLane(int probability) {
		if(endX == nextX && endY == nextY && random.nextInt(12345) % probability == 0) {
			if(Geometry.distance(v.x, v.y, endX, endY) > SPEED_LIMIT) {

				double angle = setNextInFrontOfMe();

				if(lane == 0) {
					lane ++;
					angle -= Math.PI/2;
				} else if(lane == section.getNumberOfRoads() - 1) {
					lane --;
					angle += Math.PI/2;
				} else {
					if(random.nextInt(12345) % 2 == 0) {
						lane ++;
						angle += Math.PI/2;
					} else {
						lane --;
						angle -= Math.PI/2;
					}
				}

				saveNextPointAndChangeActual(angle);
				//				System.out.println("cambio de via "+lane);
			}
		}
	}

	private double setNextInFrontOfMe() {
		double angle = Geometry.angleBetweenTwoPoints(nextX, nextY, v.x, v.y);
		nextX = v.x + SPEED_LIMIT * Math.cos(angle);
		nextY = v.y + SPEED_LIMIT * Math.sin(angle);
		return angle;
	}

	private void saveNextPointAndChangeActual(double angle) {
		endX = section.getLaneEndX(lane);
		endY = section.getLaneEndY(lane);
		nextX += section.laneWidth * Math.cos(angle);
		nextY += section.laneWidth * Math.sin(angle);
	}

	private void increasePosition() {
		double angle = Geometry.angleBetweenTwoPoints(nextX, nextY, v.x, v.y);
		nextX += 2 * Math.cos(angle);
		nextY += 2 * Math.sin(angle);
		endX = nextX;
		endY = nextY;
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
		increasePosition();
	}

}
