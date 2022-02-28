package com.platformer.main;

import com.platformer.Main;
import com.platformer.supers.GamePanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class Menu extends GamePanel {
    private final GridBagConstraints gbc = new GridBagConstraints();


    @Override
    public void start() {
        this.init();
    }

    private void init() {
        // initialize panel
        {
            this.setSize(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
            this.setLocation(0, 0);
            this.setBackground(Color.WHITE);
            this.setLayout(new GridBagLayout());
            this.requestFocus();
        }

        // initialize tile text
        {
            JLabel title = new JLabel();
            title.setFont(GamePanel.font);
            title.setText("platformer");
            title.setSize(metrics.stringWidth(title.getText()) + 2, 85);
            title.setLocation((GamePanel.SCREEN_WIDTH - metrics.stringWidth(title.getText())) / 2, 0);

            this.gbc.insets = new Insets(15, 0, 15, 0);
            this.gbc.gridx = 0;
            this.gbc.gridy = 0;
            this.add(title, this.gbc);
        }

        // initialize play button
        {
            JButton playButton = new JButton();
            playButton.setFont(GamePanel.font);
            playButton.setText("play");
            playButton.setSize(new Dimension(225, 85));
            playButton.setFocusable(false);

            playButton.addActionListener(e -> {
                this.setLayout(null);
                JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
                inner.setSize(new Dimension(GamePanel.SCREEN_WIDTH - 100, GamePanel.SCREEN_HEIGHT - 100));
                inner.setLocation((GamePanel.SCREEN_WIDTH - inner.getWidth()) / 2, (GamePanel.SCREEN_HEIGHT - inner.getHeight()) / 2);
                inner.setBackground(Color.red);

                for (int i = 1; i <= Game.LEVELS; i++) {
                    final int finalI = i;
                    final int size = Math.max(GamePanel.metrics.stringWidth(Integer.toString(i)), GamePanel.FONT_HEIGHT) + 2;

                    final JButton button = new JButton();
                    button.setFont(GamePanel.font);
                    button.setPreferredSize(new Dimension(size, size));
                    button.setText(Integer.toString(finalI));
                    button.addActionListener(ev -> Main.addGame(Integer.toString(finalI)));

                    inner.add(button);
                }

                this.removeAll();
                this.add(inner);
                this.revalidate();
                this.repaint();
            });

            this.gbc.gridy = 1;
            this.gbc.fill = GridBagConstraints.BOTH;
            this.add(playButton, this.gbc);
        }
        // initialize my levels button
        {
            JButton myLevels = new JButton();
            myLevels.setFont(GamePanel.font);
            myLevels.setText("my levels");
            myLevels.setFocusable(false);
            myLevels.addActionListener(e -> {
                GridBagLayout layout = new GridBagLayout();
                layout.columnWidths = new int[] {(950 - 255) / 2, 255, (950 - 255) / 2};
                layout.rowHeights   = new int[] {500, 85};
                this.setLayout(layout);
                JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
                inner.setSize(new Dimension(GamePanel.SCREEN_WIDTH - 100, GamePanel.SCREEN_HEIGHT - 100));
                inner.setLocation((GamePanel.SCREEN_WIDTH - inner.getWidth()) / 2, (GamePanel.SCREEN_HEIGHT - inner.getHeight()) / 2);
                inner.setBackground(Color.red);

                int maxSize = Integer.MIN_VALUE;
                for (File f : Objects.requireNonNull(Main.playerLevelsDir.listFiles())) {
                    maxSize = Math.max(maxSize, metrics.stringWidth(f.getName().substring(0, 4)));
                }
                maxSize *= 1.5;

                for (File f : Objects.requireNonNull(Main.playerLevelsDir.listFiles())) {
                    String name = f.getName().replaceAll(".lev$", "");
                    JButton button = new JButton();
                    button.setFont(GamePanel.font);
                    button.setPreferredSize(new Dimension(maxSize, GamePanel.FONT_HEIGHT));
                    button.setText(name);
                    button.addActionListener(ev -> Main.addGame(name));

                    inner.add(button);
                }

                JButton importLevel = new JButton();
                importLevel.setFont(GamePanel.font);
                importLevel.setText("import level");
                importLevel.addActionListener(ev -> System.out.println("import level pressed"));

                this.removeAll();
                this.gbc.gridx = 0;
                this.gbc.gridy = 0;
                this.gbc.gridwidth = 3;
                this.gbc.insets = new Insets(0, 0, 0, 0);
                this.add(inner, this.gbc);
                this.gbc.gridx = 1;
                this.gbc.gridy = 1;
                this.gbc.gridwidth = 1;
                this.add(importLevel, this.gbc);
                this.revalidate();
                this.repaint();
            });
            this.gbc.gridy = 2;
            this.add(myLevels, this.gbc);
        }
    }
}
