// Test class for TMIG

import java.util.*;

public class Test {

    public static void main(String[] args) throws Exception{

	Note c1 = new Note(60, 0.5);
	Note c2 = new Note(60, 0.5);
	
	Note d1 = new Note(62, 0.5);
	Note d2 = new Note(62, 0.5);

	ArrayList<Note> first = new ArrayList<Note>();
	first.add(c1); first.add(d1);

	ArrayList<Note> second = new ArrayList<Note>();
	second.add(c2); second.add(d2);

	System.out.println("Equals? " + first.equals(second));

        HashMap<ArrayList<Note>, Markov> myMap = new HashMap<ArrayList<Note>, Markov>();

	Markov firstM = new Markov(first);

	System.out.println(firstM);

	myMap.put(second, new Markov());

	System.out.println(myMap + ", ContainsKey?: " + myMap.containsKey(second));

	myMap.put(first, firstM);

	System.out.println(myMap);
   }
    
}