﻿using CodeJamUtils;
using CombPerm;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using Utils;
using Utils.math;
using Logger = Utils.LoggerFile;

namespace Round3_2013.Problem4
{

    class DynamicProgramming
    {
        public static string processInput(WheelInput input)
        {
            int nTotal = input.wheel.Length;

            int initialState = 0;
            int holeCount = 0;
            for (int i = 0; i < nTotal; ++i)
            {
                if (input.wheel[i] == 'X') initialState = initialState.SetBit(i);
                if (input.wheel[i] == '.') holeCount++;

            }

            BigInteger denom = 1;

            for (int i = 0; i < holeCount; ++i)
                denom *= nTotal;

            DynamicProgramming dp = new DynamicProgramming(nTotal);

            BigInteger num = dp.calc(initialState, denom / nTotal);

            Logger.LogDebug(" {} / {} ", num, denom);

            return ((double)num / (double)denom).ToUsString(9);
        }
        public string FormatGondolas(int state)
        {
            return string.Join("", state.ToBinaryString(nTotal).Replace('1', 'X').Replace('0', '.').Reverse());
        }
        readonly int endPosition;
        BigInteger[] expectedValue;
        int nTotal;

        public DynamicProgramming(int nTotal)
        {

            this.nTotal = nTotal;
            //nTotal bits == 1
            endPosition = (1 << nTotal) - 1;
            expectedValue = new BigInteger[1 << 20];

            Logger.LogTrace("total {} end {} = {}.", nTotal, endPosition.ToBinaryString(8), FormatGondolas(endPosition));
            for (int i = 0; i < expectedValue.Length; ++i)
            {
                expectedValue[i] = -1;
            }
        }

        public BigInteger calc(int curState, BigInteger pMult)
        {
            if (curState == endPosition)
            {
                return 0;
            }

            if (expectedValue[curState] != -1)
            {
                return expectedValue[curState];
            }

            Logger.LogTrace("calc {} pMult {}", string.Join("", curState.ToBinaryString(nTotal).Replace('1', 'X').Replace('0', '.').Reverse()), pMult);

            BigInteger totalReturn = 0;

            //Choose each possibility
            for (int i = 0; i < nTotal; ++i)
            {
                int pos = i;
                int price = nTotal;

                BitSet csBitSet = new BitSet(curState);
                while (true)
                {
                    if (!csBitSet[pos])
                    {
                        Logger.ChangeIndent(4);
                        BigInteger toAdd = calc(curState | 1 << pos, pMult / nTotal);
                        totalReturn += price * pMult + toAdd;
                        Logger.LogTrace("Calculating spot {} next free {} Add [ price {} * mult {} + rest {} = {} ].  Total {}", i, pos, price, pMult, toAdd, price * pMult + toAdd, totalReturn);
                        Logger.ChangeIndent(-4);
                        break;
                    }

                    ++pos;
                    if (pos == nTotal)
                        pos = 0;
                    --price;
                    Preconditions.checkState(price >= 1);
                }
            }

            //Here rotate the binary nTotal times as X..XX is the same as ..XXX or .XXX.
            int equivalentState = curState;
            //Logger.LogInfo("1 eq state {}", equivalentState.ToBinaryString(nTotal));
            for (int i = 0; i < nTotal; ++i)
            {
                bool firstBit = equivalentState.GetBit(0);
                equivalentState >>= 1;

                if (firstBit)
                    equivalentState = equivalentState.SetBit(nTotal - 1);
                else
                    equivalentState = equivalentState.ClearBit(nTotal - 1);
                // Logger.LogInfo("eq state {}", equivalentState.ToBinaryString(nTotal));
                expectedValue[equivalentState] = totalReturn;
            }
            Preconditions.checkState(equivalentState == curState);

            return expectedValue[curState] = totalReturn;
        }

    }

}
