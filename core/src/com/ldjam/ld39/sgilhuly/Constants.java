package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Color;

public class Constants {
	public static final int TRACK_BASE_X = 320;
	public static final int TRACK_BASE_Y = 128;
	
	public static final int TRACK_HALF_WIDTH_BASE = 36;
	public static final int TRACK_HALF_WIDTH_TOP = 12;
	public static final int TRACK_HEIGHT = 288;
	public static final int TRACK_SEP_BASE = 128;
	public static final int TRACK_SEP_TOP = 196;
	
	public static final int TRACK_TOP_Y = TRACK_BASE_Y + TRACK_HEIGHT;

	public static final int TRACK_LEFT_BASE_X = TRACK_BASE_X - TRACK_SEP_BASE;
	public static final int TRACK_LEFT_TOP_X = TRACK_BASE_X - TRACK_SEP_TOP;
	public static final int TRACK_RIGHT_BASE_X = TRACK_BASE_X + TRACK_SEP_BASE;
	public static final int TRACK_RIGHT_TOP_X = TRACK_BASE_X + TRACK_SEP_TOP;
	public static final int TRACK_BASE_LINE_WIDTH = 12;
	
	public static final float TRANSITION_SPEED = 4f; // What fraction of a second does it take to switch tracks?
	public static final float NOTE_WINDOW = 0.08f; // How much time to hit note
	public static final float NOTE_BEATS_SHOWN = 3; // How many beats before hit to show
	public static final float TRACK_END_SCALE = TRACK_HALF_WIDTH_BASE / TRACK_HALF_WIDTH_TOP;
	public static final float TRACK_VANISHING_Y = TRACK_TOP_Y + TRACK_HEIGHT / (TRACK_END_SCALE - 1);
	public static final float TRACK_VANISHING_X_TOP = TRACK_RIGHT_TOP_X + (TRACK_RIGHT_TOP_X - TRACK_RIGHT_BASE_X) / (TRACK_END_SCALE - 1) - TRACK_BASE_X;
	public static final float TRACK_VANISHING_X_BASE = TRACK_RIGHT_BASE_X - TRACK_BASE_X;

	public static final float TRACK_HALF_WIDTH_HELD_NOTE = 24;
	public static final float TRACK_HALF_HEIGHT_HELD_NOTE = 0.075f;
	
	public static final float PULSE_DECAY = 0.88f;
	public static final float PULSE_MIN = 0.4f;
	public static final float PULSE_PARTIAL = 0.75f;
	
	public static final int SCORE_PER_HIT = 10;
	public static final int SCORE_MAX_COMBO = 4;
	public static final int SCORE_HITS_PER_COMBO = 8;
	
	public static final float BRIGHTNESS_PENALTY_PER_MISS = 0.15f;
	public static final float BRIGHTNESS_PENALTY_PER_SECOND = 0.06f;
	public static final float BRIGHTNESS_BONUS_PER_HIT = 0.1f;
	public static final float LIGHTNESS_TRANSITION_SECONDS = 1.2f;
	
	public static final Color COLOUR_TRACK = new Color(0f, 0.75f, 1f, 1f);
	public static final Color COLOUR_HELD_NOTE = new Color(1f, 0.375f, 0f, 1f);
	public static final Color COLOUR_HIGHLIGHTED_NOTE = new Color(1f, 0.75f, 0.25f, 1f);
	public static final Color COLOUR_MISSED_HELD_NOTE = new Color(0.125f, 0.125f, 0.125f, 1f);
}
