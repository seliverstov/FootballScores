package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils;
import barqsoft.footballscores.db.DatabaseContract;

import static barqsoft.footballscores.db.DatabaseContract.ScoresEntry.*;

/**
 * Created by a.g.seliverstov on 29.12.2015.
 */
public class FootballWidgetService extends RemoteViewsService {
    private static final String TAG = FootballWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballRemoteViewsFactory();
    }

    class FootballRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
        private Cursor cursor;

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (cursor != null) cursor.close();

            final long identityToken = Binder.clearCallingIdentity();
            Uri weatherForLocationUri = buildScoreWithDate();
            cursor = getContentResolver().query(weatherForLocationUri,
                    null,
                    null,
                    new String[]{new SimpleDateFormat(FootballWidgetService.this.getString(R.string.date_format)).format(new Date(System.currentTimeMillis()))},
                    null);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (cursor!=null) cursor.close();
            cursor=null;
        }

        @Override
        public int getCount() {
            return (cursor==null)?0:cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            final Context context = FootballWidgetService.this;
            if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) return null;

            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            views.setTextViewText(R.id.widget_home_name, cursor.getString(cursor.getColumnIndex(HOME_COL)));
            views.setTextViewText(R.id.widget_away_name, cursor.getString(cursor.getColumnIndex(AWAY_COL)));
            views.setTextViewText(R.id.widget_match_time, cursor.getString(cursor.getColumnIndex(TIME_COL)));

            String homeScore = cursor.getString(cursor.getColumnIndex(HOME_GOALS_COL));
            String awayScore = cursor.getString(cursor.getColumnIndex(AWAY_GOALS_COL));

            views.setTextViewText(R.id.widget_match_score,homeScore+" - "+awayScore);

            String homeCrest = cursor.getString(cursor.getColumnIndex(HOME_CREST));
            String awayCrest = cursor.getString(cursor.getColumnIndex(AWAY_CREST));

            try {
                Bitmap homeBitmap = Picasso.with(context)
                        .load(Utils.updateWikipediaSVGImageUrl(homeCrest))
                        .error(R.drawable.no_icon)
                        .placeholder(R.drawable.ic_launcher)
                        .get();
                Bitmap awayBitmap = Picasso.with(context)
                        .load(Utils.updateWikipediaSVGImageUrl(awayCrest))
                        .error(R.drawable.no_icon)
                        .placeholder(R.drawable.ic_launcher)
                        .get();
                views.setImageViewBitmap(R.id.widget_home_crest, homeBitmap);
                views.setImageViewBitmap(R.id.widget_away_crest, awayBitmap);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (cursor.moveToPosition(position))
                return cursor.getLong(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_ID));
            else return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
