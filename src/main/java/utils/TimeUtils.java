package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Asher on 2016/12/22.
 */
public class TimeUtils {
    public static String timeAddMinutes(String oldTimeString, int minutes, String pattern) throws Exception{
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString(), Locale.CHINA);
        calendar.setTime(sdf.parse(oldTimeString.toString()));
        calendar.add(Calendar.MINUTE, minutes);
        return sdf.format(calendar.getTime());
    }

    public static Boolean timeCompareBefore(String time_1, String time_2, String pattern)throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString(), Locale.CHINA);
        Date time_1_date = sdf.parse(time_1.toString());
        Date time_2_date = sdf.parse(time_2.toString());
        Boolean flag = time_1_date.before(time_2_date);
        if(flag){
            return true;
        }else{
            return false;
        }
    }
}
