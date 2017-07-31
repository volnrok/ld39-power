package com.ldjam.ld39.sgilhuly;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

	public static final int LEFT = 0;
	public static final int MIDDLE = 1;
	public static final int RIGHT = 2;
	
	SpriteBatch spriteBatch;
	ShapeRenderer shapeBatch;
	Texture star;
	Texture goal;
	ArrayList<Note> activeNotes;
	int nextNote;
	OrthographicCamera cam;
	Viewport viewport;
	Color tempColour = new Color();
	Conductor conductor;
	
	int selected = MIDDLE;
	int lastSelected = LEFT;
	float transition = 1f;
	float brightness = 1f;
	
	private Vector2[] trackData;
	
	@Override
	public void create() {
		cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
		
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		star = new Texture("star.png");
		goal = new Texture("goal.png");
		
		prepGraphics();
		try {
			conductor = new Conductor("test.json");
			conductor.music.play();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		activeNotes = new ArrayList<Note>();
		nextNote = 0;
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
		
		conductor.update();
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			/*System.out.println(obj.eta);
			obj.eta = 1;*/
		}
		
		// Check for track transitions
		if(transition < 1) {
			transition += Gdx.graphics.getDeltaTime() * Constants.TRANSITION_SPEED;
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
		
		// Check for activating notes
		while(nextNote < conductor.notes.size() && conductor.notes.get(nextNote).isShown(conductor.currentTime)) {
			Note note = conductor.notes.get(nextNote++);
			note.prepareToDraw(star);
			activeNotes.add(note);
			System.out.println("Add note!");
		}
		
		spriteBatch.setProjectionMatrix(cam.combined);
		
		shapeBatch.begin(ShapeType.Filled);
		tempColour.set(Color.DARK_GRAY).mul(brightness);
		shapeBatch.setColor(tempColour);
		shapeBatch.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		drawTrack();
		shapeBatch.end();
		
		spriteBatch.begin();
		spriteBatch.draw(star, conductor.currentBar * 32, 0);
		if(conductor.isNewBar) {
			spriteBatch.draw(star, 0, 100);
		}
		if(conductor.isNewBeat) {
			spriteBatch.draw(star, 0, 50);
		}
		// Update active notes
		for(Iterator<Note> it = activeNotes.iterator(); it.hasNext();) {
			Note note = it.next();
			if(note.time < conductor.currentTime) {
				System.out.println("Remove note!");
				it.remove();
			} else {
				note.draw(conductor.currentTime, 200, spriteBatch);
			}
		}
		spriteBatch.end();
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
