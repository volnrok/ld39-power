package com.ldjam.ld39.sgilhuly;

import com.badlogic.gdx.math.Vector2;

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
	
	public static float clamp(float n, float min, float max) {
		return n < min ? min : n > max ? max : n;
	}
	
	public static void perspective(float[] in, float[] out, int track, int offset, int count) {
		for(int i = 0; i < count; i++) {
			out[offset + i*2 + 1] = tween(1 / in[offset + i*2 + 1], Constants.TRACK_VANISHING_Y, Constants.TRACK_BASE_Y);
			out[offset + i*2] = in[offset + i*2] / in[offset + i*2 + 1] + (track - 1) * tween(1 / in[offset + i*2 + 1], Constants.TRACK_VANISHING_X_TOP, Constants.TRACK_VANISHING_X_BASE) + Constants.TRACK_BASE_X;
		}
	}
	
	public static Vector2 perspective(Vector2 in, Vector2 out, int track) {
		out.y = tween(1 / in.y, Constants.TRACK_VANISHING_Y, Constants.TRACK_BASE_Y);
		out.x = in.x / in.y + (track - 1) * tween(1 / in.y, Constants.TRACK_VANISHING_X_TOP, Constants.TRACK_VANISHING_X_BASE) + Constants.TRACK_BASE_X;
		return out;
	}
}
