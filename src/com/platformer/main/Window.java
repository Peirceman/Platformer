package com.platformer.main;

import com.platformer.Main;
import com.platformer.supers.GamePanel;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.Objects;

import static java.awt.Toolkit.getDefaultToolkit;

public class Window extends JFrame{

	ServerSocket socket = null;

	public Window(int width, int height, String title, GamePanel panel) {
		// check game is only started once
		try {
			socket = new ServerSocket(48103);
		} catch (IOException e) {
			System.err.println("Game has already started");
			System.exit(1);
		}

		this.setTitle(title);
		this.pack();
		this.setSize(super.getInsets().left + width + super.getInsets().right,
					 super.getInsets().top + height + super.getInsets().bottom);

		try {
			this.setIconImage(
					new ImageIcon(Objects.requireNonNull(
								Main.class.getResourceAsStream("/res/icon.png")).readAllBytes()
					).getImage());
		} catch (IOException | NullPointerException e) {
			System.err.println("error while trying to get icon");
			e.printStackTrace();
			System.exit(1);
		}

		this.add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (panel instanceof Game g) {
					g.resetPlayerSpeed();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				if (panel instanceof Game g) {
					g.clearPlayerSpeed();
				}
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println();
			}
		});
		this.setVisible(true);

		panel.start();
	}

	@Override
	public void dispose() {
		super.dispose();
		try {
			socket.close();
		} catch (IOException ignored) {}
	}
}
