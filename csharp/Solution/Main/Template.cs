﻿#define LOGGING_DEBUG
#define LOGGING_INFO
using CodeJam._2014.Round2;
using CodeJam.Utils.geom;
using CodeJam.Utils.graph;
using CodeJam.Utils.math;
using CodeJamUtils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Utils;
using Wintellect.PowerCollections;
using Logger = Utils.LoggerFile;



namespace aYear2015.RoundQual.Problem2
{
    
    public class InfiniteHouseOfPancakesInput
    {
       
        public int N;

        public List<String> S;
    }

	public class InfiniteHouseOfPancakes : InputFileProducer<InfiniteHouseOfPancakesInput>, InputFileConsumer<InfiniteHouseOfPancakesInput, String>
    {
        public InfiniteHouseOfPancakesInput createInput(Scanner scanner)
        {
			//scanner.enablePlayback();
			InfiniteHouseOfPancakesInput input = new InfiniteHouseOfPancakesInput();
			           
            input.N = scanner.nextInt();

            input.S = new List<string>();
			
            for (int i = 0; i < input.N; ++i)
            {
                input.S.Add(scanner.nextWord());
            }

            //Logger.LogInfo("Input {}", scanner.finishPlayBack());
            return input;
		}

		public String processInput(InfiniteHouseOfPancakesInput input)
        {
			return "-1";
		}
	}

}