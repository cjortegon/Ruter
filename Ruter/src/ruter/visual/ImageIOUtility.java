package ruter.visual;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageIOUtility {

	/**
	 * Attributes
	 */
	private BufferedImage image;
	private Graphics graphics;

	public ImageIOUtility(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = image.getGraphics();
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public void saveImage(File file, String format) throws IOException {
		System.gc();
		ImageIO.write(image, format, new File(file.getAbsolutePath() + "." + format));
	}

}
