package util;

import java.util.Date;

public class DateUtils {

	public static Date now() {
		return new Date();
	}

    public static long nowLong() {
       return now().getTime();
    }

}
