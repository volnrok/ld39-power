package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ScoreScreen extends GameScreen {
	
	String rhythmFile;
	int score;
	boolean levelCompleted;

	BitmapFont font = new BitmapFont();
	String outputText;

	public ScoreScreen(PowerGame game, int score, boolean levelCompleted) {
		super(game);
		this.rhythmFile = game.getLevelPlayed().file;
		this.score = score;
		this.levelCompleted = levelCompleted;
		
		if(levelCompleted) {
			outputText = "City powered!";
			font.setColor(0, 0, 0, 1);
		} else {
			outputText = "Blackouts!";
			font.setColor(1, 1, 1, 1);
		}
		outputText += "\n\nScore: " + score;
		if(score > game.getLevelPlayed().score) {
			game.getLevelPlayed().score = score;
			outputText += " (New high score!)";
			game.saveHighScores();
		}
		outputText += "\n\nPress 'r' to restart\nPress 'Esc' to select a different level";
	}

	@Override
	public void render() {
		if(levelCompleted) {
			Gdx.gl.glClearColor(1, 1, 0.75f, 1);
		} else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.setProjectionMatrix(game.cam.combined);
		shapeBatch.setProjectionMatrix(game.cam.combined);
		
		spriteBatch.begin();
		font.draw(spriteBatch, outputText, PowerGame.VIRTUAL_WIDTH / 2 - 110, PowerGame.VIRTUAL_HEIGHT / 2 + 60);
		spriteBatch.end();
		
		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			game.goToRhythmScreen();
		} else if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			game.goToMainScreen(false);
		}
	}

	@Override
	public void dispose() {
		
	}
}
