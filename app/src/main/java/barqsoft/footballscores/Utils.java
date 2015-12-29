package barqsoft.footballscores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utils {

    public static final int CHAMPIONS_LEAGUE = 362;

    public static String getMatchDay(int match_day,int league_num, Context context){
        if(league_num == CHAMPIONS_LEAGUE){
            if (match_day <= 6) return context.getString(R.string.match_day_6);
            if (match_day == 7 || match_day == 8) return context.getString(R.string.match_day_7_8);
            if(match_day == 9 || match_day == 10) return context.getString(R.string.match_day_9_10);
            if(match_day == 11 || match_day == 12) return context.getString(R.string.match_day_11_12);
            return context.getString(R.string.match_day_final);
        } else {
            return String.format(context.getString(R.string.mathc_day), match_day);
        }
    }

    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


}
