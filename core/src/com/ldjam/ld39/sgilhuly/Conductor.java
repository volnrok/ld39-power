package com.ldjam.ld39.sgilhuly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Conductor {
	public float bpm;
	public float bps;
	public int beatdiv; // What fraction of a whole tone is a beat?
	public int beatcount; // How many beats in a bar?
	public float offset; // How many seconds to offset before the song starts?
	public Music music;
	public ArrayList<Note> notes = new ArrayList<Note>();
	
	public float currentTime;
	public float currentPosition;
	public int currentBar;
	public float currentBeat;
	public boolean isNewBar;
	public boolean isNewBeat;
	
	public Conductor(String filename) {
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(new FileHandle(filename));
		
		bpm = root.getFloat("bpm");
		bps = bpm / 60.0f;
		beatdiv = root.getInt("beatdiv");
		beatcount = root.getInt("beatcount");
		offset = root.getFloat("offset");
		
		JsonValue noteData = root.get("notes");
		for(JsonValue entry : noteData.iterator()) {
			if(entry.has("note")) {
				JsonValue data = entry.get("note");
				Note note = new Note(data.getInt(0), data.getInt(1), data.getFloat(2), beatcount, false);
				note.calculateTime(bps, offset);
				notes.add(note);
			} else if(entry.has("slide")) {
				Note lastHeldNote = null;
				for(JsonValue heldNote : entry.get("slide")) {
					if(heldNote.has("note")) {
						JsonValue data = heldNote.get("note");
						Note note = new Note(data.getInt(0), data.getInt(1), data.getFloat(2), beatcount, true);
						note.calculateTime(bps, offset);
						note.isHeld = true;
						if(lastHeldNote != null) {
							lastHeldNote.nextNote = note;
						} else {
							note.isFirstHeld = true;
						}
						notes.add(note);
						lastHeldNote = note;
					}
				}
			}
		}
		Collections.sort(notes, new Comparator<Note>() {
			@Override
			public int compare(Note a, Note b) {
				return Float.compare(a.absoluteBeat, b.absoluteBeat);
			}
		});
		
		System.out.println("File loaded, " + notes.size() + " notes found");
		
		music = Gdx.audio.newMusic(Gdx.files.internal("data/" + root.getString("filename")));
	}
	
	public void update() {
		float lastBar = currentBar;
		float lastBeat = currentBeat;
		
		currentTime = music.getPosition() - offset;
		currentPosition = currentTime * bps;
		currentBar = (int)(currentPosition / beatcount) + 1;
		currentBeat = (currentPosition % beatcount) + 1;

		isNewBar = music.isPlaying() && currentBar != lastBar;
		isNewBeat = music.isPlaying() && (int)(currentBeat) != (int)(lastBeat);
	}
}
