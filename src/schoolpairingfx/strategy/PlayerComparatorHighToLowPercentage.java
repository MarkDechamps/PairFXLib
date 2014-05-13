/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import java.util.Comparator;
import model.Player;
import model.Model;
import model.Model;

/**
 *
 * @author Mark Dechamps
 */
 class PlayerComparatorHighToLowPercentage implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2) {
        Model model = Model.getInstance();
        int percentagePlayer1= model.percentage(player1);
        int percentagePlayer2 =model.percentage(player2);
        
        if(percentagePlayer1==percentagePlayer2){
            return player2.getPoints()-player1.getPoints();
        }
        
        return percentagePlayer2-percentagePlayer1;
    }
}
