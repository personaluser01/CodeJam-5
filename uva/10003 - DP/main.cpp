#include "stdio.h" 
#include <limits>
#include <algorithm>

using namespace std;

#define FORE(k,a,b) for(int k=(a); k <= (b); ++k)
#define FOR(k,a,b) for(int k=(a); k < (b); ++k)


// dp[len][min][max] = min cost to make cuts from min to max on a board of length len 
int dp[50][50];

int cuts[50];

int N;
int L;

int cost( int minIdx, int maxIdx)
{
	int& ret = dp[minIdx][maxIdx];
	
	if (ret < numeric_limits<int>::max() )
		return ret;

	//where dimensions of the board 
	int left = minIdx == 0 ? 0 : cuts[minIdx - 1] ;
	int right  = maxIdx == N - 1 ? L : cuts[maxIdx + 1] ;
	int length = right - left; 
	
	if ( minIdx == maxIdx )
		return ret = length;
	
	//cutting ends
	ret = min(ret, length + cost( minIdx+1, maxIdx) );	
	ret = min(ret, length + cost( minIdx, maxIdx-1) );
	
	FORE(idx, minIdx+1, maxIdx-1)
	{
		ret = min(ret, length + cost( minIdx, idx - 1)  + cost( idx+1, maxIdx));
	}
	
	return ret;
}

int main() 
{
	
	while(  scanf("%d%d", &L, &N) && L )
	{
		FOR(i, 0, N)		
			scanf("%d", &cuts[i]);
				
		FOR(i, 0, N) FOR(j, 0, N)
			dp[i][j] = numeric_limits<int>::max();
		
		int minCost = N == 0 ? 0 : cost(0, N-1);
			
		printf("The minimum cutting is %d.\n", minCost);		
	}
	return 0;
}
