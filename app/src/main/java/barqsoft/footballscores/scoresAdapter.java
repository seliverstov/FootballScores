package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
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

import java.io.InputStream;

import barqsoft.footballscores.db.DatabaseContract;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter{

    public Integer detail_match_id = 0;

    private Context mContext;

    public ScoresAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){
        final ViewHolder vh = (ViewHolder) view.getTag();

        vh.homeName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_COL)));
        vh.awayName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_COL)));
        vh.date.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.TIME_COL)));
        vh.matchId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_ID));

        String hGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_GOALS_COL));
        String aGoals = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_GOALS_COL));
        vh.score.setText(hGoals+" - "+aGoals);


        String homeCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.HOME_CREST));
        String awayCrest = cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.AWAY_CREST));

        GenericRequestBuilder svgLoader = Glide.with(context)
                .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
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
            Glide.with(context).load(homeCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(vh.homeCrest);

        if (awayCrest!=null && awayCrest.endsWith("svg"))
            svgLoader.diskCacheStrategy(DiskCacheStrategy.SOURCE).load(Uri.parse(awayCrest)).into(vh.awayCrest);
        else
            Glide.with(context).load(awayCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(vh.awayCrest);

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = vi.inflate(R.layout.detail_fragment, null);

        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if(vh.matchId.equals(detail_match_id)){
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);

            match_day.setText(Utilies.getMatchDay(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.MATCH_DAY)),cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_ID_COL)), context));

            TextView league = (TextView) v.findViewById(R.id.league_textview);

            league.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.ScoresEntry.LEAGUE_COL)));

            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(vh.homeName.getText()+" "
                    +vh.score.getText()+" "+vh.awayName.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + mContext.getString(R.string.hash_tag));
        return shareIntent;
    }

    class ViewHolder  {
        public TextView homeName;
        public TextView awayName;
        public TextView score;
        public TextView date;
        public ImageView homeCrest;
        public ImageView awayCrest;
        public Integer matchId;
        public ViewHolder(View view) {
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score     = (TextView) view.findViewById(R.id.score_textview);
            date      = (TextView) view.findViewById(R.id.data_textview);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        }
    }

}
