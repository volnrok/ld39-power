package com.ldjam.ld39.sgilhuly;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PowerGame extends ApplicationAdapter {
	public static final int VIRTUAL_WIDTH = 640;
	public static final int VIRTUAL_HEIGHT = 480;
	public static final float TRANSITION_SPEED = 8f;

	public static final int LEFT = 0;
	public static final int MIDDLE = 1;
	public static final int RIGHT = 2;
	
	SpriteBatch spriteBatch;
	ShapeRenderer shapeBatch;
	Texture star;
	Texture goal;
	ArrayList<RhythmObject> objects;
	OrthographicCamera cam;
	Viewport viewport;
	RhythmObject obj;
	Color tempColour = new Color();
	Music music;
	
	int selected = MIDDLE;
	int lastSelected = LEFT;
	float transition = 1f;
	
	private Vector2[] trackData;
	
	@Override
	public void create() {
		cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
		
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		star = new Texture("star.png");
		goal = new Texture("goal.png");
		obj = new RhythmObject(2);
		obj.speed = 300;
		
		music = Gdx.audio.newMusic(Gdx.files.internal("test.ogg"));
		music.play();
		
		prepGraphics();
	}
	
	private void drawTrack() {
		// Try not to create colour objects frivolously
		for(int i = 0; i < 24; i++) {
			if(i % 8 == 0) {
				tempColour.set(Constants.COLOUR_TRACK);
				if(i / 8 == selected) {
					tempColour.mul(transition);
				} else if(i / 8 == lastSelected) {
					tempColour.mul(1f - transition);
				} else {
					tempColour.mul(0f);
				}
				shapeBatch.setColor(tempColour);
			}
			shapeBatch.rectLine(trackData[i * 2], trackData[i * 2 + 1], Constants.LINE_WIDTH);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		obj.update(Gdx.graphics.getDeltaTime());
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			System.out.println(obj.eta);
			obj.eta = 1;
		}
		
		// Check for track transitions
		if(transition < 1) {
			transition += Gdx.graphics.getDeltaTime() * TRANSITION_SPEED;
			if(transition > 1) {
				transition = 1;
			}
		}
		
		if(transition >= 1) {
			int desired = MIDDLE;
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				desired = LEFT;
			} else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				desired = RIGHT;
			}
			
			if(desired != selected) {
				// Make sure we don't switch from left to right, or vice versa
				if((desired == LEFT && selected == RIGHT) || (desired == RIGHT && selected == LEFT)) {
					desired = MIDDLE;
				}
				lastSelected = selected;
				selected = desired;
				transition = 0;
			}
		}
		
		spriteBatch.setProjectionMatrix(cam.combined);
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.DARK_GRAY);
		shapeBatch.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		shapeBatch.end();
		
		spriteBatch.begin();
		spriteBatch.draw(star, music.getPosition() * 240, 0);
		spriteBatch.end();

		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.RED);
		drawTrack();
		shapeBatch.end();
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
		star.dispose();
		goal.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	private void prepGraphics() {
		trackData = new Vector2[48];
		int i = 0;
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_1, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_1, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_1 - Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_1, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_1 - Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_1, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_1 - Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_1, Constants.TRACK_TOP_Y);

		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_2, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_2 + Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_2, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_2 + Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_2, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_2 + Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_2, Constants.TRACK_TOP_Y);

		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_3, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_3 - Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_3, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_3 - Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_3, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_3 - Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_3, Constants.TRACK_TOP_Y);

		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_4, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_4, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_4 + Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_4, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_4 + Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_4, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_4 + Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_4, Constants.TRACK_TOP_Y);

		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_5, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_5, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_5 - Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_5, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_5 - Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_5, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_5 - Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_5, Constants.TRACK_TOP_Y);

		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_6, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_6, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_6 + Constants.LINE_WIDTH, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_6, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_6 + Constants.LINE_WIDTH * 2, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_6, Constants.TRACK_TOP_Y);
		trackData[i++] = new Vector2(Constants.TRACK_BASE_X_6 + Constants.LINE_WIDTH * 3, Constants.TRACK_BASE_Y);
		trackData[i++] = new Vector2(Constants.TRACK_TOP_X_6, Constants.TRACK_TOP_Y);
	}
}
