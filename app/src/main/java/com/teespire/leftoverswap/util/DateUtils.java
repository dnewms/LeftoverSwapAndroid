package com.teespire.leftoverswap.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Matih on 21/2/2015.
 */
public class DateUtils
{
    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS*60;
    public final static long HOUR_MILLIS = MINUTE_MILLIS*60;
    public final static long DAY_MILLIS = HOUR_MILLIS*24;
    public final static long YEAR_MILLIS = DAY_MILLIS*365;

    public static String formatToYesterdayOrToday(Date dateTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("hh:mma");
        DateFormat dateFormatter = new SimpleDateFormat("EEE hh:mma MMM d, yyyy");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday " + timeFormatter.format(dateTime);
        } else {
            return dateFormatter.format(dateTime);
        }
    }

    public static int minutesDiff(Date earlierDate, Date laterDate )
    {
        if( earlierDate == null || laterDate == null ) return 0;
        return (int)((laterDate.getTime()/MINUTE_MILLIS) - (earlierDate.getTime()/MINUTE_MILLIS));
    }

    public static String getTimeAgoString(Date olderDate){
        Date dateNow = new Date();
        long diff = dateNow.getTime() - olderDate.getTime();

        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if(diffMinutes <= 0) return "now";
        if(diffMinutes < 60) return (diffMinutes + " minute" + ((diffMinutes == 1) ? "" : "s") + " ago");
        if(diffHours < 24) return (diffHours + " hour" + ((diffHours == 1) ? "" : "s") + " ago");
        return (diffDays + " day" + ((diffDays == 1) ? "" : "s") + " ago");
    }

    public static Date getOlderDateFromNow(int numberOfDays){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1 * numberOfDays);
        return cal.getTime();
    }
}
