package codejam.y2009.round_3.football_team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;
import codejam.utils.utils.Grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class Main implements TestCaseHandler<InputData>,TestCaseInputScanner<InputData> {

	final static Logger log = LoggerFactory.getLogger(Main.class);

	

	final static int MAX_WIDTH = 1000;
	final static int MAX_HEIGHT = 30;

	private static int calcMin(InputData input) {

	    Grid<Integer> grid = input.grid;
		

		List<Node<List<Integer>>> lastNode = new ArrayList<>();

		List<List<List<Integer>>> unmatchedPlayers = new ArrayList<>();

		for (int y = 0; y < MAX_HEIGHT; ++y) {
			lastNode.add(null);
			List<List<Integer>> topCenterBottom = new ArrayList<>();
			for (int i = 0; i < 3; ++i) {
				topCenterBottom.add(new ArrayList<Integer>());
			}
			unmatchedPlayers.add(topCenterBottom);
		}

		Graph<Integer> graph = new Graph<>();
		List<Integer> players = new ArrayList<>();

		for (int x = 0; x < MAX_WIDTH; ++x) {
			for (int y = 0; y < MAX_HEIGHT; ++y) {

				Integer player = grid.getEntry(y, x);

				if (player == null) {
					continue;
				}

				log.debug("Processing player {} x,y {}, {}", player, x, y);
				players.add(player);
				if (y < MAX_HEIGHT - 1) {
					// tops bottom row
					unmatchedPlayers.get(y + 1).get(0).add(player);
				}

				if (y > 0) {
					// bottoms top row
					unmatchedPlayers.get(y - 1).get(2).add(player);
				}

				List<Integer> playersSameRow = unmatchedPlayers.get(y).get(1);
				List<Integer> playersAbove = unmatchedPlayers.get(y).get(2);
				List<Integer> playersBelow = unmatchedPlayers.get(y).get(0);

				for (Integer p : playersAbove) {
					graph.addEdge(player, p);
				}
				for (Integer p : playersBelow) {
					graph.addEdge(player, p);
				}
				for (Integer p : playersSameRow) {
					graph.addEdge(player, p);
				}

				Preconditions.checkState(playersSameRow.size() <= 1);

				playersAbove.clear();
				playersBelow.clear();
				playersSameRow.clear();

				// center
				unmatchedPlayers.get(y).get(1).add(player);

			}
		}

		//Degree 0 nodes can be any color
		graph.stripNodesOfDegreeLessThan(1);

		if (graph.getNodes().isEmpty()) {
			return 1;
		}

		//A degree 1 node will just be the color that is not the node it is connected to
		graph.stripNodesOfDegreeLessThan(2);

		if (graph.getNodes().isEmpty()) {
			return 2;
		}

		boolean re = backtrack(new int[0], null, 2, graph, players);

		if (re) {
			return 2;
		}

		//Here we know we have at least 3 colors.  A node of degree 2 will just be the color that is not 
		//one of the nodes it is connected to.  
		graph.stripNodesOfDegreeLessThan(3);

        Graph<Integer> g = graph;

        players = new ArrayList<Integer>(graph.getNodes());
        Collections.sort(players);

        int[] partialSolutionArray = null;

        while (players.size() > 0) {

            partialSolutionArray = partialSolution(g, players);

            if (partialSolutionArray == null) {
                return 4;
            }

            boolean found = false;
            for (int i = 0; i < partialSolutionArray.length; ++i) {
                if (partialSolutionArray[i] != 0) {
                    g.removeNode(players.get(i));
                    found = true;
                }
            }

            if (!found) {
                break;
            }

            // This speeds things up.
            graph.stripNodesOfDegreeLessThan(3);
            players = new ArrayList<Integer>(g.getNodes());
            Collections.sort(players);

        }

        re = backtrack(new int[0], partialSolutionArray, 3, g, players);
        if (!re) {
            return 4;
        }
        log.info("Re is {}", re);

        // }

        return 3;

    }

	private static int[] partialSolution(Graph<Integer> graph, List<Integer> players) {
		int[] ret = new int[players.size()];

		/*
		 * Fill in triangles that share at least 2 vertices.  The coloring is
		 * forced.
		 */

		// Find starting triangle
		LinkedList<Set<Integer>> toSee = new LinkedList<>();

		for (int playerIndex = 0; playerIndex < players.size(); ++playerIndex) {
			Integer player = players.get(playerIndex);
			
			Set<Integer> coloredVertices = graph.getTriangle(player);

			if (coloredVertices == null) {
				continue;
			}

			Preconditions.checkState(coloredVertices.size() == 3);
			Iterator<Integer> it = coloredVertices.iterator();

			ret[players.indexOf(it.next())] = 1;
			ret[players.indexOf(it.next())] = 2;
			// The 3rd color is not filled in
			toSee.add(coloredVertices);
			break;
		}

		Set<Set<Integer>> seenTriangles = new HashSet<>();

		while (!toSee.isEmpty()) {
			Set<Integer> coloredVertices = toSee.remove(0);

			if (seenTriangles.contains(coloredVertices)) {
				continue;
			}
			
			log.debug("Processing triangle {}", coloredVertices);

			seenTriangles.add(coloredVertices);

			boolean[] usedColors = new boolean[4];
			Integer missingIndex = null;
			for (Iterator<Integer> cvIt = coloredVertices.iterator(); cvIt
					.hasNext();) {
				int index = players.indexOf(cvIt.next());
				int color = ret[index];
				if (color == 0) {
					Preconditions.checkState(missingIndex == null);
					missingIndex = index;
					continue;
				}

				if (usedColors[color]) {
					return null;
				}
				usedColors[color] = true;
			}
			for (int i = 1; i <= 3; ++i) {
				if (!usedColors[i]) {
					ret[missingIndex] = i;
					break;
				}
			}

			for (Iterator<Integer> cvIt = coloredVertices.iterator(); cvIt
					.hasNext();) {
				// odd man out
				Integer p = cvIt.next();

				Set<Integer> two = Sets.difference(coloredVertices,
						Sets.newHashSet(p));
				Preconditions.checkState(two.size() == 2);

				// all triangles connected to two
				Set<Integer> twoNeighInter = null;

				for (Integer twoP : two) {
					if (twoNeighInter == null) {
						twoNeighInter = graph.getConnectedGraphNodes(twoP);
					} else {
						twoNeighInter = Sets.intersection(twoNeighInter,
								graph.getConnectedGraphNodes(twoP));
					}
				}

				for (Integer neigTwo : twoNeighInter) {
					if (neigTwo != p) {
						Set<Integer> set = new HashSet<>(two);
						set.add(neigTwo);
						toSee.add(set);
					}
				}

			}
		}

		return ret;
	}

	private static boolean prune(int[] solution, Graph<Integer> graph,
			List<Integer> players) {
		if (solution.length == 0) {
			return false;
		}
		int solutionIndex = solution.length - 1;

		int player = players.get(solutionIndex);
		int playerColor = solution[solutionIndex];

		Set<Integer> adj = graph.getConnectedGraphNodes(player);

		if (adj == null) {
			return false;
		}

		for (int connectedPlayer : adj) {
			if (connectedPlayer >= player) {
				continue;
			}
			int connectedPlayerIndex = Collections.binarySearch(players,
					connectedPlayer);
			int connectedPlayerColor = solution[connectedPlayerIndex];
			if (playerColor == connectedPlayerColor) {
				return true;
			}
		}

		return false;
	}

	private static boolean accept(int[] solution, Graph<Integer> graph,
			List<Integer> players) {
		return solution.length == players.size();
	}

	private static boolean backtrack(int[] solution, int[] partialSolution,
			final int colorNum, Graph<Integer> graph, List<Integer> players) {

		if (prune(solution, graph, players)) {
			return false;
		}
		if (accept(solution, graph, players)) {
			return true;
		}
		log.debug("backtrack.  solution {}", solution);

		int[] solFirst = Arrays.copyOf(solution, solution.length + 1);

		if (partialSolution != null
				&& partialSolution[solFirst.length - 1] != 0) {
			solFirst[solFirst.length - 1] = partialSolution[solFirst.length - 1];
			return backtrack(solFirst, partialSolution, colorNum, graph,
					players);
		}
		for (int i = 1; i <= colorNum; ++i) {
			solFirst[solFirst.length - 1] = i;
			boolean r = backtrack(solFirst, partialSolution, colorNum, graph,
					players);
			if (r) {
				return true;
			}
		}
		return false;

	}


    /* (non-Javadoc)
     * @see codejam.utils.main.Runner.TestCaseInputScanner#readInput(java.util.Scanner, int)
     */
    @Override
    public InputData readInput(Scanner scanner, int testCase) {
        InputData input = new InputData(testCase);
        
        Grid<Integer> grid = Grid.buildEmptyGrid(MAX_HEIGHT, MAX_WIDTH, null);
        grid.setyZeroOnTop(false);
        int numPlayers = scanner.nextInt();

        for (int playerNum = 0; playerNum < numPlayers; ++playerNum) {
            int x = scanner.nextInt() - 1;
            int y = scanner.nextInt() - 1;
            grid.setEntry(y, x, x);

        }
        input.grid = grid;
        return input;
    }

    /* (non-Javadoc)
     * @see codejam.utils.multithread.Consumer.TestCaseHandler#handleCase(java.lang.Object)
     */
    @Override
    public String handleCase(InputData input) {
        int min = calcMin(input);

        log.info("Starting case {}", input.testCase);

        return("Case #" + input.testCase + ": " + min);
    }
}