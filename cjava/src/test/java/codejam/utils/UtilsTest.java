package codejam.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.datastructures.graph.GraphAdjList;
import codejam.utils.geometry.Line;
import codejam.utils.geometry.Point;
import codejam.utils.geometry.Polygon;
import codejam.utils.geometry.Rectangle;
import codejam.utils.mod.GCD;
import codejam.utils.utils.ArrayUtils;
import codejam.utils.utils.LargeNumberUtils;

import com.google.common.base.Preconditions;
import com.google.common.math.BigIntegerMath;

/**
 * Unit test for simple App.
 */
public class UtilsTest 
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UtilsTest(  )
    {
        super(  );
    }
    
    int perm(int n, int k, int mod) {
        Preconditions.checkArgument(k <= n);
        
        BigInteger p =  BigIntegerMath.factorial(n).divide(BigIntegerMath.factorial(n-k) );
        
        return p.mod(BigInteger.valueOf(mod)).intValue() ;
    }
    @Test
    public void testPerm() {
        int mod = 1000000007;
        int[][] perms = LargeNumberUtils.generateModedPerum(70,mod);
        
        for(int n = 0; n <= 70; ++n) {
            for(int k = 0; k <= n; ++k) {
                assertEquals(perm(n,k, mod), perms[n][k]);
            }
        }
    }
    
    @Test 
    public void testCombin() {
        int mod = 1000003;
        int max = 450;
        int[] fact = LargeNumberUtils.generateModFactorial(max, mod);
        
        int[][] combin = LargeNumberUtils.generateModedCombin(max, mod);
        
        for(int n = 1; n <= max; ++n) {
            for(int k = 0; k <= n; ++k) {
                int check = combin[n][k];
                assertEquals(check, LargeNumberUtils.choose(n,k,mod,fact));
            }
        }
    }
    
    @Test
    public void testCombinLucasTheorum() 
    {
        int p = 17;
        
        BigInteger pBI = BigInteger.valueOf(p);
        
        int[][] preCal = LargeNumberUtils.generateModedCombin(p-1, p);
        
        for(int n = 1; n <= 200; ++n)
        {
            for(int k = 1; k <= n; ++k)
            {
                BigInteger combin = BigIntegerMath.binomial(n,k);
                BigInteger modded = combin.mod(pBI);
                
                int val = modded.intValue();
        
                
                assertEquals(val, LargeNumberUtils.combin(n,k,preCal, p));
            }
        }
        
    }
    
    final protected static Logger log = LoggerFactory.getLogger("main");
    
    @Test
    public void testMatrix()
    {
        
        
    }
    
    @Test
    public void testGCD() {
        int[] gcdXY = GCD.gcdExtended(120, 23);
        assertEquals(1, gcdXY[0]);
        assertEquals(-9, gcdXY[1]);
        assertEquals(47, gcdXY[2]);
        
        gcdXY = GCD.gcdExtended(100,2);
        assertEquals(2, gcdXY[0]);
    }
    
    @Test
    public void testPolygon() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(3,4));
        points.add(new Point(5,6));
        points.add(new Point(9,5));
        points.add(new Point(12,8));
        points.add(new Point(5,11));
        
        
        double area = Polygon.area(points);
        
        assertEquals(30, area, 0.00001d);
    }
    
    @Test
    public void isBetween() {
        Line l = new Line(2, 1);
        Point a = l.getPointGivenX(3);
        Point b = l.getPointGivenX(-3);
        Point c = l.getPointGivenX(2);
        
        assertTrue(Line.isBetween(b,a,c));
        assertFalse(Line.isBetween(a,c,b));
        assertTrue(Line.isBetween(a,b,c));
        assertFalse(Line.isBetween(c,b,a));
    }

    @Test
    public void testRectTouching()
    {
        Rectangle r1 = new Rectangle(5, 5, 7, 7);
        Rectangle r2 = new Rectangle(3, 3, 9, 9);
        
        assertTrue(r1.touchesHorVer(r2));
        
        r1 = new Rectangle(2, 5, 2, 7);
        r2 = new Rectangle(3, 1, 3, 5);
        
        assertTrue(r1.touchesHorVer(r2));
        
        r1 = new Rectangle(2, 6, 2, 7);
        r2 = new Rectangle(3, 1, 3, 5);
        
        assertFalse(r1.touchesHorVer(r2));
        
        r1 = new Rectangle(2, 6, 2, 7);
        r2 = new Rectangle(2, 1, 2, 5);
        
        assertTrue(r1.touchesHorVer(r2));
        
        r1 = new Rectangle(7, 14, 7, 17);
        r2 = new Rectangle(3, 1, 7, 14);
        
        assertTrue(r1.touchesHorVer(r2));
        
        r1 = new Rectangle(92, 3, 93, 98);
        r2 = new Rectangle(63, 57, 92, 58);
        
        assertTrue(r1.touchesHorVer(r2));
        assertTrue(r2.touchesHorVer(r1));
    }
    
    @Test
    public void testAdjGraph() {
        GraphAdjList graph = new GraphAdjList(9);
        graph.addConnection(1, 8);
        graph.addConnection(8, 6);
        
        graph.addConnection(4, 2);
        graph.addConnection(7, 4);
        graph.addConnection(2, 7);
        
        graph.addConnection(3, 3);
        
        graph.addConnection(5, 5);
        
        List<List<Integer>> cc = graph.getConnectedComponents();
        assertEquals(4, cc.size());
        
        for(int i = 0; i < 4; ++i)
            Collections.sort(cc.get(i));
        
        assertEquals(Arrays.asList(1, 6, 8), cc.get(0));
        assertEquals(Arrays.asList(2, 4, 7), cc.get(1));
        assertEquals(Arrays.asList(3), cc.get(2));
        assertEquals(Arrays.asList(5), cc.get(3));
    }
    
    
    @Test
    public void testBinarySearch() {
        List<Integer> list = Arrays.asList(1, 3, 5, 7 ,9 , 12, 12, 14, 
                15, 19, 21, 30, 31, 31, 31, 40, 40, 41, 42, 50, 51,52,52,54);
        
        int index = ArrayUtils.binarySearch(0, list.size()-1, list, 13);
        assertEquals(12, (int) list.get(index));
        
        index = ArrayUtils.binarySearch(0, 0, list, 13);
        assertEquals(0, index);
        
        index = ArrayUtils.binarySearch(10, list.size()-1, list, 13);
        assertEquals(10, index);
        
        index = ArrayUtils.binarySearch(10, list.size()-1, list, 51);
        assertEquals(51, (int) list.get(index));
        
        index = ArrayUtils.binarySearch(1, 10, list, 3);
        assertEquals(3, (int) list.get(index));
        
        index = ArrayUtils.binarySearch(3, 5, list, 12);
        assertEquals(12, (int) list.get(index));
        
        index = ArrayUtils.binarySearch(3, 6, list, 10);
        assertEquals(9, (int) list.get(index));
    }

}
