/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Model;
import model.Pairing;
import model.Player;
import model.I18n;
import screensframework.Messages;
import util.PlayerUtil;

/**
 *
 * @author Mark Dechamps
 */
public class Export {

    private File dir;

    List<String> exportTableToHtml(List<Player> ranking) throws FileNotFoundException {
        final Model model = Model.getInstance();
        List<String> tableLines = new ArrayList<>();
        tableLines.add("<table>");

        StringBuilder title = new StringBuilder(th(Model.getInstance().getTitle()));


        for (Player player : ranking) {
            title.append(th(player.getFirstname() + "<br/>" + player.getLastname()));
        }
        title.append(th("Totaal"));
        title.append(th("%"));
        tableLines.add(title.toString());

        for (Player hor : ranking) {
            StringBuilder singleLine = new StringBuilder();
            String name = fullNameTh(hor);
            singleLine.append(name);

            for (Player ver : ranking) {

                final String scoreString = model.scoreFromAgainst(hor, ver);
                if (ver.equals(hor)) {
                    singleLine.append(ctdGray(scoreString));
                } else {
                    singleLine.append(ctd(scoreString));
                }
            }
            singleLine.append(getPointsHtml(hor));
            singleLine.append(getPercentage(hor));
            tableLines.add(tr(singleLine.toString()));

        }
        tableLines.add("</table>");
        return tableLines;
    }

    private String ctd(String string) {
        return "<td class=\"center\">" + string + "</td>";
    }

    private String ctdGray(String string) {
        return "<td class=\"gray\">" + string + "</td>";
    }

    private String tr(String string) {
        return "<tr>" + string + "</tr>";
    }

    private String th(String string) {
        return "<th>" + string + "</th>";
    }

    private String writeToFile(List<String> tableLines, String ext) throws FileNotFoundException {
        if (dir == null) {
            return "";
        }

        String path = dir.getAbsolutePath();
        if (!path.endsWith(ext)) {
            path += ext;
        }
        File output = new File(path);
        try (PrintWriter writer = new PrintWriter(output)) {

            for (String s : tableLines) {
                writer.println(s);
            }
            writer.flush();
            writer.close();

        }


        return output.getAbsolutePath();
    }

    private String writeStartHtml(boolean autoRefresh) {
        //test met altijd autorefresh
        autoRefresh = true;
        String refresh = "<meta http-equiv=\"refresh\" content=\"30\">";
        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />";
        if (autoRefresh) {
            return "<head>" + refresh + css + "</head>";
        } else {
            return "<head>" + css + "</head>";
        }

    }

    private String writeEndHtml() {
        return "";
    }

    public String exportToHtml(File location, ExportFlags flags) throws FileNotFoundException {
        final String newLine = "<br/>";
        dir = location;

        Model model = Model.getInstance();
        List<String> tableLines = new ArrayList<>();
        tableLines.add(writeStartHtml(flags.autoRefresh));
        tableLines.add("<h1>" + title() + "</h1><br/>" + dateTime());
        if (flags.includeTable) {
            tableLines.addAll(exportTableToHtml(model.getRankingByPoints()));
            tableLines.add(newLine);
        }
        if (flags.includeRankingByPoints) {
            tableLines.add(I18n.get(Messages.rankingByPoints));
            tableLines.addAll(exportRankingToHtml(model.getRankingByPoints()));
            tableLines.add(newLine);
        }
        if (flags.includeRankingByPercentage) {
            tableLines.add(I18n.get(Messages.rankingByPercentage));
            tableLines.addAll(exportRankingToHtml(model.getRankingByPercentage()));
            tableLines.add(newLine);
        }
        if (flags.includeActivePairings) {
            tableLines.add(I18n.get(Messages.activePairings));
            tableLines.addAll(exportUnfinishedPairingsToHtml(model.getOnafgewerktePartijen()));
            tableLines.add(newLine);
        }
        if (flags.includeFinishedPairings) {
            tableLines.add(I18n.get(Messages.finishedPairings));
            tableLines.addAll(exportFinishedPairingsToHtml(model.getAfgewerktePartijen()));
            tableLines.add(newLine);
        }
        if (flags.latestPairings) {
            tableLines.add(I18n.get(Messages.latestPairings));
            tableLines.addAll(exportFinishedPairingsToHtml(model.getOnafgewerktePartijenLaatsteRun()));
            tableLines.add(newLine);
        }

        tableLines.add(writeEndHtml());
        String outputPath = writeToFile(tableLines, ".html");

        copyCSSFileTo(location);
        return outputPath;
    }

