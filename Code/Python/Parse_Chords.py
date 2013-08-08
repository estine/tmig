# Program that takes in (by CLI) the name of a 
# chord sequence file and parses it into (Chord, Next_Chord)
# structures helpful for our Markov Model
# Eli Stine

import sys

def main():
    if (len(sys.argv) < 2):
        print "usage: Parse_Chords.py <filename_to_parse>"
        sys.exit(1)
        
    filename = sys.argv[1]
        
    file = open(filename, 'r')
    lines = file.readlines() # Get all lines
        
    parsed_lines = []
    i = 0
    for line in lines:
        templine = line[:-1] # Remove newlines (\n)
        if len(line) > 1 and not line[0] == '#': # Remove blank lines and comments
            parsed_lines.append(templine)
                
    parsed_chords = []

    for line in parsed_lines:
        parsed_chords.append(Chord(line))

    print parsed_chords


# Parsing individual chords
'''myChord = "Ab-Dm[4]"

pitch = myChord.split("-")[0]
print pitch
type = myChord.split("-")[1].split("[")[0]
print type
length = myChord.split("[")[1].split("]")[0]
print length'''

class Chord():
    def __init__(self, string):
        self.pitch = string.split("-")[0]
        self.type = string.split("-")[1].split("[")[0]
        self.len = string.split("[")[1].split("]")[0]

    def __str__(self):
        return "< " + self.pitch + ", " + self.type + ", " + self.len + " >"
    def __repr__(self):
        return self.pitch + " " + self.type + " : " + self.len + "m."

if __name__ == "__main__":
    main()
