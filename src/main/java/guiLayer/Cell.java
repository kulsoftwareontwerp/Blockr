package guiLayer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import guiLayer.Constants;

public class Cell implements Constants {

	private int Xcoord;
	private int Ycoord;
	private String type;
	private String resourcePath;
	private BufferedImage image;

	public Cell(int x, int y, String type) {
		setXcoord(x);
		setYcoord(y);
		setType(type);
	}

	public int getXcoord() {
		return Xcoord;
	}

	public void setXcoord(int xcoord) {

		int x = GAME_START_X + (xcoord * 50);
		Xcoord = x;
	}

	public int getYcoord() {
		return Ycoord;
	}

	public void setYcoord(int ycoord) {
		int y = (ycoord * 50);
		Ycoord = y;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null) {
			this.type = "sand";
		} else {
			this.type = type;
		}
		setResourcePath("images/"+ getType() + ".png");


		InputStream in = getClass().getClassLoader().getResourceAsStream(getResourcePath());
	

		if (in == null) {
			throw new IllegalArgumentException("image for Cell is not found ");
		} else {
			try {
				this.image = ImageIO.read(in);
			} catch (IOException e) {
				System.err.println("Got an error while loading in image");
			}
		}

	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public BufferedImage getImage() {
		return image;
	}

}
