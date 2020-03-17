package guiLayer;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		if(type == null) {
			this.type = "sand";
		}else {
			this.type = type;
		}
		setResourcePath("images/"+getType()+".png");
		
		try {
			this.image = ImageIO.read(getClass().getResource(getResourcePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
