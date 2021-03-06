﻿#if DEBUG
//#define LOGGING_DEBUG
#define LOGGING_INFO
//#define LOGGING_TRACE
#endif

using CodeJam.Utils.tree;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Utils;
using Logger = Utils.LoggerFile;

namespace CodeJam.Utils.graph
{
    public class Graph
    {
        List<int>[] outgoingEdges;
        List<int>[] incomingEdges;

        public List<int> getOutboundConnected(int node)
        {
            return outgoingEdges[node];
        }

        public Boolean isConnected(int u, int v)
        {
            return outgoingEdges[u].Contains(v);
        }

        public void PostOrderTraversal(int startNode, Action<int> action)
        {
            foreach(int outIndex in outgoingEdges[startNode])
            {
                PostOrderTraversal(outIndex, action);
            }

            action(startNode);
        }

        public Graph(int maxNodes)
        {
            outgoingEdges = new List<int>[maxNodes];
            incomingEdges = new List<int>[maxNodes];
            for (int i = 0; i < maxNodes; ++i)
            {
                outgoingEdges[i] = new List<int>();
                incomingEdges[i] = new List<int>();
            }

        }

        /// <summary>
        /// Remove directed edge u->v
        /// </summary>
        /// <param name="u"></param>
        /// <param name="v"></param>
        public void removeConnection(int u, int v)
        {
            outgoingEdges[u].Remove(v);
            incomingEdges[v].Remove(u);
        }
        /// <summary>
        /// Add directed edge u-v
        /// </summary>
        /// <param name="u"></param>
        /// <param name="v"></param>
        public void addConnection(int u, int v)
        {
            Logger.LogDebug("Add connection {0} to {1}", u, v);
            outgoingEdges[u].Add(v);
            incomingEdges[v].Add(u);
        }

        public void addUndirectedConnection(int u, int v)
        {
            Logger.LogDebug("Add undirected connection {0} to {1}", u, v);
            addConnection(u, v);
            addConnection(v, u);
        }

        public TreeInt<T> GetTree<T>(int newRoot)
        {
            TreeInt<T> newTree = new TreeInt<T>(newRoot);

            // Set<int> visited = new HashSet<int>();
            Queue<int> toVisit = new Queue<int>();

            toVisit.Enqueue(newRoot);

            while (toVisit.Count > 0)
            {
                int nodeId = toVisit.Dequeue();

                // if (visited.contains(nodeId))
                // continue;

                // visited.add(nodeId);

                Preconditions.checkState(newTree.getNodes().ContainsKey(nodeId));
                TreeInt<T>.Node newTreeNode = newTree.getNodes()[nodeId];
                
                // Get all new children from old tree
                foreach (int childNodeId in outgoingEdges[nodeId])
                {
                    // Add children to new tree node
                    if (newTreeNode.getParent() == null
                            || childNodeId != newTreeNode.getParent().id)
                    {
                        newTreeNode.addChild(childNodeId);
                        toVisit.Enqueue(childNodeId);
                    }
                }

                
            }

            return newTree;
        }

        /// <summary>
        /// Returns # of connected nodes
        /// </summary>
        /// <param name="nodeToStart"></param>
        /// <param name="nodeToIgnore"></param>
        /// <returns></returns>
        public int floodFill(int nodeToStart, int nodeToIgnore = -1)
        {
            // Search all nodes connected
            bool[] visited = new bool[outgoingEdges.Length];

            if (nodeToIgnore >= 0)
                visited[nodeToIgnore] = true;

            List<int> nodesToVisit = new List<int>();
            nodesToVisit.Add(nodeToStart);

            int count = 0;

            while (nodesToVisit.Count > 0) 
            {
                int node = nodesToVisit.GetLastValue();
                nodesToVisit.RemoveAt(nodesToVisit.Count - 1);

                if (visited[node]) {
                    continue;
                }

                visited[node] = true;
                ++count;
                
                for (int i = 0; i < outgoingEdges[node].Count; ++i) 
                {                    
                    nodesToVisit.Add(outgoingEdges[node][i]);                    
                }
            }

            return count;
        }
    }
}
