package com.technicalrj.halanxscouts.utlis;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDateUtils {

    public static final String TODAY = "Today";
    public static final String YESTERDAY = "Yesterday";
    public static final String EARLIER = "Earlier";

    public static final String STANDARD_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ";
    public static final String MOVE_IN_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String VISIT_TIME_PATTERN = "dd MMM',' yyyy hh:mm a";
    public static final String SCOUT_CHAT_TIME_PATTERN = "dd MMM yyyy hh:mm a";
    public static final String NORMAL_DATE_PATTERN = "dd MMM yyyy";
    private static final String TAG = MyDateUtils.class.getSimpleName();

    public static String epochToFormattedDate(long epochSeconds, String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date(epochSeconds);
        return formatter.format(date);
    }

    public static boolean checkTodayDate(String epochSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(Long.valueOf(epochSeconds));
        Date todayDate = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        String todayDateString = formatter.format(todayDate);
        Log.i("CheckToday"," date = "+dateString);
        Log.i("CheckToday"," current date = "+todayDateString);
        return todayDateString.equals(dateString);
    }

    public static String checkDate(String dateString){

        Date date = new Date(stringToEpoch(dateString, STANDARD_TIME_PATTERN));
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date currDate = new Date();
        String dateToCheck = formatter.format(date);
        String currentDate = formatter.format(currDate);

        if(dateToCheck.equals(currentDate)){
            return TODAY;

        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date prevDate = calendar.getTime();
            String previousDate = formatter.format(prevDate);
            if(previousDate.equals(dateToCheck)){
                return YESTERDAY;

            }else {
                return EARLIER;

            }

        }
    }


    public static long stringToEpoch(String dateString, String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            Date date = formatter.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getFormattedString(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        SimpleDateFormat monthFormatter = new SimpleDateFormat("dd MMMM yyy", Locale.getDefault());
        String newDateString = "";
        try {
            Date date = formatter.parse(dateString);
            newDateString = monthFormatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    public static String getFormattedString(String dateString, String oldPattern, String newPattern){
        SimpleDateFormat formatter = new SimpleDateFormat(oldPattern, Locale.getDefault());
        SimpleDateFormat newDateFormatter = new SimpleDateFormat(newPattern, Locale.getDefault());
        String newDateString = "";
        try {
            Date date = formatter.parse(dateString);
            newDateString = newDateFormatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    public static String getFormattedStringForBooking(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String newDateString = "";
        try {
            Date date = formatter.parse(dateString);
            newDateString = monthFormatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }


    public static long getNextSevenDays(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        return calendar.getTimeInMillis();
    }

    public static long getNextFifteenDays(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        return calendar.getTimeInMillis();
    }

    public static String getTime(String dateString, String pattern){
        String time = "";
        SimpleDateFormat receivedDateFormatter = new SimpleDateFormat(pattern, Locale.getDefault());
        SimpleDateFormat newDateFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = receivedDateFormatter.parse(dateString);
            time = newDateFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDateAndTime(String dateString, String pattern){
        String time = "";
        SimpleDateFormat receivedDateFormatter = new SimpleDateFormat(pattern, Locale.getDefault());
        SimpleDateFormat newDateFormatter = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = receivedDateFormatter.parse(dateString);
            time = newDateFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean isUpcomingVisitDate(String dateString){
        Date date = new Date(stringToEpoch(dateString, VISIT_TIME_PATTERN));
        return date.getTime() > System.currentTimeMillis();
    }


    public static String getDate(String dateWithTime, String pattern){
        String justDate = "";
        SimpleDateFormat receivedDateFormatter = new SimpleDateFormat(pattern, Locale.getDefault());
        SimpleDateFormat newDateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date date = receivedDateFormatter.parse(dateWithTime);
            justDate = newDateFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return justDate;


    }


}
