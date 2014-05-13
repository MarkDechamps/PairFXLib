/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolpairingfx.strategy;

import util.PFXUtil;

/**
 *
 * @author Mark Dechamps
 */
public class PairingStrategyFactory {

    private static final int INIT_MAX_POINT_DIFF = 30;
    private static final int INIT_MAX_PERC_DIFF = 60;
    private static int maxDifferenceInPoints = INIT_MAX_POINT_DIFF, maxDifferenceInPercentage = INIT_MAX_PERC_DIFF;
    private static PointPairingStrategy points = new PointPairingStrategy(30);
    private static PercentagePairingStrategy percentage = new PercentagePairingStrategy(60);

    public static PairingStrategy createPointStrategy() {
        return points;
    }

    public static PairingStrategy createPercentageStrategy() {
        return percentage;
    }

    public static int getMaxDifferenceInPoints() {
        return maxDifferenceInPoints;
    }

    public static void setMaxDifferenceInPoints(int maxDifferenceInPoints) {
        PFXUtil.log("Setting maxpoints to " + maxDifferenceInPoints);
        PairingStrategyFactory.maxDifferenceInPoints = maxDifferenceInPoints;
        points.setMaxPointDifference(maxDifferenceInPoints);
    }

    public static int getMaxDifferenceInPercentage() {
        return maxDifferenceInPercentage;
    }

    public static void setMaxDifferenceInPercentage(int maxDifferenceInPercentage) {
        PFXUtil.log("Setting maxpercentage to " + maxDifferenceInPercentage);
        PairingStrategyFactory.maxDifferenceInPercentage = maxDifferenceInPercentage;
        percentage.setMaxPercentageDifference(maxDifferenceInPercentage);
    }
}
