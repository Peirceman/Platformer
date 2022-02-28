package com.platformer.supers;

import javax.swing.JPanel;
import java.awt.Font;
import java.awt.FontMetrics;

public abstract class GamePanel extends JPanel {

    public static final int SCREEN_WIDTH  = 1050;
    public static final int SCREEN_HEIGHT = 600;
    protected static Font font = new Font("Comic sans ms", Font.BOLD, 40);
    protected static FontMetrics metrics = null;
    protected static int FONT_HEIGHT;

    public GamePanel() {
        super(true);
        if (metrics == null) {
            metrics = getFontMetrics(font);
            FONT_HEIGHT = metrics.getHeight();
        }
    }

    public abstract void start();
}