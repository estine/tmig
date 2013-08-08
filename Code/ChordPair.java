
public class ChordPair{

    public int rel;
    public String cType;
    public String nType;
    public int cLength;

    public ChordPair(int rel, String cType, String nType){
	this.rel = rel;
	this.cType = cType;
	this.nType = nType;
    }
    
    public ChordPair(Chord c1, Chord c2){
	this.cLength = c1.getLength();
	int pitch1 = c1.getPitch();
	int pitch2 = c2.getPitch();
	if(Math.abs(pitch2 - pitch1) <= 6){
	    rel = pitch2 - pitch1;
	}
	else if(pitch2 - pitch1 > 0){
	    rel = 12 - (pitch2 - pitch1);
	}
	else{
	    rel = pitch2 - pitch1 + 12;
	}
	cType = c1.getType();
	nType = c2.getType();
    }
    
    public int getRelation() {
	return this.rel;
    }

    public int getLength() {
	return this.cLength;
    }

    public int hashCode() {
	int hash = 1;
	hash = hash * 17 + rel;
	hash = hash * 23 + cType.hashCode();
	hash = hash * 31 + nType.hashCode();
	return hash;
    }

    public boolean equals(Object obj) {
	if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ChordPair))
            return false;
	if(obj.hashCode() == this.hashCode()) // If haseCodes equal, are equal
	    return true;
	return false;

    }

    public String toString() {
	return "" + 0 + ":" + cType + " " + rel + ":" + nType;
    }

		
}
	