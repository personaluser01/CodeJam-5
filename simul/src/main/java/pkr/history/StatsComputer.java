package pkr.history;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class StatsComputer
{

     Logger log = LoggerFactory.getLogger(StatsComputer.class);
    
    public StatsComputer(List<FlopTurnRiverState[]> hands ) {

        stats = new StatsSession();
        
        for(int hand = 0; hand < hands.size(); ++hand )
        {
            FlopTurnRiverState[] ftrStates = hands.get(hand);
            log.debug("\nStats hand : {}", hand);
            
            for(String preFlopPlayer : ftrStates[0].players)
            {
                log.debug("Player {}", preFlopPlayer);
                StatsSessionPlayer playerSesStat = getSessionStats(preFlopPlayer);
                
                playerSesStat.totalHands++;
                
                handleRoundStats(ftrStates, playerSesStat, preFlopPlayer);
                
                //Checking BB does not affect stats
                if (preFlopPlayer.equals(ftrStates[0].playerBB)
                        && ftrStates[0].tableStakes == ftrStates[0].getCurrentBet(preFlopPlayer)
                        && ftrStates[0].roundInitialBetter == null
                        )
                {
                    log.debug("Player {} is an unraised big  blind", preFlopPlayer);
                    continue;
                }
                
                
                
                int playerBet = ftrStates[0].getCurrentBet(preFlopPlayer);
                
                playerSesStat.vpipDenom++;
                
                if ( (!preFlopPlayer.equals(ftrStates[0].playerBB) && playerBet >= ftrStates[0].tableStakes)
                        ||
                        (preFlopPlayer.equals(ftrStates[0].playerBB) && playerBet > ftrStates[0].tableStakes))
                {
                    log.debug("Player {} entered pot for VPIP.  table stakes {}  player bet {} bb {}", preFlopPlayer,
                            ftrStates[0].tableStakes,
                            ftrStates[0].getCurrentBet(preFlopPlayer),
                            ftrStates[0].playerBB
                            );
                    playerSesStat.vpipNumerator++;
                }
                
                
                
                if (ftrStates[0].roundInitialBetter != null && 
                        !ftrStates[0].roundInitialBetter.equals(preFlopPlayer) &&
                        BooleanUtils.isNotTrue(ftrStates[0].hasFolded.get(preFlopPlayer))
                        ) 
                {
                    log.debug("Player {} called an initial raise", preFlopPlayer);
                    playerSesStat.callOpenDenom++;
                    playerSesStat.callOpenNumerator++;
                } else if (BooleanUtils.isTrue(ftrStates[0].foldedToBetOrRaise.get(preFlopPlayer))) {
                    playerSesStat.callOpenDenom++;
                }
                
                
                if (StringUtils.equals(ftrStates[0].roundInitialBetter, preFlopPlayer))
                {
                    playerSesStat.preFlopRaises ++;
                    
                    if (ftrStates[0].allInBet.containsKey(preFlopPlayer))
                    {
                        playerSesStat.preFlopTapis++;
                    }
                }
                
                //if (BooleanUtils.isTrue(ftrStates[0].hasFolded.get(preFlopPlayer)))
                /*
                {
                    playerSesStat.vpipDenom++;
                } else if (!preFlopPlayer.equals(ftrStates[0].playerBB)
                        || ftrStates[0].tableStakes > ftrStates[0].playerBets.get(preFlopPlayer)
                        ) 
                {
                    //Player is either not the big blind or had to put in extra
                    playerSesStat.vpipNumerator++;
                    playerSesStat.vpipDenom++;
                }*/
                
            }
        }
        
        
        
        if (hands.size() > 0)
            stats.currentPlayerList = hands.get(hands.size()-1)[0].players;
        
        for(String playerName : stats.currentPlayerList)
        {
            StatsSessionPlayer pStats = stats.playerSessionStats.get(playerName);
            
            for(int r = 0; r < 3; ++r)
            {
                pStats.roundStats[r].avgBetToPot /= pStats.roundStats[r].bets;
                pStats.roundStats[r].avgFoldToBetToPot /= pStats.roundStats[r].folded;
            }
        }
    }
    
    public StatsSession stats = null;
    
    private void handleRoundStats(FlopTurnRiverState[] ftrStates, StatsSessionPlayer player, String playerName)
    {
        Preconditions.checkNotNull(ftrStates);
        Preconditions.checkArgument(ftrStates.length == 4);
        
        for(int r = 0; r < 3; ++r)
        {
        if (ftrStates[r+1] != null && 
                ftrStates[r+1].players != null && 
                ftrStates[r+1].players.size() > 1 && 
                ftrStates[r+1].players.contains(playerName))
        {
            player.roundStats[r].seen++;
            
            if (ftrStates[r+1].amtToCall == 0)
            {
                player.roundStats[r].checkedThrough++;
            } else {
                //Make sure only 1 raise / fold / call is true
                /*
                int check = 0;
                if (BooleanUtils.isTrue(ftrStates[r+1].hasBet.get(playerName)))
                   ++check;
            
                if (BooleanUtils.isTrue(ftrStates[r+1].foldedToBetOrRaise.get(playerName)))
                    ++check;
                
                if (BooleanUtils.isTrue(ftrStates[r+1].calledABetOrRaise.get(playerName)))
                    ++check;
                
                if (BooleanUtils.isTrue(ftrStates[r+1].allInBet.containsKey(playerName)))
                    ++check;
                
                Preconditions.checkState(1 <= check && check <= 2, "Player %s has not just bet/folded/called in a raised round %s.\n  has bet [%s] has folded [%s]  has called [%s] has all in [%s]",
                        playerName, r,
                        ftrStates[r+1].hasBet.get(playerName),
                        ftrStates[r+1].foldedToBetOrRaise.get(playerName),
                        ftrStates[r+1].calledABetOrRaise.get(playerName),
                        ftrStates[r+1].allInBet.containsKey(playerName)
                        );
                        */
            }
            
            log.debug("Player {} is in round {}", playerName, r+1);
            
            if (BooleanUtils.isTrue(ftrStates[r+1].calledABetOrRaise.get(playerName)))
            {
                player.roundStats[r].calls++;
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].foldedToBetOrRaise.get(playerName)))
            {
                player.roundStats[r].folded++;
                player.roundStats[r].avgFoldToBetToPot += ftrStates[r+1].foldToBetSize.get(playerName); 
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].hasBet.get(playerName)))
            {
                player.roundStats[r].bets++;
                player.roundStats[r].avgBetToPot += ftrStates[r+1].betToPotSize.get(playerName);
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].allInBet.containsKey(playerName)))
            {
                player.roundStats[r].allIn++; 
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].hasReraised.get(playerName)))
            {
                player.roundStats[r].reraises++;
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].hasChecked.get(playerName)))
            {
                player.roundStats[r].checks++;
            }
            
            if (BooleanUtils.isTrue(ftrStates[r+1].hasChecked.get(playerName)) &&
                    BooleanUtils.isTrue(ftrStates[r+1].hasReraised.get(playerName)))
            {
                player.roundStats[r].checkRaises++;
            }
        }
        }
    }
    
    
    StatsSessionPlayer getSessionStats(String playerName)
    {
        StatsSessionPlayer ret = stats.playerSessionStats.get(playerName);
        
        if (ret == null)
        {
            ret = new StatsSessionPlayer();
            stats.playerSessionStats.put(playerName, ret);
        }
        
        return ret;
    }

}
