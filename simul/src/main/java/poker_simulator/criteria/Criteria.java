package poker_simulator.criteria;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pkr.CompleteEvaluation;
import pkr.Simulator;
import pkr.possTree.PossibilityNode.TextureCategory;
import poker_simulator.flags.*;

import com.google.common.collect.Lists;

public final class Criteria 
{
    protected static Logger log = LoggerFactory.getLogger(Simulator.class);
    protected static Logger logOutput = LoggerFactory.getLogger("mainOutput");
    
    
    public String desc;
    
    
    private iApplicable isApplicableHandler;
    private iMatcher matcher;


    public iMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(iMatcher matcher) {
		this.matcher = matcher;
	}

	public iApplicable getIsApplicableHandler() {
		return isApplicableHandler;
	}

	public void setIsApplicableHandler(iApplicable isApplicableHandler) {
		this.isApplicableHandler = isApplicableHandler;
	}

	private int applicableCount = 0;
    public int getApplicableCount() {
		return applicableCount;
	}

	public int getMatches() {
		return matches;
	}

	private int matches = 0;
    
    public Criteria(String desc) {
        super();
        
        this.desc = desc;

    }

    public void printMsg()
    {
        if (applicableCount == 0)
        {
            logOutput.debug("{}.  No applicable cases", desc);
            return;
        }
        logOutput.debug("{}.  {} / {} =  %{} ", desc, matches, applicableCount, 100.0 * matches / applicableCount);
    }

   
    private boolean matches(CompleteEvaluation[] evals)
    {
    	if (matcher == null)
    		return false;
    	
        return matcher.matches(evals);
    }

    public void calculate(CompleteEvaluation[] evals)
    {
    	if (isApplicableHandler != null && !isApplicableHandler.isApplicable(evals))
            return;

        ++applicableCount;

        if (matches(evals))
        {
            ++matches;
        }
    }

}
