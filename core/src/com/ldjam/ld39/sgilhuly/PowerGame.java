package com.ldjam.ld39.sgilhuly;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
	public static final float VOLUME = 0.65f;
	
	SpriteBatch spriteBatch;
	ShapeRenderer shapeBatch;
	Texture star;
	Texture goal;
	Texture button;
	Texture black;
	Texture x2, x3, x4;
	ArrayList<Note> activeNotes;
	ArrayList<Particle> particles;
	int nextNote;
	OrthographicCamera cam;
	Viewport viewport;
	Color tempColour = new Color();
	Conductor conductor;
	Sprite[] goals;
	Sprite darkness;
	BitmapFont font;
	Sound powerup;
	Sound powerdown;
	Sound explosion;
	
	int selected = MIDDLE;
	int lastSelected = LEFT;
	float transition = 1;
	float brightness = 1;
	float pulse = 1;
	boolean running;
	
	int combo;
	int hits;
	int score;
	
	private Vector2[] trackData;
	
	@Override
	public void create() {
		Gdx.graphics.setTitle("Power Rhythm");
		cam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
		
		spriteBatch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		star = new Texture("data/star.png");
		star.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		button = new Texture("data/button.png");
		button.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		x2 = new Texture("data/x2.png");
		x2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		x3 = new Texture("data/x3.png");
		x3.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		x4 = new Texture("data/x4.png");
		x4.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		goal = new Texture("data/goal.png");
		black = new Texture("data/black.png");
		darkness = new Sprite(black);
		font = new BitmapFont();
		powerup = Gdx.audio.newSound(Gdx.files.internal("data/pup2.wav"));
		powerdown = Gdx.audio.newSound(Gdx.files.internal("data/pdown2.wav"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("data/expl2.wav"));
		
		prepGraphics();
		
		restart();
	}
	
	private void restart() {
		selected = MIDDLE;
		transition = 1;
		brightness = 1;
		
		combo = 1;
		hits = 0;
		score = 0;
		
		try {
			conductor = new Conductor("data/YuSong.json");
			conductor.music.play();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		particles = new ArrayList<Particle>();
		activeNotes = new ArrayList<Note>();
		nextNote = 0;
		running = true;
	}
	
	private void drawTrack() {
		// Try not to create colour objects frivolously
		for(int i = 0; i < 24; i++) {
			if(i % 8 == 0) {
				tempColour.set(Constants.COLOUR_TRACK);
				if(i / 8 == selected) {
					tempColour.mul(transition * 0.7f + 0.3f);
				} else if(i / 8 == lastSelected) {
					tempColour.mul(1f - transition * 0.7f);
				} else {
					tempColour.mul(0.3f);
				}
				tempColour.mul(pulse);
				shapeBatch.setColor(tempColour);
			}
			shapeBatch.rectLine(trackData[i * 2], trackData[i * 2 + 1], Constants.LINE_WIDTH);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!running && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			restart();
		}
		
		if(running) {
			conductor.update();
			pulse = Helper.tween(Constants.PULSE_DECAY, Constants.PULSE_MIN, pulse);
			if(conductor.isNewBar) {
				pulse = 1;
			} else if(conductor.isNewBeat) {
				pulse = Constants.PULSE_PARTIAL;
			}
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
			note.prepareToDraw(button);
			activeNotes.add(note);
		}
		
		// Check for pressing spacebar/enter
		if(running) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				Note hitNote = null;
				for(Note n : activeNotes) {
					if(Math.abs(n.time - conductor.currentTime) <= Constants.NOTE_WINDOW && n.track == selected) {
						hitNote = n;
						break;
					}
				}
				if(hitNote != null) {
					hits++;
					score += Constants.SCORE_PER_HIT * combo;
					if(hits % Constants.SCORE_HITS_PER_COMBO == 0 && combo < Constants.SCORE_MAX_COMBO) {
						combo++;
						powerup.play(VOLUME);
					}
					
					// TODO: Repeated code, clean up when there is time!
					float posx = Constants.TRACK_BASE_X;
					if(selected == 0) {
						posx = Constants.TRACK_LEFT_BASE_X;
					} else if(selected == 2) {
						posx = Constants.TRACK_RIGHT_BASE_X;
					}
					float posy = Constants.TRACK_BASE_Y;
					particles.add(new Particle(posx, posy, button));
					activeNotes.remove(hitNote);
					brightness += Constants.BRIGHTNESS_BONUS_PER_HIT;
				} else {
					// No brightness penalty, but lose combo
					hits = 0;
					if(combo != 1) {
						combo = 1;
						powerdown.play(VOLUME);
					}
				}
			}
		}
		
		spriteBatch.setProjectionMatrix(cam.combined);
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.DARK_GRAY);
		shapeBatch.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		drawTrack();
		shapeBatch.end();
		
		spriteBatch.begin();
		// Draw goals
		for(Sprite s : goals) {
			s.draw(spriteBatch, pulse);
		}
		// Update active notes
		for(Iterator<Note> it = activeNotes.iterator(); it.hasNext();) {
			Note note = it.next();
			if(note.done) {
				it.remove();
				// Penalize!
				brightness -= Constants.BRIGHTNESS_PENALTY_PER_MISS;
				hits = 0;
				if(combo != 1) {
					combo = 1;
					powerdown.play(VOLUME);
				}
			} else {
				note.draw(conductor.currentTime, 200, spriteBatch);
			}
		}
		// Particles TODO: clean up particle system
		for(Iterator<Particle> it = particles.iterator(); it.hasNext();) {
			Particle p = it.next();
			p.updateRender(Gdx.graphics.getDeltaTime(), spriteBatch);
			if(p.isDone()) {
				it.remove();
			}
		}
		
		if(running && brightness <= 0) {
			conductor.music.stop();
			explosion.play(VOLUME);
			running = false;
		}
		
		// Draw brightness level
		if(conductor.currentPosition > 0) {
			brightness -= Gdx.graphics.getDeltaTime() * Constants.BRIGHTNESS_PENALTY_PER_SECOND;
		}
		brightness = Helper.clamp(brightness, 0, 1);
		tempColour.set(1, 1, 1, 1 - brightness);
		darkness.setScale(viewport.getWorldWidth() / black.getWidth() * 2, viewport.getWorldHeight() / black.getHeight() * 2);
		darkness.setColor(tempColour);
		darkness.draw(spriteBatch);
		
		// Draw score
		font.draw(spriteBatch, "Score: " + score, Constants.TRACK_BASE_X - 50, 80);
		Texture comboTexture = null;
		switch(combo) {
		case 2:
			comboTexture = x2;
			break;
		case 3:
			comboTexture = x3;
			break;
		case 4:
			comboTexture = x4;
			break;
		}
		if(comboTexture != null) {
			spriteBatch.draw(comboTexture, Constants.TRACK_BASE_X + 30, 40, comboTexture.getWidth() / 2, comboTexture.getHeight() / 2);
		}
		if(!running) {
			font.draw(spriteBatch, "Blackout!\nPress 'r' to restart", VIRTUAL_WIDTH / 2 - 90, VIRTUAL_HEIGHT / 2 + 30);
		}
		spriteBatch.end();
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
		star.dispose();
		goal.dispose();
		x2.dispose();
		x3.dispose();
		x4.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	private void prepGraphics() {
		int i;
		
		// Goal spots
		goals = new Sprite[3];
		for(i = 0; i < 3; i++) {
			float posx = Constants.TRACK_BASE_X;
			if(i == 0) {
				posx = Constants.TRACK_LEFT_BASE_X;
			} else if(i == 2) {
				posx = Constants.TRACK_RIGHT_BASE_X;
			}
			float posy = Constants.TRACK_BASE_Y;
			goals[i] = new Sprite(goal);
			goals[i].setScale(0.5f);
			goals[i].setPosition(posx - goals[i].getWidth() / 2, posy - goals[i].getHeight() / 2);
		}
		
		// Lines for track drawing
		i = 0;
		trackData = new Vector2[48];
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
