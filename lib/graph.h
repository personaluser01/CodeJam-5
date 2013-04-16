#include <fstream>
#include <map>
#include <set>
#include <iterator>
#include <iomanip>
#include <sstream>
//#include <bitset>
#include <cctype>
#include <cmath>
#include <functional>
#include <cstring>
#include <queue>


#define NE_LT(attr) if (lhs.attr != rhs.attr) return lhs.attr < rhs.attr

#define NE_GT(attr) if (lhs.attr != rhs.attr) return lhs.attr > rhs.attr


#define sz(a) int((a).size()) 

#define all(c) (c).begin(),(c).end() 
#define FOR_IT(c,i) for(typeof((c).begin() i = (c).begin(); i != (c).end(); i++) 
#define cpresent(c,x) (find(all(c),x) != (c).end()) 
#define SZ(x) ((int) (x).size())

const bool debug = false;

const int notConnected = numeric_limits<int>::max();

template<typename T> T gcd(T a, T b)
{
    while(b) b ^= a ^= b ^= a %= b;
    return a;
}

template<typename T>
ostream& operator<<( ostream& os, const vector<T>& vec )
{
    FOR(i, 0, vec.size())
    {
        os <<  vec[i] << endl;
    }
    return os;
}

#ifdef USING_FLOW

//edge from source to destination
template <typename FlowType>
struct edge
{    
    int src;
    int dest;
    
    FlowType cap;
    FlowType residue;

	bool ignore;

	//flow = capacity - residue
    
    edge(int _src, int _dest, FlowType _cap, FlowType _res) :
	src(_src), dest(_dest), cap(_cap), residue(_res),
	ignore(false)
	{
		assert(residue >= 0 && residue <= cap);
	}
};

template<typename FlowType>
ostream& operator<<(ostream& os, const edge<FlowType>& e)
{
    os <<  e.src << " --> " << e.dest
		<< " flow " << e.cap - e.residue << " / " << e.cap ;
    
    return os;
}

template<typename FlowType>
class Flow
{
	public:

	//V [ node idx ] = list of edge idxs originating from node
	vvi V;
	vector<edge<FlowType> > E;

	int source;
	int sink;

	Flow(int _source, int _sink) : source(_source), sink(_sink)
	{

	}

	//set flow back to 0
	void resetFlow()
	{
		for(int i = 0; i < E.size(); ++i)
		{
			if (i % 2 == 0)
				E[i].residue = E[i].cap;
			else
				E[i].residue = 0;
		}

	}


	void setIgnoreNode(int nodeIdx, bool ignore)
	{
		for(int e = 0; e < V[nodeIdx].size(); ++e)
		{
			int eIdx = V[nodeIdx][e];
			E[ eIdx ].ignore = ignore;
		}
	}

	void addEdge(int src, int dest, FlowType cap)
	{
		int e = E.size();

		if ( max(src,dest) >= V.size())
			V.resize( max(src,dest) + 1);

		V[src].pb(e);
		V[dest].pb(e+1);

		E.push_back(edge<FlowType>(src, dest, cap, cap));

		//Residual = 0, so backwards edge begins saturated at max flow
		E.push_back(edge<FlowType>(dest, src, cap, 0));
	}

	/*
		prev[ vertex id ] =  the edge id of the edge used to go to previous node
	*/
	FlowType findAugPathMaxFlow(const vi& prev)
	{
		FlowType canPush = numeric_limits<FlowType>::max();

		int nodeIdx = sink;

		if (debug)
			printf("Finding maximum flow through augmenting path. Sink=%d\n", sink);

		while( prev[nodeIdx] != -2 ) //nodeIdx is not the source
		{
			assert(prev[nodeIdx] >= 0);

			canPush = min(canPush, E[ prev[nodeIdx] ].residue );

			nodeIdx = E[ prev[nodeIdx] ].src;		

			if (debug)
				printf("Can push %d.  Next node in aug path %d\n", canPush, nodeIdx);
		}

		return canPush;
	}

