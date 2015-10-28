/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import ruter.exceptions.MapException;


/**
 *
 * @author Camilo Ortegon
 */
public class Map {

	private double width, height, gridSize, accuracyLevel;
	private int horizontalGrid, verticalGrid;
	private ArrayList<MapComponent> sectors[][];
	private HashMap<MapComponent, int[]> mapPosition;
	public LinkedList<PathSection> pathsections;
	private PathSection drawingPath;

	/**
	 * The Map object helps other object to locate themselves and other elements in the simulation.
	 * Map is divided in a grid scheme to optimize collisions and provide information from the world.
	 * @param width of the Map.
	 * @param height of the Map.
	 * @param gridSize dosen't affect the collisions but the speed its made. If you're not going to use this feature set the maximum double number.
	 */
	public Map(double width, double height, double gridSize) {
		this.width = width;
		this.height = height;
		this.gridSize = gridSize;
		this.accuracyLevel = gridSize/4;

		this.horizontalGrid = ((int) (width/gridSize));
		this.verticalGrid = ((int) (height/gridSize));
		if(horizontalGrid < 1)
			horizontalGrid = 1;
		if(verticalGrid < 1)
			verticalGrid = 1;

		this.sectors = new ArrayList[horizontalGrid][verticalGrid];
		this.mapPosition = new HashMap<MapComponent, int[]>();
		this.pathsections = new LinkedList<>();
	}

	public double getXBounds() {return width;}
	public double getYBounds() {return height;}
	public double getGridSize() {return gridSize;}

	/**
	 * This method is used to the the Map that a MapComponent has updated its position.
	 * @param component
	 */
	public void updateInstance(MapComponent component) {

		// Finding the sector for the las PathSection
		int xIndex = (int) (component.getX()/gridSize);
		int yIndex = (int) (component.getY()/gridSize);
		if(xIndex < 0)
			xIndex = 0;
		else if(xIndex >= sectors.length)
			xIndex = sectors.length - 1;
		if(yIndex < 0)
			yIndex = 0;
		else if(yIndex >= sectors[0].length)
			yIndex = sectors[0].length - 1;

		int[] mySector = mapPosition.get(component);
		if(mySector == null) {
			mySector = new int[2];
			mySector[0] = xIndex;
			mySector[1] = yIndex;
			if(sectors[mySector[0]][mySector[1]] == null)
				sectors[mySector[0]][mySector[1]] = new ArrayList<MapComponent>();
			sectors[mySector[0]][mySector[1]].add(component);
			mapPosition.put(component, mySector);
		} else if(xIndex != mySector[0] || yIndex != mySector[1]) {
			sectors[mySector[0]][mySector[1]].remove(component);
			mySector[0] = xIndex;
			mySector[1] = yIndex;
			if(sectors[mySector[0]][mySector[1]] == null)
				sectors[mySector[0]][mySector[1]] = new ArrayList<MapComponent>();
			sectors[mySector[0]][mySector[1]].add(component);
		}
	}

	/**
	 * This method removes a component from the map to stop tracking its position.
	 * @param component
	 */
	public void unregisterInstance(MapComponent component) {
		int[] mySector = mapPosition.get(component);
		if(mySector != null) {
			sectors[mySector[0]][mySector[1]].remove(component);
		}
	}

	/**
	 * Provides statistics information about the density of the instances in the Map.
	 * @return
	 */
	public double[][] obtainSectorsDensification() {
		double[][] densification = new double[horizontalGrid][verticalGrid];
		for (int i = 0; i < horizontalGrid; i++) {
			for (int j = 0; j < verticalGrid; j++) {
				if(sectors[i][j] != null)
					densification[i][j] = sectors[i][j].size();
			}
		}
		return densification;
	}

	/**
	 * Increase the desnification matrix according to the number of instances in each sector.
	 * @param densification densification the previous matrix processed by this method. The first time call obtainSectorsDensification().
	 * @param changingSpeed value from 0 to 1. The biggest the value the most importance given to the new state.
	 * @return
	 */
	public double modifySectorsDensification(double[][] densification, double changingSpeed) {
		double max = 0;
		final double complement = (1-changingSpeed);
		for (int i = 0; i < horizontalGrid; i++) {
			for (int j = 0; j < verticalGrid; j++) {
				if(sectors[i][j] != null) {
					densification[i][j] = densification[i][j]*complement + sectors[i][j].size()*changingSpeed;
					if(densification[i][j] > max)
						max = densification[i][j];
				}
			}
		}
		return max;
	}

	/**
	 * Increase the desnification matrix according to the number of instances in each sector.
	 * @param densification the previous matrix processed by this method. The first time call obtainSectorsDensification().
	 * @param accessTime this value helps not to keep the values under the the maximum double.
	 * @return
	 */
	public double modifySectorsDensification(double[][] densification, int accessTime) {
		double max = 0;
		for (int i = 0; i < horizontalGrid; i++) {
			for (int j = 0; j < verticalGrid; j++) {
				if(sectors[i][j] != null) {
					densification[i][j] += sectors[i][j].size();
					if(densification[i][j] > max)
						max = densification[i][j];
				}
			}
		}
		return max;
	}

