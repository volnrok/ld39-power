package com.ldjam.ld39.sgilhuly;

public abstract class Helper {
	// Should have been called lerp but w/e
	public static float untween(float n, float start, float end) {
		return (n - start) / (end - start);
	}
	
	public static float tween(float n, float start, float end) {
		return start + n * (end - start);
	}
	
	public static float calcHeight(float distance) {
		return tween(1 / distance, Constants.TRACK_VANISHING_Y, Constants.TRACK_BASE_Y);
	}
}
