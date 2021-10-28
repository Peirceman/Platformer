package com.platformer.main;

import com.platformer.supers.GamePanel;
import com.platformer.util.Format;
import com.platformer.util.JSONReader;
import com.platformer.util.JSONWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;


public class Game extends GamePanel implements Runnable {

    public static final float TICKS     = 60.0f;                 // the amount of times the game ticks every second
    public static final int LEVELS      = 1;
    private int id                      = 1;                     // the id for player-placed blocks
    private int startX                  = 0;
    private int startY                  = 0;
    private int gameState               = 1;                     // the state of the game
    public static int currentLevel      = 1;
    public static final int xObjects    = GamePanel.SCREEN_WIDTH / 50;
    public static final int yObjects    = GamePanel.SCREEN_HEIGHT / 50;
    public static final int gameObjects = xObjects * yObjects; // the amount of objects that can fit on the screen
    private boolean playing             = false;
    private boolean addedButton         = false;
    public static String testJsonPath   = "";                     // the path to the json witch contains all player levels
    public static Block[] level         = new Block[gameObjects]; // an array of blocks where all the level is stored
    private final JButton respawnButton = new JButton();
    private Player player               = null;
    private boolean isPaused            = false;
    private double savedX;
    private double savedY;

    class KL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                // change the game state
                gameState = gameState == 1 ? 2 : 1;

                if (gameState == 1)
                    // convert player level to game level
                    loadJson("/res/level.json", "level" + Game.currentLevel, false);
                else
                    // convert game level to player level
                    loadJson(testJsonPath, "level", true);

            } else if (e.getKeyCode() == KeyEvent.VK_S && gameState == 2){
                // saves the current level
                try {
                    JSONArray array   = JSONWriter.blockArrayToJSONArray(level);
                    JSONObject object = new JSONObject();
                    object.put("level", array);
                    JSONWriter.write(object);
                } catch (IOException | JSONException exc) {
                    exc.printStackTrace();
                }
            } else if (e.getKeyCode() > '0' && e.getKeyCode() <= '9')
                id = e.getKeyCode() - '0';
            else player.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    class AL extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (gameState == 1) return;
            try {
                int x = getMousePosition().x - (getMousePosition().x % 50);
                int y = getMousePosition().y - (getMousePosition().y % 50);

                Block block = new Block(id, x, y);
                level[block.getArrayIndex()] = level[block.getArrayIndex()] == null ? block : null;
            } catch (NullPointerException ignored){}
        }
    }



    public void tick() {
        player.update();
    }

    @Override
    public synchronized void start() {
        this.playing = true;
        this.init();
        Thread t = new Thread(this);
        t.start();
    }

    private void init() {
        // initialize this panel
        {
            this.setSize(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
            this.setLocation(0, 0);
            this.setBackground(Color.white);
            this.setFocusable(true);
            this.addKeyListener(new KL());
            this.addMouseListener(new AL());
            this.requestFocus();
        }

        // initialize respawn button
        {
            respawnButton.setFont(GamePanel.font);
            respawnButton.setText("respawn");
            respawnButton.setSize(225, 85);
            respawnButton.setLocation((GamePanel.SCREEN_WIDTH - respawnButton.getWidth()) / 2,
                    (GamePanel.SCREEN_HEIGHT + respawnButton.getHeight()) / 2);
            respawnButton.setFocusable(false);

            respawnButton.addActionListener(e -> {
                this.player = new Player(this.startX, this.startY);
                this.remove(respawnButton);
                this.revalidate();
                this.addedButton = false;
            });
        }

        // initialize level
        loadJson("/res/level.json", "level" + Game.currentLevel, false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.clearRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
        Arrays.stream(level).forEach(block -> {
            if (block != null) block.draw(g);
        });

        if (player == null) return;

        player.draw(g);

        if (player.isDead) playerDeath(g);
    }

    private void loadJson(String resource, String key, boolean absolutePath) {
        try {
            var loadedJSON = JSONReader.readFile(resource, absolutePath).getJSONObject(key);
            level = JSONReader.JSONArrayToBlockArray(loadedJSON.getJSONArray("level"), Game.gameObjects);
            player = new Player(loadedJSON.getInt("startX"), loadedJSON.getInt("startY"));
            startX = loadedJSON.getInt("startX");
            startY = loadedJSON.getInt("startY");
        } catch (JSONException | IOException e) {
            System.err.println("fatal error occurred when reading file: " + resource);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void playerDeath(Graphics g) {
        // give screen red tint
        g.setColor(new Color(0x99FF0000, true));
        g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // write death text
        g.setColor(Color.BLACK);
        g.setFont(GamePanel.font);
        g.drawString("u ded", (GamePanel.SCREEN_WIDTH - GamePanel.metrics.stringWidth("u ded")) / 2,
                (GamePanel.SCREEN_HEIGHT - GamePanel.metrics.getHeight() / 2) / 2);

        // add respawn button if not already added
        if (!addedButton) {
            this.add(respawnButton);
            this.addedButton = true;
        }
    }

    public void clearPlayerSpeed() {
        isPaused = true;
        savedX = player.xVelocity;
        savedY = player.yVelocity;
        player.xVelocity = player.yVelocity = 0;
    }

    public void resetPlayerSpeed() {
        isPaused = false;
        player.xVelocity = savedX;
        player.yVelocity = savedY;
    }

    @Override
    public void run() {

        long now;
        final double timeU = 1_000_000_000.0 / TICKS;
        final double timeF = 1_000_000_000.0 / 60.0;
        double deltaU      = 0;
        double deltaF      = 0;
        long timer         = System.currentTimeMillis();
        int ticks          = 0;
        int frames         = 0;
        long lastTime      = System.nanoTime();

        while (playing) {
            if (isPaused) {
                deltaF = deltaU = 0.0;
                now = System.nanoTime();
                lastTime = now;
                player.rPressed = player.lPressed = player.jumpPressed = false;
                continue;
            }

            now      = System.nanoTime();
            deltaU  += (now - lastTime) / timeU;
            deltaF  += (now - lastTime) / timeF;
            lastTime =  now;

            if (deltaU >= 1) {
                tick();
                ticks++;
                deltaU--;
            }

            if (deltaF >= 1) {
                repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.printf("\rtps: %s | fps: %s", ticks, Format.formatInt(frames));
                ticks  = frames = 0;
                timer += 1000;
            }
        }
    }
}
