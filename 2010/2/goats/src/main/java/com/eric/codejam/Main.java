package com.eric.codejam;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.geometry.Circle;
import com.eric.codejam.geometry.Point;
import com.eric.codejam.geometry.PointInt;
import com.eric.codejam.geometry.Polygon;
import com.eric.codejam.main.Runner;
import com.eric.codejam.multithread.Consumer.TestCaseHandler;
import com.eric.codejam.multithread.Producer.TestCaseInputReader;
import com.eric.codejam.utils.ArrayUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

public class Main implements TestCaseHandler<InputData>, TestCaseInputReader<InputData> {

    final static Logger log = LoggerFactory.getLogger(Main.class);
    
    private static class CircleWithAngle {
        Circle c;
        double polarAngle;
        public CircleWithAngle(Circle c, double polarAngle) {
            super();
            this.c = c;
            this.polarAngle = polarAngle;
        }
        
        
    }
    
    private static class Arc {
        double fromAngle;
        double toAngle;
        
        Point intPointTo;
        int closestCircle;
        public Arc(double fromAngle, double toAngle, int closestCircle, Point intPointTo) {
            super();
            this.fromAngle = fromAngle;
            this.toAngle = toAngle;
            this.closestCircle = closestCircle;
            this.intPointTo = intPointTo;
        }
        @Override
        public String toString() {
            return "Arc [fromAngle=" + fromAngle + ", toAngle=" + toAngle
                    + ", intPointTo=" + intPointTo + ", closestCircle="
                    + closestCircle + "]";
        }
       
        
    }
    @Override
    public String handleCase(int caseNumber, InputData input) {

        log.info("Starting calculating case {}", caseNumber);
        
        StringBuffer sb = new StringBuffer();
        
        DecimalFormat decim = new DecimalFormat("0.00000000");
        decim.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        
        DecimalFormat shortDecim = new DecimalFormat("0.00");
        shortDecim.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

        if (caseNumber > 3) {
            return "bee";
        }
        
        int bucketPosIdx = -1;
        //double ans = DivideConq.findMinPerimTriangle(input.points);
        for(PointInt bucketPos : input.bucketPositions) {
            bucketPosIdx++;
            Circle[] circles = new Circle[input.goatPolePositions.length];
            for(int gp = 0; gp < input.N; ++gp) {
                PointInt goatPos = input.goatPolePositions[gp];
                circles[gp] = new Circle(goatPos.getX(), goatPos.getY(), goatPos.distance(bucketPos));
            }
            
          
            
           
            
            CircleWithAngle[] circlesWithpolarAngles = new CircleWithAngle[input.N];
            
            
            for(int c = 0; c < circles.length; ++c) {
                double x = circles[c].getX() - bucketPos.getX();
                double y = circles[c].getY() - bucketPos.getY();
                
                //first quadrant
                double a1 = Math.atan2(0,5);
                double a2 = Math.atan2(5,5);
                
                //second
                double a3 = Math.atan2(5,0);
                double a4 = Math.atan2(5,-5);
                
                //third
                double a5 = Math.atan2(0,-5);
                double a6 = Math.atan2(-5,-5);
                
                //fourth
                double a7 = Math.atan2(-5,0);
                double a8 = Math.atan2(-5,5);
                
                double a9 = Math.atan2(-0.00001d,-5);
                
                double ang =  Math.atan2(y,x) ;
                circlesWithpolarAngles[c] = new CircleWithAngle( circles[c],  ang );
                
              
                //     2 * Math.PI  >= circlesWithpolarAngles[c].polarAngle);
                
                if (circlesWithpolarAngles[c].polarAngle < 0) {
                    //circlesWithpolarAngles[c].polarAngle += 2d * Math.PI;
                }
                //move angle between -PI / 2 < angle <= 3PI / 2
                
                // min range is  -PI < min <= PI
                // max range is  0 < max <= 2PI
                
                //Preconditions.checkState(0 < circlesWithpolarAngles[c].polarAngle + 1e-9 &&
                  //     2 * Math.PI  >= circlesWithpolarAngles[c].polarAngle);
                
                /*
                log.debug("Angle for circle {} : {} \n is rad {} deg {}." +
                		"\n  Range for cirl {}/{} to {}/{}. " +
                		"Range angles {}/{} - {}/{}", c, circles[c], 
                		shortDecim.format(circlesWithpolarAngles[c].polarAngle), 
                		shortDecim.format(circlesWithpolarAngles[c].polarAngle * 180d / Math.PI), 
                		shortDecim.format(min),
                		shortDecim.format(min * 180d / Math.PI),
                		shortDecim.format(max),
                		shortDecim.format(max* 180d / Math.PI),
                		shortDecim.format(minPolAngle),
                		shortDecim.format(minPolAngle* 180d / Math.PI),
                		shortDecim.format(maxPolAngle),
                		shortDecim.format(maxPolAngle* 180d / Math.PI)
                		);
                */
               // Preconditions.checkState(min >= 0 && max <= 2 * Math.PI);
            }
            
            Arrays.sort(circlesWithpolarAngles, new Comparator<CircleWithAngle>() {

                @Override
                public int compare(CircleWithAngle o1, CircleWithAngle o2) {
                    return ComparisonChain.start().compare(o1.polarAngle, o2.polarAngle).result();
                }
                
            });
           
            
            double minPolAngle = -8*Math.PI;
            double maxPolAngle = 8d * Math.PI;
            
            minPolAngle = circlesWithpolarAngles[0].polarAngle;
            maxPolAngle  = circlesWithpolarAngles[circlesWithpolarAngles.length-1].polarAngle;
            boolean adjNegatives = false;
            
            boolean valid = false;
            if (maxPolAngle - minPolAngle <= Math.PI) {
                //All polar angles of circles intersections between minPolAngle and maxPolAngle.  This
                //covers the case where min is in 1st quadrant / max 2nd
                //min 3rd max 4th and min 4th max 1st
                
                //In the first the angle goes 0 to PI / 2
                //Second PI / 2 to PI
                //Third -PI to -PI / 2
                //Fourth -PI / 2 to 0
                
                minPolAngle  = circlesWithpolarAngles[circlesWithpolarAngles.length-1].polarAngle - Math.PI / 2;
                maxPolAngle = circlesWithpolarAngles[0].polarAngle + Math.PI / 2;
                
                valid = true;
            } else {
                //We need to check one more case.  If the min is in the 2nd quadrant
                //and max is in the 3rd.  Our ordering will be off.  We look for a
                //jump of pi from the minimums.  The jump goes past the 4th and 1st quadrants.
                for (int i = 0; i < circlesWithpolarAngles.length - 1; i++)
                {
                    if (circlesWithpolarAngles[i + 1].polarAngle - circlesWithpolarAngles[i].polarAngle > Math.PI + 1e-9)
                    {
                        CircleWithAngle[] newArray = new CircleWithAngle[circlesWithpolarAngles.length];
                        
                        //System.arraycopy(circlesWithpolarAngles, 0, sortTmp2, 0, i + 1);
                        //i + 1 to len goes to front of array
                        System.arraycopy(circlesWithpolarAngles, i + 1, newArray, 0, circlesWithpolarAngles.length - (i + 1));
                        
                        //Then follows 0 to i to back of array
                        System.arraycopy(circlesWithpolarAngles, 0, newArray, circlesWithpolarAngles.length - i - 1, i + 1);
                        
                        minPolAngle = circlesWithpolarAngles[i + 1].polarAngle;
                        maxPolAngle = circlesWithpolarAngles[i].polarAngle + 2 * Math.PI;
                        
                        circlesWithpolarAngles = newArray;
                        valid = true;
                        adjNegatives = true;
                                                
                        for(int check = 0; check < circlesWithpolarAngles.length; ++check) {
                            if (circlesWithpolarAngles[check].polarAngle < 0) {
                                circlesWithpolarAngles[check].polarAngle += 2 * Math.PI;
                            }
                            if (check > 0) {
                                Preconditions.checkState(circlesWithpolarAngles[check].polarAngle > circlesWithpolarAngles[check-1].polarAngle);
                                
                            }
                            Preconditions.checkState(circlesWithpolarAngles[check].polarAngle >= minPolAngle);
                            Preconditions.checkState(circlesWithpolarAngles[check].polarAngle <= maxPolAngle);
                        }
                        
                        //Relax mins and maxes by PI / 2
                        minPolAngle  = circlesWithpolarAngles[circlesWithpolarAngles.length-1].polarAngle - Math.PI / 2;
                        maxPolAngle = circlesWithpolarAngles[0].polarAngle + Math.PI / 2;
                        
                        break;
                    }
                }
            }
            
            
            
            Stack<Arc> arcs = new Stack<>();

            if (valid) {
            for (int circNum = 0; circNum < circlesWithpolarAngles.length; ++circNum) {

                if (arcs.isEmpty()) {
                    arcs.add(new Arc(minPolAngle, maxPolAngle, circNum, new Point(bucketPos)));
                    continue;
                }
                double startingAngle = arcs.peek().fromAngle;

                while (true) {
                    
                    
                    Circle circ = circlesWithpolarAngles[circNum].c;
                    // get intersection of circNum and circle on stack
                    int circStack = arcs.peek().closestCircle;
                    Circle stackCircl = circlesWithpolarAngles[circStack].c;

                    Point[] intersection = stackCircl.getIntersection(circ);
                    
                    Point intPoint = stackCircl.getOtherIntersection(circ, new Point( bucketPos) );
                    
                    // Now get polar angle of intersection
                    double x = intPoint.getX() - bucketPos.getX();
                    double y = intPoint.getY() - bucketPos.getY();

                    double angle = Math.atan2(y, x);
                    if (angle < 0 && adjNegatives) {
                        angle += 2d * Math.PI;
                        // Preconditions.checkState(angle <= maxPolAngle &&
                        // angle >= minPolAngle);
                    }

                    double stackAngleMin = arcs.peek().fromAngle;
                    double stackAngleMax = arcs.peek().toAngle;

                    if (angle < stackAngleMin) {
                        // The intersection comes before the range, meaning as
                        // we increase
                        // the polar angle, the distance to the added circle
                        // will increase.
                        // the circle on stack is closest on the range
                        break;
                    } else if (angle >= stackAngleMin && angle <= stackAngleMax) {
                        // anything after angle, the added circle will be
                        // closest from stackAngleMin to angle
                        // the circle on stack closest from angle to
                        // stackAngleMax
                        Arc arcToDelete = arcs.pop();
                        Arc arc1 = new Arc(angle, stackAngleMax,
                                arcToDelete.closestCircle, arcToDelete.intPointTo);
                        Arc arc2 = new Arc(startingAngle, angle, circNum, intPoint);
                        arcs.push(arc1);
                        arcs.push(arc2);
                        break;
                    } else {
                        arcs.pop();
                        if (arcs.isEmpty()) {
                           // log.error("Stack empty? ");
                            arcs.add(new Arc(minPolAngle, maxPolAngle, circNum, intPoint));
                            break;
                        }
                    }

                }
            }
            }
            
            //Loop through arcs, skipping first one and last one
            Arc[] arcsArray = new Arc[arcs.size()];
            arcs.toArray(arcsArray);
            org.apache.commons.lang3.ArrayUtils.reverse(arcsArray);
            
            double area = 0;
            
            if (maxPolAngle < minPolAngle) {
                
            }
            
            else if (arcsArray.length == 3) {
            Circle c1 = circlesWithpolarAngles[arcsArray[0].closestCircle].c;
            Circle c2 = circlesWithpolarAngles[arcsArray[1].closestCircle].c;
            Circle c3 = circlesWithpolarAngles[arcsArray[2].closestCircle].c;
            double aaa = c2.getArea() - c1.findAreaIntersection(c2);
            double bbb = c2.getArea() - c2.findAreaIntersection(c3);
            area = c2.getArea() - aaa - bbb;
            } else if (arcsArray.length == 2){
                Circle c1 = circlesWithpolarAngles[arcsArray[0].closestCircle].c;
                Circle c2 = circlesWithpolarAngles[arcsArray[1].closestCircle].c;
                area = c1.findAreaIntersection(c2);
            } else if (arcsArray.length == 0) {
                
            } else {
                log.info("Bucket pos {}", bucketPos);
                for(Arc arc : arcsArray) {
                    log.info("Circle {} in arc {}",arc.closestCircle, circlesWithpolarAngles[arc.closestCircle].c);
                }
                log.error("Interesting.  case num {}  bucket pos {}", caseNumber, bucketPos);
                area = 69.0;
                
                List<Point> polygonPoints = new ArrayList<Point>();
                //array is in counter clockwise, same order as polar angles
                for(Arc arc : arcsArray) {
                    polygonPoints.add(arc.intPointTo);
                }
                
                area = Polygon.area(polygonPoints);
                
                Point fromPoint = new Point(bucketPos);
                
                for(Arc arc : arcsArray) {
                    double segLength = fromPoint.distance(arc.intPointTo);
                    area += circlesWithpolarAngles[arc.closestCircle].c.findSegmentArea(segLength);
                    
                    fromPoint = arc.intPointTo;
                }
                
            }

            sb.append(decim.format(area));
            sb.append(' ');
        }
        
        
        

        log.info("Done calculating answer case {}", caseNumber);
        
        
        return ("Case #" + caseNumber + ": " + sb.toString().trim());
    }
    
    
   
    
    @Override
    public InputData readInput(BufferedReader br, int testCase) throws IOException {
        
    
        String[] line = br.readLine().split(" ");
        
        InputData  input = new InputData(testCase);
        
        input.N = Integer.parseInt(line[0]);
        input.M = Integer.parseInt(line[1]);
        
        input.goatPolePositions = new PointInt[input.N];
        input.bucketPositions = new PointInt[input.M];
        
        for(int n = 0; n < input.N; ++n) {
            line = br.readLine().split(" ");
            input.goatPolePositions[n] = new PointInt(Integer.parseInt(line[0]),Integer.parseInt(line[1]));
        }
        
        for(int m = 0; m < input.M; ++m) {
            line = br.readLine().split(" ");
            input.bucketPositions[m] = new PointInt(Integer.parseInt(line[0]),Integer.parseInt(line[1]));
        }
        
        return input;
        
    }

    


    public Main() {
        super();
    }
    
    
    public static void main(String args[]) throws Exception {

        if (args.length < 1) {
         //  args = new String[] { "sample.txt" };
           // args = new String[] { "D-small-practice.in" };
            args = new String[] { "D-large-practice.in" };
         }
         log.info("Input file {}", args[0]);

         Main m = new Main();
         Runner.goSingleThread(args[0], m, m);
         //Runner.go(args[0], m, m, new InputData(-1));

        
       
    }

    
}