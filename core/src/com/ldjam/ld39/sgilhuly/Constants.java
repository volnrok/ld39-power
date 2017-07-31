package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.graphics.Color;

public class Constants {
	public static final int TRACK_BASE_X = 320;
	public static final int TRACK_BASE_Y = 128;
	
	public static final int TRACK_WIDTH_BASE = 64;
	public static final int TRACK_WIDTH_TOP = 8;
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
	
	public static final int LINE_WIDTH = 3;
	public static final Color COLOUR_TRACK = new Color(0f, 0.75f, 1f, 1f);
}
