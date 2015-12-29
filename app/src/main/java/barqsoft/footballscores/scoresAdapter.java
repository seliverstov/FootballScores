package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.samples.svg.SvgDecoder;
import com.bumptech.glide.samples.svg.SvgDrawableTranscoder;
import com.bumptech.glide.samples.svg.SvgSoftwareLayerSetter;
import com.caverock.androidsvg.SVG;
import com.skyfishjy.CursorRecyclerViewAdapter;

import java.io.InputStream;

import barqsoft.footballscores.db.DatabaseContract;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorRecyclerViewAdapter<ScoresAdapter.ViewHolder>{

    public Integer selectedMatch = 0;

    private Context mContext;

    public ScoresAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + mContext.getString(R.string.hash_tag));
        return shareIntent;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, Cursor cursor) {

        vh.homeName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_COL)));
        vh.awayName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_COL)));
        vh.date.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.TIME_COL)));
        vh.matchId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_ID));

        String hGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_GOALS_COL));
        String aGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_GOALS_COL));
        vh.score.setText(hGoals+" - "+aGoals);


        String homeCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_CREST));
        String awayCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_CREST));

        GenericRequestBuilder svgLoader = Glide.with(mContext)
                .using(Glide.buildStreamModelLoader(Uri.class, mContext), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.no_icon)
                .error(R.drawable.no_icon)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        if (homeCrest!=null && homeCrest.endsWith("svg"))
            svgLoader.diskCacheStrategy(DiskCacheStrategy.SOURCE).load(Uri.parse(homeCrest)).into(vh.homeCrest);
        else
            Glide.with(mContext).load(homeCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(vh.homeCrest);

        if (awayCrest!=null && awayCrest.endsWith("svg"))
            svgLoader.diskCacheStrategy(DiskCacheStrategy.SOURCE).load(Uri.parse(awayCrest)).into(vh.awayCrest);
        else
            Glide.with(mContext).load(awayCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(vh.awayCrest);

        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = vi.inflate(R.layout.detail_fragment, null);

        if(vh.matchId.equals(selectedMatch)){
            vh.details.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);

            match_day.setText(Utils.getMatchDay(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_DAY)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_ID_COL)), mContext));

            TextView league = (TextView) v.findViewById(R.id.league_textview);

            league.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_COL)));

            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    mContext.startActivity(createShareForecastIntent(vh.homeName.getText()+" "
                            +vh.score.getText()+" "+vh.awayName.getText() + " "));
                }
            });
        } else {
            vh.details.removeAllViews();
        }

        vh.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoresAdapter.this.selectedMatch = vh.matchId;
                MainActivity.selectedMatch = (int) vh.matchId;
                ScoresAdapter.this.notifyDataSetChanged();
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
        public TextView score;
        public TextView date;
        public ImageView homeCrest;
        public ImageView awayCrest;
        public Integer matchId;
        public ViewGroup details;
        public CardView card;
        public ViewHolder(View view) {
            super(view);
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score     = (TextView) view.findViewById(R.id.score_textview);
            date      = (TextView) view.findViewById(R.id.data_textview);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
            details = (ViewGroup)view.findViewById(R.id.details_fragment_container);
            card = (CardView)view.findViewById(R.id.card_view);
        }
    }

}
