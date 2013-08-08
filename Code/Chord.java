public class Chord {

    // Data
    public int pitch;
    public String type;
    public int len;
    
    // Constructor

    // MAJOR TODO:
    // Change pitch to be a midi note (int) - do this in client

    public Chord(int pitch, String type, int len) {
	this.pitch = pitch;
	this.type = type;
	this.len = len;
    }
    
    public Chord(){
    }
    
    // TODO: getters and setters
    public String toString() {
	return "[" + pitch + ":" + type + ":" + len + "]";
    }

    public int getPitch() {
	return this.pitch;
    }

    public int getLength() {
	return this.len;
    }

    public void setPitch(int pitch) {
	this.pitch = pitch;
    }

    public String getType() {
	return this.type;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 59 + this.pitch;
        hash = hash * 67 + this.type.hashCode();
        hash = hash * 73 + len;
        return hash;
    }
    
}