    public String exportToTxt(File location, ExportFlags flags) throws FileNotFoundException {
        final String newLine = "\n\r";
        dir = location;
        Model model = Model.getInstance();

        List<String> tableLines = new ArrayList<>();
        tableLines.add(title() + " " + dateTime());
        if (flags.includeTable) {
            tableLines.addAll(exportTableToTxt(model.getRankingByPoints()));
            tableLines.add(newLine);
        }
        if (flags.includeRankingByPoints) {
            tableLines.add(Messages.rankingByPoints);

            tableLines.addAll(exportRankingToTxt(model.getRankingByPoints()));
            tableLines.add(newLine);
            tableLines.add(newLine);
        }
        if (flags.includeRankingByPercentage) {
            tableLines.add(Messages.rankingByPercentage);
            tableLines.addAll(exportRankingToTxt(model.getRankingByPercentage()));
            tableLines.add(newLine);
        }
        if (flags.includeActivePairings) {
            tableLines.add(Messages.activePairings);
            tableLines.addAll(exportUnfinishedPairingsToTxt(model.getOnafgewerktePartijen()));
            tableLines.add(newLine);
        }
        if (flags.includeFinishedPairings) {
            tableLines.add(Messages.finishedPairings);
            tableLines.addAll(exportAllPairingsToTxt(model.getAfgewerktePartijen()));
            tableLines.add(newLine);
        }
        if (flags.latestPairings) {
            tableLines.add(I18n.get(Messages.latestPairings));
            tableLines.addAll(exportUnfinishedPairingsToTxt(model.getOnafgewerktePartijenLaatsteRun()));
            tableLines.add(newLine);
        }
        return writeToFile(tableLines, ".txt");
    }

    List< String> exportTableToTxt(List<Player> ranking) {
        List<String> tableLines = new ArrayList<>();
        //todo
        return tableLines;
    }

    List< String> exportRankingToTxt(List<Player> ranking) {
        List<String> tableLines = new ArrayList<>();
        int i = 0;
        for (Player p : ranking) {
            i++;
            StringBuilder line = new StringBuilder();
            String pos = String.format("%2d", i);
            line.append(pos).append(") ")
                    .append(getPointsTxt(p))
                    .append(" ")
                    .append(p.getFirstname())
                    .append(" ")
                    .append(p.getLastname());

            tableLines.add(line.toString());
        }
        return tableLines;
    }

    List< String> exportRankingToHtml(List<Player> ranking) {
        List<String> tableLines = new ArrayList<>();

        tableLines.add("<table>");

        StringBuilder title = new StringBuilder();
        title.append(th("Positie"));
        title.append(th("Naam"));
        title.append(th("Score"));

        tableLines.add(title.toString());

        int i = 0;
        for (Player player : ranking) {
            i++;
            StringBuilder singleLine = new StringBuilder();

            String position = th(String.valueOf(i));
            String name = fullNameTh(player);


            singleLine.append(position);
            singleLine.append(name);
            singleLine.append(getPointsHtml(player));

            tableLines.add(tr(singleLine.toString()));
        }

        tableLines.add("</table>");

        return tableLines;
    }

