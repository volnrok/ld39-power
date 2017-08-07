package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PowerGame extends ApplicationAdapter {
	public static final int VIRTUAL_WIDTH = 640;
	public static final int VIRTUAL_HEIGHT = 480;

	OrthographicCamera cam;
	Viewport viewport;
	GameScreen activeScreen = null;

	public int levelPlayed = 0;
	public Entry[] playableLevels = {
			new Entry("Easy", "data/test.json"),
			new Entry("Hard", "data/YuSong.json")
	};
	
	@Override
	public void create() {
		Resources.loadResources();
		
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
	
	public Entry getLevelPlayed() {
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
	
	public class Entry {
		public String name;
		public String rhythmFile;
		public int highScore;
		
		public Entry(String name, String rhythmFile) {
			this.name = name;
			this.rhythmFile = rhythmFile;
			highScore = 0;
		}
	}
}
