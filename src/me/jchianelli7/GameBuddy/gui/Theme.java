package me.jchianelli7.GameBuddy.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Theme {

	private final String name;

	private BufferedImage iconImage;
	private BufferedImage backgroundImage;

	private Color color;

	public Theme(String name) {
		this.name = name;

		try {
			this.iconImage = ImageIO.read(ClassLoader.getSystemResource("themes/" + name + "/iconImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.backgroundImage = ImageIO.read(ClassLoader.getSystemResource("themes/" + name + "/backgroundImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.color = Color.GRAY;
	}

	public String getName() {
		return name;
	}

	public boolean hasIconImage() {
		return iconImage != null;
	}

	public BufferedImage getIconImage() {
		return iconImage;
	}

	public boolean hasBackgroundImage() {
		return backgroundImage != null;
	}

	public BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	public Color getColor() {
		if (color == null) {
			return Color.GRAY;
		}
		return color;
	}

}
