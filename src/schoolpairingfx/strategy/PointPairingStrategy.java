/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import model.Pairing;
import model.Player;
import model.PairingType;

/**
 *
 * @author Mark Dechamps
 */
class PointPairingStrategy implements PairingStrategy {

    int maxPoints;

    public PointPairingStrategy(int maxPoints) {
        this.maxPoints=maxPoints;
    }
    
    public void setMaxPointDifference(int maxPoints){
     
      this.maxPoints=maxPoints;
    }
    
    public int getMaxPointDifference(){
        return maxPoints;
    }
    
    @Override
    public List<Player> orderRanking(List<Player> playerList) {
        List result = new ArrayList();
        result.addAll(playerList);

        Collections.sort(result, new PlayerComparatorHighToLowByPoints());
        return result;

    }

    @Override
    public List<Player> sortPlayersFromLowToHigh(List<Player> players) {
        List result = new ArrayList();
        result.addAll(players);
        Collections.sort(result, new PlayerComparatorLowToHighByPoints());
        return result;
    }

    @Override
    public List<Player> sortPlayersFromHighToLow(List<Player> players) {
        List result = new ArrayList();
        result.addAll(players);
        Collections.sort(result, new PlayerComparatorHighToLowByPoints());
        return result;
    }

    @Override
    public boolean areYPlacesSeparatedInRanking(Player player1, Player player2) {
         return Math.abs(player1.getPoints() - player2.getPoints()) >= maxPoints;
    }

    @Override
    public PairingType getType() {
      return PairingType.POINTS;
    }

   

  
}