	void updateViaAugPath(const vi& prev, FlowType flowAdded)
	{
		int nodeIdx  = sink;

		while( prev[nodeIdx] != -2 ) //nodeIdx is not the source
		{
			assert(prev[nodeIdx] >= 0);

			E[ prev[nodeIdx] ].residue -= flowAdded;
			assert(E[ prev[nodeIdx] ].residue >= 0);

			//Because we added the edges in pairs xor will either add one or subtract one
            E[ prev[nodeIdx] ^ 1].residue += flowAdded;
			assert( E[ prev[nodeIdx] ^ 1 ].residue <= E[ prev[nodeIdx] ^ 1 ].cap);
            
			if (debug)
				printf("Pushing %d flow at node %d edge ids %d and %d \n", 
				flowAdded, nodeIdx, prev[nodeIdx], prev[nodeIdx] ^ 1);

			nodeIdx = E[ prev[nodeIdx] ].src;
		}

	}

	vector<ii>  getMinCut()
	{
		const int nNodes = V.size();
		vi prev(nNodes, -1);
		vector<bool> seen(nNodes, false);

		prev[source] = -2;

		/*
		From the source vertex, do a depth-first search along edges that still 
		have residual capacity (i.e., non-saturated edges). 
		The cut consists of all edges that were "seen" (i.e., are incident on a visited vertex),
		but were not traversed since they are saturated. As you noted, there might
		be other saturated edges that are not part of the minimum cut.
		*/
		set<int> visited;

		queue<int> q;

		q.push(source);
		seen[source] = true;
		while (!q.empty())
		{
			int nodeIdx = q.front();
			q.pop();

			assert(seen[nodeIdx]);

			visited.insert(nodeIdx);

			if (debug) printf("Popped node %d\n", nodeIdx);
			//Sink?


		    if (debug) printf("Looking at node %d.  Edges count %d\n", nodeIdx, V[nodeIdx].size());
			for (int i = 0; i < V[nodeIdx].size(); i++)
			{
				const int edgeIdx = V[nodeIdx][i];
				const edge<FlowType>& anEdge = E[ edgeIdx ];

				int trgNodeIdx = anEdge.dest;

				if (debug) printf("edges id %d target %d flow %d capacity %d seen: %s\n", edgeIdx, trgNodeIdx, 
					anEdge.cap - anEdge.residue, anEdge.cap, seen[trgNodeIdx] ? "yes" : "no");

				if (anEdge.residue == 0)
					continue;

				if ( !seen[trgNodeIdx])
				{
					prev[trgNodeIdx] = edgeIdx;
					seen[trgNodeIdx] = true;
					q.push(trgNodeIdx);
				}
			}			
		}

		//if it is max flow, there should be no augmenting path to the sink;
		assert(!seen[sink]);

		vector<ii> ret;

		//Loop through visited verticies, looking for edges to non visited verticies
		for(set<int>::iterator it = visited.begin(); it != visited.end(); ++it)
		{
			int nodeIdx = *it;

			for (int i = 0; i < V[nodeIdx].size(); i++)
			{
				const int edgeIdx = V[nodeIdx][i];

				//Only consider originally added edges
				if (edgeIdx % 2 == 1)
					continue;

				const edge<FlowType>& anEdge = E[ edgeIdx ];

				int trgNodeIdx = anEdge.dest;

				if (contains(visited, trgNodeIdx))
					continue;

				//If there was residue it should have been traversed
				assert (anEdge.residue == 0);

				ret.pb( mp(nodeIdx, trgNodeIdx) );
			}			

		}

		return ret;

	}

