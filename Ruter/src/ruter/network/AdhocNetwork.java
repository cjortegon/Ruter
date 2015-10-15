/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.network;

import ruter.map.Geometry;

/**
 *
 * @author Camilo Ortegon
 */
public class AdhocNetwork extends Network {

	private double minimumDistance;
	private int adjacencyMatrix[][];

	public double getMinimumDistanceConnection() {return minimumDistance;}

	/**
	 * As Ad-Hoc networks has no infrastructure, the minimum distance specifies the distance of visibility between each other.
	 * @param minimumDistance to be connected.
	 */
	public AdhocNetwork(double minimumDistance) {
		this.minimumDistance = minimumDistance;
	}

	/**
	 * Updates the connections according to its minimum distance between each other specified in the constructor
	 */
	public void updateConnections(long clock) {
		int connections[] = new int[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			connections[i] = nodes.get(i).connections.size();
		}
		adjacencyMatrix = new int[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				try {
					double distance = Geometry.distance(nodes.get(i).getX(), nodes.get(i).getY(), nodes.get(j).getX(), nodes.get(j).getY());
					if (distance < minimumDistance) {
						nodes.get(i).connections.put(nodes.get(j), comunicationQuality(connections[i], distance));
						nodes.get(j).connections.put(nodes.get(i), comunicationQuality(connections[j], distance));
						adjacencyMatrix[i][j] = 1;
						adjacencyMatrix[j][i] = 1;
					} else {
						nodes.get(i).connections.remove(nodes.get(j));
						nodes.get(j).connections.remove(nodes.get(i));
						adjacencyMatrix[i][j] = Integer.MAX_VALUE;
						adjacencyMatrix[j][i] = Integer.MAX_VALUE;
					}
				} catch(UnavailableInformationException uie) {}
			}
		}
		// Make nodes send the messages
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).timeToSendMessages(clock);
		}
	}

	private double comunicationQuality(int interferenceSignals, double distance) {
		//        double v = (minimumDistance-distance)/(minimumDistance*(Math.log10(interferenceSignals+10)+1));
		double v = Math.pow(minimumDistance-distance, 2)/Math.pow(minimumDistance, 2);
		//        if(v < 0.05)
		//            System.out.println("Quality: "+v);
		return v;
	}

	/**
	 * Tells the number of sub groups formed in the network, meaning that in each one every node can reach the others from the same subnet.
	 * Floyd Warshall algorithm is used to determine this.
	 * @return the number of sub groups formed.
	 */
	public int numberOfSubnets() {
		//		int path[][] = adjacencyMatrix.clone();
		//        int next[][] = new int[adjacencyMatrix.length][adjacencyMatrix.length];
		for (int k = 0; k < adjacencyMatrix.length; k++) {
			for (int i = 0; i < adjacencyMatrix.length; i++) {
				for (int j = 0; j < adjacencyMatrix.length; j++) {
					if(adjacencyMatrix[i][k] != Integer.MAX_VALUE && adjacencyMatrix[k][j] != Integer.MAX_VALUE) {
						if (adjacencyMatrix[i][k] + adjacencyMatrix[k][j] < adjacencyMatrix[i][j]) {
							adjacencyMatrix[i][j] = adjacencyMatrix[i][k] + adjacencyMatrix[k][j];
							//                        next[i][j] = k;
						}
					}
				}
			}
		}
		int groupId = 1;
		int groups[] = new int[adjacencyMatrix.length];
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			if(groups[i] == 0) {
				groups[i] = groupId;
				for (int j = 0; j < adjacencyMatrix.length; j++) {
					if(adjacencyMatrix[i][j] != Integer.MAX_VALUE) {
						groups[j] = groupId;
					}
				}
				groupId ++;
			}
		}
		return groupId-1;
	}

}