    String getPointsTxt(Player hor) {
        int nrGames = Model.getInstance().getAantalPartijen(hor);
        String pointsString = PlayerUtil.getPointsString(hor);
        return String.format("%4s/%-3d", pointsString, nrGames);
    }

    private String getPointsHtml(Player hor) {
        return ctd(getPointsTxt(hor));
    }

    private String getPercentage(Player hor) {
        return ctd(String.valueOf(Model.getInstance().percentage(hor)) + "%");
    }

    private void copyCSSFileTo(File location) {
        File css = new File("export/style.css");
        if (!css.exists()) {
            System.out.println("Error:" + css.getAbsoluteFile() + " not found.");
        } else {
            File destCss = new File(location.getParentFile() + "/style.css");
            if (destCss.exists()) {
                System.out.println("Info:" + css.getAbsoluteFile() + " already there.");
                return;
            }
            System.out.println("copy css file to " + location.getParent());
            Path source = Paths.get(css.getAbsolutePath());
            Path target = Paths.get(destCss.getAbsolutePath());
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private List<String> exportUnfinishedPairingsToHtml(List<Pairing> onafgewerktePartijen) {
        return pairingToHtml(onafgewerktePartijen);
    }

    private List<String> pairingToTxt(List<Pairing> pairings) {
        List<String> tableLines = new ArrayList<>();

        int i = 0;
        for (Pairing pairing : pairings) {
            i++;
            StringBuilder singleLine = new StringBuilder();
            singleLine.append(fullName(pairing.getWhite()));
            if (pairing.isPlayed()) {
                if (pairing.whiteWon()) {
                    singleLine.append("   1 - 0   ");
                } else if (pairing.isDraw()) {
                    singleLine.append(" 0.5 - 0.5 ");
                } else {
                    singleLine.append("   0 - 1   ");
                }
            } else {
                singleLine.append(" - ");
            }
            singleLine.append(fullName(pairing.getBlack()));
            singleLine.append("\r\n");
            tableLines.add(singleLine.toString());
        }
        return tableLines;
    }

    private List<String> pairingToHtml(List<Pairing> pairings) {
        List<String> tableLines = new ArrayList<>();
        tableLines.add("<table>");

        StringBuilder title = new StringBuilder();
        title.append(th("Nr"));
        title.append(th("Wit"));
        title.append(th(" "));
        title.append(th("Zwart"));

        tableLines.add(title.toString());

        int i = 0;
        for (Pairing pairing : pairings) {
            i++;
            StringBuilder singleLine = new StringBuilder();
            singleLine.append(ctd(String.valueOf(i)));
            singleLine.append(fullNameTh(pairing.getWhite()));
            if (pairing.isPlayed()) {
                if (pairing.whiteWon()) {
                    singleLine.append(ctd("1-0"));
                } else if (pairing.isDraw()) {
                    singleLine.append(ctd("&frac12;-&frac12;"));
                } else {
                    singleLine.append(ctd("0-1"));
                }
            } else {
                singleLine.append(ctd("-"));
            }
            singleLine.append(fullNameTh(pairing.getBlack()));
            tableLines.add(tr(singleLine.toString()));
        }
        tableLines.add("</table>");
        return tableLines;
    }

    private List<String> exportFinishedPairingsToHtml(List<Pairing> afgewerktePartijen) {
        return pairingToHtml(afgewerktePartijen);
    }

    private String fullNameTh(Player player) {
        String name = th(fullName(player));
        return name;
    }

    private List<String> exportUnfinishedPairingsToTxt(List<Pairing> onafgewerktePartijen) {
        return pairingToTxt(onafgewerktePartijen);
    }

    private List<String> exportAllPairingsToTxt(List<Pairing> afgewerktePartijen) {
        return pairingToTxt(afgewerktePartijen);
    }

    private String fullName(Player player) {
        return player.getFirstname() + " " + player.getLastname();
    }

    private String title() {

        return "PairFX ";
    }

    private String dateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(new Date());
    }
}
