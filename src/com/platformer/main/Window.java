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

	ServerSocket socket;
	@SuppressWarnings("unused")
	public Window(int width, int height, String title, GamePanel panel) {
		this(width, height, title, panel, "center");
	}

	public Window(int width, int height, String title, GamePanel panel, String location) {
		// check game is only started once
//		try {
//			socket = new ServerSocket(48103);
//		} catch (IOException e) {
//			System.err.println("Game has already started");
//			System.exit(1);
//		}

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

		// set location to what was requested
		location = location.toLowerCase(Locale.ROOT);
		Dimension defaultD = getDefaultToolkit().getScreenSize();
		if (location.contains("center"))
			this.setLocationRelativeTo(null);

		if (location.contains("left"))
			this.setLocation(0, this.getY());
		else if (location.contains("right"))
			this.setLocation(defaultD.width - this.getWidth(), this.getY());

		if (location.contains("top"))
			this.setLocation(this.getX(), 0);
		else if (location.contains("bottom"))
			this.setLocation(this.getX(), defaultD.height - this.getHeight() - 35);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (panel instanceof Game g)
					g.resetPlayerSpeed();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				if (panel instanceof Game g)
					g.clearPlayerSpeed();
			}
		});
		this.setVisible(true);

		panel.start();
	}

	@Override
	public void dispose() {
		if (socket != null)
		try {
			socket.close();
		} catch (IOException ignored) {}
		super.dispose();
	}
}