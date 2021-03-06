package com.platformer.main;

import com.platformer.Main;
import com.platformer.supers.GameObject;
import com.platformer.supers.GamePanel;
import com.platformer.util.Format;
import com.platformer.util.JSONReader;
import com.platformer.util.JSONWriter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;


public class Game extends GamePanel implements Runnable {
    public static final float TICKS     = 60.0f; // the amount of times the game ticks every second
    public static final int LEVELS      = 1;
    private int id                      = 1;
    public static boolean editing       = false; // the state of the game
    private boolean playing             = false;
    private boolean addedButton         = false;
    private final JButton respawnButton = new JButton();
    private static Player player        = null;
    private boolean isPaused            = false;
    private int cam                     = 0;
    public static Level level           = new Level();
    public static String currentLevel;
    private static int maxCam;
    private double savedX;
    private double savedY;

    class KL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            if (editing && e.getKeyCode() == KeyEvent.VK_S){
                saveLevel(Main.getPlayerLevelPath(currentLevel), "level");
            } else if (e.getKeyCode() > '0' && e.getKeyCode() <= '9') {
                id = e.getKeyCode() - '0';
            }
            else player.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    class AL extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (editing || addedButton) return;
            try {
                int x = getMousePosition().x - (getMousePosition().x % 50) + cam - (cam % 50);
                int y = getMousePosition().y - (getMousePosition().y % 50);

                Block block = new Block(id, x, y);
                Game.level.blocks[block.getArrayIndex()] = Game.level.blocks[block.getArrayIndex()] == null ? block : null;
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
            this.setIgnoreRepaint(true);
            this.setFocusable(true);
            this.addKeyListener(new KL());
            this.addMouseListener(new AL());
            this.requestFocus();
        }

        // initialize respawn button
        {
            this.respawnButton.setFont(GamePanel.font);
            this.respawnButton.setText("respawn");
            this.respawnButton.setSize(225, 85);
            this.respawnButton.setLocation((GamePanel.SCREEN_WIDTH - respawnButton.getWidth()) / 2,
                    (GamePanel.SCREEN_HEIGHT + respawnButton.getHeight()) / 2);
            this.respawnButton.setFocusable(false);

            this.respawnButton.addActionListener(e -> {
                Game.player = new Player(Game.level.startX, Game.level.startY);
                this.remove(respawnButton);
                this.respawnButton.setLocation(respawnButton.getX() - cam, respawnButton.getY());
                this.revalidate();
                this.addedButton = false;
            });
        }

        // initialize level
        loadLevel(editing ? Main.getPlayerLevelPath(currentLevel) : "/res/levels.lev", currentLevel, editing);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.translate(-cam, 0);
        g.setColor(Color.white);
        for (Block block : Game.level.blocks) {
            if (block != null && block.getX() + GameObject.UNIT_SIZE > cam && block.getX() < cam + GamePanel.SCREEN_WIDTH)
                block.draw(g);
        }

        if (player == null) return;

        player.draw(g);

        if (player.isDead) drawDeath(g);
    }

    private void drawDeath(Graphics g) {
        // give screen red tint
        g.setColor(new Color(0x99FF0000, true));
        g.fillRect(cam, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // write death text
        g.setColor(Color.BLACK);
        g.setFont(GamePanel.font);
        String deathString = "yuo ded";
        g.drawString(deathString, cam + (GamePanel.SCREEN_WIDTH - GamePanel.metrics.stringWidth(deathString)) / 2,
                (GamePanel.SCREEN_HEIGHT - GamePanel.metrics.getHeight() / 2) / 2);

        // add respawn button if not already added
        if (!addedButton) {
            respawnButton.setLocation(respawnButton.getX() + cam, respawnButton.getY());
            this.add(respawnButton);
            this.addedButton = true;
        }
    }

    public static void saveLevel(String resource, String key) {
        try {
            JSONObject levelObj = new JSONObject();
            levelObj.put("startX"  , Game.level.startX);
            levelObj.put("startY"  , Game.level.startY);
            levelObj.put("width"   , Game.level.width);
            levelObj.put("blocks"  , JSONWriter.blockArrayToJSONArray(Game.level.blocks));
            JSONWriter.write(resource, JSONReader.readFile(resource, true).put(key, levelObj));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void loadLevel(String resource, String key, boolean absolutePath) {
        try {
            JSONObject levelObj = JSONReader.readFile(resource, absolutePath).getJSONObject(key);
            Game.level.startX = levelObj.getInt("startX");
            Game.level.startY = levelObj.getInt("startY");
            Game.level.setWidth(levelObj.getInt("width"));
            Game.level.blocks = JSONReader.JSONArrayToBlockArray(levelObj.getJSONArray("blocks"),
                                                           (Game.level.width / GameObject.UNIT_SIZE) * Level.yObjects);
            Game.player = new Player(Game.level.startX, Game.level.startY);
            Game.maxCam = Game.level.width - GamePanel.SCREEN_WIDTH;
        } catch (JSONException | IOException e) {
            System.err.println("fatal error occurred when reading file: " + resource);
            e.printStackTrace();
            System.exit(1);
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
    @SuppressWarnings("all")
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
                cam = (player.getX() - SCREEN_WIDTH / 2);
                cam = Math.max(Math.min(cam, maxCam), 0);
                repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.printf("tps: %s | fps: %s\r", ticks, Format.formatInt(frames));
                ticks  = frames = 0;
                timer += 1000;
            }

            try {
                Thread.sleep(0);
            } catch (InterruptedException ignored) {}
        }
    }
}
