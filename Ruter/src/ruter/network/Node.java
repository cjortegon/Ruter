/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ruter.network;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Camilo Ortegon
 */
public abstract class Node {

	public ConcurrentHashMap<Node, Double> connections = new ConcurrentHashMap<Node, Double>();

	public abstract double getX() throws UnavailableInformationException;
	public abstract double getY() throws UnavailableInformationException;
	public abstract void setId(int id);
	public abstract int getId();
	public abstract int getWeight();
	public abstract void timeToSendMessages(long clock);
	public abstract void receiveMessage(Node sender, NetworkPackage msj);

}
