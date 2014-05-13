package model;

import java.io.Serializable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Transient;

public class Player implements Serializable{

    private static final int DEFAULT_RATING = 1200;
   
    @Element(name = "voornaam", required = false)
    private String firstname = "";
    @Element(name = "naam", required = false)
    private String lastname = "";
    @Element(name = "rating")
    private int rating = 0;
    @Element(name = "kleurvoorkeurVolgendePartij")
    private ColorPreference colorPreference = ColorPreference.NO_PREFERENCE;
    @Element(name = "punten")
    int points = 0;
    boolean hasBeenBye = false;
    @Transient
    private float sonnebornBerner;
    @Element(name = "wasNetZwart")
    private boolean wasJustBlack;
    @Element(name = "wasNetWit")
    private boolean wasJustWhite;
    @Element(name="afwezig",required=false)
    private boolean absent=false;
    @Element(name="id",required=false)
    private int id=-1;
    
    @Transient
    private final int POINT = 10;
    @Transient
    private final int HALF_POINT = 5;

    public Player() {
    }

    private Player(@Element(name = "voornaam") String firstName, @Element(name = "naam") String lastName) {
        setFirstname(firstName);
        setLastname(lastName);
        setRating(DEFAULT_RATING);
    }

    public static Player createPlayerWithFirstnameLastname(String firstName, String lastName) {
        Player player = new Player(firstName, lastName);
        return player;
    }

    public String getFirstname() {
        return firstname == null ? "" : firstname;
    }

    public final void setFirstname(String firstname) {

        if (firstname == null) {
            this.firstname = "";
        } else {
            this.firstname = firstname;
        }
    }

    public String getLastname() {
        return lastname == null ? "" : lastname;
    }

    public final void setLastname(String lastname) {
        if (lastname == null) {
            this.lastname = "";
        } else {
            this.lastname = lastname;
        }
    }

    public final void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "[" + firstname + " " + lastname + " " + rating + " " + points + " " + colorPreference + "]";
    }

    public int getPoints() {
        return points;
    }

    public void addWin() {
        points += 10;
    }

    public void addDraw() {
        points += 5;
    }

    public void setBye() {
        hasBeenBye = true;
        points += 10;
    }

    public void isWhite() {
        colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
        wasJustBlack = false;
        wasJustWhite = true;
    }

    public void isBlack() {
        colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
        wasJustBlack = true;
        wasJustWhite = false;
    }

    public int getColorScore() {
        return colorPreference.getScore();
    }

    public boolean hasBeenBye() {
        return hasBeenBye;
    }

    public float getSonnebornBerner() {
        return sonnebornBerner;
    }

    public void addSonnebornBerner(float i) {
        this.sonnebornBerner += i;

    }

    public boolean wantsToBeWhite() {
        return colorPreference == ColorPreference.MUST_BE_WHITE //
                || colorPreference == ColorPreference.PREFERS_WHITE //
                || colorPreference == ColorPreference.NO_PREFERENCE;
    }

    public boolean wantsToBeBlack() {
        return colorPreference == ColorPreference.MUST_BE_BLACK //
                || colorPreference == ColorPreference.PREFERS_BLACK //
                || colorPreference == ColorPreference.NO_PREFERENCE;
    }

    boolean wasJustWhite() {
        return wasJustWhite;
    }

    boolean wasJustBlack() {
        return wasJustBlack;
    }

    public int getColorScoreIfYouWouldBeWhite() {
        int score;
        try {
            score = colorPreference.getNewStatusIfPlayerIsNowWhite().getScore();
        } catch (IllegalStateException e) {
            score = ColorPreference.getMaxScore();
        }
        return score;
    }

    public int getColorScoreIfYouWouldBeBlack() {
        int score = 0;
        try {
            score = colorPreference.getNewStatusIfPlayerIsNowBlack().getScore();
        } catch (IllegalStateException e) {
            score = ColorPreference.getMaxScore();
        }
        return score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
        result = prime * result + rating;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        if (firstname == null) {
            if (other.firstname != null) {
                return false;
            }
        } else if (!firstname.equals(other.firstname)) {
            return false;
        }
        if (lastname == null) {
            if (other.lastname != null) {
                return false;
            }
        } else if (!lastname.equals(other.lastname)) {
            return false;
        }
        if (rating != other.rating) {
            return false;
        }

        return true;
    }

    public void undoDraw() {
        points -= HALF_POINT;
    }

    public void undoWin() {
        points -= POINT;
    }

    public void undoIsWhite() {
        colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
        wasJustBlack = false;
        wasJustWhite = false;
    }

    public void undoIsBlack() {
        colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
        wasJustBlack = false;
        wasJustWhite = false;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setAbsent(boolean absent) {
      this.absent = absent;
    }
    public boolean isAbsent(){
        return absent;
    }

    public Player copyByName() {
        return createPlayerWithFirstnameLastname(firstname, lastname);
    }
    
    public int getId(){
        return id;        
    }
    public void setId(int id){
        this.id = id;
    }

    public boolean hasAnId() {
       return id>=0;
    }
}
