/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.map;

import java.util.ArrayList;

/**
 * Streets are made of many PathSection connected, the first node (PathSection) has previous in null and in the last the next is pointing to null.
 * The objects of this class must be created by a map instance.
 * @author Camilo Ortegon
 */
public class PathSection implements MapComponent {

	public static final int START = 0;
	public static final int END = 1;
	private static final int OBJECTIVE = 2;
	public static final int X = 0;
	public static final int Y = 1;

	public double x, y, laneWidth;
	public int leftLanes, rigthLanes;
	private PathSection previous;
	private PathSection next;
	public ArrayList<PathSection> adjacents;

	private double border[][];
	private double road[][][];
	private double roadLine[][][];

	/**
	 * PathSection initialized with the position and configuration, if is the head of the path previous must be set as null.
	 * @param x Position in x axis
	 * @param y Position in y axis
	 * @param laneWidth 
	 * @param leftLanes
	 * @param rigthLanes
	 * @param previous The constructing pathSection that goes before
	 */
	public PathSection(double x, double y, double laneWidth, int leftLanes, int rigthLanes, PathSection previous) throws MapException {

		this.x = x;
		this.y = y;

		if(leftLanes < 0 || rigthLanes < 0)
			throw new MapException("leftLanes and rigthLanes paramethers must be 0 or positive integer");

		this.laneWidth = laneWidth;
		this.leftLanes = leftLanes;
		this.rigthLanes = rigthLanes;

		// Start figure points
		border = new double[2][4];
		road = new double[2][2][leftLanes+rigthLanes+1];
		roadLine = new double[2][2][leftLanes+rigthLanes];
		road[START][X][leftLanes] = x;
		road[START][Y][leftLanes] = y;

		this.previous = previous;
	}

	protected void finishStartingPoint(double x, double y) {
		road[END][X][leftLanes] = x;
		road[END][Y][leftLanes] = y;

		double angle = 0;
		if(previous == null)
			angle = Geometry.angleBetweenTwoPoints(getEndX(), getEndY(), getStartX(), getStartY()) + Math.PI/2;
		else
			angle = Geometry.angleBetweenTwoPoints(getEndX(), getEndY(), previous.getStartX(), previous.getStartY()) + Math.PI/2;

		// Defining starting border
		border[X][0] = getStartX()-laneWidth*(leftLanes+0.5)*Math.cos(angle);
		border[Y][0] = getStartY()-laneWidth*(leftLanes+0.5)*Math.sin(angle);
		border[X][1] = getStartX()+laneWidth*(rigthLanes+0.5)*Math.cos(angle);
		border[Y][1] = getStartY()+laneWidth*(rigthLanes+0.5)*Math.sin(angle);

		// Defining starting points of lanes
		for (int i = 0; i < leftLanes; i++) {
			road[START][X][i] = getStartX()-laneWidth*(leftLanes-i)*Math.cos(angle);
			road[START][Y][i] = getStartY()-laneWidth*(leftLanes-i)*Math.sin(angle);
			roadLine[START][X][i] = getStartX()-laneWidth*(i+0.5)*Math.cos(angle);
			roadLine[START][Y][i] = getStartY()-laneWidth*(i+0.5)*Math.sin(angle);
		}
		for (int i = 0; i < rigthLanes; i++) {
			road[START][X][i+leftLanes+1] = getStartX()+laneWidth*(i+1)*Math.cos(angle);
			road[START][Y][i+leftLanes+1] = getStartY()+laneWidth*(i+1)*Math.sin(angle);
			roadLine[START][X][i+leftLanes] = getStartX()+laneWidth*(i+0.5)*Math.cos(angle);
			roadLine[START][Y][i+leftLanes] = getStartY()+laneWidth*(i+0.5)*Math.sin(angle);
		}

		if(previous != null)
			previous.forwardFinish(this, border, angle);
	}

	protected void finishPath() {
		if(previous != null) {
			// Defining ending border from previous
			double angle = Geometry.angleBetweenTwoPoints(getStartX(), getStartY(), previous.getStartX(), previous.getStartY()) + Math.PI/2;
			border[X][0] = getStartX()-laneWidth*(leftLanes+0.5)*Math.cos(angle);
			border[Y][0] = getStartY()-laneWidth*(leftLanes+0.5)*Math.sin(angle);
			border[X][1] = getStartX()+laneWidth*(rigthLanes+0.5)*Math.cos(angle);
			border[Y][1] = getStartY()+laneWidth*(rigthLanes+0.5)*Math.sin(angle);

			previous.forwardFinish(null, border, angle);
		}
	}

	private void forwardFinish(PathSection next, double[][] nextBorder, double angle) {
		this.next = next;
		border[X][3] = nextBorder[X][0];
		border[Y][3] = nextBorder[Y][0];
		border[X][2] = nextBorder[X][1];
		border[Y][2] = nextBorder[Y][1];

		// Defining starting points of lanes
		for (int i = 0; i < leftLanes; i++) {
			road[END][X][i] = getEndX()-laneWidth*(leftLanes-i)*Math.cos(angle);
			road[END][Y][i] = getEndY()-laneWidth*(leftLanes-i)*Math.sin(angle);
			roadLine[END][X][i] = getEndX()-laneWidth*(i+0.5)*Math.cos(angle);
			roadLine[END][Y][i] = getEndY()-laneWidth*(i+0.5)*Math.sin(angle);
		}
		for (int i = 0; i < rigthLanes; i++) {
			road[END][X][i+leftLanes+1] = getEndX()+laneWidth*(i+1)*Math.cos(angle);
			road[END][Y][i+leftLanes+1] = getEndY()+laneWidth*(i+1)*Math.sin(angle);
			roadLine[END][X][i+leftLanes] = getEndX()+laneWidth*(i+0.5)*Math.cos(angle);
			roadLine[END][Y][i+leftLanes] = getEndY()+laneWidth*(i+0.5)*Math.sin(angle);
		}
	}

	public PathSection getPrevious() {return previous;}
	public PathSection getNext() {return next;}
	public double getLaneStartX(int lan) {return road[START][X][lan];}
	public double getLaneStartY(int lan) {return road[START][Y][lan];}
	public double getLaneEndX(int lan) {return road[END][X][lan];}
	public double getLaneEndY(int lan) {return road[END][Y][lan];}
	public double getLaneObjectiveX(int lan) {return road[END][X][lan];}
	public double getLaneObjectiveY(int lan) {return road[END][Y][lan];}
	public double getStartX() {return road[START][X][leftLanes];}
	public double getStartY() {return road[START][Y][leftLanes];}
	public double getEndX() {return road[END][X][leftLanes];}
	public double getEndY() {return road[END][Y][leftLanes];}
	public double[][][] getPaintingLanes() {return roadLine;}

	/**
	 * This method is used to join 2 PahtSection if they are closer than the minimalAproach
	 */
	public void joinPath(PathSection pathToJoin, double minimalAproach) {
		// Checking if it's close to me
		if(Geometry.distance(getEndX(), getEndY(), pathToJoin.getStartX(), pathToJoin.getStartY()) < minimalAproach) {
			if(adjacents == null)
				adjacents = new ArrayList<PathSection>();
			adjacents.add(pathToJoin);
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

	public double[][] getBorder() {
		return border;
	}

	public int getNumberOfRoads() {
		return leftLanes + rigthLanes + 1;
	}

	public int getNumberOfAdjacents() {
		if(adjacents == null)
			return 0;
		else
			return adjacents.size();
	}

}
