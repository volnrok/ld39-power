package com.ldjam.ld39.sgilhuly;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class RhythmScreen extends GameScreen implements InputProcessor {

	public static final int LEFT = 0;
	public static final int MIDDLE = 1;
	public static final int RIGHT = 2;
	public static final float SFX_VOLUME = 0.65f;
	
	ArrayList<Note> activeNotes = new ArrayList<Note>();
	ArrayList<Particle> particles = new ArrayList<Particle>();
	PolygonSpriteBatch polyBatch = new PolygonSpriteBatch();
	Sprite bottomGradient;
	Sprite topGradient;
	int nextNote = 0;
	Color tempColour = new Color();
	Conductor conductor;
	Sprite[] goals;
	Sprite white;
	BitmapFont font = new BitmapFont();
	String rhythmFile;
	Note heldNote = null;
	
	int selected = MIDDLE;
	int lastSelected = LEFT;
	float transition = 1;
	float brightness = 1;
	float lightness = 0;
	float pulse = 1;
	
	int combo = 1;
	int hits = 0;
	int score = 0;
	
	private float[][] trackData;
	private PolygonRegion[] trackPolygons;
	
	public RhythmScreen(PowerGame game) {
		super(game);
		this.rhythmFile = game.getLevelPlayed().rhythmFile;
		
		white = new Sprite(Resources.white);
		white.setScale(PowerGame.VIRTUAL_WIDTH / Resources.white.getWidth() * 2, PowerGame.VIRTUAL_HEIGHT / Resources.white.getHeight() * 2);
		
		prepGraphics();
		
		try {
			conductor = new Conductor(rhythmFile);
			conductor.music.play();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Gdx.input.setInputProcessor(this);
	}
	
	private void drawTrack() {
		// Try not to create colour objects frivolously
		for(int i = 0; i < trackData.length; i++) { // 6
			if(i % 2 == 0) {
				tempColour.set(Constants.COLOUR_TRACK);
				if(i / 2 == selected) {
					tempColour.mul(transition * 0.7f + 0.3f);
				} else if(i / 2 == lastSelected) {
					tempColour.mul(1f - transition * 0.7f);
				} else {
					tempColour.mul(0.3f);
				}
				tempColour.mul(pulse);
				tempColour.a = 1;
				polyBatch.setColor(tempColour);
			}
			//shapeBatch.polygon(trackData[i]);
			polyBatch.draw(trackPolygons[i], 0, 0);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.setProjectionMatrix(game.cam.combined);
		shapeBatch.setProjectionMatrix(game.cam.combined);
		polyBatch.setProjectionMatrix(game.cam.combined);
		
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
		
		// Check for end of held note
		if(heldNote != null && (heldNote.nextNote.time + Constants.NOTE_WINDOW) < conductor.currentTime) {
			System.out.println("Hold lapsed");
			releaseNote();
		}
		
		/*if(transition >= 1) {
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
		}*/
		
		// Check for activating notes
		while(nextNote < conductor.notes.size() && conductor.notes.get(nextNote).isShown(conductor.currentTime)) {
			Note note = conductor.notes.get(nextNote++);
			note.prepareToDraw();
			activeNotes.add(note);
		}
		
		// Update active notes
		
		// Check for pressing spacebar/enter
		/*if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
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
		}*/
		// Update active notes
		for(Iterator<Note> it = activeNotes.iterator(); it.hasNext();) {
			Note n = it.next();
			if(!n.done && n.update(conductor.currentTime) && n != heldNote) {
				if(n.isHeld && !n.isFirstHeld && !n.isGray) {
					releaseNote();
				} else if(!n.isGray) {
					
					if(n.isHeld) {
						n.deselectChain();
					} else {
						// Penalize!
						losePoints(false);
					}
				}
			}
			if(n.done) {
				it.remove();
			}
		}
		
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(Color.DARK_GRAY);
		shapeBatch.rect(0, 0, PowerGame.VIRTUAL_WIDTH, PowerGame.VIRTUAL_HEIGHT);
		shapeBatch.setAutoShapeType(true);
		shapeBatch.end();

		polyBatch.begin();
		drawTrack();
		for(Note n : activeNotes) {
			n.drawHeld(polyBatch, pulse);
		}
		polyBatch.end();
		
		spriteBatch.begin();
		// Draw gradient to hide tracks
		topGradient.draw(spriteBatch);
		bottomGradient.draw(spriteBatch);
		// Draw goals
		for(Sprite s : goals) {
			s.draw(spriteBatch, pulse);
		}
		for(Note n : activeNotes) {
			n.drawSprite(spriteBatch);
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
			game.goToScoreScreen(score, true);
		} else if(brightness <= 0) {
			conductor.music.stop();
			Resources.explosion.play(SFX_VOLUME);
			game.goToScoreScreen(score, false);
		}
	}

	@Override
	public void dispose() {
		conductor.music.dispose();
		Gdx.input.setInputProcessor(null);
	}
	
	private void prepGraphics() {
		// Gradients
		bottomGradient = new Sprite(Resources.gradient);
		bottomGradient.setSize(PowerGame.VIRTUAL_WIDTH, Resources.gradient.getHeight());
		
		topGradient = new Sprite(Resources.gradient);
		topGradient.setPosition(0, Constants.TRACK_TOP_Y - 32);
		topGradient.setFlip(false, true);
		topGradient.setSize(PowerGame.VIRTUAL_WIDTH, Resources.gradient.getHeight());
		
		// Goal spots
		goals = new Sprite[3];
		for(int i = 0; i < 3; i++) {
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
		Vector2 in = new Vector2(), out = new Vector2();
		short[] triangles = {0, 1, 2, 0, 2, 3};
		trackData = new float[6][8];
		trackPolygons = new PolygonRegion[6];
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 4; j++) {
				in.x = Constants.TRACK_HALF_WIDTH_BASE;
				if(j == 2 || j == 3) {
					in.x += Constants.TRACK_BASE_LINE_WIDTH;
				}
				if(i % 2 == 1) {
					in.x = -in.x;
				}
				
				in.y = 1;
				if(j == 1 || j == 2) {
					in.y = Constants.TRACK_END_SCALE;
				}
				
				Helper.perspective(in, out, i / 2);
				trackData[i][j * 2] = out.x;
				trackData[i][j * 2 + 1] = out.y;
			}
			trackPolygons[i] = new PolygonRegion(white, trackData[i], triangles);
		}
	}
	
	private void checkKeyPresses(boolean spaceHit) {
		Note hitNote = null;
		
		// Check for track transitions
		if(!spaceHit) {
			int desired = MIDDLE;
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				desired = LEFT;
			} else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				desired = RIGHT;
			}
			
			if(desired != selected) {
				/*// Make sure we don't switch from left to right, or vice versa
				if((desired == LEFT && selected == RIGHT) || (desired == RIGHT && selected == LEFT)) {
					desired = MIDDLE;
				}*/
				lastSelected = selected;
				selected = desired;
				transition = 0;
				
				// Check for hit slide notes
				if(heldNote != null && heldNote.nextNote.isHit(conductor.currentTime) && heldNote.nextNote.track == selected) {
					System.out.println("Note Slid");
					hitNote = heldNote.nextNote;
					hitNote.isHit = true;
					heldNote.done = true;
					if(heldNote.nextNote == null) {
						releaseNote();
					}
				} else if(heldNote != null) {
					System.out.println("Slide missed - now: " + conductor.currentTime + " next note: " + heldNote.nextNote.time); 
					releaseNote();
				}
			}
		}
		
		// Check for hit regular notes
		if(spaceHit) {
			for(Note n : activeNotes) {
				if(n.isHit(conductor.currentTime) && n.track == selected && !n.isGray && (!n.isHeld || n.isFirstHeld)) {
					System.out.println("Note Hit");
					hitNote = n;
					break;
				}
			}
		}
		
		// Add score for hitting / sliding, and penalize for missing
		if(hitNote != null) {
			gainPoints();
			
			// TODO: Repeated code, clean up when there is time!
			float posx = Constants.TRACK_BASE_X;
			if(selected == 0) {
				posx = Constants.TRACK_LEFT_BASE_X;
			} else if(selected == 2) {
				posx = Constants.TRACK_RIGHT_BASE_X;
			}
			float posy = Constants.TRACK_BASE_Y;
			if(hitNote.spr != null) {
				particles.add(new Particle(posx, posy, hitNote.spr.getTexture()));
			}
			brightness += Constants.BRIGHTNESS_BONUS_PER_HIT;
			if(hitNote.isHeld) {
				heldNote = hitNote;
				hitNote.spr = null;
				hitNote.isHit = true;
				hitNote.highlightChain();
			} else {
				activeNotes.remove(hitNote);
			}
		} else if(spaceHit) {
			// No brightness penalty, but lose combo
			losePoints(true);
		}
	}
	
	private void releaseNote() {
		if(heldNote != null) {
			System.out.println("Resease Note");
			if(heldNote.nextNote != null && heldNote.nextNote.nextNote != null) {
				// Penalty for missing at least one note
				losePoints(false);
			}
			heldNote.deselectChain();
			heldNote = null;
		}
	}
	
	private void gainPoints() {
		hits++;
		score += Constants.SCORE_PER_HIT * combo;
		if(hits % Constants.SCORE_HITS_PER_COMBO == 0 && combo < Constants.SCORE_MAX_COMBO) {
			combo++;
			Resources.powerup.play(SFX_VOLUME);
		}
	}
	
	private void losePoints(boolean comboOnly) {
		if(!comboOnly) {
			brightness -= Constants.BRIGHTNESS_PENALTY_PER_MISS;
		}
		hits = 0;
		if(combo != 1) {
			combo = 1;
			Resources.powerdown.play(SFX_VOLUME);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT) {
			checkKeyPresses(false);
			return true;
		} else if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
			checkKeyPresses(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT) {
			checkKeyPresses(false);
			return true;
		} else if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
			releaseNote();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
