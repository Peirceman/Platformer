package com.platformer.main;

import com.platformer.supers.GameObject;

public class Block extends GameObject {
	private final int id;
	private final int arrayIndex;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" +
				"x=" + x +
				",y=" + y +
				",id=" + id +
				",arrayIndex=" + arrayIndex +
				']';
	}

	public Block(int id, int x, int y) {
		super(x, y);
		this.id = id;
		this.arrayIndex = x / 50 + y / 50 * Game.level.xObjects;
		int textureX = id % 4 == 0 ? 150 : (id % 4 - 1) * 50;
		int textureY = (id - textureX / 50) / 4 * 50;
		this.image = GameObject.textureMap.getSubimage(textureX, textureY, GameObject.UNIT_SIZE, GameObject.UNIT_SIZE);
	}

	public int getArrayIndex() {
		return this.arrayIndex;
	}

	public int getId() {
		return this.id;
	}
}
