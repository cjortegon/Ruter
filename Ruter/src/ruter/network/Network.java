/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ruter.network;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;

/**
 *
 * @author Camilo Ortegon
 */
public abstract class Network {

	/**
	 * Contains the instances of the network.
	 */
	protected CopyOnWriteArrayList<Node> nodes;

	/**
	 * Initialize the network.
	 */
	public Network() {
		this.nodes = new CopyOnWriteArrayList<>();
	}

	/**
	 * Add the new node to the network and assigns an id to it.
	 * @param node
	 */
	public void addNode(Node node) {
		node.setId(nodes.size());
		this.nodes.add(node);
	}

	public CopyOnWriteArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * Returns the iterator of connections from one node.
	 * @param from is the ID of the node.
	 * @return The iterator of nodes that has connections to the selected node.
	 */
	public Iterator<Node> getConnections(int from) {
		return nodes.get(from).connections.keySet().iterator();
	}

	public abstract void updateConnections(long clock);

}
