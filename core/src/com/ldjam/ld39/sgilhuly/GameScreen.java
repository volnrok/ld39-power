package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameScreen {
	
	PowerGame game;
	SpriteBatch spriteBatch;
	ShapeRenderer shapeBatch;

	public GameScreen(PowerGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
	}
	
	public abstract void render();
	
	public abstract void dispose();
}
