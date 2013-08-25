package pkr.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pkr.history.PlayerAction.Action;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class FlopTurnRiverState implements ParserListener
{
    static Logger log = LoggerFactory.getLogger(Parser.class);
    static Logger logOutput = LoggerFactory.getLogger("handOutput");
    
    
    public int lineNumber;
    
    List<FlopTurnRiverState[]> masterList; 
    FlopTurnRiverState[] roundStates;
    
    //La liste complète d'actions faites dans la main
    public List<PlayerAction> actions;    
    public List<List<Integer>> playerPosToActions;
    
    //Qui a fait la dernière action aggressive dans la tournée précédente
    public String agresseur;
    
    //La liste de joeurs dans l'ordre à parler 
    public List<String> players;
    int pot;
    
    final static int MAX_PLAYERS = 5;
    
    //Full amount needed to stay in
    int amtToCall = 0;
    
    public Map<String , Boolean> hasFolded;
    public boolean[] hasFoldedArr;
    public Map<String, Integer> allInMinimum;
    
    String lastTapisPlayer;
    Map<String, Integer> playerBets;
    
    Map<String, Double> betToPotSize;
    Map<String, Double> foldToBetSize;
    
    public String roundInitialBetter;
   
    
    
    
    String playerSB;
    public String playerBB;
    public int tableStakes;
    
    
    int currentPlayer = 0;
    
    boolean replayLine = false;
    final int round;
    
    
    /**
     * Each object is a round in a hand
     * 
     * @param players
     * @param pot
     * @param replayLine
     * @param round
     * @param stats
     * @param masterList   holds for all hands in a session
     * @param roundStates  holds all objects for the hand 
     */
    public FlopTurnRiverState(List<String> players, int pot, boolean replayLine, int round, 
            List<FlopTurnRiverState[]> masterList, int lineNumber, FlopTurnRiverState[] roundStates) 
    {
        super();
        Preconditions.checkNotNull(roundStates);
        Preconditions.checkNotNull(masterList);
        
        this.players = players;
        this.pot = pot;
        this.replayLine = replayLine;
        this.round = round;
        this.masterList = masterList;
        this.roundStates = roundStates;
        this.lineNumber = lineNumber;
        
        this.betToPotSize = Maps.newHashMap();
        this.foldToBetSize = Maps.newHashMap();
        
        actions = Lists.newArrayList();
        playerPosToActions = Lists.newArrayList();
        
        for (int i  = 0; i < players.size(); ++i)
        {
            playerPosToActions.add(new ArrayList<Integer>());
        }
        
        this.currentPlayer = players.size() - 1;
        
        hasFolded = Maps.newHashMap();
        hasFoldedArr = new boolean[MAX_PLAYERS];
        playerBets = Maps.newHashMap();
        allInMinimum = Maps.newHashMap();
        
        
        if (round == 0) {
            logOutput.debug("\n***********************************************");
            logOutput.debug("Starting Hand {} line {}", masterList.size()+1,lineNumber);
            
        }
        
        Preconditions.checkState(roundStates.length == 4);
        Preconditions.checkState(roundStates[round] == null);
        roundStates[round] = this;
        
        log.debug("Nouveau round.  Players {} pot {}", players.size(), pot);
        
        logOutput.debug("\n------------------------------");
        logOutput.debug("\nStarting round {} with {} players.  Pot is ${}\n",
               Statistics.roundToStr(round),
                        players.size(),
                        Statistics.moneyFormat.format(pot));
                        
        
    }
    
    
    
    public static int updatePlayerBet(Map<String, Integer> playerBets, String playerName, int playerBet, int pot) 
    {
        if (playerBets.containsKey(playerName)) {
            pot -= playerBets.get(playerName);
        }
        
        pot += playerBet;
        
        playerBets.put(playerName, playerBet);
        
        log.debug("Update player {} with bet {} in pot {}",  playerName, playerBet, pot);
        
        return pot;
    }
    
    public void addAction(PlayerAction action)
    {
        actions.add(action);
        playerPosToActions.get(currentPlayer).add(actions.size()-1);
    }
    
    public int getCurrentBet(String playerName)
    {
        Integer bet = playerBets.get(playerName);
            
        if (bet == null)
            return 0;
        
        
        return bet;
    }
    
    public int getAllInBet(String playerName)
    {
        Integer bet = allInMinimum.get(playerName);
        
        if (bet == null)
            return 0;
        
        return bet;
    }
    
    
    
    private ParserListener getNextStateAfterPreflop(boolean replayline) {
        log.debug("Preflop fini : Tous les joueurs pris en compte");
        List<String> playersInOrder = Lists.newArrayList();
        
        //add sb
        playerSB = players.get(players.size()-2);
        playerBB = players.get(players.size()-1);
        
        if (BooleanUtils.isNotTrue(hasFolded.get(playerSB)) &&  !allInMinimum.containsKey(playerSB)) {
            log.debug("Adding small blind {}", playerSB);
            playersInOrder.add(playerSB);
        } else {
            //Adjust pot if necessary
            
            if (getCurrentBet(playerSB) == 0)
            {
                log.debug("Adding small blind from player {}  bet {} tablestakes {}", playerSB, tableStakes/2, tableStakes);
                pot = updatePlayerBet(playerBets, playerSB, tableStakes / 2, pot);
            }
        }
        
        if (BooleanUtils.isNotTrue(hasFolded.get(playerBB)) &&  !allInMinimum.containsKey(playerBB)) {
            log.debug("Adding big blind {}", playerBB);
            playersInOrder.add(playerBB);
        } else {
            if (getCurrentBet(playerBB) == 0)
            {
                log.debug("Adding big blinds to pot");
                pot = updatePlayerBet(playerBets, playerBB, tableStakes, pot);
            }
        }
        
        for(int i = 0; i < players.size() - 2; ++i) {
            String playerName = players.get(i);
            if (BooleanUtils.isNotTrue(hasFolded.get(playerName))
                    &&  !allInMinimum.containsKey(playerName)
                    ) {
                log.debug("Adding player {}", playerName);
                playersInOrder.add(playerName);
            }
        }
        
        FlopTurnRiverState ftrState = new FlopTurnRiverState(playersInOrder,
                pot, replayline, 1, masterList, lineNumber,roundStates);
        
        return ftrState;
    }
    
    private ParserListener getNextState(boolean replayLine) {
        log.debug("Tous les joueurs pris en compte");
        
        if (round == 0) {
            return getNextStateAfterPreflop(replayLine);
        } 
        
        if (round == 3) {
            return null;
        }
        List<String> playersInOrder = Lists.newArrayList();
        
        for(int i = 0; i < players.size() ; ++i) {
            String playerName = players.get(i);
            if (BooleanUtils.isNotTrue(hasFolded.get(playerName))
                    && !allInMinimum.containsKey(playerName)
                    
                    ) {
                log.debug("Adding player {}", playerName);
                playersInOrder.add(playerName);
            }
        }
        
        FlopTurnRiverState ftrState = new FlopTurnRiverState(playersInOrder, 
                pot, replayLine, 1+round, masterList, lineNumber, roundStates);
        
        return ftrState;
    }

    private boolean potsGood() 
    {
        for(int i = 0; i < players.size(); ++i)
        {
            String playerName = players.get(i);
            if (BooleanUtils.isTrue(hasFolded.get(playerName))) {
                continue;
            }
            
            Integer playerBet = playerBets.get(playerName);
            
            if (playerBet == null) 
            {
                log.debug("Pot is not good.  Player {} idx {} has not acted",
                        playerName, i);
                return false;
            }
            
            if (!allInMinimum.containsKey(playerName) && playerBet < amtToCall)
            {
                log.debug("Pot is not good.  Player {} idx {} bet only {}",
                        playerName, i, playerBet);
                
                return false;
            }
            
            Preconditions.checkState(allInMinimum.containsKey(playerName) || amtToCall == playerBet, "Player %s amtToCall %s  bet %s", playerName, amtToCall, playerBet);
        }
        
        return true;
    }

    
    /*
     * Position currentPlayer au joeur courant
     * 
     * Post condition : currentPlayer positionné à playerName
     */
    private boolean incrementPlayer(String playerName)
    {
        if (!players.contains(playerName))
        {
            log.debug("Player [{}] ajouté avec index {} ", playerName, players.size());
            players.add(playerName);
            
            playerPosToActions.add( new ArrayList<Integer>() );
            currentPlayer = players.size() - 1;
            return false;
        }
              
        
        int loopCheck = 0;
        
        
        while(true)
        {
            ++loopCheck;
            
            if (loopCheck > 15) {
                Preconditions.checkState(false, "Loop all players folded");
            }
            ++currentPlayer;
            if (currentPlayer == players.size()) {
                currentPlayer = 0;
            }
            
            String loopPlayerName = players.get(currentPlayer);
            if (BooleanUtils.isNotTrue(hasFolded.get(loopPlayerName))
                    && !allInMinimum.containsKey(loopPlayerName)
                    ) {
                Preconditions.checkState(loopPlayerName.equals(playerName), "Player name " + playerName + " cur " + currentPlayer + " " + loopPlayerName);
                
                return true;
            }
        }
    }
    
    
    
    private void printHandHistory(String action)
    {
        printHandHistory(action, 0);
    }
    private void printHandHistory(String action, int raiseAmt)
    {
        
        logOutput.debug("** Player {} position {} Action [< {} >]", 
                players.get(currentPlayer), 1+currentPlayer, action);
        
        Integer playerBet = playerBets.get(players.get(currentPlayer)); 
        if (playerBet == null)
            playerBet = 0;
        
        if (round >= 0 && amtToCall > playerBet)
        {
            int diff = amtToCall - playerBet;
            double perc = 100.0 * diff / (pot + diff);
            double ratio = pot * 1.0 / diff; 
            
            double outsOne = perc / 2;
            
           // double betSizeToPot = 1.0 * diff / pot;
            //% must be ahead
           // double callBluff = 100*betSizeToPot / (1+betSizeToPot);
            
            
            logOutput.debug("Amount to call ${} for pot ${}.\n  Pot ratio (bluff catching) : {}%  | 1 to {} | {} outs", 
                    Statistics.moneyFormat.format(diff), Statistics.moneyFormat.format(pot),
                    Statistics.df2.format(perc), Statistics.df2.format(ratio), Statistics.df2.format(outsOne));
          //  logOutput.debug("Must be ahead {}% of the time to call a bluff", 
              //      Statistics.df2.format(callBluff));
        }
        
        if (round >= 0 && raiseAmt > playerBet)
        {
            int diff = raiseAmt - amtToCall;
            double betSizeToPot = 1.0 * diff / pot;
            double bluff = 100.0*(betSizeToPot) / (1+betSizeToPot);
            
            logOutput.debug("Raise amt ${} | %{} of pot " +
            		"\nbluff % chance everyone must fold {}%",
                    Statistics.moneyFormat.format(diff),
                    Statistics.df2.format(100*betSizeToPot),
                     Statistics.df2.format(bluff));
        }
        
        logOutput.debug("\n");
    }

    private void changeLastTapisAction(PlayerAction.Action pAction, boolean clearLastTapisPlayer)
    {
        int tapisPos = players.indexOf(lastTapisPlayer);
        List<Integer> actionIndexes = playerPosToActions.get(tapisPos);
        PlayerAction tapisAction = actions.get(actionIndexes.get(actionIndexes.size()-1));
        
        Preconditions.checkState(tapisAction.action == Action.ALL_IN || tapisAction.action == Action.RAISE_ALL_IN);
        
        tapisAction.action = pAction;
        
        if (clearLastTapisPlayer)
            lastTapisPlayer = null;
    }
    @Override
    public ParserListener handleSuivi(String playerName, int betAmt)
    {
        log.debug("Suivi player {} bet {} ; pot {}", playerName, betAmt, pot);
        
        if (round == 0 && amtToCall == 0) 
        {
            //Set table stakes
            amtToCall = betAmt;
            log.debug("Table mise à {}", amtToCall);
            tableStakes = amtToCall;
        } else {
            
            //Check if there is an unknown tapis we can deduce
            if (lastTapisPlayer != null)
            {
                if (betAmt < amtToCall)
                {
                    log.debug("Last tapis was a call");
                    changeLastTapisAction( Action.CALL_ALL_IN, true );
                    amtToCall = betAmt;
                } else {
                    int diff = betAmt - amtToCall;
                    
                    int tapisGuess = getAllInBet(lastTapisPlayer);
                    
                    //+1 was added in handleTapis to make it act like a raise
                    amtToCall = tapisGuess + diff + 1;
                    log.debug("Adjusting tapis guess to {}", tapisGuess + diff);
                    
                    pot = updatePlayerBet(playerBets, lastTapisPlayer, tapisGuess+ diff, pot);
                    
                    allInMinimum.put(lastTapisPlayer, amtToCall);
                    
                    
                    
                    changeLastTapisAction( Action.RAISE_ALL_IN, true );
                }
                
                
                
            }
            
            //Check internal state
            Preconditions.checkState(amtToCall > 0); 
            Preconditions.checkState(betAmt == amtToCall, "Bet amount %s does not equal incoming amount to call %s", betAmt, amtToCall);
        }
        
        
        boolean seenPlayer = incrementPlayer(playerName);
        printHandHistory("Call $" + Statistics.moneyFormat.format(betAmt));
        
        PlayerAction action = PlayerAction.createCall(currentPlayer, playerName, betAmt, pot);
        addAction(action);
        
        pot = updatePlayerBet(playerBets, playerName, betAmt, pot);
        
        Preconditions.checkState(players.contains(playerName));
        
        
        if (seenPlayer && potsGood() && round < 3) 
        {
            return getNextState(false);
        }
    
        return this;
    }

    @Override
    public ParserListener handleRelance(String playerName, int betAmt)
    {
      //Le prochain round a commencé
        if (round == 0 && 
                players.contains(playerName) && 
                potsGood())
        {
            return getNextState(true);
        }
        
        Preconditions.checkState(betAmt > amtToCall, "Player %s betAmt %s amtToCall %s", playerName, betAmt, amtToCall );
        
        if (roundInitialBetter == null) {
            roundInitialBetter = playerName;
           
            
            Preconditions.checkState(round == 0 || amtToCall == 0);
            
            int raiseAmt = betAmt - amtToCall;
            double potRatio = 1.0 * raiseAmt / pot;
            
            log.debug("Player %{} bet %{} of pot", playerName, Statistics.df2.format(potRatio));
            betToPotSize.put(playerName, potRatio);
            
        } 
        
        if (round == 0 && tableStakes <= 0)
        {
            //we have to guess what the stakes were
            tableStakes = 1;
        }
        
        //Check if there is an unknown tapis we can only guess their bet is less than the current amount
        if (lastTapisPlayer != null)
        {
            int tapisGuess = getAllInBet(lastTapisPlayer);
            
            log.debug("Reraise after tapis, no real guess possible {}", tapisGuess );
            
            
            int tapisPos = players.indexOf(lastTapisPlayer);
            List<Integer> actionIndexes = playerPosToActions.get(tapisPos);
            PlayerAction tapisAction = actions.get(actionIndexes.get(actionIndexes.size()-1));
            
            Preconditions.checkState(tapisAction.action == Action.ALL_IN || tapisAction.action == Action.RAISE_ALL_IN);
            if (tapisAction.action == Action.ALL_IN)
            {
                log.debug("All in guessed to be a call");
                tapisAction.action = Action.CALL_ALL_IN;
            }
            
            lastTapisPlayer = null;
            
        }
        
        incrementPlayer(playerName);
        printHandHistory("Raise $" + Statistics.moneyFormat.format(betAmt), betAmt);
        
        PlayerAction action = PlayerAction.createReraise(currentPlayer, playerName, amtToCall, betAmt, pot);
        addAction(action);
        
        amtToCall = betAmt;
        
        pot = updatePlayerBet(playerBets, playerName, betAmt, pot);
        
        return this;
    }

    @Override
    public ParserListener handleCoucher(String playerName)
    {
        //Preconditions.checkState(players.contains(playerName));
        
        //Il est possible le flop a commencé avec un joeur qui se couche
        if (round == 0 && players.contains(playerName) && potsGood())
        {
            return getNextState(true);
        }
        
        //Stats
        final int playerBet = getCurrentBet(playerName);
        int raiseAmt = amtToCall - playerBet;
        
        log.debug("Fold raiseAmt {} amtToCall {} player bet {}", raiseAmt, amtToCall, playerBet);
        
        if ( 1 == raiseAmt && lastTapisPlayer != null)
        {
            log.debug("{} se couche une relance, alors le dernier tapis était une relance.");
            
            changeLastTapisAction( Action.RAISE_ALL_IN, false );
        }
        
        double potRatio = pot > 0 ? 1.0 * raiseAmt / pot : 0;
        
        Preconditions.checkArgument(potRatio >= 0, "Amttocall %s  current bet %s", amtToCall, getCurrentBet(playerName));
        
        foldToBetSize.put(playerName, potRatio);
        
        
        
        
        boolean seenPlayer = incrementPlayer(playerName);
        printHandHistory("Fold");
        
        addAction(PlayerAction.createFold(currentPlayer, playerName, amtToCall, pot));
        
        hasFoldedArr[currentPlayer] = true;
        hasFolded.put(playerName, true);
        
        //Want to process winning line
        if (round < 3 && seenPlayer && potsGood()) 
        {
            return getNextState(false);
        }
        
        return this;
    }

    @Override
    public ParserListener handleParole(String playerName)
    {
        /*
        if (playerBets.containsKey(playerName)) 
        {
            log.warn("Next round appears to have started; maybe a player left?");
            return null;
        }*/
        //Preconditions.checkState(!playerBets.containsKey(playerName));
        
        
        
        
        //Le prochain round a commencé
        if (round == 0 && players.contains(playerName) && potsGood())
        {
            return getNextState(true);
        }
        
        //If there is a check after a tapis we assume the next round started
        if (lastTapisPlayer != null)
        {
            log.debug("Parole après une tapis, le dernier tapis était un suivi", playerName);
            allInMinimum.put(lastTapisPlayer, getAllInBet(lastTapisPlayer) - 1);
            
            int tapisPos = players.indexOf(lastTapisPlayer);
            List<Integer> actionIndexes = playerPosToActions.get(tapisPos);
            PlayerAction tapisAction = actions.get(actionIndexes.get(actionIndexes.size()-1));
            
            Preconditions.checkState(tapisAction.action == Action.ALL_IN);
            
            tapisAction.action = Action.CALL_ALL_IN;
            
            return getNextState(true);
        }
        
        Preconditions.checkState(amtToCall == 0);
        
        
      //  boolean seenPlayer = 
        incrementPlayer(playerName);
        printHandHistory("Check");
        
        addAction(PlayerAction.createCheck(currentPlayer, playerName, pot));
        
        playerBets.put(playerName, 0);
        
        //We exclude the river since we want to parse the last line showdown
        if (round < 3 && potsGood())
        {
            //La ligne actuel était prise en compte
            return getNextState(false);
        }
        
        return this;
    }

    @Override
    public ParserListener handleTapis(String playerName)
    {
      //Tapis est un cas diffil car on ne sais pas si c'est un relancement ou pas ; aussi on ne sais plus le pot
        log.debug("{} Tapis", playerName);
        
        //Le prochain round a commencé
        if (round == 0 && 
                players.contains(playerName) && 
                potsGood())
        {
            return getNextState(true);
        }
        
        //If the user has less than the buy in this will be wrong but that's rare
        if (roundInitialBetter == null) {
            roundInitialBetter = playerName;
        }
        
        if (round == 0 && tableStakes <= 0)
        {
            //we have to guess what the stakes were
            tableStakes = 1;
        }
                
        
        incrementPlayer(playerName);
        printHandHistory("All in for unknown amount");
        
        if (amtToCall == 0)
        {
            log.debug("All was a bet, no previous bets");
            addAction(PlayerAction.createBetAllin(currentPlayer, playerName, amtToCall, pot));
                        
        } else {
            log.debug("All in, unknown, was a previous bet");
            addAction(PlayerAction.createAllin(currentPlayer, playerName, amtToCall, pot));
            
            
        }
        
        allInMinimum.put(playerName, amtToCall);
        
      //Assume it was  a raise
        amtToCall += 1;
        
        //Guess it was a raise
        //amtToCall = 1+getAllInBet(playerName);
        
        pot = updatePlayerBet(playerBets, playerName, amtToCall, pot);
        lastTapisPlayer = playerName;
        
        return this;
    }

    @Override
    public ParserListener handleShowdown(String playerName, int finalPot)
    {
        if (finalPot != pot ) {
            log.warn("Final pot calculated as {} but is {}", pot, finalPot);
        }
        logOutput.debug("{} wins showdown with pot ${}", playerName, 
                Statistics.moneyFormat.format(finalPot));
        
        this.masterList.add(this.roundStates);
        
        return null;
    }

    @Override
    public ParserListener handleGagne(String playerName)
    {
        log.debug("{} gagne", playerName);
        
        //To make sure the player list is accurate
        if (!players.contains(playerName))
        {
            log.debug("Player [{}] ajouté avec index {} ", playerName, players.size());
            players.add(playerName);
            currentPlayer = players.size() - 1;
            
            playerPosToActions.add( new ArrayList<Integer>() );
        }
        
        this.masterList.add(this.roundStates);
        return null;
    }

    @Override
    public boolean replayLine()
    {
        if (replayLine) {
            replayLine = false;
            return true;
        }
        return false;
    }
}
