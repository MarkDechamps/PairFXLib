/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import java.util.Comparator;
import java.util.Date;
import model.Pairing;

/**
 *
 * @author Mark Dechamps
 */
public class PairingComparator implements Comparator<Pairing> {

    @Override
    public int compare(Pairing o1, Pairing o2) {
        final Date creationDate1 = o1.getCreationDate();
        final Date creationDate2 = o2.getCreationDate();
        if (creationDate1 == null) {
            return -1;
        }
        if (creationDate2 == null) {
            return 1;
        }
        return -1*creationDate1.compareTo(creationDate2);
    }
}
