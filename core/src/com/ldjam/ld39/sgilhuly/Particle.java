package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particle {
	public static final float DELTA_SCALE = 4;
	public static final float DELTA_ALPHA = 6;
	
	public float posx;
	public float posy;
	public float dx;
	public float dy;
	public Sprite spr;
	public float scale = 0.5f;
	public float alpha = 1;
	public float theta = 0;
	public float dscale;
	public float dalpha;
	public float dtheta;
	
	public Particle(float posx, float posy, Texture tex) {
		this(posx, posy, 0, 0, DELTA_SCALE, DELTA_ALPHA, 0, tex);
	}
	
	public Particle(float posx, float posy, float dx, float dy, float dscale, float dalpha, float dtheta, Texture tex) {
		this.posx = posx;
		this.posy = posy;
		this.dx = dx;
		this.dy = dy;
		this.dscale = dscale;
		this.dalpha = dalpha;
		this.dtheta = dtheta;
		spr = new Sprite(tex);
	}
	
	public void updateRender(float elapsed, SpriteBatch batch) {
		scale = Math.max(scale + dscale * elapsed, 0);
		alpha = Helper.clamp(alpha - dalpha * elapsed, 0, 1);
		theta += dtheta * elapsed;
		posx += dx * elapsed;
		posy += dy * elapsed;
		//spr.setPosition(0, 0);
		spr.setScale(scale);
		spr.setOrigin(spr.getWidth() / 2, spr.getHeight() / 2);
		spr.setPosition(posx - spr.getWidth() / 2, posy - spr.getHeight() / 2);
		spr.setRotation(theta);
		spr.draw(batch, alpha);
	}
	
	public boolean isDone() {
		return alpha <= 0 || scale <= 0;
	}
}
