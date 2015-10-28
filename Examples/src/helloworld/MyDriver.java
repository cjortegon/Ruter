package helloworld;

import ruter.map.Geometry;
import ruter.map.PathSection;
import ruter.simulator.Driver;
import ruter.simulator.Vehicle;

public class MyDriver implements Driver {

	private static final double MAX_ANGLE_RANGE = Math.PI * 1.5;
	private static final double SPEED_LIMIT = 16;
	private static final double CHANGING_LIMIT = SPEED_LIMIT / 2;
	private static final double TURNING_ANGLE = Math.PI;

	private Vehicle vehicle;
	private PathSection section;
	private double nextX, nextY;
	private int lane;

	public MyDriver(PathSection section) {
		this.section = section;
		this.lane = 2;
		this.nextX = section.getLaneEndX(lane);
		this.nextY = section.getLaneEndY(lane);
	}

	@Override
	public void drive(double frameDuration) {

		this.vehicle.speed = SPEED_LIMIT;

		double distance = Geometry.distance(vehicle.x, vehicle.y, nextX, nextY);
		if(distance < CHANGING_LIMIT) {
			if(section.getNext() == null) {
				if(section.adjacents != null) {
					this.section = section.adjacents.get(0);
				}
			} else
				this.section = section.getNext();
			this.nextX = section.getLaneEndX(lane);
			this.nextY = section.getLaneEndY(lane);
		}

		this.vehicle.autoDrive(nextX, nextY, TURNING_ANGLE * frameDuration, MAX_ANGLE_RANGE);
	}

	@Override
	public void obtainVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

}
