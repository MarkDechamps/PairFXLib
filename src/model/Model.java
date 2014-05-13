/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import export.Export;
import export.ExportFlags;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schoolpairingfx.strategy.PairingStrategyFactory;
import util.PFXUtil;
import util.PlayerUtil;
import winstone.Launcher;

/**
 *
 * @author Mark Dechamps
 */
public class Model {

    private static String TITLE = "PairFX 1.15 - 27/03/2014";
    private static String directory = "toernooien/";

    public static String getApplicationTitle() {
        return TITLE;
    }
    private Tournament tournament;
    private File toernooiFile;
    private static Model model;
    private Set<Pairing> lastGeneratedPairings = new HashSet<>();
    private static boolean online = false;
    private Launcher server;

    public static Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    private ResourceBundle resourceBundle;

    public void init() {
        tournament = new Tournament();
        tournament.setPairingStrategy(PairingStrategyFactory.createPercentageStrategy());
    }

    public void addPlayer(Player player) {
        tournament.addPlayer(player);
    }

    public void removePlayer(Player player) {
        tournament.removePlayer(player);
    }

    public List<Player> getPlayers() {
        return tournament.getAllPlayers();
    }

    public List<Player> getNietSpelendeSpelers() {
        return tournament.getNonPlayingPlayers();
    }

    public List<Pairing> getAllePartijen() {

        return tournament.getAllPairings();
    }

    public List<Pairing> pairSelectedPlayers() {
        List<Pairing> result = tournament.pairWithSelectedPlayers();
        removeAllSelections();
        lastGeneratedPairings.clear();
        lastGeneratedPairings.addAll(result);
        exportLastGeneratedIfWebserverIsRunning();
        return result;
    }

    public boolean spelerIsBeschikbaar(Player speler) {
        return tournament.playerIsAvailable(speler);
    }

    private void removeAllSelections() {
        for (Player p : tournament.players()) {
            tournament.unselectPlayerForPairing(p);
        }
    }

    public List<Pairing> getOnafgewerktePartijen() {
        return tournament.unfinishedPairings();
    }

    public List<Pairing> getOnafgewerktePartijenLaatsteRun() {
        List<Pairing> result = new ArrayList<>();
        for (Pairing p : lastGeneratedPairings) {
            if (!p.isPlayed()) {
                result.add(p);
            }
        }
        return result;
    }

    public String getLastError() {
        return tournament.lastError();
    }

    public void unselectPlayerForPairing(Player p) {
        tournament.unselectPlayerForPairing(p);
    }

    public void selectPlayerForPairing(Player p) {
        tournament.selectPlayerForPairing(p);
    }

    public boolean hasSelectedPlayers() {
        return !tournament.selectedPlayersForPairing().isEmpty();
    }

    public void unselectAllPlayersForPairing() {
        tournament.unselectAllPlayersForPairing();
    }

