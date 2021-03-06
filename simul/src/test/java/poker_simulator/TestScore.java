package poker_simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pkr.Card;
import pkr.CardRank;
import pkr.CompleteEvaluation;
import pkr.HoleCards;
import poker_simulator.evaluation.EvalHands;
import poker_simulator.scoring.HandLevel;



@RunWith(JUnit4.class)
public class TestScore {
    
    private static Logger log = LoggerFactory.getLogger(TestScore.class);
    
   // @Test(expected=IllegalStateException.class)
    public void test1() {
        HoleCards h1 = new HoleCards(Card.parseCards("5c 8s"));
        HoleCards h2 = new HoleCards(Card.parseCards("6c 8s"));                
                
        EvalHands.evaluate(false, new HoleCards[] {h1, h2}, Card.parseCards("2h 7h kh"));
    }
    
    @Test
    public void testHighCardVsPair() {
        HoleCards h1 = new HoleCards(Card.parseCards("5c 8s"));
        HoleCards h2 = new HoleCards(Card.parseCards("6c Ks"));                
                                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2}, Card.parseCards("2h 7h kh"));
        
        assertTrue(evals[0].getScore().getHandLevel() == HandLevel.HIGH_CARD);
        assertTrue(evals[0].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[0].getScore().getKickers()[1] == CardRank.EIGHT);
        assertTrue(evals[0].getScore().getKickers()[2] == CardRank.SEVEN);
        assertTrue(evals[0].getScore().getKickers()[3] == CardRank.FIVE);
        assertTrue(evals[0].getScore().getKickers()[4] == CardRank.TWO);
        
        assertTrue(evals[1].getScore().getHandLevel() == HandLevel.PAIR);
        assertTrue(evals[1].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[1].getScore().getKickers()[1] == CardRank.SEVEN);
        assertTrue(evals[1].getScore().getKickers()[2] == CardRank.SIX);
        assertTrue(evals[1].getScore().getKickers()[3] == CardRank.TWO);
        
        assertTrue(evals[1].getScore().compareTo(evals[0].getScore()) > 0);
        
    }
    
    @Test
    public void testHighCardVsHighCardThirdKicker() {
        HoleCards h1 = new HoleCards(Card.parseCards("Kc 8s"));
        HoleCards h2 = new HoleCards(Card.parseCards("6c Ks"));                
        
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2},
                Card.parseCards("2h 7h 4h Qd 5s"));
        
        assertTrue(evals[0].getScore().getHandLevel() == HandLevel.HIGH_CARD);
        assertTrue(evals[0].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[0].getScore().getKickers()[1] == CardRank.QUEEN);
        assertTrue(evals[0].getScore().getKickers()[2] == CardRank.EIGHT);
        assertTrue(evals[0].getScore().getKickers()[3] == CardRank.SEVEN);
        assertTrue(evals[0].getScore().getKickers()[4] == CardRank.FIVE);
        
        assertTrue(evals[1].getScore().getHandLevel() == HandLevel.HIGH_CARD);
        assertTrue(evals[1].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[1].getScore().getKickers()[1] == CardRank.QUEEN);
        assertTrue(evals[1].getScore().getKickers()[2] == CardRank.SEVEN);
        assertTrue(evals[1].getScore().getKickers()[3] == CardRank.SIX);
        assertTrue(evals[1].getScore().getKickers()[4] == CardRank.FIVE);
        
        assertTrue(evals[0].compareTo(evals[1]) > 0);
        
    }
    
    @Test
    public void testStr8VsStr8() {
        HoleCards h1 = new HoleCards(Card.parseCards("Ac 8s"));
        HoleCards h2 = new HoleCards(Card.parseCards("6c 7s"));                
        HoleCards h3 = new HoleCards(Card.parseCards("6s 2s"));
                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2, h3},
                Card.parseCards("2h 8h 4h 3d 5s"));
        
        assertTrue(evals[0].getScore().getHandLevel() == HandLevel.STRAIGHT);
        assertTrue(evals[0].getScore().getKickers()[0] == CardRank.FIVE);
        
        assertTrue(evals[1].getScore().getHandLevel() == HandLevel.STRAIGHT);
        assertTrue(evals[1].getScore().getKickers()[0] == CardRank.EIGHT);
        
        assertTrue(evals[2].getScore().getHandLevel() == HandLevel.STRAIGHT);
        assertTrue(evals[2].getScore().getKickers()[0] == CardRank.SIX);
                
        assertTrue(evals[1].getScore().compareTo(evals[0].getScore()) > 0);
        assertTrue(evals[1].getScore().compareTo(evals[2].getScore()) > 0);
        assertTrue(evals[0].getScore().compareTo(evals[1].getScore()) < 0);
    }
    
    @Test
    public void testFullHouse() {
        HoleCards h1 = new HoleCards(Card.parseCards("2s 3c"));
        HoleCards h2 = new HoleCards(Card.parseCards("4h 2c"));                
        HoleCards h3 = new HoleCards(Card.parseCards("3h 4c"));
                                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2, h3},
                Card.parseCards("3s 2h 3d 4d 4s"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.FULL_HOUSE);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.THREE);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.FOUR);
        assertEquals(0, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.FULL_HOUSE);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.FOUR);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.THREE);
        assertEquals(.5, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.FULL_HOUSE);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.FOUR);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.THREE);
        assertEquals(.5, evals[handNum].getRealEquity(), 0.00001);
        
        Arrays.sort(evals);
        
        assertTrue(evals[2].getScore().compareTo(evals[1].getScore()) == 0);
        //assertTrue(evals[1].getHoleCards().equals(h2));
        assertTrue(evals[0].getHoleCards().equals(h1));
        
    }
    
    @Test
    public void test3ofaKind() {
    	HoleCards h1 = new HoleCards(Card.parseCards("2c 2d"));
        HoleCards h2 = new HoleCards(Card.parseCards("8s 8h"));
                                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2},
                Card.parseCards("2s 5h 3s 8c 4d"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.TRIPS);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.TWO);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.EIGHT);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.FIVE);
        assertTrue(evals[handNum].getScore().getKickers().length == 3);        
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.TRIPS);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.EIGHT);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.FIVE);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.FOUR);
        assertTrue(evals[handNum].getScore().getKickers().length == 3);        
        ++handNum;
        
        
    }
    
    @Test
    public void testFlush() {
        HoleCards h1 = new HoleCards(Card.parseCards("2s 3s"));
        HoleCards h2 = new HoleCards(Card.parseCards("Ks Js"));                
        HoleCards h3 = new HoleCards(Card.parseCards("Ts 4s"));
                                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2, h3},
                Card.parseCards("6s 8s 5s 7s Qs"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.FLUSH);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.QUEEN);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.EIGHT);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.SEVEN);
        assertTrue(evals[handNum].getScore().getKickers()[3] == CardRank.SIX);
        assertTrue(evals[handNum].getScore().getKickers()[4] == CardRank.FIVE);
        assertEquals(0, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.FLUSH);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.QUEEN);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.JACK);
        assertTrue(evals[handNum].getScore().getKickers()[3] == CardRank.EIGHT);
        assertTrue(evals[handNum].getScore().getKickers()[4] == CardRank.SEVEN);
        assertEquals(0, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.STRAIGHT_FLUSH);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.EIGHT);
        assertEquals(1, evals[handNum].getRealEquity(), 0.00001);
        
       
        
    }
    
    @Test
    public void testTwoPairVsPair() {
        HoleCards h1 = new HoleCards(Card.parseCards("Jh Jd"));
        HoleCards h2 = new HoleCards(Card.parseCards("7h 2d"));
                               
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2},
                Card.parseCards("3s Qc 6d Kc Kh"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.TWO_PAIR);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.JACK);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.QUEEN);
        assertEquals(1, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.PAIR);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.KING);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.QUEEN);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.SEVEN);
        assertTrue(evals[handNum].getScore().getKickers()[3] == CardRank.SIX);
        assertEquals(0, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        
    }
    
    @Test
    public void testTwoPair() {
        HoleCards h1 = new HoleCards(Card.parseCards("8c 8h"));
        HoleCards h2 = new HoleCards(Card.parseCards("2h 7h"));                
                                 
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2},
                Card.parseCards("3c Th 3h 6c Td"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.TWO_PAIR);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.TEN);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.EIGHT);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.SIX);
        assertEquals(1, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.TWO_PAIR);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.TEN);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.THREE);
        assertTrue(evals[handNum].getScore().getKickers()[2] == CardRank.SEVEN);
        assertEquals(0, evals[handNum].getRealEquity(), 0.00001);
        ++handNum;
        
       
        
    }
    
    @Test
    public void testThreeWayTie() {
        HoleCards h1 = new HoleCards(Card.parseCards("2s 2c"));
        HoleCards h2 = new HoleCards(Card.parseCards("3h 3c"));                
        HoleCards h3 = new HoleCards(Card.parseCards("4h 4c"));
                                       
        CompleteEvaluation[] evals = EvalHands.evaluate(false, new HoleCards[] {h1, h2, h3},
                Card.parseCards("5s 5h 5d 4d 5c"));
        
        int handNum = 0;
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.QUADS);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.FIVE);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.FOUR);
        assertEquals(1.0/3, evals[handNum].getRealEquity(), 0.00001);
        
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.QUADS);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.FIVE);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.FOUR);
        assertEquals(1.0/3, evals[handNum].getRealEquity(), 0.00001);
        
        ++handNum;
        
        assertTrue(evals[handNum].getScore().getHandLevel() == HandLevel.QUADS);
        assertTrue(evals[handNum].getScore().getKickers()[0] == CardRank.FIVE);
        assertTrue(evals[handNum].getScore().getKickers()[1] == CardRank.FOUR);
        assertEquals(1.0/3, evals[handNum].getRealEquity(), 0.00001);
        
        Arrays.sort(evals);
        
        assertTrue(evals[2].getScore().compareTo(evals[1].getScore()) == 0);
        //assertTrue(evals[1].getHoleCards().equals(h2));
        assertTrue(evals[0].getHoleCards().equals(h1));
    }
    
    @Test
    public void testIndicesHoleCards() 
    {
        /*
         * 0 13 26 39
         * 
         * 
         * 
         * 
         */
        HoleCards h1 = HoleCards.getByIndices(0, 1);
        log.debug("Hole cards {}", h1);
        assertTrue(h1.equals(new HoleCards(Card.parseCards("2h 2c"))));
        
        h1 = HoleCards.getByIndices(2, 3);
        log.debug("Hole cards {}", h1);
        assertTrue(h1.equals(new HoleCards(Card.parseCards("2d 2s"))));
    }
}
