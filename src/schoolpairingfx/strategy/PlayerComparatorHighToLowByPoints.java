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
public class PlayerComparatorHighToLowByPoints implements Comparator<Player>{

    public PlayerComparatorHighToLowByPoints() {
    }
 @Override
    public int compare(Player player1, Player player2) {
        Model model = Model.getInstance();
        int pointsPlayer1= player1.getPoints();
        int pointsPlayer2 =player2.getPoints();
        
        if(pointsPlayer1==pointsPlayer2){
            return model.percentage(player2)-model.percentage(player1);
        }
        
        return pointsPlayer2-pointsPlayer1;
    }
}
