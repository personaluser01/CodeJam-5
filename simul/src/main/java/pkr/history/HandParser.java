package pkr.history;

import java.io.File;
import java.io.IOException;

public class HandParser
{

    public HandParser() {
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        String brutefileName = "C:\\codejam\\CodeJam\\simul\\hands.txt";
        String fileName =  "C:\\codejam\\CodeJam\\simul\\output\\cleanhands.txt";
        File file = new File(fileName);
        File inputFile = new File(brutefileName);
        
        HandInfoCollector hands = Parser.parseFile(inputFile, file);
        
        //StatsSession stats = computeStats(hands);
    }
}
