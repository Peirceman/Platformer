package com.platformer.main;

import com.platformer.supers.GameObject;
import com.platformer.supers.GamePanel;

import java.awt.event.*;

public class Player extends GameObject {
	boolean jumpPressed = false;
	boolean lPressed = false;
	boolean rPressed = false;
	boolean isDead = false;
	final double speed = 400 / Game.TICKS;
	double yVelocity = 0;
	double xVelocity;

	Player(int x, int y) {
		super(x, y);
		this.image = GameObject.textureMap.getSubimage(50, 50, 50, 50);
	}

	//used to move
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT -> rPressed = true;
			case KeyEvent.VK_LEFT  -> lPressed = true;
			case KeyEvent.VK_UP    -> jumpPressed = true;
		}
	}

	//used to stop moving
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT  -> lPressed	  = false;
			case KeyEvent.VK_RIGHT -> rPressed	  = false;
			case KeyEvent.VK_UP    -> jumpPressed = false;
		}
	}

	// method to move in the x direction
	private void updateX() {
		this.xVelocity  = 0;
		this.xVelocity += rPressed ? speed : 0;
		this.xVelocity -= lPressed ? speed : 0;

		double nextX = this.x + this.xVelocity;

		if (this.xVelocity != 0)
			this.x = (collidesWall((int) nextX, xVelocity < 0));

	}

	// method to move in the y direction
	private void updateY() {
		// moves the player down if not touching ground
		if(!touchesGround(this.y)) {
			int nextY = (int) (this.y + this.yVelocity);
			//if the next position would be in a block or in the ground
			this.y = blockCollision(nextY, this.yVelocity >= 0);
			if (this.y != nextY)
				this.yVelocity = 0;
			else
				this.yVelocity += super.g;
		//jumps if the jump key is pressed and is touching ground
		} else if (jumpPressed) {
			yVelocity = -14.2;
			int nextY = (int) (this.y - 14.2);
			this.y = blockCollision(nextY, false);

			if (this.y != nextY)
				yVelocity = 0;
			else
				yVelocity += g;
		}
	}

	// base update method called
	public void update() {
		if (!this.isDead){
			updateX();
			updateY();
		}
	}

	// checks if a certain y value touches the ground
	public boolean touchesGround(int y) {
		if (y == GamePanel.SCREEN_HEIGHT - GameObject.UNIT_SIZE)
			return true;

		// set idx to the index of possibly colliding blocks
		int rmd50 =  this.x % 50;
		int idx;
		{
			int xIdx = (this.x - rmd50) / GameObject.UNIT_SIZE;
			int yIdx = (y - (y % 50)) / GameObject.UNIT_SIZE * Game.xObjects + Game.xObjects;
			idx = xIdx + yIdx;
		}

		boolean touchesBlock = false, touchesSpike = false;

		{
			Block block = Game.level.blocks()[idx];
			if (block != null) {
				if (block.getId() == 2) touchesSpike = true;

				touchesBlock = true;
			}
		}

		if (rmd50 != 0){
			Block block = Game.level.blocks()[idx + 1];
			if (block != null) {
				if (block.getId() != 2)		touchesSpike = false;
				else if (!touchesBlock) touchesSpike = true;

				touchesBlock = true;
			}
		}

		if (touchesSpike)
			this.isDead = true;

		return touchesBlock;
	}

	private int blockCollision(int y, boolean goingDown) {
		// check for screen bounds
		if (goingDown) {
			if (y >= GamePanel.SCREEN_HEIGHT - GameObject.UNIT_SIZE)
				return GamePanel.SCREEN_HEIGHT - GameObject.UNIT_SIZE;
		} else {
			if (y <= 0) return 0;
		}

		// set idx to the index a possibly colliding block
		final int rmd50 =  this.x % 50;
		final int xIdx = (this.x - rmd50) / GameObject.UNIT_SIZE;
		final int yIdx = (y - (y % 50)) * Game.xObjects / GameObject.UNIT_SIZE;
		int idx = xIdx + yIdx + (goingDown ? Game.xObjects : 0);

		// the y value that needs to be checked
		int numToCheck = y + GameObject.UNIT_SIZE * (goingDown ? 1 : -1);


		boolean touchesBlock = false, touchesSpike = false;
		int collisionY = y;

		// check for block in first possible position
		if (Game.level.blocks()[idx] != null) {
			Block block = Game.level.blocks()[idx];
			if ((goingDown && numToCheck > block.getY()) || (!goingDown && numToCheck < block.getY())) {
				collisionY = block.getY() + GameObject.UNIT_SIZE * (goingDown ? -1 : 1);
				touchesBlock = true;
				if (goingDown) {
					   if (block.getId() == 2) touchesSpike = true;
				} else if (block.getId() == 4) touchesSpike = true;
			}
		}

		idx++;

		// check for second position if the player is not perfectly aligned with the first block
		if (idx < Game.level.blocks().length && Game.level.blocks()[idx] != null && rmd50 != 0) {
			Block block = Game.level.blocks()[idx];
			if ((goingDown && numToCheck > block.getY()) || (!goingDown && numToCheck < block.getY())) {
				collisionY = block.getY() + GameObject.UNIT_SIZE * (goingDown ? -1 : 1);
				if (goingDown) {
					if (block.getId() != 2) touchesSpike = false;
					else if (!touchesBlock) touchesSpike = true;
				} else if (block.getId() != 4) touchesSpike = false;
				else if (!touchesBlock) touchesSpike = true;
			}
		}

		if (touchesSpike) this.isDead = true;

		// return false if no blocks were found
		return collisionY;
	}

	private int collidesWall(int x, boolean goingLeft) {
		if (goingLeft) {
			if (x <= 0) {
				return 0;
			}
		} else {
			if (x >= Game.level.width() - GameObject.UNIT_SIZE) {
				return Game.level.width() - GameObject.UNIT_SIZE;
			}
		}

		// variables used for checking
		int rmd50 = super.y % 50;
		final int xIdx = (x - (x % 50)) / 50;
		final int yIdx = (super.y - rmd50) / 50 * Game.xObjects;
		int idx = xIdx + yIdx + (goingLeft ? 0 : 1);

		// the x value that needs to be checked
		int numToCheck = x + GameObject.UNIT_SIZE * (goingLeft ? -1 : 1);

		boolean touchesBlock = false, touchesSpike = false;
		int collisionX = x;

		// check for block in first possible position
		if (Game.level.blocks()[idx] != null) {
			Block block = Game.level.blocks()[idx];
			if ((goingLeft && numToCheck <= block.getX()) || (!goingLeft && numToCheck >= block.getX())) {
				collisionX = block.getX() + GameObject.UNIT_SIZE * (goingLeft ? 1 : -1);
				touchesBlock = true;
				if (goingLeft) {
					   if (block.getId() == 3) touchesSpike = true;
				} else if (block.getId() == 5) touchesSpike = true;
			}
		}

		idx += Game.xObjects;

		if (idx < Game.level.blocks().length) {
			// check for second position if the player is not perfectly aligned with first block
			if (Game.level.blocks()[idx] != null && rmd50 != 0) {
				Block block = Game.level.blocks()[idx];
				if ((goingLeft && numToCheck <= block.getX()) || (!goingLeft && numToCheck >= block.getX())) {
					collisionX = block.getX() + GameObject.UNIT_SIZE * (goingLeft ? 1 : -1);
					if (goingLeft) {
						if (block.getId() != 3) touchesSpike = false;
						else if (!touchesBlock) touchesSpike = true;
					} else if (block.getId() != 5) touchesSpike = false;
					else if (!touchesBlock) touchesSpike = true;
				}
			}
		}

		if (touchesSpike) this.isDead = true;

		return collisionX;
	}
}
