/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.core.Persister;
import util.PFXUtil;

/**
 *
 * @author Mark Dechamps
 */
@Root
class SchoolPairingPersister {

    @ElementList
    private List<Player> players = new ArrayList<>();
    @ElementList
    private List<Pairing> pairings = new ArrayList<>();
    @Transient
    private File file;
    @Element(name = "aantalRondenDatSpelersNietTegenElkaarMogenGespeeldHebbenVoorParing", required = false)
    private int nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain;
    @Element(name = "aantalPuntenDatSpelersMaxMogenVerschillenVoorParing", required = false)
    private int nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay;
    @Element(name = "aantalPercentDatSpelersMaxMogenVerschillenVoorParing", required = false)
    private int maxDifferenceInPercentage;
    @Element(name = "paringsType", required = false)
    private PairingType pairingsType = PairingType.PERCENTAGE;
    private static final String SYS_SCHOOL_NR_ROUNDS_NOT_PLAYED = "school_nr_rounds_not_played";
    private static final String SYS_SCHOOL_MAX_POINTS_BETWEEN_PLAYERS = "school_maxpoints_between";
    private static final String SYS_SCHOOL_MAX_PERCENTAGE_BETWEEN_PLAYERS = "school_maxpercentage_between";

    public SchoolPairingPersister() {
        init();
    }

    public SchoolPairingPersister(File file) {
        this.file = file;

    }

    private void init() {
        Integer nr = Integer.getInteger(SYS_SCHOOL_NR_ROUNDS_NOT_PLAYED);
        Integer maxPoints = Integer.getInteger(SYS_SCHOOL_MAX_POINTS_BETWEEN_PLAYERS);
        Integer maxPercentage = Integer.getInteger(SYS_SCHOOL_MAX_PERCENTAGE_BETWEEN_PLAYERS);
        if (nr != null) {
            PFXUtil.log("Initializing nr rounds players may not have played (for NEW tournaments) each other for pairing to be possible to " + nr);
            nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain = nr;
        }
        if (maxPoints != null) {
            PFXUtil.log("Initializing max nr points players may be separated (for NEW tournaments) for pairing to be possible to " + maxPoints);
            nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay = maxPoints;
        }
        if (maxPercentage != null) {
            PFXUtil.log("Initializing max % players may be separated (for NEW tournaments) for pairing to be possible to " + maxPercentage);
            maxDifferenceInPercentage = maxPercentage;
        }

    }

    void setFile(File file) {
        this.file = file;
    }

    void setPlayers(List<Player> players) {
        if (players != null) {
            this.players = new ArrayList<>();
            this.players.addAll(players);
        }
    }

    void setPairings(List<Pairing> pairings) {
        if (pairings != null) {
            this.pairings = new ArrayList<>();
            this.pairings.addAll(pairings);
        }
    }

    public int getNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain() {
        return nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain;
    }

    public void setNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain(int nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain) {
        this.nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain = nrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain;
    }

    public int getNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay() {
        return nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay;
    }

    public void setNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay(int nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay) {
        this.nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay = nrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay;
    }

    public void setPairingsType(PairingType pairingType) {
        //this.pairingsType=pairingType.name();
        this.pairingsType = pairingType;
    }

    public PairingType getPairingsType() {
        //return PairingType.valueOf(pairingsType);
        return pairingsType;
    }

    List<Pairing> getPairings() {
        return pairings;
    }

    List<Player> getPlayers() {
        return players;
    }

    void load() throws Exception {
        Serializer serializer = new Persister();
        if (file.exists()) {
            SchoolPairingPersister model = serializer.read(SchoolPairingPersister.class, file);

            Set<Player> playerSet = new HashSet<>();

            for (Pairing p : model.getPairings()) {
                playerSet.add(p.getBlack());
                playerSet.add(p.getWhite());
            }
            playerSet.addAll(model.getPlayers());
            setPairings(model.getPairings());
            List<Player> playerList = new ArrayList<>();
            playerList.addAll(playerSet);
            setPlayers(playerList);
            setPairingsType(model.getPairingsType());
            setMaximumDifferenceInPercentage(model.getMaximumDifferenceInPercentage());
            setNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay(model.getNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay());
            setNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain(model.getNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain());

        }
    }

    void save() throws Exception {
        Serializer serializer = new Persister();
        serializer.write(this, file);
    }

    public void setMaximumDifferenceInPercentage(int maxDifferenceInPercentage) {
        this.maxDifferenceInPercentage = maxDifferenceInPercentage;
    }

    public int getMaximumDifferenceInPercentage() {
        return maxDifferenceInPercentage;
    }
}