	/**
	 * This method is used to draw streets.
	 * @param x coordinate.
	 * @param y coordinate.
	 * @param laneWidth with of the each lane from the street.
	 * @param leftLanes number of lanes in the left of the main lane.
	 * @param rightLanes number of lanes in the right of the main lane.
	 * @throws MapException if something went wrong.
	 */
	public PathSection makePath(double x, double y, double laneWidth, int leftLanes, int rightLanes) throws MapException {

		if(drawingPath == null)
			drawingPath = new PathSection(x, y, laneWidth, leftLanes, rightLanes, null);
		else {
			pathsections.add(drawingPath);
			drawingPath.finishStartingPoint(x, y);
			collideOtherSections();
			drawingPath = new PathSection(x, y, laneWidth, leftLanes, rightLanes, drawingPath);
		}
		return drawingPath;
	}

	private void collideOtherSections() {

		double x = drawingPath.x;
		double y = drawingPath.y;

		// Finding the sector for the las PathSection
		int myX = (int) (x/gridSize);
		int myY = (int) (y/gridSize);
		int endingX = (int) (drawingPath.getEndX()/gridSize);
		int endingY = (int) (drawingPath.getEndY()/gridSize);

		// Collide with other PathSection in the sector
		if(sectors[endingX][endingY] != null)
			joinPath(drawingPath, endingX, endingY);

		// Colliding to nearby zone
		boolean left = (endingX > 0 && x - endingX*gridSize < accuracyLevel);
		boolean right = (endingX < sectors.length - 1 && (endingX+1)*gridSize - x < accuracyLevel);
		boolean up = (endingY > 0 && y - endingY*gridSize < accuracyLevel);
		boolean down = (endingY < sectors[0].length - 1 && (endingY+1)*gridSize - y < accuracyLevel);

		if(left && sectors[endingX-1][endingY] != null)
			joinPath(drawingPath, endingX-1, endingY);

		if(right && sectors[endingX+1][endingY] != null)
			joinPath(drawingPath, endingX+1, endingY);

		if(up && sectors[endingX][endingY-1] != null)
			joinPath(drawingPath, endingX, endingY-1);

		if(down && sectors[endingX][endingY+1] != null)
			joinPath(drawingPath, endingX, endingY+1);

		if(up && left && sectors[endingX-1][endingY-1] != null)
			joinPath(drawingPath, endingX-1, endingY-1);

		if(up && right && sectors[endingX+1][endingY-1] != null)
			joinPath(drawingPath, endingX+1, endingY-1);

		if(down && left && sectors[endingX-1][endingY+1] != null)
			joinPath(drawingPath, endingX-1, endingY+1);

		if(down && right && sectors[endingX+1][endingY+1] != null)
			joinPath(drawingPath, endingX+1, endingY+1);

		// Add to the main sector
		if(sectors[myX][myY] == null)
			sectors[myX][myY] = new ArrayList<>();
		sectors[myX][myY].add(drawingPath);
	}

	private void joinPath(PathSection p, int x, int y) {
		for (int i = 0; i < sectors[x][y].size(); i++) {
			if(sectors[x][y].get(i) instanceof PathSection)
				p.joinPath((PathSection)sectors[x][y].get(i), accuracyLevel);
		}
	}

	/**
	 * Stop building an started path.
	 */
	public void finishPath() {
		this.drawingPath.finishPath();
		//		collideOtherSections();
		this.drawingPath = null;
	}

	public void finishMap() {
		if(drawingPath != null)
			finishPath();
	}

	/**
	 * Not implemented yet.
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public LinkedList<MapComponent> getNearby(MapComponent component, double radius) {

		// Finding the sector for the las PathSection
		int xIndex = (int) (component.getX()/gridSize);
		int yIndex = (int) (component.getY()/gridSize);

		boolean left = (xIndex > 0 && component.getX() - xIndex*gridSize < accuracyLevel);
		boolean right = (xIndex < sectors.length - 1 && (xIndex+1)*gridSize - component.getX() < accuracyLevel);
		boolean up = (yIndex > 0 && component.getY() - yIndex*gridSize < accuracyLevel);
		boolean down = (yIndex < sectors[0].length - 1 && (yIndex+1)*gridSize - component.getY() < accuracyLevel);

		LinkedList<MapComponent> nearby = new LinkedList<MapComponent>();

		if(sectors[xIndex][yIndex] != null)
			checkNearby(nearby, drawingPath, xIndex, yIndex);

		if(left && sectors[xIndex-1][yIndex] != null)
			checkNearby(nearby, drawingPath, xIndex-1, yIndex);

		if(right && sectors[xIndex+1][yIndex] != null)
			checkNearby(nearby, drawingPath, xIndex+1, yIndex);

		if(up && sectors[xIndex][yIndex-1] != null)
			checkNearby(nearby, drawingPath, xIndex, yIndex-1);

		if(down && sectors[xIndex][yIndex+1] != null)
			checkNearby(nearby, drawingPath, xIndex, yIndex+1);

		if(up && left && sectors[xIndex-1][yIndex-1] != null)
			checkNearby(nearby, drawingPath, xIndex-1, yIndex-1);

		if(up && right && sectors[xIndex+1][yIndex-1] != null)
			checkNearby(nearby, drawingPath, xIndex+1, yIndex-1);

		if(down && left && sectors[xIndex-1][yIndex+1] != null)
			checkNearby(nearby, drawingPath, xIndex-1, yIndex+1);

		if(down && right && sectors[xIndex+1][yIndex+1] != null)
			checkNearby(nearby, drawingPath, xIndex+1, yIndex+1);

		return nearby;
	}

	private void checkNearby(LinkedList<MapComponent> nearby, MapComponent c, int x, int y) {
		for (int i = 0; i < sectors[x][y].size(); i++) {
			if(sectors[x][y].get(i) != c && !(sectors[x][y].get(i) instanceof PathSection))
				nearby.add(sectors[x][y].get(i));
		}
	}
}
