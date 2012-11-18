package com.eric.codejam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.utils.Rational;
import com.google.common.base.Preconditions;

public class Node {
    final static Logger log = LoggerFactory.getLogger(Node.class);
    
   //aka Base, spoke, diagonal node
    boolean isBubbleNode;
    
    int nodeNumber;
    int[][] bottomWeights;
    int[][] rightWeights;
    
    int[][] topWeights;
    int[][] leftWeights;
    
    //int[][] oldNextNodeWeights;
    int[][] prevNodeWeights;
    int[][] nextNodeWeights;
    
    //int[][][] prevNodeWeightsComplete;
    //int[][][] nextNodeWeightsComplete;
    
    int[][][] prevDoubleNodeWeights;
    int[][][] nextDoubleNodeWeights;
    
    Node prevDoubleNode;
    Node nextDoubleNode;
    
    int[] count;
    
    Node rightConnectedNode;
    Node bottomConnectedNode;
    
    Node prevConnectedNode;
    Node nextConnectedNode;
    
    Node leftConnectedNode;
    Node topConnectedNode;
    
    public static int LETTER_MAX = DoubleRowSolver.LETTER_MAX;

    public Node(int nodeNumber) {
        super();
        this.nodeNumber = nodeNumber;
        
        
        bottomWeights = new int[LETTER_MAX][LETTER_MAX];
        rightWeights = new int[LETTER_MAX][LETTER_MAX];
        leftWeights = new int[LETTER_MAX][LETTER_MAX];
        topWeights = new int[LETTER_MAX][LETTER_MAX];
        
        count = new int[LETTER_MAX];
        
        isBubbleNode = false;
    }
    
    public int getCount() {
        int sum = 0;
        for(int letter = 0; letter < LETTER_MAX; ++letter) {
            sum += count[letter];
        }
        return sum;
    }
    
   
    
    
    public static int BOTTOM = 1;
    public static int RIGHT = 2;
    public static int TOP = 4;
    public static int LEFT = 8;
    
    /**
     * 
     * @param prevNode upper / right
     * @param nextNode lower / left
     * @param increases [top][left]
     * @param origin
     */
    /*
    public static void propogateIncrease(Node prevNode, Node nextNode, int[][] increases) {
        
        
        int[] oldTopRightCount = new int[LETTER_MAX];
        int[] oldBottomLeftCount = new int[LETTER_MAX];
        for (int topRightLetter = 0; topRightLetter < LETTER_MAX; ++topRightLetter) {
            
            
            for (int bottomLeftLetter = 0; bottomLeftLetter < LETTER_MAX; ++bottomLeftLetter) {
                oldTopRightCount[topRightLetter] += prevNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
                
                oldBottomLeftCount[bottomLeftLetter]  += prevNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
            }
        }

        for (int letterFirst = 0; letterFirst < LETTER_MAX; ++letterFirst) {

            for (int letter = 0; letter < LETTER_MAX; ++letter) {

                int factor = increases[letterFirst][letter];

                prevNode.nextNodeWeights[letterFirst][letter] *= factor;
                // nextNode.prevNodeWeights[letterFirst][letter] *= factor;

            }

        }
        
        int[] newTopRightCount = new int[LETTER_MAX];
        int[] newBottomLeftCount = new int[LETTER_MAX];
        for (int topRightLetter = 0; topRightLetter < LETTER_MAX; ++topRightLetter) {
            
            
            for (int bottomLeftLetter = 0; bottomLeftLetter < LETTER_MAX; ++bottomLeftLetter) {
                newTopRightCount[topRightLetter] += prevNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
                
                newBottomLeftCount[bottomLeftLetter]  += prevNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
            }
        }

        
        Rational[] incRatTopRight = new Rational[LETTER_MAX];
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            incRatTopRight[letter] = new Rational(newTopRightCount[letter], oldTopRightCount[letter]);
        }
        
        prevNode.propogateIncrease(incRatTopRight, BOTTOM | NEXT);
        
        Rational[] incRatBottomLeft = new Rational[LETTER_MAX];
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            incRatBottomLeft[letter] = new Rational(newBottomLeftCount[letter], oldBottomLeftCount[letter]);
        }
        
        nextNode.propogateIncrease(incRatBottomLeft, RIGHT | PREV);
    }
*/
    
