/*

  This class consists of a hashmap from pairs of chords -> Markov models representing pairs of notes and the notes that follow them

 */

import java.util.*;

public class Models{

    public HashMap<ChordPair, MarkovModel> list;
    int order;
	
	public Models(int order){
	    list = new HashMap<ChordPair, MarkovModel>();
	    this.order = order;
	}
	
	public boolean sufficientData(ArrayList<Chord> outputChords){
		for(int i = 0; i<outputChords.size() - 1; i++){
		ChordPair cp = new ChordPair(outputChords.get(i), outputChords.get(i+1));
			if(!list.containsKey(cp)){
				return false;
			}
		}
		return true;
	}
	
    public void add(Chord c1, Chord c2, ArrayList<Note> melody){
		ChordPair cp = new ChordPair(c1, c2);
		MarkovModel model = new MarkovModel();

		if(list.containsKey(cp)) {
		    model = list.get(cp);
		}

		for(int i = 0; i<melody.size() - order; i++){
			ArrayList<Note> noteTup = new ArrayList<Note>();
			Note tempNote = new Note();
			for(int j = 0; j<order; j++){
			    Note note = new Note(melody.get(i+j).getPitch(), melody.get(i+j).getLength());
			    noteTup.add(note);
			}

			for( Note note : noteTup ) {
			    if(note.getPitch() != -1) {
				note.setPitch(note.getPitch() - c1.getPitch()); //shift by root of first chord in cp
			    }
			}

			tempNote = new Note(melody.get(i+order).getPitch(), melody.get(i+order).getLength()); //next note
			if(tempNote.getPitch() != -1) {
			    tempNote.setPitch(tempNote.getPitch() - c1.getPitch()); // Have to shift this too
			}			

			model.add(noteTup);
			model.get(noteTup).add(tempNote); // Add our next note to the melody

			// Add reverse
			ArrayList<Note> noteTupReverse = new ArrayList<Note>();
			noteTupReverse.add(tempNote);
			for(int j = noteTup.size() - 1; j>=1; j--){
				noteTupReverse.add(noteTup.get(j));
			}
			
			Note tempNoteReverse = new Note(noteTup.get(0).getPitch(), noteTup.get(0).getLength());

			model.add(noteTupReverse);
			model.get(noteTupReverse).add(tempNoteReverse);

		}

		if(melody.size() > order) {
		    list.put(cp, model);
		}

	}
	

    public ArrayList<Note> generate(Chord c1, Chord c2) throws Exception { //Todo: add input that is base of chordpair

	ChordPair cp = new ChordPair(c1, c2);
	MarkovModel model = list.get(cp);

	ArrayList<Note> output = new ArrayList<Note>();
	double count = (double)cp.getLength(); //get the length of the bar we're filling up, in quarter notes

	// Don't use a reference to it!
	ArrayList<Note> prevNotesTemp = model.getRandom();
	ArrayList<Note> prevNotes = new ArrayList<Note>();
	for(int i = 0; i < prevNotesTemp.size(); i++) {
	    prevNotes.add(prevNotesTemp.get(i).copy());
	}

	// Get first notes of (initial) input, change the pitch of them
	for(int i = 0; i<prevNotes.size(); i++){
		output.add(prevNotes.get(i).copy());
		count -= prevNotes.get(i).getLength();
	}

	Markov m;

	int reverseCounter = 0; //How many times we wrap around

	while(count > 0.0) { // While we still have an eighth note left in chord

	    // Reverse if we get null
	    if(!model.contains(prevNotes)) {
		ArrayList<Note> reverseTemp = new ArrayList<Note>();
		for(int i = prevNotes.size() - 1; i>=0; i--){
		    reverseTemp.add(prevNotes.get(i).copy());
		}
		prevNotes = reverseTemp;
		reverseCounter++;
		if(reverseCounter == 1) { // 50% chance the first time

		    Random rand = new Random();
		    if(reverseCounter == 2 || (rand.nextInt(2) == 0)) { // Get entirely new prevNotes if we've looped with chance of 50%
			
			ArrayList<Note> prevNotesRef = model.getRandom();
			ArrayList<Note> prevNotesTemporary = new ArrayList<Note>();
			for(int i = 0; i < prevNotesRef.size(); i++) {
			    prevNotesTemporary.add(prevNotesRef.get(i).copy());
			}
			reverseCounter = 0;
			prevNotes = prevNotesTemporary;
		    }

		}


	    }
	    m  = model.get(prevNotes);
	    
	    for(int i = 0; i<prevNotes.size() - 1; i++){ //shift prevNotes forward after generation
		prevNotes.set(i, prevNotes.get(i+1).copy());
	    }

	    Note tempNoteFL = m.getRandom();
	    prevNotes.set(prevNotes.size() - 1, tempNoteFL.copy());
	    //secondnote = m.getRandom(); //get a random note, now second note is our new note

	    if(count - prevNotes.get(prevNotes.size() - 1).getLength() < 0) {
			Note toAdd = prevNotes.get(prevNotes.size() - 1).copy();
			toAdd.setLength(count);
			output.add(toAdd); //add to output
			break;
	    } else {
			output.add(prevNotes.get(prevNotes.size() - 1).copy()); //add to output
			count -= prevNotes.get(prevNotes.size() - 1).getLength();
	    }

	    
	    
	}

	// Change pitches
	for(int i = 0; i<output.size(); i++){
		if(output.get(i).getPitch() != -1) {//if it's not silence
			output.get(i).setPitch(output.get(i).getPitch() + c1.getPitch());
		}
	}
	
	return output;
    }
	
}