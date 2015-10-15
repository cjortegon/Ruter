/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.map;

/**
 * Tools to make some geometric calculus.
 * @author camilo
 */
public class Geometry {

	/**
	 * TAU is a constant defined as 2*Pi
	 */
	public static final double TAU = Math.PI * 2;

	/**
	 * Using the 2 (x,y) pair of points it plots a line and calculates the angle from the cartesian zero placing the Tail at the (0,0)
	 * @param xHead x coordinate from head of the arrow.
	 * @param yHead y coordinate from head of the arrow.
	 * @param xTail x coordinate from tail of the arrow.
	 * @param yTail y coordinate from tail of the arrow.
	 * @return Angle calculated.
	 */
	public static double angleBetweenTwoPoints(double xHead, double yHead, double xTail, double yTail) {

		if (xHead == xTail) {
			if (yHead > yTail) {
				return Math.PI / 2;
			} else {
				return (Math.PI * 3) / 2;
			}
		} else if (yHead == yTail) {
			if (xHead > xTail) {
				return 0;
			} else {
				return Math.PI;
			}
		} else if (xHead > xTail) { // Derecha
			if (yHead > yTail) // Cuadrante 1
			{
				return Math.atan((yHead - yTail) / (xHead - xTail));
			} else // Cuadrante 4
			{
				return TAU - Math.atan((yTail - yHead) / (xHead - xTail));
			}
		} else { // Izquierda
			if (yHead > yTail) // Cuadrante 2
			{
				return Math.PI - Math.atan((yHead - yTail) / (xTail - xHead));
			} else // Cuadrante 3
			{
				return Math.PI + Math.atan((yTail - yHead) / (xTail - xHead));
			}
		}
	}

	/**
	 * Tells in witch quadrant is the angle.
	 * @param angle in radians.
	 * @return the quadrant (from 1 to 4).
	 */
	public static int quadrantAngle(double angle) {
		angle = checkAngle(angle);
		if (angle < Math.PI / 2) {
			return 1;
		} else if (angle < Math.PI) {
			return 2;
		} else if (angle < Math.PI * (3 / 4)) {
			return 3;
		} else {
			return 4;
		}
	}

	/**
	 * Distance between 2 points calculated with Pitagoras rule.
	 * @param fromX x from the first point.
	 * @param fromY y from the first point.
	 * @param toX x from the second point.
	 * @param toY y from the second point.
	 * @return The distance.
	 */
	public static double distance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromY - toY, 2));
	}

	/**
	 * Makes all the angles in radians be smaller than TAU and non-negatives.
	 * @param angle to process.
	 * @return the processed angle.
	 */
	public static double checkAngle(double angle) {
		if (angle < 0) {
			angle += (TAU);
		} else if (angle > (TAU)) {
			angle -= (TAU);
		}
		return angle;
	}

	/**
	 * This method tells you if the second angle is to the left (-1) or to the
	 * right (1).
	 * @param firstAngle the reference angle.
	 * @param secondAngle the pointing angle.
	 * @return 0 if both are same.
	 */
	public static int whereIsPointing(double firstAngle, double secondAngle) {
		if (firstAngle < 0) {
			firstAngle += TAU;
		}
		if (secondAngle < 0) {
			secondAngle += TAU;
		}
		if (firstAngle == secondAngle) {
			return 0;
		} else {
			boolean greaterThanPi = Math.abs(secondAngle - firstAngle) > Math.PI;
			boolean diference = (secondAngle - firstAngle) > 0;
			if (greaterThanPi) {
				if (diference) {
					return 1;
				} else {
					return -1;
				}
			} else {
				if (diference) {
					return -1;
				} else {
					return 1;
				}
			}
		}
	}

	/**
	 * Calculates the difference between 2 angles.
	 * @param firstAngle is the reference angle.
	 * @param secondAngle is the aimed angle.
	 * @return negative value if the second angle is to the left of the reference, and positive if it is to the right.
	 */
	public static double angleDifference(double firstAngle, double secondAngle) {
		if (firstAngle < 0)
			firstAngle += TAU;
		if (secondAngle < 0)
			secondAngle += TAU;
		boolean greaterThanPi = Math.abs(secondAngle - firstAngle) > Math.PI;
		double diference = secondAngle - firstAngle;
		if (greaterThanPi) {
			if (diference > 0)
				return - TAU + secondAngle - firstAngle;
			else
				return TAU - firstAngle + secondAngle;
		} else
			return secondAngle - firstAngle;
	}

}
