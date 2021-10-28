package com.platformer.main;

import com.platformer.Main;
import com.platformer.supers.GamePanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;

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

            gbc.insets = new Insets(15, 0, 15, 0);
            gbc.gridx = 0;
            gbc.gridy = 0;
            this.add(title, gbc);
        }

        // initialize play button
        {
            JButton playButton = new JButton();
            playButton.setFont(GamePanel.font);
            playButton.setText("play");
            playButton.setSize(new Dimension(225, 85));
            playButton.setLocation((GamePanel.SCREEN_WIDTH - 225) / 2,
                                    GamePanel.SCREEN_HEIGHT / 2);
            playButton.setFocusable( false);

            playButton.addActionListener(e -> {
                this.setLayout(null);
                this.removeAll();
                this.revalidate();
                this.repaint();

                JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
                inner.setSize(new Dimension(GamePanel.SCREEN_WIDTH - 100, GamePanel.SCREEN_HEIGHT - 100));
                inner.setLocation((GamePanel.SCREEN_WIDTH - inner.getWidth()) / 2, (GamePanel.SCREEN_HEIGHT - inner.getHeight()) / 2);
                inner.setBackground(Color.white);

                for (int i = 1; i <= Game.LEVELS; i++) {
                    final int finalI = i;

                    JButton button = new JButton();
                    button.setFont(GamePanel.font);
                    int maxSize = Math.max(metrics.stringWidth("" + i), metrics.getHeight()) + 2;
                    button.setPreferredSize(new Dimension(maxSize, maxSize));
                    button.setText("" + i);
                    button.addActionListener(ev -> Main.addGame(finalI));

                    inner.add(button);
                }

                this.add(inner);
                this.validate();
            });

            gbc.gridy = 1;
            gbc.fill  = GridBagConstraints.BOTH;
            this.add(playButton, gbc);
        }

    }
}
