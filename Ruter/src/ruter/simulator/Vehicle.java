/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.simulator;

import java.util.Iterator;
import java.util.LinkedList;

import ruter.exceptions.NotDriverException;
import ruter.map.Geometry;
import ruter.map.Map;
import ruter.map.MapComponent;
import ruter.map.PathSection;

/**
 *
 * @author Camilo Ortegon
 */
public class Vehicle implements MapComponent {

	public double x, y;
	public double radians;
	public double direction, speed;
	protected double length, width, height;

	// Driver
	private Driver driver;

	// Map
	private Map map;

	// Vehicle accesories
	private LinkedList<Updatable> vehicleAccesories;

	// Node
	protected PathSection node;

	// Front distances
	protected double rightApproach[], frontApproach, leftApproach[];

	// Backside distances
	protected double bRightApproach[], backApproach, bLeftApproach[];

	// Shape to draw
	public int shapeX[] = new int[4];
	public int shapeY[] = new int[4];

	/**
	 *
	 * @param x initial coordinate value.
	 * @param y initial coordinate value.
	 * @param length of the vehicle.
	 * @param width of the vehicle.
	 * @param height of the vehicle.
	 * @param degrees initial alignment.
	 */
	public Vehicle(double x, double y, double length, double width, double height, double degrees) {
		this.x = x;
		this.y = y;
		this.radians = Math.toRadians(degrees);
		this.length = length;
		this.width = width;
		this.height = height;
	}

	/**
	 * This method assigns a driver that is used after to determine the vehicle's behavior.
	 * @param driver that will drive this vehicle.
	 */
	public void putDriver(Driver driver) {
		this.driver = driver;
		this.driver.obtainVehicle(this);
	}

	/**
	 * If this method is call, the vehicle will update its position in the Map every time it changes it position.
	 * @param map to be assigned to track its position.
	 */
	public void putMap(Map map) {
		this.map = map;
		this.map.updateInstance(this);
	}

	/**
	 * Removes the Map to prevent it tracks its position during the simulation.
	 */
	public void removeMap() {
		if(map != null) {
			this.map.unregisterInstance(this);
			this.map = null;
		}
	}

	/**
	 * Accessories are moved with the vehicle and must be Updatable instances to update its position every time the vehicle moves.
	 * @param accesory to be attached to this vehicle.
	 */
	public void addAccesory(Updatable accesory) {
		if(vehicleAccesories == null) {
			vehicleAccesories = new LinkedList<Updatable>();
		}
		vehicleAccesories.add(accesory);
	}

	public Map getMap() {return map;}
	public LinkedList<Updatable> getAccesories() {return vehicleAccesories;}
	public Driver getDriver() {return driver;}

	/**
	 * Builds the vehicle shape.
	 * @param cx coordinates of x. No initial value needed, only make sure its size is 4.
	 * @param cy coordinates of y. No initial value needed, only make sure its size is 4.
	 * @param scale of the drawing.
	 */
	public void formShape(int cx[], int cy[], double scale) {
		double angle = radians + (Math.PI / 2);
		double xGap = (width / 2) * Math.cos(angle) * scale;
		double yGap = (width / 2) * Math.sin(angle) * scale;
		cx[0] = (int) (x * scale + xGap);
		cy[0] = (int) (y * scale + yGap);
		cx[1] = (int) (x * scale - xGap);
		cy[1] = (int) (y * scale - yGap);
		cx[2] = (int) (x * scale - xGap - length * Math.cos(radians) * scale);
		cy[2] = (int) (y * scale - yGap - length * Math.sin(radians) * scale);
		cx[3] = (int) (x * scale + xGap - length * Math.cos(radians) * scale);
		cy[3] = (int) (y * scale + yGap - length * Math.sin(radians) * scale);
	}

	/**
	 * Builds the vehicle shape and saves it in the shapeX and shapeY vector.
	 * @param pixelSize
	 */
	public void formShape(double pixelSize) {
		formShape(shapeX, shapeY, pixelSize);
	}

	public void requestFrontalVision() {
	}

	public void requestBacksideVision() {
	}

	public void autoDrive(double objectiveX, double objectiveY, double turningAngle, double maxAngle, double frameDuration) {
		autoDrive(objectiveX, objectiveY, turningAngle * frameDuration, maxAngle);
	}

	/**
	 * Calculates the next moving of the direction according to the given objective.
	 * @param objectiveX objective point.
	 * @param objectiveY objective point.
	 * @param turningAngle maximum angle of turn.
	 * @param maxAngle maximum angle to set the direction.
	 */
	public void autoDrive(double objectiveX, double objectiveY, double turningAngle, double maxAngle) {

		// Calculating pointing angle
		double pointingAngle = Geometry.angleBetweenTwoPoints(x, y, objectiveX, objectiveY);

		// Searching objective
		double angleDifference = Geometry.angleDifference(radians, pointingAngle);
		if (Math.abs(angleDifference) < direction && direction < turningAngle) {
			direction = 0;
			radians = pointingAngle;
			return;
		}
		double timesToArrive = Math.abs(angleDifference / direction);
		double timesToReturnDirection = Math.abs(direction / turningAngle);
		if (timesToReturnDirection < timesToArrive) {
			if (angleDifference < 0) {
				direction -= turningAngle;
			} else {
				direction += turningAngle;
			}
		} else {
			if (direction > 0) {
				direction -= turningAngle;
			} else {
				direction += turningAngle;
			}
		}

		// Setting maximum direction
		if (direction > maxAngle) {
			direction = maxAngle;
		} else if (direction < -maxAngle) {
			direction = -maxAngle;
		}
	}

	/**
	 * This method is called by the Simulation thread to update it position.
	 * A driver must be assigned to determine the vehicle's behavior.
	 * @param frameDuration duration of the frame to determine the next position.
	 * This is because simulation speed can be modified on the way.
	 * @throws NotDriverException 
	 */
	public void drive(double frameDuration) {
		try {
		driver.drive(frameDuration);
		} catch(NullPointerException e) {
			throw new RuntimeException(new NotDriverException("This vehicle doesn't have a Driver"));
		}
		roll(frameDuration);
	}

	/**
	 * Roll only applies the cinematic rules to calculate the next position of the robot.
	 * @param frameDuration duration of the frame to determine the next position.
	 * This is because simulation speed can be modified on the way.
	 */
	public void roll(double frameDuration) {
		this.radians += (direction * speed) / (length * 16);
		this.radians = Geometry.checkAngle(radians);
		this.x -= speed * frameDuration * Math.cos(radians);
		this.y -= speed * frameDuration * Math.sin(radians);
		if(vehicleAccesories != null)
			updateVehicleAccesories();
		if(map != null)
			map.updateInstance(this);
	}

	private void updateVehicleAccesories() {
		Iterator<Updatable> it = vehicleAccesories.iterator();
		while(it.hasNext()) {
			it.next().update(x, y);
		}
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
