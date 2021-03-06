package codejam.y2008.round_1B.mouse_trap;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import ch.qos.logback.classic.Level;
import codejam.utils.datastructures.FenwickTree;
import codejam.utils.datastructures.SegmentTreeSum;
import codejam.utils.main.InputFilesHandler;
import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class MouseTrap extends InputFilesHandler implements TestCaseHandler<InputData>, TestCaseInputScanner<InputData> {

    
    public MouseTrap()
    {
        super("C", 1,1);
        (( ch.qos.logback.classic.Logger) log).setLevel(Level.INFO);
    }
    
    
   
    @Override
    public InputData readInput(Scanner scanner, int testCase) {
       
        InputData in = new InputData(testCase);
       
        in.K = scanner.nextInt();
        in.n = scanner.nextInt();
        
        in.d = new int[in.n];
        
        for(int i = 0; i < in.n; ++i)
        {
            in.d[i] = scanner.nextInt();
        }
        
        return in;
    }

    public String handleCaseSmall(InputData in) {
       
        int[] deck = new int[in.K];
        
        int currentIndex = 0;
        int currentCount = 1;
        
        for(int currentCard = 1; currentCard <= in.K; ++currentCard)
        {
            while(currentCount < currentCard) {
                currentIndex++;
                currentIndex %= in.K;
                
                if (deck[currentIndex] == 0) {
                    ++currentCount;
                }
            }
            
            Preconditions.checkState(deck[currentIndex] == 0);
            deck[currentIndex] = currentCard;
            
            currentCount = 0;
        }
        
        List<Integer> ans = Lists.newArrayList();
        
        for(Integer ansIdx : in.d) {
            ans.add(deck[ansIdx-1]);
        }
        
        return String.format("Case #%d: %s ", in.testCase, Joiner.on(' ').join(ans));
        
    }
    
        @Test
        public void test() 
        {
            int[] ft = FenwickTree.ft_create(15);
            
            FenwickTree.ft_adjust(ft, 1, 1);
            log.debug("FT {}", ft);
            
            FenwickTree.ft_adjust(ft, 3, 1);
            log.debug("FT {}", ft);
            
            FenwickTree.ft_adjust(ft, 4, 1);
            log.debug("FT {}", ft);
            
            FenwickTree.ft_adjust(ft, 7, 1);
            log.debug("FT {}", ft);
            
            FenwickTree.ft_adjust(ft, 10, 1);
            log.debug("FT {}", ft);
            
            FenwickTree.ft_adjust(ft, 15, 1);
            log.debug("FT {}", ft);
            
            
            
            
            assertEquals(1, FenwickTree.findLowestIndexWithFreq(ft, 1));
            
            assertEquals(3, FenwickTree.findLowestIndexWithFreq(ft, 2));
            
            assertEquals(4, FenwickTree.findLowestIndexWithFreq(ft, 3));
            
            assertEquals(7, FenwickTree.findLowestIndexWithFreq(ft, 4));
            
            assertEquals(10, FenwickTree.findLowestIndexWithFreq(ft, 5));
            
            assertEquals(15, FenwickTree.findLowestIndexWithFreq(ft, 6));
        }
        
    
        public String handleCase(InputData in) {
            
            
            
            int[] deck = new int[in.K];
            
            int currentIndex = 0;
            
            SegmentTreeSum st = new SegmentTreeSum(in.K);
            
            st.update(1, in.K - 1, SegmentTreeSum.SET);
            
            deck[0] = 1;
            
            for(int currentCard = 2; currentCard <= in.K; ++currentCard)
            {
                //Current index is filled
                Preconditions.checkState(deck[currentIndex] != 0);
                
                int cardsLeft = in.K - currentCard + 1;
                
                //Loop as much as possible
                int nCardsToSkip = currentCard ;
                
                nCardsToSkip %= cardsLeft;
                if (nCardsToSkip == 0)
                    nCardsToSkip = cardsLeft;
                          
                //How many cards before / after?  +1 because Fenwick tree is 1 based
                int cardsBeforePos = st.rangeSumQuery(0, currentIndex);
                
                int cardsAfterPos = cardsLeft - cardsBeforePos;
                
                int checkIndex = currentIndex;
                
                
                if (nCardsToSkip <= cardsAfterPos)
                {
                    /**
                     * Next position to fill does not require wrapping around, find freuency
                     * [] [] cp [] [] [] []
                     * 
                     * say I wanted the 3rd position, then I want frencuency 2 + 3
                     */
                    currentIndex = st.findLowestIndexWithSum( 
                            nCardsToSkip + cardsBeforePos);
                    
                    Preconditions.checkState(currentIndex > checkIndex && currentIndex < in.K);
                } else {
                    //Next position is somewhere before currentp position
                    
                    //Skip all cards up to the end
                    nCardsToSkip -= cardsAfterPos;
                    
                    currentIndex = st.findLowestIndexWithSum( 
                            nCardsToSkip ) ;
                    
                    Preconditions.checkState(currentIndex>= 0 && currentIndex < checkIndex);
                }
                
                Preconditions.checkState(deck[currentIndex] == 0);
                deck[currentIndex] = currentCard;
                
                //Remove current card from binary interval tree
                st.update(currentIndex, currentIndex, SegmentTreeSum.ERASE);
                
                
            }
            
            List<Integer> ans = Lists.newArrayList();
            
            for(Integer ansIdx : in.d) {
                ans.add(deck[ansIdx-1]);
            }
            
            return String.format("Case #%d: %s ", in.testCase, Joiner.on(' ').join(ans));
            
        }
            
    
    //Fenwick tree
    public String handleCase2(InputData in) {
       
        
        
        int[] deck = new int[in.K];
        
        int currentIndex = 0;
        
        int[] ft = FenwickTree.ft_create(in.K);
        for(int currentCard = 2; currentCard <= in.K; ++currentCard)
        {
            FenwickTree.ft_adjust(ft, currentCard, 1);
        }
        deck[0] = 1;
        
        for(int currentCard = 2; currentCard <= in.K; ++currentCard)
        {
            //Current index is filled
            Preconditions.checkState(deck[currentIndex] != 0);
            
            int cardsLeft = in.K - currentCard + 1;
            
            //Loop as much as possible
            int nCardsToSkip = currentCard ;
            
            nCardsToSkip %= cardsLeft;
            if (nCardsToSkip == 0)
                nCardsToSkip = cardsLeft;
                      
            //How many cards before / after?  +1 because Fenwick tree is 1 based
            int cardsBeforePos = FenwickTree.ft_rsq(ft, currentIndex+1);
            
            int cardsAfterPos = cardsLeft - cardsBeforePos;
            
            int checkIndex = currentIndex;
            
            
            if (nCardsToSkip <= cardsAfterPos)
            {
                /**
                 * Next position to fill does not require wrapping around, find freuency
                 * [] [] cp [] [] [] []
                 * 
                 * say I wanted the 3rd position, then I want frencuency 2 + 3
                 */
                currentIndex = FenwickTree.findLowestIndexWithFreq(ft, 
                        nCardsToSkip + cardsBeforePos) - 1;
                
                Preconditions.checkState(currentIndex > checkIndex && currentIndex < in.K);
            } else {
                //Next position is somewhere before currentp position
                
                //Skip all cards up to the end
                nCardsToSkip -= cardsAfterPos;
                
                currentIndex = FenwickTree.findLowestIndexWithFreq(ft, 
                        nCardsToSkip ) - 1;
                
                Preconditions.checkState(currentIndex>= 0 && currentIndex < checkIndex);
            }
            
            Preconditions.checkState(deck[currentIndex] == 0);
            deck[currentIndex] = currentCard;
            
            //Remove current card from binary interval tree
            FenwickTree.ft_adjust(ft, currentIndex+1, -1);
            
            
        }
        
        List<Integer> ans = Lists.newArrayList();
        
        for(Integer ansIdx : in.d) {
            ans.add(deck[ansIdx-1]);
        }
        
        return String.format("Case #%d: %s ", in.testCase, Joiner.on(' ').join(ans));
        
    }
        
        
        
}