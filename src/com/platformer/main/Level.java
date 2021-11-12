package com.platformer.main;

import com.platformer.supers.GameObject;
import com.platformer.supers.GamePanel;

public class Level {
    public static int yObjects = GamePanel.SCREEN_HEIGHT / 50;
    public int startX;
    public int startY;
    public int width;
    public int xObjects;
    public Block[] blocks;

    public Level(int startX, int startY, int width, int xObjects, Block[] blocks) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.xObjects = xObjects;
        this.blocks = blocks;
    }

    public Level() {
        this(0, 0, 0, 0, null);
    }

    public void setWidth(int width) {
        this.width    = width;
        this.xObjects = width/ GameObject.UNIT_SIZE;
    }
}
