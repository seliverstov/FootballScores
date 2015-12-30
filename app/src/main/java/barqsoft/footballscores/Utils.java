package barqsoft.footballscores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String updateWikipediaSVGImageUrl(String url) {
        if (url != null && !url.isEmpty() && url.toLowerCase().endsWith(".svg")) {
            String[] tmp = url.split("/wikipedia/");
            if (tmp.length==2) {
                String[] s = tmp[1].split("/");
                if (s.length > 1) {
                    StringBuilder result = new StringBuilder();
                    result.append(tmp[0]).append("/wikipedia/").append(s[0]).append("/thumb");
                    for (int i = 1; i < s.length; i++) {
                        result.append("/").append(s[i]);
                    }
                    result.append("/200px-").append(s[s.length - 1]).append(".png");
                    return result.toString();
                }else{
                    return url;
                }
            }else{
                return url;
            }
        }else{
            return url;
        }
    }

}
