/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import util.PFXUtil;

/**
 *
 * @author Mark Dechamps
 */
public class Filter {

    public List<Pairing> filter(List<Pairing> teTonen, String filter) {
        if (PFXUtil.isEmpty(filter)) {
            return teTonen;
        }

        List<Pairing> filtered = new ArrayList<>();
        for (Pairing pairing : teTonen) {
            if (accordingToFilter(pairing, filter)) {
                filtered.add(pairing);
            }
        }
        return filtered;
    }

    private boolean accordingToFilter(Pairing pairing, String filter) {
        Player white = pairing.getWhite();
        Player black = pairing.getBlack();
        boolean blackOk = checkPlayerMatchesFilter(black, filter);
        boolean whiteOk = checkPlayerMatchesFilter(white, filter);
        return blackOk || whiteOk;
    }

    private boolean checkPlayerMatchesFilter(Player player, String filter) {
       
        try {
            //if the filter is numeric, check the id of the player
            int id = Integer.parseInt(filter);
            final int playerId = player.getId();
            return player.hasAnId() && playerId==id;
        }catch(NumberFormatException nfe){            
        }
            
        filter = filter.toLowerCase();
        boolean playerOk = player.getFirstname().toLowerCase().startsWith(filter)
                || player.getLastname().toLowerCase().startsWith(filter);
        return playerOk;
    }
}
