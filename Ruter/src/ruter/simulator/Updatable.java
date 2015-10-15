package ruter.simulator;

/**
 * Updatable is an interface that provides methods to include a behavior in the simulation thread contained in Simulation class.
 * @author Camilo Ortegon
 *
 */
public interface Updatable {

	public void update(Simulation simulation);
	public void update(double x, double y);

}
