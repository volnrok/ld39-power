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
	
	public void goToMainScreen(boolean showAnimation) {
		changeScreen(new StartScreen(this, showAnimation));
	}
	
	public void goToRhythmScreen(String rhythmFile) {
		changeScreen(new RhythmScreen(this, rhythmFile));
	}
	
	public void goToScoreScreen(String rhythmFile, int score, boolean levelCompleted) {
		changeScreen(new ScoreScreen(this, rhythmFile, score, levelCompleted));
	}
}
