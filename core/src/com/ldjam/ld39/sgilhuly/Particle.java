package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particle {
	public static final float DELTA_SCALE = 4;
	public static final float DELTA_OPACITY = 6;
	
	public float posx;
	public float posy;
	public Sprite spr;
	public float scale = 0.5f;
	public float opacity = 1;
	
	public Particle(float posx, float posy, Texture tex) {
		this.posx = posx;
		this.posy = posy;
		spr = new Sprite(tex);
	}
	
	public void updateRender(float elapsed, SpriteBatch batch) {
		scale += DELTA_SCALE * elapsed;
		opacity = Helper.clamp(opacity - DELTA_OPACITY * elapsed, 0, 1);
		spr.setScale(scale);
		spr.setPosition(posx - spr.getWidth() / 2, posy - spr.getHeight() / 2);
		spr.draw(batch, opacity);
	}
	
	public boolean isDone() {
		return opacity <= 0;
	}
}
