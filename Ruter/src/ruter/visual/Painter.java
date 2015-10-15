/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ruter.visual;

import ruter.simulator.Updatable;
import ruter.simulator.Vehicle;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import ruter.map.MapComponent;
import ruter.network.Node;
import ruter.network.UnavailableInformationException;

/**
 *
 * @author Camilo Ortegon
 */
public class Painter {

	public static void paintVehicle(Graphics g, Vehicle v, double ppm) {
		int x[] = new int[4];
		int y[] = new int[4];
		v.formShape(x, y, ppm);
		g.fillPolygon(x, y, 4);
		LinkedList<Updatable> accesories = v.getAccesories();
		if(accesories != null) {
			Iterator<Updatable> it = accesories.iterator();
			while(it.hasNext()) {
				Updatable a = it.next();
				if(a instanceof Drawable) {
					((Drawable) a).draw(g, ppm);
				}
			}
		}
	}

	public static void paintVehicles(Graphics g, Vehicle[] vehicles, double ppm) {
		for (int i = 0; i < vehicles.length; i++)
			paintVehicle(g, vehicles[i], ppm);
	}

	public static void paintVehicle(Graphics g, Vehicle v) {
		g.fillPolygon(v.shapeX, v.shapeY, 4);
	}
	
	public static void paintSpotRepresetationOfMapComponent(Graphics g, MapComponent component, double ppm, double radius) {
		g.fillOval((int)(component.getX()*ppm - radius), (int)(component.getY()*ppm - radius), (int)(radius*2), (int)(radius*2));
	}

}
