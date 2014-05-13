/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import model.Pairing;
import model.Player;

/**
 *
 * @author Mark Dechamps
 */
public class PlayerUtil {

    public static String getPointsString(Player player) {
        String pointsString = "" + player.getPoints() / 10;
        if (player.getPoints() % 10 == 5) {
            pointsString += ".5";
        }
        return pointsString;
    }

    public static String toPairingString(Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getFirstname())
                .append(" ")
                .append(player.getLastname());
        return builder.toString();
    }

    public  static String buildPairingStringFor(Pairing pairing) {
        StringBuilder txt = new StringBuilder(toPairingString(pairing.getWhite()) + " vs " + toPairingString(pairing.getBlack()));
        if (pairing.isPlayed()) {
            txt.append("     ");
            if (pairing.whiteWon()) {
                txt.append("1 - 0");
            } else if (pairing.isDraw()) {
                txt.append("0.5 - 0.5");
            } else {
                txt.append("0 - 1");
            }

        }
        return txt.toString();
    }
}
