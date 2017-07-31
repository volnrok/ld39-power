package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Note {
	public int track;
	public int bar;
	public float beat;
	public float position;
	public float time;
	public float showtime;
	
	public Sprite spr;
	
	public Note(int track, int bar, float beat, int beatcount) {
		this.track = track;
		this.bar = bar;
		this.beat = beat;
		position = (this.bar - 1) * beatcount + (this.beat - 1);
	}
	
	public void calculateTime(float bps, float offset) {
		time = (position / bps) + offset;
		showtime = ((position - Constants.NOTE_BEATS_SHOWN) / bps) + offset;
		System.out.println(String.format("Note on beat %d:%f (%f), time is %f, show at %f", bar, beat, position, time, showtime));
	}
	
	public void prepareToDraw(Texture tex) {
		spr = new Sprite(tex);
	}
	
	public boolean isShown(float currentTime) {
		return currentTime >= showtime;
	}
	
	public void draw(float currentTime, float speed, SpriteBatch batch) {
		float distance = (1 - Helper.untween(currentTime, showtime, time)) * Constants.TRACK_END_SCALE + 1;
		float posy = Helper.calcHeight(distance);
		float xbase = 0, xtop = 0;
		switch(track) {
		case PowerGame.LEFT:
			xbase = Constants.TRACK_LEFT_BASE_X;
			xtop = Constants.TRACK_LEFT_TOP_X;
			break;
		case PowerGame.MIDDLE:
			xbase = Constants.TRACK_BASE_X;
			xtop = Constants.TRACK_BASE_X;
			break;
		case PowerGame.RIGHT:
			xbase = Constants.TRACK_RIGHT_BASE_X;
			xtop = Constants.TRACK_RIGHT_TOP_X;
			break;
		}
		float posx = Helper.tween(Helper.untween(posy, Constants.TRACK_BASE_Y, Constants.TRACK_TOP_Y), xbase, xtop);
		spr.setScale(1 / distance);
		//spr.set
		spr.setPosition(posx - (spr.getWidth() / 2), posy - (spr.getHeight() / 2));
		spr.draw(batch);
	}
}
