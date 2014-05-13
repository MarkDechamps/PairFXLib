/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import java.util.Comparator;
import model.Player;
import model.Model;

/**
 *
 * @author Mark Dechamps
 */
 class PlayerComparatorLowToHighByPercentage implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2) {
        //return arg1.getPoints() - arg0.getPoints();
        int percentagePlayer1= Model.getInstance().percentage(player1);
        int percentagePlayer2 =Model.getInstance().percentage(player2);
        
         if(percentagePlayer1==percentagePlayer2){
            return player1.getPoints()-player2.getPoints();
        }
        return percentagePlayer1-percentagePlayer2;
    }
}
