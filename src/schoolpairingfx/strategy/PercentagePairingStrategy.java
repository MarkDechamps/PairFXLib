/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Player;
import model.Model;
import model.PairingType;

/**
 *
 * @author Mark Dechamps
 */
 class PercentagePairingStrategy implements PairingStrategy {
    private int maxPercentageDifference;
    public PercentagePairingStrategy(int maxPercentageDifference) {
        this.maxPercentageDifference=maxPercentageDifference;
    }

    public void setMaxPercentageDifference(int maxPercentageDifference) {
        this.maxPercentageDifference = maxPercentageDifference;
    }

    public int getMaxPercentageDifference() {
        return maxPercentageDifference;
    }

    
     
     
    @Override
    public List<Player> orderRanking(List<Player> playerList) {
        List<Player> result = new ArrayList<>();
        result.addAll(playerList);

        Collections.sort(result, new PlayerComparatorHighToLowPercentage());
        return Collections.unmodifiableList(result);

    }

    @Override
    public List<Player> sortPlayersFromLowToHigh(List<Player> players) {
        List result = new ArrayList();
        result.addAll(players);
        Collections.sort(result, new PlayerComparatorLowToHighByPercentage());
        return result;
    }

    @Override
    public List<Player> sortPlayersFromHighToLow(List<Player> players) {
         List result = new ArrayList();
        result.addAll(players);
        Collections.sort(result, new PlayerComparatorHighToLowPercentage());
        return result;
    }
    
     @Override
    public boolean areYPlacesSeparatedInRanking(Player player1, Player player2) {
         Model model = Model.getInstance();
         double percentageP1=model.percentage(player1);
         double percentageP2=model.percentage(player2);
         
         return Math.abs(percentageP1 - percentageP2) >= maxPercentageDifference;
    }

    @Override
    public PairingType getType() {
       return PairingType.PERCENTAGE;
    }
    
}
