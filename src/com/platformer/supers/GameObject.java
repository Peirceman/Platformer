package com.platformer.supers;

import com.platformer.Main;
import com.platformer.main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class GameObject {
	public static int UNIT_SIZE = 50;

	protected int x, y;
	protected BufferedImage image;
	protected static BufferedImage textureMap;
	protected final double g =  50 / Game.TICKS;

	static {
		try {
			InputStream is = Main.class.getResourceAsStream("/res/textures.png");

			GameObject.textureMap = ImageIO.read(Objects.requireNonNull(is));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NullPointerException e) {
			System.err.println("a fatal error has occurred: '/res/textures.png' was not found");
			System.exit(1);
		}
	}

	public GameObject (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void draw(Graphics g) {
		g.drawImage(image, x, y, null);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" +
				"x=" + this.x +
				", y=" + this.y +
				"]";
	}
}
