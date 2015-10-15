/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.simulator;

/**
 *
 * @author camilo
 */
public interface Driver {

	/**
	 * This method is called by the Vehicle to make it drive him.
	 * @param frameDuration 
	 */
	public abstract void drive(double frameDuration);

	/**
	 * This method is called by the Vehicle where driver was assigned.
	 * @param vehicle that this driver is driving.
	 */
	public abstract void obtainVehicle(Vehicle vehicle);

}
