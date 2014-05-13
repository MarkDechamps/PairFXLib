/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Dechamps
 */
public class PFXUtil {

    public static final Logger log = Logger.getAnonymousLogger();

    public static void log(String logMe) {
        log.log(Level.INFO, logMe);
    }

    public static boolean isEmpty(String checkMe) {
        return !notEmpty(checkMe);
    }

    public static boolean notEmpty(String checkMe) {
        return checkMe != null && checkMe.length() > 0 && checkMe.trim().length() > 0;
    }

    public static boolean isInt(String anInt) {
        try {
            Integer.parseInt(anInt);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String aDouble) {
        try {
            Double.parseDouble(aDouble);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
      public static boolean isNull(Object checkMe) {
        return checkMe == null;
    }

    public static boolean notNull(Object checkMe) {
        return !isNull(checkMe);
    }
}
