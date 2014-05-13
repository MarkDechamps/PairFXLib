package model;

import java.io.Serializable;
import java.util.Date;
import org.simpleframework.xml.Element;
import util.DateUtils;

public class PairingImpl implements Pairing,Serializable {

    @Element(name = "witspeler")
    private Player white;
    @Element(name = "zwartspeler")
    private Player black;
    @Element(name = "kleurScore")
    private final int colorScore;
    @Element(name = "draw")
    private boolean draw;
    @Element(name = "witwint")
    private boolean whiteWins;
    @Element(name = "puntenWitVoorDePartij")
    private float whitePointsBeforePlaying;
    @Element(name = "puntenZwartVoorDePartij")
    private float blackPointsBeforePlaying;
    @Element(name = "gespeeld")
    private boolean played = false;
    @Element(name = "datumParing")
    private final Date creationDate;
    @Element(name = "datumGespeeld",required=false)
    private Date playedDate;

    private PairingImpl( @Element(name="witspeler")Player white,  @Element(name="zwartspeler")Player black,@Element(name="kleurScore") int colorScore,@Element(name="datumParing")Date creationDate) {
        this.white = white;
        this.black = black;
        this.colorScore = colorScore;
        whitePointsBeforePlaying = white.getPoints();
        blackPointsBeforePlaying = black.getPoints();
        this.creationDate = creationDate;
    }

    public static PairingImpl createPairing(Player white, Player black) {
        if (white.equals(black)) {
            throw new IllegalArgumentException(
                    "White player can not be the same as black player:" + white
                    + " " + black);
        }

        int score = colorScore(white, black);
        int scoreReversed = colorScore(black, white);

        int preferredScore = Math.min(score, scoreReversed);

        if (score > scoreReversed) {
            Player temp = white;
            white = black;
            black = temp;
        }
        return new PairingImpl(white, black, preferredScore,DateUtils.now());
    }

    static int colorScore(Player w, Player b) {
        int score = 0;
        int colorChangeWeight = 1;

        score += w.wantsToBeWhite() ? 0 : w.getColorScore();// w.getColorScoreIfYouWouldBeWhite();
        score += b.wantsToBeBlack() ? 0 : b.getColorScore();// b.getColorScoreIfYouWouldBeBlack();

        if (w.wasJustWhite()) {
            score += colorChangeWeight;
        }
        if (b.wasJustBlack()) {
            score += colorChangeWeight;
        }

        return score;
    }

    @Override
    public Player getWhite() {
        return white;
    }

    @Override
    public Player getBlack() {
        return black;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("[" + white.getFirstname() + " " + white.getLastname());
        builder.append(" (" + white.getRating() + " (" + white.getPoints()
                + ")(sb:" + white.getSonnebornBerner() + ")");
        builder.append(" - ");
        builder.append(black.getFirstname() + " " + black.getLastname());
        builder.append(" (" + black.getRating() + " (" + black.getPoints()
                + ")(sb:" + black.getSonnebornBerner() + ")]");
        if (getResultString().length() > 0) {
            builder.append("Result:" + getResultString());
        }
        return builder.toString();
    }

    private String getResultString() {
        if (!played) {
            return "";
        }
        if (draw) {
            return "1/2 - 1/2";
        }
        if (whiteWins) {
            return " 1  -  0 ";
        } else {
            return " 0  -  1 ";
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object otherPairing) {
        if (this == otherPairing) {
            return true;
        }
        if (otherPairing == null) {
            return false;
        }
        if (getClass() != otherPairing.getClass()) {
            return false;
        }
        PairingImpl other = (PairingImpl) otherPairing;
        if (!(white.equals(other.black) || white.equals(other.white))) {
            return false;
        }
        if (!(black.equals(other.black) || black.equals(other.white))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean existsOfPlayers(Player p1, Player p2) {
        return (white.equals(p1) || white.equals(p2))
                && (black.equals(p1) || black.equals(p2));
    }

    @Override
    public boolean isBye() {
        return false;
    }

    @Override
    public void whiteWins() {
        if(isPlayed()){
            return;
        }
        white.addSonnebornBerner(blackPointsBeforePlaying);
        white.addWin();
        processColors();
        whiteWins = true;
        played = true;
        setPlayedNow();
    }

    @Override
    public void blackWins() {
        if(isPlayed()){
           return;
        }
        black.addSonnebornBerner(whitePointsBeforePlaying);
        black.addWin();
        processColors();
        whiteWins = false;
        played = true;
        setPlayedNow();
    }

    @Override
    public void undoResult() {
        if (isPlayed()) {
            if (whiteWins) {
                white.undoWin();
            } else {
                if (isDraw()) {
                    white.undoDraw();
                    black.undoDraw();
                } else {
                    black.undoWin();
                }
            }
            undoProcessColors();
            played = false;
            whiteWins = false;
            draw = false;
            playedDate=null;
        }
    }

    @Override
    public void draw() {
        if(isPlayed()){
            return;
        }
        black.addSonnebornBerner(whitePointsBeforePlaying / 2);
        white.addSonnebornBerner(blackPointsBeforePlaying / 2);

        white.addDraw();
        black.addDraw();
        processColors();
        draw = true;
        whiteWins = false;
        played = true;
         setPlayedNow();
    }

    private void processColors() {
        white.isWhite();
        black.isBlack();
    }

    private void undoProcessColors() {
        white.undoIsWhite();
        black.undoIsBlack();
    }

    @Override
    public boolean hasColorConflicts() {
        return colorScore >= ColorPreference.getMaxScore();
    }

    @Override
    public boolean containsPlayer(Player floater) {
        return white.equals(floater) || black.equals(floater);
    }

    @Override
    public int getWhiteRating() {
        return getWhite().getRating();
    }

    @Override
    public int getBlackRaring() {
        return getBlack().getRating();
    }

    @Override
    public int getColorScore() {
        return colorScore;
    }

    @Override
    public boolean isPlayed() {
        return played;
    }

    @Override
    public boolean isDraw() {
        return draw;
    }

    @Override
    public boolean whiteWon() {
        return whiteWins;
    }
    
    @Override
    public Date getCreationDate(){
        return creationDate;
    }
    @Override
    public Date getPlayedDate(){
        return playedDate;
    }

    private void setPlayedNow() {
        playedDate=DateUtils.now();
    }
}
