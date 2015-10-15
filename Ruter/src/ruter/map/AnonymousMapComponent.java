package ruter.map;

public class AnonymousMapComponent implements MapComponent {

	private double x, y;

	/**
	 * This class uses only the basic attributes of a MapComponent, and it doesn't provide any other functionality.
	 * @param x position
	 * @param y position
	 */
	public AnonymousMapComponent(double x, double y) {
		this.x = x;
		this.y = y;
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
