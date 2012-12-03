package com.eric.codejam;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.Main.SingleColumnCounts;
import com.google.common.primitives.Ints;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest  {

    final static Logger log = LoggerFactory.getLogger(AppTest.class);

    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public AppTest() {
        super();
    }

    
    @Test
    public void testSumTermCount() {
        Main m = new Main();
        
        //public int[] getSumTermCount(int maxColumn, int nextColumnDigit, int columnDigit, int maxDigit, final int base) {
        
        final int BASE = 10;
        
        int[] sumTermCount = m.getSumTermCount(1,0,1,9,BASE);
        assertEquals(1, sumTermCount[0]); //sum count
        assertEquals(1, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,2,2,BASE);
        assertEquals(1, sumTermCount[0]); //sum count
        assertEquals(1, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,3,9,BASE);
        assertEquals(2, sumTermCount[0]); //sum count
        assertEquals(3, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,4,9,BASE);
        assertEquals(2, sumTermCount[0]); //sum count
        assertEquals(3, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,5,9,BASE);
        assertEquals(3, sumTermCount[0]); //sum count
        assertEquals(5, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,6,9,BASE);
        assertEquals(4, sumTermCount[0]); //sum count
        assertEquals(8, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,7,7,BASE);
        assertEquals(5, sumTermCount[0]); //sum count
        assertEquals(10, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,8,9,BASE);
        assertEquals(6, sumTermCount[0]); //sum count
        assertEquals(13, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,0,9,9,BASE);
        assertEquals(8, sumTermCount[0]); //sum count
        assertEquals(18, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,1,0,9,BASE);
        assertEquals(9, sumTermCount[0]); //sum count
        assertEquals(24, sumTermCount[1]); //term count
        
        
        sumTermCount = m.getSumTermCount(1,4,5,9,BASE);
        assertEquals(1, sumTermCount[0]); //sum count
        assertEquals(9, sumTermCount[1]); //term count
        
        sumTermCount = m.getSumTermCount(1,4,6,9,BASE);
        assertEquals(0, sumTermCount[0]); //sum count
        assertEquals(0, sumTermCount[1]); //term count
    }
    
    @Test
    public void testUberArray() {
        Main m = new Main();
        
        SingleColumnCounts[][] array = m.getSumTermArray();
        
        for(int total = 1; total <= Main.MAX_SINGLE_DIGIT_SUM; ++total) {
            SingleColumnCounts terms = array[total][Main.MAX_DIMENSION-1];
            
            if (terms == null)
                continue;
            
            long count = 0;
            for (Integer termCount : terms.set.elementSet()) {
                count += (long)termCount * terms.set.count(termCount);
            }
            log.debug("Total {} distinct term lens {} total terms {}", total,terms.set.size(), count);
            //for(int termCount : terms) {
               // log.debug("Term count {}",termCount);
            //}
        }
        
        long a = m.count(38,array,10);
        
        log.debug("{}",a);
        
        
        
        final int base = 10;
        
        for(int i = 1; i < 9001; ++i) {
        int c = Ints.checkedCast(m.solve(i,array,base));
        int[] checks = new int[5];
        int check = getCount(i,base,checks);
        assertEquals(check,c);
        
        }
        
        log.debug("{}",a);
    }
    
    public int getCount(int n, int base, int[] checks) {
        Main m = new Main();
        String s = Long.toString(n,base);
    boolean[][] fd = new boolean[s.length()][base];
    
    int[] counts = m.count(n,n, base,fd, new ArrayList<String>(), checks, n);
    
    int termCount = counts[1];
    int sumCount = counts[0];
    
    return sumCount;
    }

}
