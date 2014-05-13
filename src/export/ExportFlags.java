/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

/**
 *
 * @author Mark Dechamps
 */
public class ExportFlags {

    public boolean includeTable = true;
    public boolean includeRankingByPercentage = true;
    public boolean includeRankingByPoints = true;
    public boolean includeActivePairings;
    public boolean includeFinishedPairings;
    public boolean latestPairings;
    public boolean autoRefresh = false;

    public void clearAll() {
        includeTable = false;
        includeRankingByPercentage = false;
        includeRankingByPoints = false;
        includeActivePairings = false;
        includeFinishedPairings = false;
        latestPairings = false;
        autoRefresh = false;
    }
}
