package codejam.y2009.round_1C.bribe_prisoner;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface PrisonSelectionAlgorithm {

	final static Logger log = LoggerFactory.getLogger(Main.class);
	
	public int findMinCost(final int segStart, final int segEnd, List<Integer> toBeFreed); 
		
}