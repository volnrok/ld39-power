package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Color;

public class Constants {
	public static final int TRACK_BASE_X = 320;
	public static final int TRACK_BASE_Y = 128;
	
	public static final int TRACK_WIDTH_BASE = 72;
	public static final int TRACK_WIDTH_TOP = 24;
	public static final int TRACK_HEIGHT = 288;
	public static final int TRACK_SEP_BASE = 24;
	public static final int TRACK_SEP_TOP = 144;
	
	public static final int TRACK_TOP_Y = TRACK_BASE_Y + TRACK_HEIGHT;

	public static final int TRACK_BASE_X_3 = TRACK_BASE_X - TRACK_WIDTH_BASE / 2;
	public static final int TRACK_TOP_X_3 = TRACK_BASE_X - TRACK_WIDTH_TOP / 2;
	public static final int TRACK_BASE_X_4 = TRACK_BASE_X + TRACK_WIDTH_BASE / 2;
	public static final int TRACK_TOP_X_4 = TRACK_BASE_X + TRACK_WIDTH_TOP / 2;

	public static final int TRACK_BASE_X_2 = TRACK_BASE_X_3 - TRACK_SEP_BASE;
	public static final int TRACK_TOP_X_2 = TRACK_TOP_X_3 - TRACK_SEP_TOP;
	public static final int TRACK_BASE_X_1 = TRACK_BASE_X_2 - TRACK_WIDTH_BASE;
	public static final int TRACK_TOP_X_1 = TRACK_TOP_X_2 - TRACK_WIDTH_TOP;

	public static final int TRACK_BASE_X_5 = TRACK_BASE_X_4 + TRACK_SEP_BASE;
	public static final int TRACK_TOP_X_5 = TRACK_TOP_X_4 + TRACK_SEP_TOP;
	public static final int TRACK_BASE_X_6 = TRACK_BASE_X_5 + TRACK_WIDTH_BASE;
	public static final int TRACK_TOP_X_6 = TRACK_TOP_X_5 + TRACK_WIDTH_TOP;

	public static final int TRACK_LEFT_BASE_X = TRACK_BASE_X_2 - TRACK_WIDTH_BASE / 2;
	public static final int TRACK_LEFT_TOP_X = TRACK_TOP_X_2 - TRACK_WIDTH_TOP / 2;
	public static final int TRACK_RIGHT_BASE_X = TRACK_BASE_X_5 + TRACK_WIDTH_BASE / 2;
	public static final int TRACK_RIGHT_TOP_X = TRACK_TOP_X_5 + TRACK_WIDTH_TOP / 2;
	
	public static final int LINE_WIDTH = 3;
	public static final Color COLOUR_TRACK = new Color(0f, 0.75f, 1f, 1f);
	public static final float TRANSITION_SPEED = 8f;
	public static final float NOTE_WINDOW = 0.08f; // How much time to hit note
	public static final float NOTE_BEATS_SHOWN = 3; // How many beats before hit to show
	public static final float TRACK_END_SCALE = TRACK_WIDTH_BASE / TRACK_WIDTH_TOP;
	public static final float TRACK_VANISHING_Y = TRACK_TOP_Y + TRACK_HEIGHT / TRACK_END_SCALE;
	
	public static final float PULSE_DECAY = 0.9f;
	public static final float PULSE_MIN = 0.4f;
	public static final float PULSE_PARTIAL = 0.7f;
	
	public static final int SCORE_PER_HIT = 10;
	public static final int SCORE_MAX_COMBO = 4;
	public static final int SCORE_HITS_PER_COMBO = 8;
	
	public static final float BRIGHTNESS_PENALTY_PER_MISS = 0.2f;
	public static final float BRIGHTNESS_PENALTY_PER_SECOND = 0.04f;
	public static final float BRIGHTNESS_BONUS_PER_HIT = 0.1f;
}
