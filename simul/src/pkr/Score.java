package pkr;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

public class Score implements Comparable<Score> {
    HandLevel handLevel;
    
    CardRank[] kickers;

    /**
     * @return the handLevel
     */
    public HandLevel getHandLevel() {
        return handLevel;
    }

    /**
     * @param handLevel the handLevel to set
     */
    public void setHandLevel(HandLevel handLevel) {
        this.handLevel = handLevel;
    }

    /**
     * @return the kickers
     */
    public CardRank[] getKickers() {
        return kickers;
    }

    /**
     * @param kickers the kickers to set
     */
    public void setKickers(CardRank[] kickers) {
        this.kickers = kickers;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Score rhs) {
        
        
        ComparisonChain cc = ComparisonChain.start().compare( rhs.getHandLevel().ordinal(), handLevel.ordinal());
        
        if (cc.result() != 0)
            return cc.result();
        
        Preconditions.checkState(this.kickers.length == rhs.kickers.length);
        
        for(int i = 0; i < this.kickers.length; ++i) {
            cc = cc.compare(this.kickers[i].getIndex(), rhs.kickers[i].getIndex());
            
            if (cc.result() != 0)
                return cc.result();
        }
        
        return 0;
    }

    @Override
    public String toString()
    {
        return "Score [handLevel=" + handLevel.name() +
                ", kickers=" + Arrays.toString(kickers) + "]";
    }
    
    
   
    
}
