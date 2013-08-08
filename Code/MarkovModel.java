/*
This class contains a hashmap from pairs_of_notes -> notes that occur after these notes

 */

import java.util.*;


public class MarkovModel{

    public HashMap<ArrayList<Note>, Markov> model;
	
    public MarkovModel() {
	this.model = new HashMap<ArrayList<Note>, Markov>();
    }

    public void add(ArrayList<Note> notePair) {

	if(!model.containsKey(notePair)) { // If it doesn't already contain it
	    model.put(notePair, new Markov(notePair));
	}
	    
    }

    public Markov get(ArrayList<Note> notePair){
	return model.get(notePair);
    }

    public void printModel() {
	System.out.println("MarkovModel: " + model);
    }

    public boolean contains(ArrayList<Note> notePair) {
	return model.containsKey(notePair);
    }

    public String toString() {
        String retString = "MarkMod: ";
        Iterator<ArrayList<Note>> itr = model.keySet().iterator();
        while(itr.hasNext()) {
            ArrayList<Note> next = itr.next();
            retString += model.get(next) + " ";
        }
        return retString;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 83 + model.hashCode();
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof MarkovModel))
            return false;
        if(obj.hashCode() == this.hashCode()) // If haseCodes equal, are equal
            return true;
        return false;
    }

    public ArrayList<Note> getRandom(){
		Random rand = new Random();
		Iterator<ArrayList<Note>> itr = model.keySet().iterator();

		int grand = 0;
		ArrayList<Note> next = new ArrayList<Note>();
		while(itr.hasNext()){
			next = itr.next();
			grand += model.get(next).total;
		}
		itr = model.keySet().iterator();
		int choice = rand.nextInt(grand);
		while(itr.hasNext() && choice > 0){
			next = itr.next();
			choice -= model.get(next).total;
		}
		return next;
    }


}