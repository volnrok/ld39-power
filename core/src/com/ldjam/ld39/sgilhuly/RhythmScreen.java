package com.ldjam.ld39.sgilhuly;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class RhythmScreen extends GameScreen {

	public static final int LEFT = 0;
	public static final int MIDDLE = 1;
	public static final int RIGHT = 2;
	public static final float SFX_VOLUME = 0.65f;
	
	ArrayList<Note> activeNotes = new ArrayList<Note>();
	ArrayList<Particle> particles = new ArrayList<Particle>();
	int nextNote = 0;
	Color tempColour = new Color();
	Conductor conductor;
	Sprite[] goals;
	Sprite white;
	BitmapFont font = new BitmapFont();
	String rhythmFile;
	
	int selected = MIDDLE;
	int lastSelected = LEFT;
	float transition = 1;
	float brightness = 1;
	float lightness = 0;
	float pulse = 1;
	
	int combo = 1;
	int hits = 0;
	int score = 0;
	
	private Vector2[] trackData;
	
	public RhythmScreen(PowerGame game, String rhythmFile) {
		super(game);
		this.rhythmFile = rhythmFile;
		
		white = new Sprite(Resources.white);
		white.setScale(PowerGame.VIRTUAL_WIDTH / Resources.white.getWidth() * 2, PowerGame.VIRTUAL_HEIGHT / Resources.white.getHeight() * 2);
		
		prepGraphics();
		
		try {
			conductor = new Conductor(rhythmFile);
			conductor.music.play();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
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
		
		conductor.update();
		pulse = Helper.tween(Constants.PULSE_DECAY, Constants.PULSE_MIN, pulse);
		if(conductor.isNewBar) {
			pulse = 1;
		} else if(conductor.isNewBeat) {
			pulse = Constants.PULSE_PARTIAL;
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
			note.prepareToDraw(Resources.button);
			activeNotes.add(note);
		}
		
		// Check for pressing spacebar/enter
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
					Resources.powerup.play(SFX_VOLUME);
				}
				
				// TODO: Repeated code, clean up when there is time!
				float posx = Constants.TRACK_BASE_X;
				if(selected == 0) {
					posx = Constants.TRACK_LEFT_BASE_X;
				} else if(selected == 2) {
					posx = Constants.TRACK_RIGHT_BASE_X;
				}
				float posy = Constants.TRACK_BASE_Y;
				particles.add(new Particle(posx, posy, Resources.button));
				activeNotes.remove(hitNote);
				brightness += Constants.BRIGHTNESS_BONUS_PER_HIT;
			} else {
				// No brightness penalty, but lose combo
				hits = 0;
				if(combo != 1) {
					combo = 1;
					Resources.powerdown.play(SFX_VOLUME);
				}
			}
		}
		
		//spriteBatch.setProjectionMatrix(cam.combined);
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.DARK_GRAY);
		shapeBatch.rect(0, 0, PowerGame.VIRTUAL_WIDTH, PowerGame.VIRTUAL_HEIGHT);
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
					Resources.powerdown.play(SFX_VOLUME);
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
		
		// Draw brightness level
		if(!activeNotes.isEmpty()) {
			brightness -= Gdx.graphics.getDeltaTime() * Constants.BRIGHTNESS_PENALTY_PER_SECOND;
		}
		brightness = Helper.clamp(brightness, 0, 1);
		tempColour.set(0, 0, 0, 1 - brightness);
		// Need to draw an image, drawing a rectangle doesn't use opacity
		white.setColor(tempColour);
		white.draw(spriteBatch);
		
		// Draw score
		font.draw(spriteBatch, "Score: " + score, Constants.TRACK_BASE_X - 50, 80);
		Texture comboTexture = null;
		switch(combo) {
		case 2:
			comboTexture = Resources.combo2;
			break;
		case 3:
			comboTexture = Resources.combo3;
			break;
		case 4:
			comboTexture = Resources.combo4;
			break;
		}
		if(comboTexture != null) {
			spriteBatch.draw(comboTexture, Constants.TRACK_BASE_X + 30, 40, comboTexture.getWidth() / 2, comboTexture.getHeight() / 2);
		}
		
		// Draw lightness
		if(!conductor.music.isPlaying() && brightness > 0) {
			lightness += Gdx.graphics.getDeltaTime() / Constants.LIGHTNESS_TRANSITION_SECONDS;
			lightness = Helper.clamp(lightness, 0, 1);
			// Draw yellow and white layers
			white.setColor(1, 1, 0, lightness);
			white.draw(spriteBatch);
			white.setColor(1, 1, 1, lightness * 0.75f);
			white.draw(spriteBatch);
		}
		
		spriteBatch.end();
		
		// Check for the end of the level
		if(lightness >= 1) {
			game.goToScoreScreen(rhythmFile, score, true);
		} else if(brightness <= 0) {
			conductor.music.stop();
			Resources.explosion.play(SFX_VOLUME);
			game.goToScoreScreen(rhythmFile, score, false);
		}
	}

	@Override
	public void dispose() {
		conductor.music.dispose();
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
			goals[i] = new Sprite(Resources.goal);
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
