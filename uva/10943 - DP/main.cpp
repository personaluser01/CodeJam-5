//STARTCOMMON
#include <cmath>
#include <vector>
#include "stdio.h" 
#include <limits>
#include <cassert>
#include <string>
#include <iostream>
#include <cstring>

using namespace std;

#define mp make_pair 
#define pb push_back 
#define contains(c,x) ((c).find(x) != (c).end()) 
#define all(c) (c).begin(),(c).end() 
#define MIN(a,b) ((a)<(b)?(a):(b))
#define MAX(a,b) ((a)<(b)?(b):(a))

typedef unsigned int uint;
typedef long long ll;
typedef unsigned long long ull;

#define FORE(k,a,b) for(int k=(a); k <= (b); ++k)
#define FOR(k,a,b) for(int k=(a); k < (b); ++k)

typedef vector<int> vi; 
typedef vector<double> vd;
typedef vector<bool> vb;
typedef vector<vb> vvb;
typedef vector<vi> vvi;
typedef vector<uint> uvi; 
typedef vector<uvi> uvvi;
typedef vector<vd> vvd;
typedef pair<int,int> ii;
typedef pair<uint,uint> uu;
typedef vector<ii> vii;
typedef vector<vii> vvii;


const double tolerance = 0.000002;
template<typename T> 
int cmp(T a, T b, T epsilon = tolerance)
{
	T dif = a - b;
	if (abs(dif) <= epsilon)
	{
		return 0;
	}
	
	if (dif > 0)
	{
		return 1; //a > b
	}
	
	return -1;
}  


//STOPCOMMON

// dp[N remaining][ numbers remaining ]
int dp[101][101];

int main() 
{

	int N; int K;
	
	memset(dp, 0, sizeof dp);
	
	for(int num = 0; num <= 100; ++num)
	{
		dp[num][1] = 1;
	}
	
	for(int numRemaining = 2; numRemaining <= 100; ++numRemaining)
	{
		for(int sumRemaining = 0; sumRemaining <= 100; ++sumRemaining)
		{
			for(int num = 0; num <= sumRemaining; ++num)
			{
				dp[sumRemaining][numRemaining] += dp[sumRemaining - num][ numRemaining - 1];
				if (dp[sumRemaining][numRemaining] > 1000000)
					dp[sumRemaining][numRemaining] -= 1000000;
			}
		}
	}
	while(scanf("%d%d", &N, &K) == 2 && (N|K))
	{
		printf("%d\n", dp[N][K]);
		
	}
	return 0;
}
