﻿#if DEBUG
#define LOGGING_DEBUG
#define LOGGING_INFO
#define LOGGING_TRACE
#endif

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using CodeJamUtils;
using Utils;
using Utils.tree;
using Logger = Utils.LoggerFile;

using CodeJam.Utils.graph;

namespace CodeJam.RoundQual_2014
{
    public class WarInput
    {
        public int N;
        public int[] perm;
    }
    public class War : InputFileProducer<WarInput>, InputFileConsumer<WarInput, string>
    {
        public WarInput createInput(Scanner scanner)
        {
            WarInput input = new WarInput();
            input.N = scanner.nextInt();
            input.perm = new int[input.N];

            for (int i = 0; i < input.N; ++i)
            {
                input.perm[i] = scanner.nextInt();
            }

            return input;
        }

        public string processInput(WarInput input)
        {
            return "buh";
        }
    }
}