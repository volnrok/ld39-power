package com.ldjam.ld39.sgilhuly;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PowerGame extends ApplicationAdapter {
	public static final int VIRTUAL_WIDTH = 640;
	public static final int VIRTUAL_HEIGHT = 480;
	
	SpriteBatch spriteBatch;
	ShapeRenderer shapeBatch;
	Texture star;
	Texture goal;
	int posx = 100;
	ArrayList<RhythmObject> objects;
	OrthographicCamera cam;
	Viewport viewport;
	
	@Override
	public void create () {
		cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
		
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		star = new Texture("star.png");
		goal = new Texture("goal.png");
	}

	@Override
	public void render () {
		posx--;
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.setProjectionMatrix(cam.combined);
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.GRAY);
		shapeBatch.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		shapeBatch.end();
		
		spriteBatch.begin();
		spriteBatch.draw(goal, 0, 0);
		spriteBatch.end();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		star.dispose();
		goal.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
}
