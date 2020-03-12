package guiLayer;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Cell {
	
	private int Xcoord;
	private int Ycoord;
	private String type;
	private String resourcePath;
	private BufferedImage image;
	
	public Cell(int x, int y, String type) {
		setXcoord(x);
		setYcoord(y);
		setType(type);
		setResourcePath("images/"+type+".png");
		try {
			this.image = ImageIO.read(getClass().getResource(getResourcePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getXcoord() {
		return Xcoord;
	}

	public void setXcoord(int xcoord) {
		Xcoord = xcoord;
	}

	public int getYcoord() {
		return Ycoord;
	}

	public void setYcoord(int ycoord) {
		Ycoord = ycoord;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
