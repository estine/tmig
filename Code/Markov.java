/*
  This class consists of a hashmap of Note -> Int representing how many times it occurs
 */

import java.util.*;

public class Markov{
	ArrayList<Note> pastNotes;
	public Integer total;
	private HashMap<Note, Integer> map;
	
	public Markov(ArrayList<Note> pastNotes){
		this.pastNotes = pastNotes;
		total = 0;
		map = new HashMap<Note, Integer>();
	}

    public Markov(){
	map = new HashMap<Note, Integer>();
	pastNotes = new ArrayList<Note>();
	total = 0;
    }
	
    public void add(Note n) {

	// We wight silences > normal notes > short notes
	int addVal = 10;
	if(n.getPitch() == -1) { //Extra weighting for silence
	    addVal += 300;
	} else if (n.getLength() >= 0.5) {
	    addVal += 150;
	}

	if(!map.containsKey(n)) {
	    map.put(n, addVal);
	}
	else {
	    map.put(n, addVal + map.get(n));
	}
	total += addVal;
    }
    
    public String toString() {
	String retString = "Mark: " + pastNotes + " Notes: ";
	Iterator<Note> itr = map.keySet().iterator();
	while(itr.hasNext()) {
	    Note next = itr.next();
	    retString += next + " " + map.get(next) + " ";
	}
	return retString;
    }

    public Note getRandom(){
	Random rand = new Random();
	int choice = rand.nextInt(total);
	Iterator<Note> itr = map.keySet().iterator();
	Note next = itr.next();
	while(itr.hasNext() && choice > 0){
	    choice = choice - map.get(next);
	    next = itr.next();
	}
	return next;
    }
    
    public int hashCode() {
        int hash = 1;
        hash = hash * 67 + pastNotes.hashCode();
        hash = hash * 73 + total.hashCode();
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
	if (!(obj instanceof Markov))
            return false;
	if(obj.hashCode() == this.hashCode()) // If haseCodes equal, are equal
            return true;
        return false;

    }
	
}
