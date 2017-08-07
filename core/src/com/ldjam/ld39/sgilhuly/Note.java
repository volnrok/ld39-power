package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Note {
	public int track;
	/*public int bar;
	public float beat;*/
	public float absoluteBeat;
	public float time;
	public float showtime;
	public float progress;
	public float distance;
	public boolean done; // If the note is ready to unload
	
	public boolean isHeld = false; // *is hold*
	public boolean isHit = false; // Has this held note been hit?
	public boolean isGray = false; // Was the string of held notes missed?
	public boolean isHighlighted = false; // Is this note highlighted?
	public boolean isFirstHeld = false;
	public Note nextNote = null;
	
	public Sprite spr;
	
	// Temporary variables for transformation
	private static Vector2 position = new Vector2();
	private static Vector2 transformedPosition = new Vector2();
	private static float[] quad = new float[8];
	private static float[] transformedQuad = new float[8];
	private static Color tempColour = new Color();
	private static Sprite white = new Sprite(Resources.white);
	private static PolygonRegion polygon = new PolygonRegion(white, transformedQuad, new short[] {0, 1, 2, 0, 2, 3});
	
	public Note(int track, int bar, float beat, int beatcount, boolean isHeld) {
		this.track = track;
		/*this.bar = bar;
		this.beat = beat;*/
		this.isHeld = isHeld;
		absoluteBeat = (bar - 1) * beatcount + (beat - 1);
	}
	
	public void calculateTime(float bps, float offset) {
		time = (absoluteBeat / bps) + offset;
		showtime = ((absoluteBeat - Constants.NOTE_BEATS_SHOWN) / bps) + offset;
	}
	
	public void prepareToDraw() {
		if(!isHeld) {
			spr = new Sprite(Resources.button);
		} else if(isHeld && isFirstHeld) {
			spr = new Sprite(Resources.buttonHeld);
		} else {
			spr = null;
		}
		done = false;
	}
	
	public boolean isShown(float currentTime) {
		return currentTime >= showtime;
	}
	
	private void calcTime(float currentTime) {
		progress = Helper.untween(currentTime, showtime, time);
		distance = (1 - progress) * (Constants.TRACK_END_SCALE - 1) + 1;
	}
	
	public boolean update(float currentTime) { // Return true if the note was missed
		calcTime(currentTime);
		
		if(!isHeld) {
			done = time + Constants.NOTE_WINDOW < currentTime;
			return done;
		} else if(isHeld && nextNote == null) {
			done = true;
			return false;
		} else {
			nextNote.calcTime(currentTime);
			done = nextNote.progress > 1.05f;
			return done && !isHit;
		}
	}
	
	public void drawHeld(PolygonSpriteBatch batch, float pulse) {
		if(isHeld && nextNote != null) {
			quad[0] = quad[6] = Constants.TRACK_HALF_WIDTH_HELD_NOTE;
			quad[2] = quad[4] = -Constants.TRACK_HALF_WIDTH_HELD_NOTE;
			if(!isGray && isHit) {
				quad[1] = quad[3] = Math.max(distance, 1);
			} else {
				quad[1] = quad[3] = Math.max(distance, 0.5f);
			}
			quad[5] = quad[7] = Math.max(nextNote.distance, distance);
			Helper.perspective(quad, transformedQuad, track, 0, 4);
			
			if(isGray) {
				tempColour.set(Constants.COLOUR_MISSED_HELD_NOTE);
			} else if(isHighlighted) {
				tempColour.set(Constants.COLOUR_HIGHLIGHTED_NOTE);
				//pulse = Helper.tween(0.25f, pulse, 1);
			} else {
				tempColour.set(Constants.COLOUR_HELD_NOTE);
			}
			tempColour.mul(pulse, pulse, pulse, 1);
			
			batch.setColor(tempColour);
			batch.draw(polygon, 0, 0);
			
			if(track != nextNote.track) {
				if(track < nextNote.track) {
					quad[0] = quad[2] = -Constants.TRACK_HALF_WIDTH_HELD_NOTE;
					quad[4] = quad[6] = Constants.TRACK_HALF_WIDTH_HELD_NOTE;
				} else {
					quad[0] = quad[2] = Constants.TRACK_HALF_WIDTH_HELD_NOTE;
					quad[4] = quad[6] = -Constants.TRACK_HALF_WIDTH_HELD_NOTE;
				}
				/*quad[1] = quad[7] = nextNote.distance - Constants.TRACK_HALF_HEIGHT_HELD_NOTE;
				quad[3] = quad[5] = nextNote.distance + Constants.TRACK_HALF_HEIGHT_HELD_NOTE;*/
				quad[1] = nextNote.distance - Constants.TRACK_HALF_HEIGHT_HELD_NOTE;
				quad[5] = nextNote.distance + Constants.TRACK_HALF_HEIGHT_HELD_NOTE;
				quad[3] = quad[7] = nextNote.distance;
				Helper.perspective(quad, transformedQuad, track, 0, 2);
				Helper.perspective(quad, transformedQuad, nextNote.track, 4, 2);

				batch.draw(polygon, 0, 0);
			}
		}
	}
	
	public void drawSprite(SpriteBatch batch) {
		if(spr != null) {
			position.set(0, distance);
			Helper.perspective(position, transformedPosition, track);
			// Calculate opacity, fade in, and fade out if wasn't hit
			float opacity = 1;
			if(progress < 0.5f) {
				opacity = progress * 2;
			} else if(progress > 1) {
				opacity = Helper.clamp(1 - (progress - 1) * 20, 0, 1);
			}
			spr.setScale(0.5f / distance);
			spr.setPosition(transformedPosition.x - spr.getWidth() / 2, transformedPosition.y - spr.getHeight() / 2);
			spr.draw(batch, opacity);
		}
	}
	
	public boolean isHit(float t) {
		return Math.abs(time - t) <= Constants.NOTE_WINDOW;
	}
	
	public void deselectChain() {
		System.out.println("Deselect Chain");
		Note n = this;
		while(n != null) {
			n.isGray = true;
			n = n.nextNote;
		}
	}
	
	public void highlightChain() {
		System.out.println("Hightlight Chain");
		Note n = this;
		while(n != null) {
			n.isHighlighted = true;
			n = n.nextNote;
		}
	}
}
