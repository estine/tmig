/*
  A class that can be used to parse the pitches of a MIDI file
 */

import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import java.util.*;

public class MIDIParser {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;

    public static void main(String[] args) throws Exception {

	ArrayList<Note> notes = new MIDIParser().parsePitches("../Giant_Steps_4_Choruses.mid");
	System.out.print(notes);

    }

    public ArrayList<Note> parsePitches(String filename)  throws Exception {

        Sequence sequence = MidiSystem.getSequence(new File(filename));
	
	Track track = sequence.getTracks()[1]; // Get first track
	ArrayList<Note> notes = new ArrayList<Note>();

	long MicroLen = sequence.getMicrosecondLength() ;
	long TickLen = sequence.getTickLength();
	int PPQ = sequence.getResolution();

	double ms_per_tick = (double) MicroLen / TickLen;
	double BPM = Math.round(1000 * (60000.0 / ms_per_tick / PPQ));

	long prevTick = 0;
	for (int i=0; i < track.size(); i++) {
	    MidiEvent event = track.get(i); // Get next event
	    
	    long tick = event.getTick(); // Get our tick
	    
	    MidiMessage message = event.getMessage();

	    if (message instanceof ShortMessage) {
		ShortMessage sm = (ShortMessage) message;
		
		if (sm.getCommand() == NOTE_ON) {
		    // Do nothing on note on
		} else if (sm.getCommand() == NOTE_OFF) {
		    
		    int key = sm.getData1();
		    int velocity = sm.getData2();
		    
		    if(key == 24){
			key = -1; // Key = -1 for silences
		    }
		    double length = (double)(tick - prevTick)/15360.0; // Divide by size of quarter note

		    notes.add(new Note(key, length)); // Add to our list
		    prevTick = tick; // Update our tick to get length
		} else {
		    //Commands
		    //System.out.println("Command:" + sm.getCommand());
		}
	    } else {
		//Other messages
		//System.out.println("Other message: " + message.getClass());
	    }
	}
	return notes;
    }
}
