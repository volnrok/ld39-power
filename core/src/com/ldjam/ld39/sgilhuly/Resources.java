package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public abstract class Resources {
	public static Texture button;
	public static Texture goal;
	public static Texture title;
	public static Texture white;
	public static Texture combo2, combo3, combo4;

	public static Sound powerup;
	public static Sound powerdown;
	public static Sound explosion;
	
	public static Music introMusic;
	
	public static void loadResources() {
		button = new Texture("data/button.png");
		button.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		goal = new Texture("data/goal.png");
		goal.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		title = new Texture("data/title.png");
		title.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		white = new Texture("data/white.png");
		combo2 = new Texture("data/x2.png");
		combo2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		combo3 = new Texture("data/x3.png");
		combo3.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		combo4 = new Texture("data/x4.png");
		combo4.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		powerup = Gdx.audio.newSound(Gdx.files.internal("data/pup2.wav"));
		powerdown = Gdx.audio.newSound(Gdx.files.internal("data/pdown2.wav"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("data/expl2.wav"));
		
		introMusic = Gdx.audio.newMusic(Gdx.files.internal("data/intro.mp3"));
	}
	
	public static void dispose() {
		white.dispose();
		button.dispose();
		goal.dispose();
		title.dispose();
		combo2.dispose();
		combo3.dispose();
		combo4.dispose();

		powerup.dispose();
		powerdown.dispose();
		explosion.dispose();
		
		introMusic.dispose();
	}
}
