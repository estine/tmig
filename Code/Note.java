public class Note {

    // Data
    public int note;
    public double length;

    // Constructor
    public Note(int note, double length) {
	this.note = note;
	this.length = length;
    }

    public Note(){
    }
	
	public Note copy(){
		return new Note(note, length);
	}

    // TODO: getters and setters and print method

    public String toString() {
        return "[" + note + ":" + length + "]";
    }

    public int getPitch() {
	return this.note;
    }

    public void setPitch(int note) {
	this.note = note;
    }

    public double getLength() {
	return this.length;
    }

    public void setLength(double length) {
	this.length = length;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 41 + (note * 1000);
        hash = hash * 47 + ((int)length * 100);
        return hash;
    }

    public boolean equals(Object obj) {
	if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Note))
            return false;
        if(obj.hashCode() == this.hashCode()) // If haseCodes equal, are equal
            return true;
        return false;

    }

}