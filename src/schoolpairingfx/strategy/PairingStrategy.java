

package schoolpairingfx.strategy;

import java.util.List;
import model.Player;
import model.PairingType;

/**
 *
 * @author Mark Dechamps
 */
public interface PairingStrategy {

    public List<Player> orderRanking(List<Player> unmodifiableList);

    public List<Player> sortPlayersFromLowToHigh(List<Player> players);
    public List<Player> sortPlayersFromHighToLow(List<Player> players);

   // public boolean playersMayPlay(Player player1, Player player2);

    public boolean areYPlacesSeparatedInRanking(Player player1, Player player2);

    public PairingType getType();

}
