package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class StartScreen extends GameScreen {
	
	public static final float INTRO_BPM = 32.5f; // 130 bpm, but we want every bar
	
	BitmapFont font = new BitmapFont();
	float animationTimer = 0;
	float animationBarNumber;
	int selected = 0;
	String[][] options = {
			{"Easy", "data/test.json"},
			{"Hard", "data/YuSong.json"}
	};

	public StartScreen(PowerGame game, boolean showAnimation) {
		super(game);
		Resources.introMusic.setLooping(true);
		Resources.introMusic.play();
		
		if(!showAnimation) {
			animationTimer = 50;
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		animationTimer += Gdx.graphics.getDeltaTime();
		animationBarNumber = animationTimer * INTRO_BPM / 60f;
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && selected > 0) {
			selected--;
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && selected + 1 < options.length) {
			selected++;
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			if(animationBarNumber < 2) {
				animationTimer = 50;
			} else {
				game.goToRhythmScreen(options[selected][1]);
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(0.25f, 0.25f, 0.25f, 1);
		shapeBatch.rect(0, 0, PowerGame.VIRTUAL_WIDTH, PowerGame.VIRTUAL_HEIGHT);
		shapeBatch.end();
		
		spriteBatch.begin();
		spriteBatch.setColor(1, 1, 1, Helper.clamp(animationBarNumber, 0, 1));
		spriteBatch.draw(Resources.title, 0, -60 + 60 * (float) Math.sqrt(Helper.clamp(animationBarNumber, 0, 1)));
		
		// Draw options
		if(animationBarNumber >= 2) {
			for(int i = 0; i < options.length; i++) {
				String text = options[i][0];
				if(i == selected) {
					text = "> " + text;
					font.setColor(0.612f, 0.584f, 0.482f, Helper.clamp(animationBarNumber - 2, 0, 1));
				} else {
					text = "   " + text;
					font.setColor(1, 1, 1, Helper.clamp(animationBarNumber - 2, 0, 1));
				}
				font.draw(spriteBatch, text, 280, 240 - 24 * i + 60 * (float) Math.sqrt(Helper.clamp(animationBarNumber - 2, 0, 1)));
			}
		}
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		Resources.introMusic.stop();
	}

}