    public void propogateIncrease(Rational[] increases, int origin) {
        
        int[] oldTopCount = new int[LETTER_MAX];
        int[] oldBottomCount = new int[LETTER_MAX];
        int[] oldLeftCount = new int[LETTER_MAX];
        int[] oldRightCount = new int[LETTER_MAX];
        
                        
        for (int thisLetter = 0; thisLetter < LETTER_MAX; ++thisLetter) {

            for (int nextLetter = 0; nextLetter < LETTER_MAX; ++nextLetter) {
                oldTopCount[thisLetter] += topWeights[nextLetter][thisLetter];
                
                oldLeftCount[thisLetter] += leftWeights[nextLetter][thisLetter];

                oldBottomCount[thisLetter] += bottomWeights[thisLetter][nextLetter];
                
                oldRightCount[thisLetter] += rightWeights[thisLetter][nextLetter];
            }
        }
    


        for (int increasedLetter = 0; increasedLetter < LETTER_MAX; ++increasedLetter) {
            Rational factor = increases[increasedLetter];
            for (int secondLetter = 0; secondLetter < LETTER_MAX; ++secondLetter) {

                
                
                if ((origin & BOTTOM) == 0 && nextDoubleNode == null) {
                    bottomWeights[increasedLetter][secondLetter] = factor
                            .multiplyToInt(bottomWeights[increasedLetter][secondLetter]);
                    // bottomConnectedNode.propogateIncrease(pivotLetter,
                    // factor, From.BOTTOM);
                }

                if ((origin & RIGHT) == 0 && prevDoubleNode == null) {
                    rightWeights[increasedLetter][secondLetter] = factor
                            .multiplyToInt(rightWeights[increasedLetter][secondLetter]);
                    // rightConnectedNode.propogateIncrease(pivotLetter, factor,
                    // origin)
                }
                if (topConnectedNode != null && (origin & TOP) == 0 ) {
                    // increasedLetter is the 2nd dimension
                    topWeights[secondLetter][increasedLetter] = factor
                            .multiplyToInt(topWeights[secondLetter][increasedLetter]);
                }
                if (leftConnectedNode != null && (origin & LEFT) == 0) {
                    leftWeights[secondLetter][increasedLetter] = factor
                            .multiplyToInt(leftWeights[secondLetter][increasedLetter]);
                }
            }

        }
        
     
        //int[] newTopRightCount = new int[LETTER_MAX];
        //int[] newBottomLeftCount = new int[LETTER_MAX];
       // if (prevConnectedNode != null) {

            /*
            if (prevConnectedNode.nextDoubleNode != null) {
            for (int increasedLetter = 0; increasedLetter < LETTER_MAX; ++increasedLetter) {
                Rational factor = increases[increasedLetter];
                for (int secondLetter = 0; secondLetter < LETTER_MAX; ++secondLetter) {

                    if ((origin & PREV) == 0 && prevDoubleNode != null) {
                        for (int thirdLetter = 0; thirdLetter < LETTER_MAX; ++thirdLetter) {
                            Rational testR = new Rational(
                                    prevConnectedNode.nextNodeWeights[increasedLetter][secondLetter],
                                    prevConnectedNode.oldNextNodeWeights[increasedLetter][secondLetter]);

                            prevConnectedNode.nextDoubleNodeWeights[increasedLetter][secondLetter][thirdLetter] = testR
                                    .multiplyToInt(prevConnectedNode.nextDoubleNodeWeights[increasedLetter][secondLetter][thirdLetter]);
                        }
                    }
                }
            }
            
            updateVerticalHorizontalWeightsFromDoubleNodes(prevConnectedNode, this);
            
            }
*/
  /*      for (int topRightLetter = 0; topRightLetter < LETTER_MAX; ++topRightLetter) {
            
            
            for (int bottomLeftLetter = 0; bottomLeftLetter < LETTER_MAX; ++bottomLeftLetter) {
                newTopRightCount[topRightLetter] += prevConnectedNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
                
                newBottomLeftCount[bottomLeftLetter]  += prevConnectedNode.nextNodeWeights[topRightLetter][bottomLeftLetter];
            }
        }
        }*/

            int[] newTopCount = new int[LETTER_MAX];
            int[] newBottomCount = new int[LETTER_MAX];
            int[] newLeftCount = new int[LETTER_MAX];
            int[] newRightCount = new int[LETTER_MAX];
            
                            
            for (int thisLetter = 0; thisLetter < LETTER_MAX; ++thisLetter) {

                for (int nextLetter = 0; nextLetter < LETTER_MAX; ++nextLetter) {
                    newTopCount[thisLetter] += topWeights[nextLetter][thisLetter];
                    
                    newLeftCount[thisLetter] += leftWeights[nextLetter][thisLetter];

                    newBottomCount[thisLetter] += bottomWeights[thisLetter][nextLetter];
                    
                    newRightCount[thisLetter] += rightWeights[thisLetter][nextLetter];
                }
            }

        
            Rational[] incRatTop = new Rational[LETTER_MAX];
            Rational[] incRatBottom = new Rational[LETTER_MAX];
            Rational[] incRatLeft = new Rational[LETTER_MAX];
            Rational[] incRatRight = new Rational[LETTER_MAX];
            
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            incRatTop[letter] = new Rational(newTopCount[letter], oldTopCount[letter]);
            incRatLeft[letter] = new Rational(newLeftCount[letter], oldLeftCount[letter]);
            incRatRight[letter] = new Rational(newRightCount[letter], oldRightCount[letter]);
            incRatBottom[letter] = new Rational(newBottomCount[letter], oldBottomCount[letter]);
        }
        
        
        
