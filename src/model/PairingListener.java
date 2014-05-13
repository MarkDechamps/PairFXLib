package model;

import java.util.List;

public interface PairingListener {

	void pairingFailed(Player player1, Player player2,
			boolean alreadyPlayedEachOther, boolean areYPlacesSeparated);
       
}
