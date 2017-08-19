package com.ldjam.ld39.sgilhuly;

import java.io.FileNotFoundException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PowerGame extends ApplicationAdapter {
	public static final int VIRTUAL_WIDTH = 640;
	public static final int VIRTUAL_HEIGHT = 480;
	public static final String LEVEL_FILE = "data/levels.json";

	OrthographicCamera cam;
	Viewport viewport;
	GameScreen activeScreen = null;

	public int levelPlayed = 0;
	public Level[] playableLevels;/* = {
			new Level("Easy", "data/easy.json", 0),
			new Level("Medium", "data/echoes.json", 0),
			new Level("Hard", "data/YuSong.json", 0)
	};*/
	
	@Override
	public void create() {
		Resources.loadResources();
		loadHighScores();
		
		Gdx.graphics.setTitle("Power Rhythm");
		cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
		
		goToMainScreen(true);
	}

	@Override
	public void render() {
		if(activeScreen != null) {
			activeScreen.render();
		}
	}
	
	@Override
	public void dispose() {
		Resources.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	private void changeScreen(GameScreen screen) {
		if(activeScreen != null) {
			activeScreen.dispose();
		}
		
		activeScreen = screen;
	}
	
	public Level getLevelPlayed() {
		return playableLevels[levelPlayed];
	}
	
	public void goToMainScreen(boolean showAnimation) {
		changeScreen(new StartScreen(this, showAnimation));
	}
	
	public void goToRhythmScreen() {
		changeScreen(new RhythmScreen(this));
	}
	
	public void goToScoreScreen(int score, boolean levelCompleted) {
		changeScreen(new ScoreScreen(this, score, levelCompleted));
	}
	
	public void saveHighScores() {
		new Json(OutputType.json).toJson(playableLevels, Level[].class, Gdx.files.local(LEVEL_FILE));
	}
	
	public void loadHighScores() {
		try {
			playableLevels = new Json().fromJson(Level[].class, Gdx.files.local(LEVEL_FILE));
		} catch(SerializationException e) {
			playableLevels = new Level[] {
					new Level("Easy", "data/easy.json", 0),
					new Level("Medium", "data/echoes.json", 0),
					new Level("Hard", "data/YuSong.json", 0)
			};
		}
	}
}

class Level {
	public String name;
	public String file;
	public int score;
	
	public Level() {
		this("", "", 0);
	}
	
	public Level(String name, String file, int score) {
		this.name = name;
		this.file = file;
		this.score = score;
	}
}
