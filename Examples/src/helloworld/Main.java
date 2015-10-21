package helloworld;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ruter.map.Map;
import ruter.map.MapException;
import ruter.map.PathSection;
import ruter.simulator.Simulation;

public class Main extends JFrame {

	private static final Dimension simulationSize = new Dimension(1000, 1000);
	private static final Dimension windowSize = new Dimension(900, 600);
	private static final long FRAME_TIME = Simulation.ACCURACY_HIGH;

	private Map map;
	private Simulation simulation;
	private PathSection firstSection;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {

		initWindow();

	}

	public void initWindow() {

		setSize(windowSize);

		ScrollPane scroll = new ScrollPane();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scroll.setPreferredSize(new Dimension((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8)));
		JPanel panelToDraw = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintCanvas(g);
			}
		};
		panelToDraw.setPreferredSize(simulationSize);
		scroll.add(panelToDraw);
		add(scroll);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void initSimulation() {

		map = new Map(simulationSize.width, simulationSize.height, 10);
		try {
			firstSection = map.makePath(80, 10, 2.5, 2, 3);
			map.makePath(150, 10, 2.5, 2, 3);
			map.makePath(160, 10, 2.5, 2, 3);
			map.makePath(180, 20, 2.5, 2, 3);
			map.makePath(190, 40, 2.5, 2, 3);
			map.makePath(190, 60, 2.5, 2, 3);
			map.makePath(180, 80, 2.5, 2, 3);
			map.makePath(160, 90, 2.5, 2, 3);
			map.makePath(140, 90, 2.5, 2, 3);
			map.makePath(120, 80, 2.5, 2, 3);
			map.makePath(110, 60, 2.5, 2, 3);
			map.makePath(110, 40, 2.5, 2, 3);
			map.makePath(120, 20, 2.5, 2, 3);
			map.makePath(140, 10, 2.5, 2, 3);
			map.makePath(150, 10, 2.5, 2, 3);
			map.finishMap();
		} catch (MapException e) {}

		simulation = new Simulation(this, map, null, simulationSize.width, simulationSize.height, FRAME_TIME);

	}

	public void paintCanvas(Graphics g) {

	}

}
