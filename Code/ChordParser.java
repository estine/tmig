/* 
   Class that parses a specifically 
   formatted chord list file
 */


import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// Taken from http://www.roseindia.net/java/beginners/java-read-file-line-by-line.shtml

public class ChordParser {

    List<String> sharpnotes = Arrays.asList("C", "Cs", "D", "Ds", "E", "F", "Fs", "G", "Gs", "A", "As", "B");
    List<String> flatnotes = Arrays.asList("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B");

    public static void main(String[] args) {

	ArrayList<Chord> chords = new ChordParser().parseChords("../Giant_Steps_Chords");
	System.out.println(chords);

    }

    public ArrayList<Chord> parseChords(String filename) {

	try{
	    // Open file
	    FileInputStream fstream = new FileInputStream(filename);

	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));

	    ArrayList<Chord> chords = new ArrayList<Chord>();
	    String s;
	    
	    while ((s = br.readLine()) != null)   {
		// Parse the content
		if( s.length() > 1 && s.charAt(0) != '#') { // Remove blank lines and comments

		    String pitch = s.split("-")[0];
		    int parsedPitch = 0;
		    if(sharpnotes.contains(pitch)) {
			parsedPitch = sharpnotes.indexOf(pitch);
		    } else if (flatnotes.contains(pitch)) {
			parsedPitch = flatnotes.indexOf(pitch);
		    }

		    if(parsedPitch > 7) { //Make C centric
			parsedPitch = parsedPitch - 12;
		    }

		    // Create a new chord
		    chords.add( new Chord( parsedPitch, s.split("-")[1].split("\\[")[0], Integer.parseInt(s.split("\\[")[1].split("]")[0]) ) );
		}
	    }

	    //Close the input stream
	    in.close();

	    return chords;

	}catch (Exception e){ //Catch exceptions
	    System.err.println("Error: " + e.getMessage());
	}

	// Return empty if error
	return new ArrayList<Chord>();

    }
}