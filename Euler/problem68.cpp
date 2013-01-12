
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <set>
#include <vector>
#include <algorithm>
#include <cassert>
#include <iterator>
#include <cctype>
#include <functional>
#include <queue>
#include "prettyprint.h"
using namespace std;

typedef unsigned int uint;
typedef unsigned long long ull;

#define FORE(k,a,b) for(uint k=(a); k <= (b); ++k)
#define FOR(k,a,b) for(uint k=(a); k < (b); ++k)

#define pb push_back 
#define mp make_pair 

typedef vector<int> vi; 
typedef vector<vi> vvi;
typedef vector<uint> uvi; 
typedef vector<uvi> uvvi;
typedef pair<int,int> ii;
typedef pair<uint,uint> uu;
#define sz(a) int((a).size()) 
#define pb push_back 
#define all(c) (c).begin(),(c).end() 
#define FOR_IT(c,i) for(typeof((c).begin() i = (c).begin(); i != (c).end(); i++) 
#define present(c,x) ((c).find(x) != (c).end()) 
#define cpresent(c,x) (find(all(c),x) != (c).end()) 


bool rejectRow(const uvi& sol,uint a1, uint a2, uint a3,
    uint b1, uint b2, uint b3)
{
    uint checkRow1 = sol[a1] + sol[a2] + sol[a3];
    uint checkRow2 = sol[b1] + sol[b2] + sol[b3];

    if (checkRow1 != checkRow2)
        return true;
    
    return false;    
}

//No longer used too slow
bool reject(const vector<uint>& sol)
{
    uint usedDigits = 0;

    FOR(solIdx, 0, sol.size())
    {
        if (  (usedDigits & 1 << sol[solIdx] ) != 0 )
			return true;
		
		usedDigits |= 1 << sol[solIdx];		
    }

	if (sol.size() >= 5) {
		bool rejRow = rejectRow(sol, 0,1,2, 3,2,4);
		
		if (rejRow)
			return true;
		
		//0 must be lowest external
		if (sol[0] > sol[3])
		    return true;
		
		//10 external
		if (sol[1] == 10 || sol[2] == 10 || sol[4] == 10) 
		    return true;
	}

	if (sol.size() >= 7) {
		bool rejRow = rejectRow(sol, 0,1,2, 6,4,5);
		
		if (rejRow)
			return true;
		
		if (sol[0] > sol[5])
		    return true;
		
		//10 external
		if (sol[6] == 10) 
		    return true;
	}
	
	if (sol.size() >= 9) {
		bool rejRow = rejectRow(sol, 0,1,2, 6,7,8);
		
		if (rejRow)
			return true;
		
		if (sol[0] > sol[7])
		    return true;
		
		if (sol[8] == 10) 
		    return true;
	}
	
	
	if (sol.size() >= 10) {
		bool rejRow = rejectRow(sol, 0,1,2, 1,8,9);
		
		if (rejRow)
			return true;
		
		if (sol[0] > sol[9])
		    return true;
	}

	return false;
}

/**
 * index 0 == row 1
 * index 1 == row 2
 */
void findSolutions(vector<uint>& partial, uint N,
				   vector<vector<uint> >& solutions, 
				   uint& solutionCount)
{
    if (reject(partial))
        return;
    
    if(partial.size() == N) {
		cout << "Solution found ! " << endl;
		cout << partial << endl;
		
		solutions.push_back(partial);
		
		++solutionCount;

		
        return;
    }
    
    FORE(num, 1, N)
    {		
        partial.push_back(num);
		findSolutions(partial, N, 
			solutions, solutionCount);
        partial.erase(partial.begin() + partial.size() - 1);
    }
}


ull concatNums(ull left, ull right)
{
   // cout << "Concat " << left << " + "	<< right;
	//123 ; 456
	ull rightDigits = right;

	while(rightDigits > 0)
	{
		rightDigits /= 10;
		left *= 10;
	}

	// cout << " = " << left+right << endl;
	
	return left+right;
}

int main() {
    

	vector<uint> partial;
    vector< vector<uint> > allSolutions;
    uint solutionCount = 0;
    
    findSolutions(partial, 10, allSolutions, solutionCount);
    
    ull maxSol = 0;
    
    for(auto solIt = allSolutions.begin();
        solIt != allSolutions.end();
        ++solIt)
    {
        const uvi& sol = *solIt;
        ull num = 0;
        num = concatNums(num,sol[0]);
        num = concatNums(num,sol[1]);
        num = concatNums(num,sol[2]);
        
        num = concatNums(num,sol[3]);
        num = concatNums(num,sol[2]);
        num = concatNums(num,sol[4]);
        
        num = concatNums(num,sol[5]);
        num = concatNums(num,sol[4]);
        num = concatNums(num,sol[6]);
        
        num = concatNums(num,sol[7]);
        num = concatNums(num,sol[6]);
        num = concatNums(num,sol[8]);
        
        num = concatNums(num,sol[9]);
        num = concatNums(num,sol[8]);
        num = concatNums(num,sol[1]);
        
        
        
        cout << num << endl;
        
        maxSol = max(num, maxSol);
    }
	
    cout << "Max : " << maxSol << endl;
	return 0;
}