        if ( (origin & TOP) == 0 && topConnectedNode != null) {
            topConnectedNode.propogateIncrease(incRatTop, BOTTOM);
            
        }
        if ( (origin & BOTTOM) == 0 && nextConnectedNode != null) {
            
            bottomConnectedNode.propogateIncrease(incRatBottom, TOP);
        }
        if ( (origin & LEFT) == 0 && topConnectedNode != null) {
            topConnectedNode.propogateIncrease(incRatTop, RIGHT);
            
        }
        if ( (origin & RIGHT) == 0 && nextConnectedNode != null) {
            
            bottomConnectedNode.propogateIncrease(incRatBottom, LEFT);
        }
        
        updateCounts();
    }
    
    public Node connectSingleNode(int nodeNum, boolean isRight) {
        Node singleNode = Node.createEmptyNode(nodeNum);
        
        if (isRight) {
            this.rightConnectedNode = singleNode;
            singleNode.rightConnectedNode = this;
            singleNode.rightWeights = rightWeights;
        } else {
            this.bottomConnectedNode = singleNode;
            singleNode.bottomConnectedNode = this;
            singleNode.bottomWeights = bottomWeights;
        }
        
        int[] increases = new int[LETTER_MAX];
        for (int letter = 0; letter < LETTER_MAX; ++letter) {

            for (int singleNodeLetter = 0; singleNodeLetter < LETTER_MAX; ++singleNodeLetter) {
                if (singleNodeLetter >= letter) {
                    if (isRight) {
                        rightWeights[letter][singleNodeLetter] = count[letter];
                        
                        
                        //topBottomWeights[letter][singleNodeLetter] *= count[letter];
                    } else {
                        bottomWeights[letter][singleNodeLetter] = count[letter];
                    }
                    
                  //Propogate increase
                    increases[letter] += 1;
                }
            }
        }

        Rational[] incRat = new Rational[LETTER_MAX];
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            incRat[letter] = Rational.fromInt(increases[letter]);
        }
        propogateIncrease(incRat, isRight ? RIGHT : BOTTOM);
        
        
        singleNode.updateCounts();
        //updateCounts();
        
        return singleNode;

    }
    
    public static int[][] copyArray(int[][] matrix) {
        int [][] myInt = new int[matrix.length][matrix.length];
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j  = 0; j < matrix.length; ++j) {
                myInt[i][j] = matrix[i][j];
            }
        }
        
        
        return myInt;
    }
    
    public static void updateVerticalHorizontalWeightsFromDoubleNodes(Node topNode, Node leftNode) {
        topNode.bottomWeights = new int[LETTER_MAX][LETTER_MAX];
        leftNode.rightWeights = new int[LETTER_MAX][LETTER_MAX];
        
        topNode.nextDoubleNode.bottomWeights = topNode.bottomWeights;
        topNode.nextDoubleNode.rightWeights = leftNode.rightWeights;
        
        for (int leftLetter = 0; leftLetter < LETTER_MAX; ++leftLetter) {
            for (int topLetter = 0; topLetter < LETTER_MAX; ++topLetter) {

                for (int singleNodeLetter = 0; singleNodeLetter < LETTER_MAX; ++singleNodeLetter) {
                    topNode.bottomWeights[topLetter][singleNodeLetter] += topNode.nextDoubleNodeWeights[topLetter][leftLetter][singleNodeLetter];
                    leftNode.rightWeights[leftLetter][singleNodeLetter] += topNode.nextDoubleNodeWeights[topLetter][leftLetter][singleNodeLetter];
                }
            }
        }
    }
    
    public static Node connectSingleNode(Node topNode, Node leftNode, int nodeNum) {
        Node singleNode = Node.createEmptyNode(nodeNum);
        
        topNode.bottomConnectedNode = singleNode;
        leftNode.rightConnectedNode = singleNode;
        
        topNode.nextDoubleNode = singleNode;
        leftNode.prevDoubleNode = singleNode;
        
        int[][][] weights = new int[LETTER_MAX][LETTER_MAX][LETTER_MAX];
        topNode.nextDoubleNodeWeights = weights;
        leftNode.prevDoubleNodeWeights = weights;
        
        singleNode.rightWeights = leftNode.rightWeights;
        singleNode.bottomWeights = topNode.bottomWeights;
        
        singleNode.bottomConnectedNode = topNode;
        singleNode.rightConnectedNode = leftNode;
        
        int[][] increases = new int[LETTER_MAX][LETTER_MAX];
        
        int[] topIncreases = new int[LETTER_MAX];
        int[] leftIncreases = new int[LETTER_MAX];
        
        for (int leftLetter = 0; leftLetter < LETTER_MAX; ++leftLetter) {
            for (int topLetter = 0; topLetter < LETTER_MAX; ++topLetter) {

                for (int singleNodeLetter = 0; singleNodeLetter < LETTER_MAX; ++singleNodeLetter) {
                    if (singleNodeLetter >= topLetter
                            && singleNodeLetter >= leftLetter) {

                        weights[topLetter][leftLetter][singleNodeLetter] += leftNode.prevNodeWeights[topLetter][leftLetter];
                        
                        //Preconditions.checkState(leftNode.prevNodeWeights[topLetter][leftLetter] == topNode.nextNodeWeights[topLetter][leftLetter]);
                                                // Propogate increase
                        // topBottomWeights[topLetter][singleNodeLetter] *=
                        // count[topLetter];
                        
                        increases[topLetter][leftLetter] ++;
                        topIncreases[topLetter]++;
                        leftIncreases[leftLetter]++;
                    } 
                    
                    
                }
            }
        }
    
        updateVerticalHorizontalWeightsFromDoubleNodes(topNode, leftNode);
        
        for (int leftLetter = 0; leftLetter < LETTER_MAX; ++leftLetter) {
            for (int topLetter = 0; topLetter < LETTER_MAX; ++topLetter) {
                for(int i=0; i < LETTER_MAX; ++i) {
                topNode.leftWeights[i][topLetter] *= increases[topLetter][leftLetter];
                }
                for(int i=0; i < LETTER_MAX; ++i) {
                    leftNode.topWeights[i][leftLetter] *= increases[topLetter][leftLetter];
                    }
            }
        }
        
        Rational[] rat = new Rational[LETTER_MAX];
        Rational[] ratLeft = new Rational[LETTER_MAX];
        for(int i=0; i < LETTER_MAX; ++i) {
            rat[i] = Rational.fromInt(topIncreases[i]);
            ratLeft[i] = Rational.fromInt(leftIncreases[i]);
        }
        topNode.propogateIncrease(rat, BOTTOM | LEFT);
        leftNode.propogateIncrease(ratLeft, RIGHT | TOP);
        //Node.propogateIncrease(topNode, leftNode, topIncreases);
                    

        
        // Update node values
        singleNode.updateCounts();
        topNode.updateCounts();
        leftNode.updateCounts();
        
        return singleNode;

    }
    
    
    private static int[] getCounts(int[][] weights) {
        int sum = 0;
        int[] counts = new int[LETTER_MAX];
    
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            int total1 = 0;
            for (int singleLetter = 0; singleLetter < LETTER_MAX; ++singleLetter) {
                
                    total1 += weights[letter][singleLetter];
                
                
            }

            counts[letter] = total1;
        }
        return counts;
    }
    private void updateCounts() {
        
        if (nextConnectedNode != null) {
            Preconditions.checkArgument(leftConnectedNode != null);
            
            nextNodeWeights = getDiagonalWeights(leftConnectedNode.rightWeights, leftConnectedNode.bottomWeights);
         
        }

        if (rightConnectedNode != null) {
            count = getCounts(rightWeights);
        } else if (bottomConnectedNode != null) {
            count = getCounts(bottomWeights);
        } else if (prevConnectedNode != null) {
            count = getCounts(prevNodeWeights);
        }else if (nextConnectedNode != null) {
         
            count = getCounts(nextNodeWeights);
        }
        
        
    }
    
    
    public static int[][] getDiagonalWeights(int[][] horizontal, int[][] vertical) {
        
        int[][] diag = new int[LETTER_MAX][LETTER_MAX];
        

        for (int letterToMerge = 0; letterToMerge < LETTER_MAX; ++letterToMerge) {
            
            int totalRightWeight = 0;
            for (int rightLetter = 0; rightLetter < LETTER_MAX; ++rightLetter) {
                totalRightWeight += horizontal[letterToMerge][rightLetter];
            }
            
            int totalBottomWeight = 0;
            for (int bottomLetter = 0; bottomLetter < LETTER_MAX; ++bottomLetter) {
                totalBottomWeight += vertical[letterToMerge][bottomLetter];
            }
            
            Preconditions.checkState(totalBottomWeight == totalRightWeight);
            
            for (int rightLetter = 0; rightLetter < LETTER_MAX; ++rightLetter) {
                
                int leftRightWeight = horizontal[letterToMerge][rightLetter];

                for (int bottomLetter = 0; bottomLetter < LETTER_MAX; ++bottomLetter) {
                    int topBottomEdgeWeight = vertical[letterToMerge][bottomLetter];

                    if (leftRightWeight > 0) {
                   // bottomConnectedNode.prevNodeWeights[rightLetter][bottomLetter]
                     //       += topBottomEdgeWeight * leftRightWeight / totalRightWeight;
                    
                        diag[rightLetter][bottomLetter]
                            += topBottomEdgeWeight * leftRightWeight / totalRightWeight;
                    }
                }

            }
        }
        
        return diag;
    }
    
    public static int getTotal(int[][] weights) {
        int sum = 0;
        
    
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            int total1 = 0;
            for (int singleLetter = 0; singleLetter < LETTER_MAX; ++singleLetter) {
                
                    total1 += weights[letter][singleLetter];
                
                
            }

            sum+= total1;
        }
        return sum;
    }
    
    public void mergeNode() {
        
        
        this.rightConnectedNode.nextNodeWeights = new int[LETTER_MAX][LETTER_MAX];
        //this.rightConnectedNode.nextNodeWeightsComplete = new int[LETTER_MAX][LETTER_MAX][LETTER_MAX];
        this.bottomConnectedNode.prevNodeWeights  =rightConnectedNode.nextNodeWeights ;// new int[LETTER_MAX][LETTER_MAX];
        //this.bottomConnectedNode.prevNodeWeightsComplete = rightConnectedNode.nextNodeWeightsComplete;
        
        this.rightConnectedNode.leftWeights = this.rightWeights;
        this.rightConnectedNode.leftConnectedNode = this;
        this.bottomConnectedNode.topWeights = this.bottomWeights;
        this.bottomConnectedNode.topConnectedNode = this;
        
        
        
        for (int letterToMerge = 0; letterToMerge < LETTER_MAX; ++letterToMerge) {
            
            int totalRightWeight = 0;
            for (int rightLetter = 0; rightLetter < LETTER_MAX; ++rightLetter) {
                totalRightWeight += rightWeights[letterToMerge][rightLetter];
            }
            
            int totalBottomWeight = 0;
            for (int bottomLetter = 0; bottomLetter < LETTER_MAX; ++bottomLetter) {
                totalBottomWeight += bottomWeights[letterToMerge][bottomLetter];
            }
            
            Preconditions.checkState(totalBottomWeight == totalRightWeight);
            
            for (int rightLetter = 0; rightLetter < LETTER_MAX; ++rightLetter) {
                
                int leftRightWeight = rightWeights[letterToMerge][rightLetter];

                for (int bottomLetter = 0; bottomLetter < LETTER_MAX; ++bottomLetter) {
                    int topBottomEdgeWeight = bottomWeights[letterToMerge][bottomLetter];

                    if (leftRightWeight > 0) {
                   // bottomConnectedNode.prevNodeWeights[rightLetter][bottomLetter]
                     //       += topBottomEdgeWeight * leftRightWeight / totalRightWeight;
                    
                        rightConnectedNode.nextNodeWeights[rightLetter][bottomLetter]
                            += topBottomEdgeWeight * leftRightWeight / totalRightWeight;
                    }
                }

            }
        }
        
        
        for (int letter = 0; letter < LETTER_MAX; ++letter) {
            int total = 0;
            for (int singleLetter = 0; singleLetter < LETTER_MAX; ++singleLetter) {
                
                    total += bottomConnectedNode.prevNodeWeights[letter][singleLetter];
                
                  //  total += rightConnectedNode.nextNodeWeights[letter][singleLetter];
                
            }

            count[letter] = total;    
            //TODO wrong probably
            rightConnectedNode.count[letter] = total;
            bottomConnectedNode.count[letter] = total;
        }
        
        this.rightConnectedNode.nextConnectedNode = bottomConnectedNode;
        this.bottomConnectedNode.prevConnectedNode = rightConnectedNode;
        
        
        this.rightConnectedNode.makeBubbleNode();
        
        this.bottomConnectedNode.makeBubbleNode();
        
        this.leftConnectedNode = null;
        this.topConnectedNode = null;
    }
    
    private void makeBubbleNode() {
        bottomWeights = new int[LETTER_MAX][LETTER_MAX];
        rightWeights = new int[LETTER_MAX][LETTER_MAX];
        rightConnectedNode = null;
        bottomConnectedNode = null;
        
        this.isBubbleNode = true;
    }
    
   

    static Node createEmptyNode(int nodeNum) {
        Node n = new Node(nodeNum);
        
        return n;
    }
    
    static Node createFirstNode() {
        Node n = new Node(1);
        for(int i = 0; i < LETTER_MAX; ++i) {
            n.count[i] = 1;
        }
        
        return n;
    }
    
    
}