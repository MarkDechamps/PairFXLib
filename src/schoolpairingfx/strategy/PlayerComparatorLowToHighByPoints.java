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
public class PlayerComparatorLowToHighByPoints implements Comparator<Player>{

    @Override
    public int compare(Player player1, Player player2) {
       Model model = Model.getInstance();
        int pointsPlayer1= player1.getPoints();
        int pointsPlayer2 =player2.getPoints();
        
        if(pointsPlayer1==pointsPlayer2){
            return model.percentage(player1)-model.percentage(player2);
        }
        
        return pointsPlayer1-pointsPlayer2;
    }

}
