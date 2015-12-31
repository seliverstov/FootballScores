package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.CursorRecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import barqsoft.footballscores.api.SelectedMatchChangedListener;
import barqsoft.footballscores.db.DatabaseContract;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorRecyclerViewAdapter<ScoresAdapter.ViewHolder>{
    private static final String TAG = ScoresAdapter.class.getSimpleName();

    private Context mContext;

    public ScoresAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, Cursor cursor) {
        vh.homeName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_COL)));
        vh.awayName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_COL)));
        vh.matchTime.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.TIME_COL)));
        vh.matchId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_ID));

        String hGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_GOALS_COL));
        String aGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_GOALS_COL));
        vh.matchScore.setText(hGoals + " - " + aGoals);

        String homeCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_CREST));
        String awayCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_CREST));

        Picasso.with(mContext).load(Utils.updateWikipediaSVGImageUrl(homeCrest)).error(R.drawable.no_icon).placeholder(R.drawable.ic_launcher).into(vh.homeCrest);
        Picasso.with(mContext).load(Utils.updateWikipediaSVGImageUrl(awayCrest)).error(R.drawable.no_icon).placeholder(R.drawable.ic_launcher).into(vh.awayCrest);

        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = vi.inflate(R.layout.details, null);

        if(vh.matchId.equals(MainActivity.sSelectedMatch)){
            vh.details.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView matchTime = (TextView) v.findViewById(R.id.matchday);

            matchTime.setText(Utils.getMatchDay(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_DAY)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_ID_COL)), mContext));

            TextView league = (TextView) v.findViewById(R.id.league);

            league.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_COL)));

            Button shareButton = (Button) v.findViewById(R.id.share_button);

            final String ShareText = vh.homeName.getText() + " " + vh.matchScore.getText() + " " + vh.awayName.getText() + " " + mContext.getString(R.string.hash_tag);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText);
                    mContext.startActivity(shareIntent);
                }
            });
        } else {
            vh.details.removeAllViews();
        }

        vh.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Match clicked "+vh.matchId);
                if (vh.matchId.equals(MainActivity.sSelectedMatch)){
                    MainActivity.sSelectedMatch = -1;
                }else {
                    MainActivity.sSelectedMatch = vh.matchId;
                }
                ScoresAdapter.this.notifyDataSetChanged();
                if (mContext instanceof SelectedMatchChangedListener){
                    ((SelectedMatchChangedListener)mContext).notifySelectedItemChanged();
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView homeName;
        public TextView awayName;
        public TextView matchScore;
        public TextView matchTime;
        public ImageView homeCrest;
        public ImageView awayCrest;
        public Integer matchId;
        public ViewGroup details;
        public CardView card;
        public ViewHolder(View view) {
            super(view);
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            matchScore = (TextView) view.findViewById(R.id.match_score);
            matchTime = (TextView) view.findViewById(R.id.match_time);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
            details = (ViewGroup)view.findViewById(R.id.details_container);
            card = (CardView)view.findViewById(R.id.card_view);
        }
    }

}
