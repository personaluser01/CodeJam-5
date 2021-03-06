package codejam.y2012.round_3.havannah;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.datastructures.BitSetInt;
import codejam.utils.datastructures.DynamicUnionFind;
import codejam.utils.datastructures.DynamicUnionFind.Component;
import codejam.utils.main.DefaultInputFiles;
import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class Main implements TestCaseHandler<InputData>, TestCaseInputScanner<InputData>, DefaultInputFiles
{

    final static Logger log = LoggerFactory.getLogger(Main.class);

    @Override
    public String[] getDefaultInputFiles()
    {
      //  return new String[] { "sample.in" };
        //   return new String[] { "B-small-practice.in" };
          return new String[] { "B-small-practice.in", "B-large-practice.in" };
    }


    /**
     * Corners have an index 0 to 5 inclusive
     * @param in
     * @param cellToIndex
     */
    void enumerateCorners(InputData in, Map<Pair<Integer,Integer>, Integer> cellToIndex)
    {
        cellToIndex.put(new ImmutablePair<>(1, 1), 0);
        cellToIndex.put(new ImmutablePair<>(1, in.S), 1);
        cellToIndex.put(new ImmutablePair<>(in.S, in.S*2-1), 2);
        cellToIndex.put(new ImmutablePair<>(in.S*2-1, in.S*2-1), 3);
        cellToIndex.put(new ImmutablePair<>(in.S*2-1, in.S), 4);
        cellToIndex.put(new ImmutablePair<>(in.S, 1), 5);
    }
    
    /**
     * 
     *  {(1, X), (X, 1), (X, S * 2 - 1), (S * 2 - 1, X)}
     *   where X is any integer, together with all cells for which |row - col| == S - 1.
     *  
     *  Edges given index 6 to 11
     *  
     * @param in
     * @param cellToIndex
     */
    
    void enumerateEdges(InputData in, Map<Pair<Integer,Integer>, Integer> cellToIndex)
    {
        
        int startSize = cellToIndex.size();

        for(int i = 2; i <= in.S-1; ++i) {
            //Lower right
            cellToIndex.put(new ImmutablePair<>(1, i), 6);
            
            //Lower left
            cellToIndex.put(new ImmutablePair<>(i, 1), 11);
        }
        
        Preconditions.checkState(startSize + (in.S-2) * 2 == cellToIndex.size());
        
        //Right
        int a = 2;
        int b = in.S + 1;
        
        //Side is S minus both corners
        for(int i = 0; i < in.S - 2; ++i)
        {
            cellToIndex.put(new ImmutablePair<>(a+i, b+i), 7);
            
            cellToIndex.put(new ImmutablePair<>(b+i, a+i), 10);
        }
        
        Preconditions.checkState(startSize + (in.S-2) * 4 == cellToIndex.size());
        
        //Upper right edge and upper left
        for( int i = in.S + 1; i < 2*in.S - 1; ++ i) {
            cellToIndex.put(new ImmutablePair<>(i, 2*in.S-1), 8);
            
            b = a;
            cellToIndex.put(new ImmutablePair<>(2*in.S-1, i), 9);
        }        
        
        int endSize = cellToIndex.size();
        
        //Make sure all edges added
        Preconditions.checkState(startSize + (in.S-2) * 6 == endSize);
        
    }
    
    @Override
    public InputData readInput(Scanner scanner, int testCase)
    {

        InputData in = new InputData(testCase);

        in.S = scanner.nextInt();
        in.Moves = scanner.nextInt();
        
        in.moveList = Lists.newArrayList();
        
        for(int i = 0; i < in.Moves; ++i) {
            in.moveList.add(new ImmutablePair<>(scanner.nextInt(), scanner.nextInt()));
        }
        
        return in;
    }

    int[][] neighborDeltas = new int[][]
            {
            {1,1},
            {0,1},
            {-1,0},
            {-1,-1},
            {0,-1},
            {1,0}
            };
    
    
    //S * 6 + moves
    final int maxNodeId = 3000 * 6 + 10000; 
    
    void buildNeighborLists(List<Integer> sequenceNeighborComponentIds,
            List<Integer> neighborIds,
            List<Integer> edgeCornerIds,
            Pair<Integer,Integer> move,
            Map<Pair<Integer,Integer>, Integer> cellToIndex,    
            Map<Pair<Integer,Integer>, Integer> cornersEdges,
            DynamicUnionFind uf
            )
    {
        for( int d = 0; d < 6; ++d) {
            int[] delta = neighborDeltas[d];
        
            Pair<Integer,Integer> neighbor = new ImmutablePair<>(
                    move.getLeft()+delta[0],
                    move.getRight()+delta[1]);
        
            Integer neighborId = cellToIndex.get(neighbor);
            
            
            
            if (neighborId == null)
            {
                sequenceNeighborComponentIds.add(null);
                neighborIds.add(null);
                continue;
            } else
            {
                //Neighbor has already been added
                Integer componentId = uf.getGroupNumber(neighborId);
                sequenceNeighborComponentIds.add(componentId);

                Preconditions.checkState(componentId != null);
                neighborIds.add(neighborId);
                
                //Is it an edge/corner
                Integer edgeCornerId = cornersEdges.get(neighbor);
                if (edgeCornerId != null)
                    edgeCornerIds.add(edgeCornerId);
            }
        }
        
        //Double it to 12
        sequenceNeighborComponentIds.addAll(new ArrayList<Integer>(sequenceNeighborComponentIds));

    }
    
    public String handleCase(InputData in)
    {
        Map<Pair<Integer,Integer>, Integer> cellToIndex = Maps.newHashMap();
        
        Map<Pair<Integer,Integer>, Integer> cornersEdges = Maps.newHashMap();
        enumerateCorners(in, cornersEdges);
        enumerateEdges(in, cornersEdges);
        
        BitSetInt edgeMask = new BitSetInt();
        BitSetInt cornerMask = new BitSetInt();
        for(int i = 0; i <= 5; ++i) {
            cornerMask.set(i);
            edgeMask.set(i+6);
        }
        

        DynamicUnionFind uf = new DynamicUnionFind(maxNodeId);
        
        StringBuffer r = new StringBuffer( String.format("Case #%d: ", in.testCase) );
        
        for(int moveIdx = 0; moveIdx < in.moveList.size(); ++moveIdx) {
            
            Pair<Integer,Integer> move = in.moveList.get(moveIdx);
            
            //Change into an id
            int moveId = -1;
            if (!cellToIndex.containsKey(move)) {
                moveId = cellToIndex.size() ;
                cellToIndex.put(move, moveId);
            } else {
                moveId = cellToIndex.get(move);
            }
            
          //Check for rings before we merg neighbors
            /**
             * Build lists of neighbors to check for edges/corners/rings
             */
            List<Integer> sequenceNeighborComponentIds = Lists.newArrayList();
            List<Integer> neighborIds = Lists.newArrayList();
            List<Integer> edgeCornerIds = Lists.newArrayList();
            
            buildNeighborLists(sequenceNeighborComponentIds,neighborIds,edgeCornerIds,
                    move,cellToIndex,cornersEdges,uf);
                        
            //Create a new component ; hovewer because edges and corners
            //are considered as 
            uf.addNode(moveId);
                       
            
            /**
             * Merge neighbors
             */
            for( int n = 0; n < 6; ++n ) {
                
                Integer neighborId = neighborIds.get(n);
                
                if (neighborId == null)
                    continue;
                
                uf.mergeComponentsOfNodes(moveId, neighborId);
            }
            
            
            /**
             * Add in edge/corner markers
             */
            Component com = uf.getGroup(moveId);
            
           
            
            for(Integer edgeCornerId : edgeCornerIds) {

                if (edgeCornerId == null)
                    continue;
                
                com.attrs.set(edgeCornerId);
            }
            
            //Also check self
            Integer edgeCorner = cornersEdges.get(move);
            if (edgeCorner != null) {
                com.attrs.set(edgeCorner);
            }
            
            /**
             * Check ring sequence
             */
            boolean hasRing = false;
            
            for(int offset = 0; offset <= 6; ++offset) {
                Integer s1 = sequenceNeighborComponentIds.get(0+offset);
                Integer s2 = sequenceNeighborComponentIds.get(1+offset);
                Integer s3 = sequenceNeighborComponentIds.get(2+offset);
                Integer s4 = sequenceNeighborComponentIds.get(3+offset);
                Integer s5 = sequenceNeighborComponentIds.get(4+offset);
                Integer s6 = sequenceNeighborComponentIds.get(5+offset);
                
                //Ring formed
                if ( Objects.equal(s1, s3) && s1 != null && s2 == null && (s4==null||s5==null||s6==null)) {
                    hasRing = true;
                    break;
                }
                
                if ( Objects.equal(s1, s4) && s1 != null && (s2 == null||s3==null) && (s5==null||s6==null)) {
                    hasRing = true;
                    break;
                }
            }
            
            
            
            
            int edges = edgeMask.getBits() & com.attrs.getBits();
            
            int corners = cornerMask.getBits() & com.attrs.getBits();
            
            boolean hasBridge = Integer.bitCount(corners) >= 2;
            boolean hasFork = Integer.bitCount(edges) >= 3;
            
            
            if (!hasRing && !hasFork && !hasBridge)
                continue;
            
            if (hasFork && hasRing && hasBridge) {
                r.append(String.format("bridge-fork-ring in move %d", moveIdx+1));
            } else if (hasFork && hasRing) {
                r.append(String.format("fork-ring in move %d", moveIdx+1));  
            } else if (hasBridge && hasRing) {
                r.append(String.format("bridge-ring in move %d", moveIdx+1));
            } else if (hasFork && hasBridge) {
                r.append(String.format("bridge-fork in move %d", moveIdx+1));
            } else if (hasRing) {
                r.append(String.format("ring in move %d", moveIdx+1));
            } else if (hasFork) {
                r.append(String.format("fork in move %d", moveIdx+1));
            } else if (hasBridge) {
                r.append(String.format("bridge in move %d", moveIdx+1));
            }
        
            return r.toString();
        }



        r.append("none");
        return r.toString();
    }

    
}
