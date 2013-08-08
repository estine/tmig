/*
  A simple Java class that writes a MIDI file

  (c)2011 Kevin Boone, all rights reserved
*/
//package com.kevinboone.music;
import java.io.*;
import java.util.*;


public class MidiFile
{
    // Note lengths
    //  We are working with 32 ticks to the crotchet. So
    //  all the other note lengths can be derived from this
    //  basic figure. Note that the longest note we can
    //  represent with this code is one tick short of a 
    //  two semibreves (i.e., 8 crotchets)

    static final int SIXTEENTH = 4;
    static final int EIGTH = 8;
    static final int QUARTER = 16;
    static final int HALF = 32;
    static final int WHOLE = 64;

    // Standard MIDI file header, for one-track file
    // 4D, 54... are just magic numbers to identify the
    //  headers
    // Note that because we're only writing one track, we
    //  can for simplicity combine the file and track headers
  static final int header[] = new int[]
  {
      0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
      0x00, 0x00, // single-track format
      0x00, 0x01, // one track
      0x00, 0x10, // 16 ticks per quarter
      0x4d, 0x54, 0x72, 0x6B
  };

    // Standard footer
  static final int footer[] = new int[]
  {
      0x01, 0xFF, 0x2F, 0x00
  };

    // A MIDI event to set the tempo
  static final int tempoEvent[] = new int[]
  {
      0x00, 0xFF, 0x51, 0x03,
      0x0F, 0x42, 0x40 // Default 1 million usec per crotchet // 290 = 32830
  };

    // A MIDI event to set the key signature. This is irrelent to
    //  playback, but necessary for editing applications 
  static final int keySigEvent[] = new int[]
  {
      0x00, 0xFF, 0x59, 0x02,
      0x00, // C
      0x00  // major
  };


    // A MIDI event to set the time signature. This is irrelavent to
    //  playback, but necessary for editing applications 
  static final int timeSigEvent[] = new int[]
  {
      0x00, 0xFF, 0x58, 0x04,
      0x04, // numerator
      0x02, // denominator (2==4, because it's a power of 2)
      0x30, // ticks per click (not used)
      0x08  // 32nd notes per crotchet 
  };

    // The collection of events to play, in time order
    protected Vector<int[]> playEvents;

    /** Construct a new MidiFile with an empty playback event list */
    public MidiFile()
    {
	playEvents = new Vector<int[]>();
    }


    /** Write the stored MIDI events to a file */
    public void writeToFile (String filename, int BPM)
    throws IOException
    {
	FileOutputStream fos = new FileOutputStream (filename);


	fos.write (intArrayToByteArray (header));

	// Calculate the amount of track data
	// _Do_ include the footer but _do not_ include the 
	// track header

    int size = tempoEvent.length + keySigEvent.length + timeSigEvent.length
	+ footer.length;

    for (int i = 0; i < playEvents.size(); i++)
	size += playEvents.elementAt(i).length;

    // Write out the track data size in big-endian format
    // Note that this math is only valid for up to 64k of data
    //  (but that's a lot of notes) 
    int high = size / 256;
    int low = size - (high * 256);
    fos.write ((byte) 0);
    fos.write ((byte) 0);
    fos.write ((byte) high);
    fos.write ((byte) low);


    // Here we set tempo:
    // Tempo in usecs = 1,000,000 / (tempo/60)
    int tempo = Math.round((float)1000000.0 / (float)(BPM/60.0));
    String parsedToHex = Integer.toHexString(tempo);
    
    if(parsedToHex.length() < 6) { // Padding
	while(parsedToHex.length() < 6) {
	    parsedToHex = "0" + parsedToHex;
	}
    }
    tempoEvent[4] = Integer.parseInt(parsedToHex.substring(0, 2), 16);
    tempoEvent[5] = Integer.parseInt(parsedToHex.substring(2, 4), 16);
    tempoEvent[6] = Integer.parseInt(parsedToHex.substring(4, 6), 16);

    // Write the standard metadata — tempo, etc
    // At present, tempo is stuck at crotchet=60 
    fos.write (intArrayToByteArray (tempoEvent));
    fos.write (intArrayToByteArray (keySigEvent));
    fos.write (intArrayToByteArray (timeSigEvent));

    // Write out the note, etc., events
    for (int i = 0; i < playEvents.size(); i++)
	{
	    fos.write (intArrayToByteArray (playEvents.elementAt(i)));
	}

    // Write the footer and close
    fos.write (intArrayToByteArray (footer));
    fos.close();
    }


