package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by a.g.seliverstov on 23.12.2015.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    public static final int PAGE_COUNT = 5;
    public static final int ONE_DAY_MILISECONDS = 86400000;
    public static final int SHIFT = 2;

    private Context mContext;
    private PageFragment[] pages = new PageFragment[PAGE_COUNT];

    public PageAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        for (int i = 0;i < PAGE_COUNT;i++){
            PageFragment page = new PageFragment();
            Bundle args = new Bundle();
            args.putString(PageFragment.DATE,new SimpleDateFormat(context.getString(R.string.date_format)).format(new Date(getDayForPosition(i))));
            page.setArguments(args);
            pages[i] = page;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return pages[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getDayName(mContext, getDayForPosition(position));
    }

    long getDayForPosition(int position){
        return System.currentTimeMillis()+((position-SHIFT)*ONE_DAY_MILISECONDS);
    }

    public String getDayName(Context context, long dateInMillis) {
        if (DateUtils.isToday(dateInMillis)){
            return context.getString(R.string.today);
        }else if (DateUtils.isToday(dateInMillis+ONE_DAY_MILISECONDS)){
            return context.getString(R.string.yesterday);
        }else if (DateUtils.isToday(dateInMillis-ONE_DAY_MILISECONDS)){
            return context.getString(R.string.tomorrow);
        }else{
            return new SimpleDateFormat(context.getString(R.string.day_format),Locale.getDefault()).format(dateInMillis);
        }
    }
}