	FlowType augment()
	{
		const int nNodes = V.size();
		vi prev(nNodes, -1);
		vector<bool> seen(nNodes, false);

		prev[source] = -2;

		queue<int> q;

		q.push(source);
		seen[source] = true;
		while (!q.empty())
		{
			int nodeIdx = q.front();
			q.pop();

			assert(seen[nodeIdx]);

			if (debug) printf("Popped node %d\n", nodeIdx);
			//Sink?


		    if (debug) printf("Looking at node %d.  Edges count %d\n", nodeIdx, V[nodeIdx].size());
			for (int i = 0; i < V[nodeIdx].size(); i++)
			{
				const int edgeIdx = V[nodeIdx][i];
				const edge<FlowType>& anEdge = E[ edgeIdx ];

				int trgNodeIdx = anEdge.dest;

				if (debug) printf("edges id %d target %d flow %d capacity %d seen: %s\n", edgeIdx, trgNodeIdx, 
					anEdge.cap - anEdge.residue, anEdge.cap, seen[trgNodeIdx] ? "yes" : "no");

				if (anEdge.residue == 0)
					continue;

				if (anEdge.ignore)
					continue;



				if ( !seen[trgNodeIdx])
				{
					prev[trgNodeIdx] = edgeIdx;
					seen[trgNodeIdx] = true;
					q.push(trgNodeIdx);
				}
			}
			//printf("Done\n");
		}

		if (seen[sink])
		{
			if (debug) printf("reached sink\n");

			FlowType canPush = findAugPathMaxFlow(prev);
			assert(canPush > 0);

			updateViaAugPath( prev, canPush );

			return canPush;
		}

		//printf("Return 0\n");
		return 0;
	}
};
#endif 

typedef map<string, int> msi;

template<typename OrigType>
int getId(
	map<OrigType, int>& mapNameId, 
	map<int, OrigType>& mapNames, 
	const OrigType& name, int nextId)
{
	typename map<OrigType, int>::iterator lowerBound = mapNameId.lower_bound(name);

	if(lowerBound != mapNameId.end() && !(mapNameId.key_comp()(name, lowerBound->first)))
	{
	   return lowerBound->second;
	}
	else
	{
	   mapNameId.insert(lowerBound, std::make_pair(name, nextId));
	   mapNames[nextId] = name;
	   return nextId;
	}	
}

#ifdef USING_DFS
namespace DFS
{

int V, E;
vi dfs_low;    
vi dfs_num;
vi dfs_parent;

vi articulation_vertex;
int dfsNumberCounter, dfsRoot, rootChildren;
//outgoing edges
vvi adjList;
//Incoming edges
//vvi incAdjList;

//Used to create min amt edges s.t. graph remains connected
//vvb  needed;

vi S;
vi visited;
int numSCC;

//strongly connected components
vvi SCC;
vii bridges;

vi topoSort;             // global vector to store the toposort in reverse order

void reset()
{
	
	articulation_vertex.assign(V, false);
	
	adjList.assign(V, vi() );
	//incAdjList.assign(V, vi() );
	
	//needed.assign(V, vb(V, false) );
	dfsNumberCounter = 0;
	dfs_num.assign(V, -1);
	dfs_low.assign(V, 0);
	dfs_parent.assign(V, -1);
	dfsRoot = 0;
	rootChildren = 0;

	visited.assign(V, 0);
	numSCC = 0;

	SCC.clear();
	topoSort.clear();

	bridges.clear();

}

void addEdge(int u, int v)
{
	adjList[u].pb(v);
	//incAdjList[v].pb(u);
}
/*
for (int i = 0; i < V; i++)            
			if (dfs_num[i] == -1)
				dfs2(i);
				*/
void dfs2(int u) 
{    // change function name to differentiate with original dfs
	dfs_num[u] = 0;
	for (int j = 0; j < (int)adjList[u].size(); j++) 
	{
		int v = adjList[u][j];
		if (dfs_num[v] == -1)
			dfs2(v);
	}

	topoSort.push_back(u); 
}                   // that is, this is the only change


void articulationPointAndBridge(int u) 
{
	dfs_low[u] = dfs_num[u] = dfsNumberCounter++;      // dfs_low[u] <= dfs_num[u]

	if (debug) printf("Entry u=%d low = num = %d \n",
				u+1, dfs_num[u], dfs_low[u]);

	for (int j = 0; j < (int)adjList[u].size(); j++) 
	{
		int v = adjList[u][j];
		if (dfs_num[v] == -1) 
		{
			//tree edge
			dfs_parent[v] = u;

			//needed[u][v] = true;

			if (u == dfsRoot) rootChildren++;  // special case, count children of root

			articulationPointAndBridge(v);

			if (dfs_low[v] >= dfs_num[u])              // for articulation point
				articulation_vertex[u] = true;           // store this information first

			if (dfs_low[v] > dfs_num[u])   
			{
				// for bridge
				if (debug) printf(" Edge (%d, %d) is a bridge\n", u, v);
				//needed[v][u] = true;
				bridges.pb( mp (min(u,v), max(u,v) ) );
			}

			dfs_low[u] = min(dfs_low[u], dfs_low[v]);       // update dfs_low[u]
		}
        else if (v != dfs_parent[u])       // a back edge and not direct cycle
		{
			if (debug) printf("Back edge u=%d v=%d  u dfs num %d low %d  v num %d low %d\n",u+1, v+1, dfs_num[u], dfs_low[u], dfs_num[v], dfs_low[v]);

			dfs_low[u] = min(dfs_low[u], dfs_num[v]);       // update dfs_low[u]

			//connect from higher number to lower 
			if (dfs_num[u] > dfs_num[v] )
			{
				//needed[u][v] = true;
			} else {
				//needed[v][u] = true;
			}

		}
	}
} 

/*
Puts each strongly connected component in SCC

for (int i = 0; i < V; i++)
    if (dfs_num[i] == -1)
      tarjanSCC(i);
	  */
void tarjanSCC(int u) 
{
	dfs_low[u] = dfs_num[u] = dfsNumberCounter++;      // dfs_low[u] <= dfs_num[u]

	S.push_back(u);           // stores u in a vector based on order of visitation
	visited[u] = 1;
	for (int j = 0; j < (int)adjList[u].size(); j++) 
	{
		int v = adjList[u][j];
		if (dfs_num[v] == -1)
			tarjanSCC(v);
    
		if (visited[v])                                // condition for update
			dfs_low[u] = min(dfs_low[u], dfs_low[v]);
	}

	if (dfs_low[u] == dfs_num[u]) 
	{         // if this is a root (start) of an SCC
		//printf("SCC %d:", numSCC);            // this part is done after recursion
		++numSCC;
		SCC.pb(vi());
		
		while (1) 
		{
			int v = S.back(); S.pop_back(); visited[v] = 0;
			SCC[numSCC-1].pb(v);
			//printf(" %d", v);
			if (u == v) break;
		}
		
		//puts("");

    }
    
} 

}

