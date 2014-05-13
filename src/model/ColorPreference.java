package model;

public enum ColorPreference {

	MUST_BE_WHITE(5), PREFERS_WHITE(2), NO_PREFERENCE(0), PREFERS_BLACK(2), MUST_BE_BLACK(5);

	private int score = 0;

	private ColorPreference(int score) {
		this.score = score;
	}

	ColorPreference getNewStatusIfPlayerIsNowWhite() {
		if (this.equals(MUST_BE_BLACK)) {
			//throw new IllegalStateException("Player MUST be black next round");
                     //System.out.println("Player MUST be black next round");
                     return this;
		}else{
		int ordinal = this.ordinal();
		return ColorPreference.values()[++ordinal];
                }
	}

	ColorPreference getNewStatusIfPlayerIsNowBlack() {
		if (this.equals(MUST_BE_WHITE)) {
			//throw new IllegalStateException("Player MUST be black next round");
                    //System.out.println("Player MUST be white next round");
                     return this;
		}else{
		int ordinal = this.ordinal();
		return ColorPreference.values()[--ordinal];
                }
               
	}

	public int getScore() {
		return score;
	}

	public static int getMaxScore() {
		return MUST_BE_WHITE.getScore() + 1;
	}
}