    /** Convert an array of integers which are assumed to contain
	unsigned bytes into an array of bytes */
    protected static byte[] intArrayToByteArray (int[] ints)
    {
	int l = ints.length;
	byte[] out = new byte[ints.length];
	for (int i = 0; i < l; i++)
	    {
		out[i] = (byte) ints[i];
	    }
	return out;
    }


    /** Store a note-on event */
    public void noteOn (int delta, int note, int velocity)
    {
	int[] data = new int[4];
	data[0] = delta;
	data[1] = 0x90;
	data[2] = note;
	data[3] = velocity;
	playEvents.add (data);
    }


    /** Store a note-off event */
    public void noteOff (int delta, int note)
    {
	int[] data = new int[4];
	data[0] = delta;
	data[1] = 0x80;
	data[2] = note;
	data[3] = 0;
	playEvents.add (data);
    }


    /** Store a program-change event at current position */
    public void progChange (int prog)
    {
	int[] data = new int[3];
	data[0] = 0;
	data[1] = 0xC0;
	data[2] = prog;
	playEvents.add (data);
    }


    /** Store a note-on event followed by a note-off event a note length
      later. There is no delta value — the note is assumed to
      follow the previous one with no gap. */
    public void noteOnOffNow (int duration, int note, int velocity)
    {
	noteOn (0, note, velocity);
	noteOff (duration, note);
    }


    public void noteSequenceFixedVelocity (int[] sequence, int velocity)
    {
	boolean lastWasRest = false;
	int restDelta = 0;
	for (int i = 0; i < sequence.length; i += 2)
	    {
		int note = sequence[i];
		int duration = sequence[i + 1];
		if (note < 0)
		    {
			// This is a rest
			restDelta += duration;
			lastWasRest = true;
		    }
		else
		    {
			// A note, not a rest
			if (lastWasRest)
			    {
				noteOn (restDelta, note, velocity);
				noteOff (duration, note);
			    }
			else
			    {
				noteOn (0, note, velocity);
				noteOff (duration, note);
			    }
			restDelta = 0;
			lastWasRest = false;
		    }
	    }
    }

    public int[] makeSequence(ArrayList<Note> notes) {
	
	int[] noteSequence = new int[notes.size()*2];

	int noteSeqPtr = 0;
	for(int i = 0; i < notes.size(); i++ ) { // Go by twos
	    int pitch = notes.get(i).getPitch();
	    double length = notes.get(i).getLength();

	    int parsedLength = (int)Math.round(length * 8);

	    noteSequence[noteSeqPtr] = pitch;
	    noteSequence[++noteSeqPtr] = parsedLength;
	    noteSeqPtr++;
	}
	return noteSequence;
    }

    /** Test method — creates a file test1.mid when the class
	is executed */
    public static void main (String[] args)
    throws Exception
    {
	MidiFile mf = new MidiFile();

	// Test 3 — play a short tune using noteSequenceFixedVelocity
	//  Note the rest inserted with a note value of -1

	ArrayList<Note> notes = new MIDIParser().parsePitches("../Giant_Steps_4_Choruses.mid");

	int[] noteSequence = new MidiFile().makeSequence(notes);

    int[] sequence = new int[]
    {
	60, EIGTH + SIXTEENTH,
	65, SIXTEENTH,
	70, QUARTER + EIGTH,
	69, EIGTH,
	65, EIGTH / 3,
	62, EIGTH / 3,
	67, EIGTH / 3,
	72, HALF + EIGTH,
	-1, SIXTEENTH,
	72, SIXTEENTH,
	76, HALF,
    };

    // What the heck — use a different instrument for a change
    mf.progChange (67);

    mf.noteSequenceFixedVelocity (noteSequence, 127);

    mf.writeToFile ("Output.mid", 290); // Output file and BPM
    }
}