    public List<File> getToernooiBestanden() {
        File dir = new File(directory);

        if (dir.exists()) {
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            };
            return Arrays.asList(dir.listFiles(filter));
        } else {
            dir.mkdir();
            return new ArrayList<>();
        }
    }

    public void maakToernooi(String text, List<Player> startPlayer) {
        boolean addSlash = directory.endsWith("/");
        addSlash |= directory.endsWith("\\");

        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        text = text.replaceAll("/", "_").replaceAll("\\\\", "_");

        File nieuwToernooiFile = new File(directory + (addSlash ? "/" : "") + text);
        if (!nieuwToernooiFile.exists()) {
            try {
                nieuwToernooiFile.createNewFile();
                log("Created new file:" + nieuwToernooiFile.getAbsolutePath());
                toernooiFile = nieuwToernooiFile;
            } catch (IOException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
            init();

            for (Player player : startPlayer) {
                addPlayer(player);
            }
            bewaarToernooi();
        } else {
            toernooiFile = nieuwToernooiFile;
        }
    }

    public void laadToernooi(File toernooi) {
        init();
        toernooiFile = toernooi;
        SchoolPairingPersister loader = new SchoolPairingPersister(toernooiFile);
        try {
            loader.load();
            tournament.reloadPairings(loader.getPairings());

            for (Player player : loader.getPlayers()) {
                tournament.addPlayer(player);
            }
            tournament.initializeWith(loader.getPairings());
            tournament.setNrOfROundsPlayersMayNotHavePlayed(loader.getNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain());
            tournament.setMaxDifferenceInPoints(loader.getNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay());
            tournament.setMaxDifferenceInPercentage(loader.getMaximumDifferenceInPercentage());
            if (PairingType.POINTS.equals(loader.getPairingsType())) {
                tournament.setPairingStrategyByPoints();
            } else {
                tournament.setPairingStrategyByPercentage();
            }

        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void bewaarToernooi() {
        PFXUtil.log("SAVe TOERNOOI:" + toernooiFile.getAbsolutePath());

        SchoolPairingPersister persister = new SchoolPairingPersister(toernooiFile);
        persister.setPlayers(getPlayers());
        persister.setPairings(getAllePartijen());
        persister.setNrRoundsPlayersMayNotHavePlayedBeforeTheyCanPlayAgain(tournament.getNrOfROundsPlayersMayNotHavePlayed());
        persister.setNrPlacesPlayersMustBeSepraratedInRankingBeforeTheyCanPlay(tournament.getMaxDifferenceInPoints());
        persister.setMaximumDifferenceInPercentage(tournament.getMaxDifferenceInPercentage());
        persister.setPairingsType(tournament.getPairingsType());
        try {
            persister.save();
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Player> getTegenstandersVan(Player player) {
        return tournament.opponentsFrom(player);
    }

    public void removePairing(Pairing pairing) {
        final Player white = pairing.getWhite();
        final Player black = pairing.getBlack();
        unselectPlayerForPairing(white);
        unselectPlayerForPairing(black);
        tournament.removePairing(pairing);
        if (pairing.isPlayed()) {
            boolean whiteWon = pairing.whiteWon();
            boolean draw = pairing.isDraw();

            if (draw) {
                white.undoDraw();
                white.isBlack();
                black.undoDraw();
                black.isWhite();
            } else {
                if (whiteWon) {
                    white.undoWin();
                    white.isBlack();
                } else {
                    black.undoWin();
                    black.isWhite();
                }
            }
        }

    }

    public void addPlayerWithFirstNameLastName(String firstName, String lastName) {
        Player player = Player.createPlayerWithFirstnameLastname(firstName, lastName);
        addPlayer(player);
    }

    public int getNrOfPlayersWithFirstnameLastname(String firstName, String lastName) {
        Player player = Player.createPlayerWithFirstnameLastname(firstName, lastName);

        int cnt = 0;
        for (Player p : tournament.getAllPlayers()) {
            if (p.equals(player)) {
                cnt++;
            }
        }
        return cnt;
    }

    public boolean playerHasNoGames(Player player) {
        for (Pairing p : getAllePartijen()) {
            if (p.containsPlayer(player)) {
                return false;
            }
        }
        return true;
    }

    private void log(String string) {
        PFXUtil.log(string);
    }

    public void toggleSelectionOnAllAvailable() {
        tournament.selectAllAvailablePlayersForPairing();
    }

    public String scoreFromAgainst(Player from, Player against) {
        if (from.equals(against)) {
            return "-";
        }
        double fromScore = 0;
        int gameCounter = 0;
        for (Pairing p : getAllePartijen()) {
            if (p.isPlayed() && p.existsOfPlayers(from, against)) {
                gameCounter++;
                //handle draw
                if (p.isDraw()) {
                    fromScore += 0.5;
                    continue;
                }
                //handle win
                boolean whiteWon = p.whiteWon();
                boolean fromWasWhite = p.getWhite().equals(from);
                if (fromWasWhite && whiteWon) {
                    fromScore++;
                } else if (!fromWasWhite && !whiteWon) {
                    fromScore++;
                }
            }
        }
        if (gameCounter == 0) {
            return "";
        }

        if (fromScore % 1 == 0.5) {
            return String.valueOf(fromScore);//+" / "+gameCounter;
        } else {
            return String.valueOf((int) fromScore);//+" / "+gameCounter;
        }
    }

    public int getAantalPartijen(Player player) {
        return getTegenstandersVan(player).size();
    }

    public String getTitle() {
        return toernooiFile.getName();
    }

    public String getPointString(Player player) {
        return PlayerUtil.getPointsString(player);
    }

    public void setServer(boolean selected) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isNewPairing(Pairing pairing) {
        return lastGeneratedPairings.contains(pairing);
    }

    public List<Player> getRanking() {
        List<Player> result = tournament.ranking();
        return Collections.unmodifiableList(result);

    }

    public List<Pairing> getAfgewerktePartijen(Player selected) {
        return tournament.getFinishedGamesFor(selected);
    }

    public List<Pairing> getAfgewerktePartijen() {
        return tournament.getAllFinishedGames();
    }

    public void setPairingStrategyByPercentage() {
        tournament.setPairingStrategyByPercentage();
    }

    public void setPairingStrategyByPoints() {
        tournament.setPairingStrategyByPoints();
    }

    public List<Player> getRankingByPoints() {
        return tournament.getRankingByPoints();
    }

    public List<Player> getRankingByPercentage() {
        return tournament.getRankingByPercentage();
    }

    public int percentage(Player player) {
        return tournament.percentage(player);
    }

    public void toggleSelection(Player speler) {
        tournament.toggleSelectionForPairing(speler);
    }

    public boolean isSelectedForPairing(Player player) {
        return tournament.isSelectedForPairing(player);
    }

    /**
     * For testing only (
     *
     * @todo remove this getter)
     * @return
     */
    public Tournament getTournament() {
        return tournament;
    }

    public boolean usesPercentageStrategy() {
        return PairingType.PERCENTAGE.equals(tournament.getPairingsType());
    }

    public boolean isOnline() {
        return online;
    }

    public List<String> getFilesAsStrings() {
        List<String> result = new ArrayList<>();
        for (File f : getToernooiBestanden()) {
            PFXUtil.log("model::getfilesastrings " + f.getName());
            result.add(f.getName());
        }
        return result;
    }

    /* pairing options */
    public int nrRoundsPlayerMayNotHavePlayed() {
        return tournament.getNrOfROundsPlayersMayNotHavePlayed();
    }

    public int maxDiffInPoints() {
        return tournament.getMaxDifferenceInPoints();
    }

    public int maxDiffInPercentage() {
        return tournament.getMaxDifferenceInPercentage();
    }

    public void setNrRoundsPlayersMayNotHavePlayed(int nr) {
        tournament.setNrOfROundsPlayersMayNotHavePlayed(nr);
    }

    public void setMaxDifferenceInPoints(int max) {
        tournament.setMaxDifferenceInPoints(max);
    }

    public void setMaxDiffInPercentage(int max) {
        tournament.setMaxDifferenceInPercentage(max);
    }
    /* end pairing options*/

    public boolean noTournamentChosen() {
        return toernooiFile == null;
    }

    public static List<Player> parsePlayersFrom(File tournamentFile) {
        if (tournamentFile == null) {
            return new ArrayList<>();
        }
        List<Player> result = new ArrayList<>();

        if (tournamentFile == null || !tournamentFile.exists() || !tournamentFile.isFile()) {
            return result;
        }
        SchoolPairingPersister loader = new SchoolPairingPersister(tournamentFile);
        try {
            loader.load();

            for (Player p : loader.getPlayers()) {
                Player copy = p.copyByName();
                result.add(copy);
            }

        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String translate(String key) {
        return resourceBundle.getString(key);
    }

    public void setResourceBundle(ResourceBundle rb) {
        this.resourceBundle = rb;
    }

    public void stopWebServer() {
        if (PFXUtil.notNull(server) && server.isRunning()) {
            try {
                PFXUtil.log("Stopping webserver");
                server.shutdown();
                server = null;
                online=false;
            } catch (Exception ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startWebServer() {

        Logger log = Logger.getAnonymousLogger();


        try {
            if (server != null && server.isRunning()) {
                server.shutdown();

            }
            int port = 8090;
            try {
                port = Integer.parseInt(System.getProperty("pairfx.port"));
            } catch (NumberFormatException nfe) {
                log.log(Level.INFO, "Failed to parse port, defaulting to:{0}", port);
            }
            log.log(Level.INFO, "Starting the embedded web server port {0}", port);
            Map<String, String> args = new HashMap();
            args.put("webroot", "web"); // or any other command line args, eg port
            Launcher.initLogger(args);
            server = new Launcher(args); // spawns threads, so your application doesn't block   
            online=true;
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private void exportLastGeneratedIfWebserverIsRunning() {

        if (PFXUtil.notNull(server) && server.isRunning()) {
            try {
                File location = new File("web/export.html");
                Logger.getLogger(Model.class.getName()).log(Level.INFO, "Web server is running. Export to {0}", location);
                ExportFlags flags = new ExportFlags();
                flags.clearAll();
                flags.latestPairings = true;
                flags.autoRefresh = true;
                new Export().exportToHtml(location, flags);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setToernooiDir(String dir){
        directory=dir;
    }
}
