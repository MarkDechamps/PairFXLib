package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import schoolpairingfx.strategy.PairingComparator;
import schoolpairingfx.strategy.PairingStrategy;
import schoolpairingfx.strategy.PairingStrategyFactory;

public class Tournament {

    private List<Player> players = new ArrayList<>();
    private List<Player> selectedForPairing = new ArrayList<>();
    private List<PairingListener> listeners = new ArrayList<>();
    private int nrOfROundsPlayersMayNotHavePlayed = 3;
    private String lastMsg = "";
    private List<Pairing> allPairings = new ArrayList<>();
    private PairingStrategy strategy;

    public Tournament() {
        strategy = PairingStrategyFactory.createPercentageStrategy();
    }

    public void addListener(PairingListener pl) {
        listeners.add(pl);
    }

    public void removeListener(PairingListener pl) {
        listeners.remove(pl);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> players() {
        return Collections.unmodifiableList(players);
    }

    public List<Pairing> pairWithSelectedPlayers() {
         List<Pairing> pairings;
        //if only 2 players are selected, pairing should always work.
        if(selectedForPairing.size()==2){
            Pairing pairing = createPairing(selectedForPairing.get(0), selectedForPairing.get(1));
            pairings = new ArrayList<>();
            pairings.add(pairing);
           
        }else{
           pairings = makePairingWithPlayers(selectedForPairing);
        }
        
        allPairings.addAll(pairings);                
        selectedForPairing.clear();
        return pairings;
    }

    public void selectPlayerForPairing(Player toSelect) {
        if (players.contains(toSelect)) {
            selectedForPairing.add(toSelect);
        }
    }

    public void selectPlayersForPairing(Collection<Player> toSelect) {
        for (Player p : toSelect) {
            selectPlayerForPairing(p);
        }
    }

    public void unselectPlayerForPairing(Player toUnselect) {
        if (selectedForPairing.contains(toUnselect)) {
            selectedForPairing.remove(toUnselect);
        }
    }

    List<Pairing> makePairingWithPlayers(List<Player> players) {
        List<Player> sorted = strategy.sortPlayersFromLowToHigh(players);
        List<Pairing> result = new ArrayList<>();
        List<Player> alreadyProcessed = new ArrayList<>();

        for (Player player1 : sorted) {
            for (Player player2 : sorted) {
                if (skipPlayer(player1, player2, alreadyProcessed)) {
                    continue;
                }

                if (playersMayPlay(player1, player2)) {
                    Pairing pairing = createPairing(player1, player2);

                    result.add(pairing);
                    alreadyProcessed.add(player1);
                    alreadyProcessed.add(player2);
                }
            }
        }
        return result;
    }

    static boolean skipPlayer(Player player1, Player player2,
            List<Player> alreadyProcessed) {
        if (player1 == player2) {
            return true;
        }

        if (alreadyProcessed.contains(player1)) {
            return true;
        }
        if (alreadyProcessed.contains(player2)) {
            return true;
        }

        return false;
    }

    private Pairing createPairing(Player player1, Player player2) {
        return PairingImpl.createPairing(player1, player2);
    }

    private void informListeners(Player player1, Player player2,
            boolean alreadyPlayedEachOther, boolean areYPlacesSeparated) {
        for (PairingListener pl : listeners) {
            pl.pairingFailed(player1, player2, alreadyPlayedEachOther,
                    areYPlacesSeparated);
        }

    }

    public boolean playersMayPlay(Player player1, Player player2) {

        boolean didntPlay = didntPlayTheLastXRounds(player1, player2,
                nrOfROundsPlayersMayNotHavePlayed);
        boolean areYPlacesSeparated = strategy.areYPlacesSeparatedInRanking(player1,
                player2);

        if (pairingFailed(didntPlay, areYPlacesSeparated)) {
            buildErrorMessage(player1, player2, didntPlay, areYPlacesSeparated);
            informListeners(player1, player2, !didntPlay, areYPlacesSeparated);
        }
        return didntPlay && !areYPlacesSeparated;

    }

    private boolean pairingFailed(boolean didntPlay, boolean areYPlacesSeparated) {
        return !didntPlay || areYPlacesSeparated;
    }

    private void buildErrorMessage(Player player1, Player player2,
            boolean didntPlay, boolean areYPlacesSeparated) {
        String didntPlayString = "didntPlayLast "
                + nrOfROundsPlayersMayNotHavePlayed + " rounds?:" + didntPlay;
        String areSeparated = "Are players to much separated in ranking?:" + areYPlacesSeparated;
        lastMsg = player1.toString() + " and " + player2.toString() + " "
                + didntPlayString + " || " + areSeparated;
    }

    boolean didntPlayTheLastXRounds(Player player1, Player player2, int maxNrRounds) {
        List<Pairing> sortedByDateP1 = getAllPairingsByAscendingDateFor(player1, allPairings);
        List<Pairing> sortedByDateP2 = getAllPairingsByAscendingDateFor(player2, allPairings);

        boolean notOkForP1 = hasPairingAgainstPlayerLastXRounds(sortedByDateP1, maxNrRounds, player2);
        boolean notOkForP2 = hasPairingAgainstPlayerLastXRounds(sortedByDateP2, maxNrRounds, player1);

        if (notOkForP1 || notOkForP2) {
            return false;
        }
        return true;
    }

    private List<Pairing> getAllPairingsByAscendingDateFor(Player player,
            List<Pairing> pairings) {
        Map<Date, Pairing> resultMap = new HashMap<>();
        //all pairings for this player
        for (Pairing pairing : pairings) {
            boolean pairingIsOk = pairing.containsPlayer(player);
            if (pairingIsOk) {
                resultMap.put(pairing.getCreationDate(), pairing);
            }
        }

        List<Pairing> result = new ArrayList<>();
        if (resultMap.isEmpty()) {
            return result;
        }
        SortedSet<Date> keys = new TreeSet<>(resultMap.keySet());
        for (Date key : keys) {
            Pairing value = resultMap.get(key);
            result.add(value);
        }
        return result;
    }

    public List<Player> selectedPlayersForPairing() {
        return Collections.unmodifiableList(selectedForPairing);
    }

    public int getNrOfROundsPlayersMayNotHavePlayed() {
        return nrOfROundsPlayersMayNotHavePlayed;
    }

    public void setNrOfROundsPlayersMayNotHavePlayed(
            int nrOfROundsPlayersMayNotHavePlayed) {
        this.nrOfROundsPlayersMayNotHavePlayed = nrOfROundsPlayersMayNotHavePlayed;
    }

    public int getMaxDifferenceInPoints() {
        return PairingStrategyFactory.getMaxDifferenceInPoints();
    }

    public void setMaxDifferenceInPoints(int maxDifferenceInPoints) {
        PairingStrategyFactory.setMaxDifferenceInPoints(maxDifferenceInPoints);
    }

    public int getMaxDifferenceInPercentage() {
        return PairingStrategyFactory.getMaxDifferenceInPercentage();
    }

    public void setMaxDifferenceInPercentage(int maxDifferenceInPercentage) {
        PairingStrategyFactory.setMaxDifferenceInPercentage(maxDifferenceInPercentage);
    }

    public String lastError() {
        return lastMsg;
    }

    public void unselectAllPlayersForPairing() {
        selectedForPairing.clear();
    }

    public List<Player> opponentsFrom(Player player) {
        List<Player> result = new ArrayList<>();
        if (player == null) {
            return result;
        }


        for (Pairing pairing : allPairings) {
            if (pairing.containsPlayer(player)) {
                if (pairing.getWhite().equals(player)) {
                    result.add(pairing.getBlack());
                } else {
                    result.add(pairing.getWhite());
                }
            }
        }
        return result;
    }

    public void removePairing(Pairing pairing) {
        allPairings.remove(pairing);
    }

    public void initializeWith(List<Pairing> pairings) {
        this.allPairings.clear();
        this.allPairings.addAll(pairings);
    }

    public void setPairingStrategy(PairingStrategy pairingStrategy) {
        this.strategy = pairingStrategy;
    }

    public void setPairingStrategyByPercentage() {
        System.out.println("Pairingstrategy: PERCENTAGE");
        setPairingStrategy(PairingStrategyFactory.createPercentageStrategy());
    }

    public void setPairingStrategyByPoints() {
        System.out.println("Pairingstrategy: POINTS");
        setPairingStrategy(PairingStrategyFactory.createPointStrategy());
    }

    public List<Player> getRankingByPoints() {
        return PairingStrategyFactory.createPointStrategy().orderRanking(players);
    }

    public List<Player> getRankingByPercentage() {
        return PairingStrategyFactory.createPercentageStrategy().orderRanking(players);
    }

    public List<Player> ranking() {
        return strategy.orderRanking(players);
    }

    public List<Pairing> getFinishedGamesFor(Player selected) {
        List<Pairing> result = new ArrayList<>();
        for (Pairing pairing : allPairings) {
            if (pairing.isPlayed() && pairing.containsPlayer(selected)) {
                result.add(pairing);
            }
        }
        return result;
    }

    int getNrFinishedGames(Player player) {
        int nrOpponents = opponentsFrom(player).size();
        int nrOngoing = 0;
        for (Pairing p : allPairings) {
            if (p.containsPlayer(player) && p.isPlayed() == false) {
                nrOngoing++;
            }
        }
        int result = nrOpponents - nrOngoing;
        return result;
    }

    public int percentage(Player player) {
        //points is stored * 10 to avoid doubles with draws
        double points = (double) player.getPoints() / 10;
        int totalFinishedGames = getNrFinishedGames(player);
        return calculatePercentage(points, totalFinishedGames);
    }

    int calculatePercentage(double points, int nrGames) {
        if (nrGames == 0) {
            return 0;
        }
        double percentage = points / nrGames;
        int result = (int) Math.round(percentage * 100);
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    public List<Pairing> getAllPairings() {
        //set met comparator van maken?
        Collections.sort(allPairings, new PairingComparator());
        return Collections.unmodifiableList(allPairings);
    }

    public void reloadPairings(List<Pairing> pairings) {
        allPairings.clear();
        allPairings.addAll(pairings);
    }

    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Pairing> unfinishedPairings() {
        List<Pairing> result = new ArrayList<>();

        for (Pairing p : allPairings) {
            if (!p.isPlayed()) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean playerIsAvailable(Player player) {
        for (Pairing paring : allPairings) {
            final boolean playsRightNow = !paring.isPlayed() && paring.containsPlayer(player);
            if (playsRightNow) {
                return false;
            }
        }
        return true;
    }

    public List<Player> getNonPlayingPlayers() {
        List<Player> result = new ArrayList();

        result.addAll(players);
        for (Pairing pairing : unfinishedPairings()) {
            result.remove(pairing.getBlack());
            result.remove(pairing.getWhite());
        }
        List<Player> absentPlayers = new ArrayList<>();        
        for (Player player : result) {
            if (player.isAbsent()) {
              absentPlayers.add(player);  
            }
        }
        result.removeAll(absentPlayers);

        result = strategy.sortPlayersFromHighToLow(result);
        return result;
    }

    public void selectAllAvailablePlayersForPairing() {
        unselectAllPlayersForPairing();
        for (Player player : getNonPlayingPlayers()) {
            toggleSelectionForPairing(player);
        }
    }

    public void toggleSelectionForPairing(Player selectedItem) {
        if (isSelectedForPairing(selectedItem)) {
            unselectPlayerForPairing(selectedItem);
        } else {
            selectPlayerForPairing(selectedItem);
        }
    }

    public boolean isSelectedForPairing(Player player) {
        return selectedForPairing.contains(player);
    }

    public PairingType getPairingsType() {
        return strategy.getType();
    }

    public List<Pairing> getAllFinishedGames() {
        List<Pairing> result = new ArrayList<>();
        for (Pairing p : allPairings) {
            if (p.isPlayed()) {
                result.add(p);
            }
        }
        return result;
    }

    private boolean hasPairingAgainstPlayerLastXRounds(List<Pairing> sortedByDateP1, int maxNrRounds, Player player2) {
        int loopTill;
        if (sortedByDateP1.size() < maxNrRounds) {
            loopTill = sortedByDateP1.size();
        } else {
            loopTill = maxNrRounds;
        }
        for (int i = 0; i < loopTill; i++) {
            Pairing checkMe = sortedByDateP1.get(i);
            if (checkMe.containsPlayer(player2)) {
                return true;
            }
        }
        return false;
    }
}
