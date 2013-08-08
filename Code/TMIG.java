// Trained Music Improvisation Generator
// Eli Stine and Nick Towbin-Jones
// Artificial Intelligence
// Spring 2013

import java.util.*;
import java.io.*;
import javax.sound.midi.*; // ADDED

public class TMIG {

    public static void main(String[] args) throws Exception {

	if(args.length < 3) {
	    System.out.println("error: usage: java TMIG <input MIDI file> <input chords list> <order>");
	    System.exit(1);
	}

	// Call buildModel with input data (does parsing, etc.) - notify user of any errors
	Models models = new Models(Integer.parseInt(args[2])); //possibly add CLI for args 

	models = new TMIG().buildModels(args[0], args[1], models);

	// Calculate BPM
        Sequence sequence = MidiSystem.getSequence(new File(args[0]));

        long MicroLen = sequence.getMicrosecondLength() ;
        long TickLen = sequence.getTickLength();
        int PPQ = sequence.getResolution();

        double ms_per_tick = (double) MicroLen / TickLen;
        double BPM = Math.round(1000 * (60000.0 / ms_per_tick / PPQ));

	Console console = System.console();

	if(args.length == 3) { // Skip over if have extra CLI
	    // Prompt user if they want to specify another input MIDI file/chord list pair
	    String input = console.readLine("More MIDI/chord list training data ('n' to stop)?: ");
	    boolean gettingInput;
	    if(input.equals("n")) {
		gettingInput = false;
	    } else {
		gettingInput = true;
	    }
	    while(gettingInput) {
		
		input = console.readLine("MIDI file: ");
		String midifile = input;
		input = console.readLine("Chord list: ");
		String chordlist = input;
		
		models = new TMIG().buildModels(midifile, chordlist, models); //update our already exists models

		input = console.readLine("More MIDI/chord list training data ('n' to stop)?: ");
		
		if(input.equals("n")) {
		    gettingInput = false;
		} else {
		    gettingInput = true;
		}
	    }
	}
	// Prompt user for output chord list file
	// Prompt user for output MIDI file location (or if they just want to play the file)
	System.out.println();
	String outputchordlist = console.readLine("Chord list to generate improv. with: ");
	String outputmidifile = console.readLine("Output MIDI file location: ");

	// Call generateOutput and create file (or simply play the file)
	new TMIG().generateOutput(models, outputchordlist, outputmidifile, (int) (BPM / 2)); // ADDED
	
	String input = console.readLine("Generate how many more improvisations ('n' to stop)?: ");
	if(input.equals("n")) {
	    System.exit(0);
	} else {
	    for(int i = 0; i < Integer.parseInt(input); i++) {
		
		//String[] splitstring = outputmidifile.split("[.]");
		String multifilename = outputmidifile.split("[.]")[0];
		int countFrom = i + 2;
		multifilename += "_" + countFrom; multifilename += ".mid";
		
		new TMIG().generateOutput(models, outputchordlist, multifilename, (int) (BPM / 2)); // Output more, subscript starting with 2
		
	    }
	}

    }


    public Models buildModels(String midifile, String chordlist, Models models)  throws Exception {
	ArrayList<Note> notes = new MIDIParser().parsePitches(midifile);

        Sequence sequence = MidiSystem.getSequence(new File(midifile));

        long MicroLen = sequence.getMicrosecondLength() ;
        long TickLen = sequence.getTickLength();
        int PPQ = sequence.getResolution();

        double ms_per_tick = (double) MicroLen / TickLen;
        double BPM = Math.round(1000 * (60000.0 / ms_per_tick / PPQ));

	System.out.println("Parsed " + notes.size() + " input notes, BPM detected as " + (int)BPM + ".");

	ArrayList<Chord> chords = new ChordParser().parseChords(chordlist);

	// Add on first chord to end of chords (cyclical)
	chords.add(chords.get(0));

	//possibly check if sum of note lengths == sum of chord lengths (so that we have a tight match), return error if not

	Chord c1 = new Chord();
	Chord c2 = new Chord();
	Note tempnote;
	double count;
	double nextCount = 0;
	ArrayList<Note> melody = new ArrayList<Note>();
	int noteptr = 0; //pointer to next note in input

	for(int i = 0; i < chords.size() - 1; i++) { //for each chord

	    c1 = chords.get(i);
	    c2 = chords.get(i+1);

	    System.out.print("Building Markov Model for chord " + c1 + "->" + c2 + "                      \r");
	    
	    count = c1.getLength() - nextCount; //subtract prev measure
	    nextCount = 0; //reset nextCount

	    while(count > 0.0) {
		tempnote = notes.get(noteptr);
		if(count - tempnote.getLength() < 0.0) {
		    nextCount -= (count - tempnote.getLength()); //if overlap, remove from next chord
		}
		melody.add(tempnote);
		count -= tempnote.getLength();
		if ((noteptr + 1) < notes.size()) {
		    noteptr++;
		}
	    }

	    models.add(c1, c2, melody);
	    melody.clear(); // Clear melody
	}

	return models;
    }

    // In each MarkovModel, add in last two notes of the previous chord, along with first note of current chord
    // AND last note of previous chord, with first two notes of current chord
    public void generateOutput(Models models, String outputchordlist, String outputmidifile, int tempo) throws Exception {
	
	ArrayList<Chord> outputchords = new ChordParser().parseChords(outputchordlist);
	if(!models.sufficientData(outputchords)){
		System.out.println("Insufficient data: try a lower order or different output chords list");
		System.exit(1);
	}

	outputchords.add(outputchords.get(0)); // add first chord to end

	Chord c1 = new Chord();
	Chord c2 = new Chord();
	ArrayList<Note> finaloutput = new ArrayList<Note>();
	ArrayList<Note> tempmelody = new ArrayList<Note>();

	// The first time we do it, we use our first seed
	c1 = outputchords.get(0);
	c2 = outputchords.get(1);

	for(int i = 0; i < outputchords.size() - 1; i++) { //for each chord pair
	    c1 = outputchords.get(i);
	    c2 = outputchords.get(i+1);
	
	    System.out.print("Generating notes for chord " + c1 + "->" + c2 + "                      \r");
	    tempmelody = models.generate(c1, c2); // For now, first two notes of that chord
	    
	    finaloutput.addAll(tempmelody); //append all to list
	}

	MidiFile mf = new MidiFile();

	int[] noteSequence = mf.makeSequence(finaloutput);

	System.out.println("                                                 ");
	System.out.println("Generated improvisation of " + noteSequence.length + " notes with BPM " + (tempo*2) + ".                     ");

	mf.progChange (1); //tenor sax
	mf.noteSequenceFixedVelocity (noteSequence, 64);

	mf.writeToFile(outputmidifile, tempo);
    }


}