using namespace DFS;
#endif 


template<typename CoordType>
double distance( CoordType x1, CoordType y1, 
	CoordType x2, CoordType y2)
{
	return sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
}

template<typename CoordType>
CoordType distanceSq( const pair<CoordType,CoordType>& pt1, 
	const pair<CoordType,CoordType>& pt2)
{
	return (pt1.first-pt2.first)*(pt1.first-pt2.first) + 
	(pt1.second-pt2.second)*(pt1.second-pt2.second) ;
}

template<typename WeightType>
class WeightedEdge
{
public:
	int u;
	int v;

	WeightType weight;

	WeightedEdge(int _u = 0, int _v = 0, WeightType _w = WeightType()) : u(_u), v(_v), weight(_w)
	{

	}
};


template<typename WeightType>
int operator<( const WeightedEdge<WeightType>& lhs, const WeightedEdge<WeightType>& rhs )
{
	NE_LT(weight);

	NE_LT(u);

	NE_LT(v);

	return 0;

}


class UnionFind
{
public:
	vi id; vi sz;

	int nComp;

	void initSet(int n)
	{
		id.assign(n, 0);
		sz.assign(n, 1);
		for(int i = 0; i < n; ++i) id[i] = i;

		nComp = n;
	}

	int findSet(int i)
	{
		//printf("Find set %d\n", i);
		return (i == id[i]) ? i : id[i] = findSet(id[i]);
	}

	bool isSameSet(int p, int q)
	{
		return findSet(p) == findSet(q);
	}

	int size(int i)
	{
		return sz[ findSet(i) ];
	}

	//Returns id of unified set
	int unionSet(int p, int q)
	{
        int i = findSet(p);
        int j = findSet(q);

		if (i == j)
			return i;

		--nComp;
        if(sz[i] > sz[j])
        {
            id[j] = i;
            sz[i] += sz[j];
			return i;
        }
        else
        {
			id[i] = j;
			sz[j] += sz[i];
			return j;
        }

		//printf("Union set %d to %d ; sizes %d and %d\n", p, q, sz[p], sz[q]);
	}
};