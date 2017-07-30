package com.ldjam.ld39.sgilhuly;

public class RhythmObject {
	public float eta;
	public float speed;
	
	public RhythmObject(float _eta) {
		eta = _eta;
	}
	
	public void update(float deltaTime) {
		eta -= deltaTime;
	}
	
	public float distance() {
		return eta * speed;
	}
}
