package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.Utils;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.rest.FootballDataClient;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Match;
import barqsoft.footballscores.rest.model.MatchResult;
import barqsoft.footballscores.rest.model.Team;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FootballDataService extends IntentService
{
    public static final String TAG = FootballDataService.class.getSimpleName();

    public FootballDataService(){
        super(FootballDataService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Intent messageIntent = new Intent(MainActivity.ACTION_UPDATE_SCORES);
        if (Utils.isNetworkConnectionAvailable(this)) {
            try {
                FootballDataClient fdc = new FootballDataClient(getString(R.string.api_key));
                getMatches(fdc, FootballDataClient.DEFAULT_NEXT_TIMEFRAME);
                getMatches(fdc, FootballDataClient.DEFAULT_PAST_TIMEFRAME);
                messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES, getString(R.string.scored_updated));
            }catch(Exception e){
                Log.e(TAG,e.getMessage(),e);
                messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES,getString(R.string.server_error));
            }
        }else{
            messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES,getString(R.string.no_network));
        }
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(messageIntent);
    }

    private void getMatches(FootballDataClient fdc,String timeFrame){
        try {
            List<Match> matches = fdc.listMatches(timeFrame);
            if (matches==null || matches.size()==0) return;
            Log.i(TAG, "Fetched "+matches.size()+" records");
            ContentValues[] vals = new ContentValues[matches.size()];
            for(int i=0;i<matches.size();i++){
                Match m = matches.get(i);
                ContentValues v = new ContentValues();
                v.put(DatabaseContract.ScoresEntry.MATCH_ID, ContentUris.parseId(Uri.parse(m.getLinks().getSelf())));
                v.put(DatabaseContract.ScoresEntry.MATCH_DAY, m.getMatchday());
                v.put(DatabaseContract.ScoresEntry.HOME_COL, m.getHomeTeamName());
                v.put(DatabaseContract.ScoresEntry.AWAY_COL, m.getAwayTeamName());

                MatchResult r = m.getMatchResult();
                if (r!=null){
                    String h = (r.getGoalsHomeTeam()!=null)?String.valueOf(r.getGoalsHomeTeam()):"?";
                    String a = (r.getGoalsAwayTeam()!=null)?String.valueOf(r.getGoalsAwayTeam()):"?";
                    v.put(DatabaseContract.ScoresEntry.HOME_GOALS_COL, h);
                    v.put(DatabaseContract.ScoresEntry.AWAY_GOALS_COL, a);
                }
                long leagueId = ContentUris.parseId(Uri.parse(m.getLinks().getSoccerSeason()));
                getLeagueInfo(fdc,leagueId,v);

                long homeTeamId = ContentUris.parseId(Uri.parse(m.getLinks().getHomeTeam()));
                getTeamInfo(fdc,homeTeamId, DatabaseContract.ScoresEntry.HOME_CREST, v);

                long awayTeamId = ContentUris.parseId(Uri.parse(m.getLinks().getAwayTeam()));
                getTeamInfo(fdc,awayTeamId, DatabaseContract.ScoresEntry.AWAY_CREST, v);

                SimpleDateFormat sdf = new SimpleDateFormat(FootballDataClient.DEFAULT_DATE_FORMAT, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone(FootballDataClient.DEFAULT_TIME_ZONE));
                Date date = sdf.parse(m.getDate());

                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.time_format), Locale.getDefault());
                timeFormat.setTimeZone(TimeZone.getDefault());
                v.put(DatabaseContract.ScoresEntry.DATE_COL, dateFormat.format(date));
                v.put(DatabaseContract.ScoresEntry.TIME_COL, timeFormat.format(date));
                vals[i]=v;
            }
            int insCnt = getContentResolver().bulkInsert(DatabaseContract.ScoresEntry.CONTENT_URI,vals);
            Log.i(TAG, "Inserted "+insCnt+" records");
        } catch (IOException | ParseException e) {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    void getTeamInfo(FootballDataClient fdc, long teamId, String column, ContentValues v) throws IOException {
        Cursor c = getContentResolver().query(DatabaseContract.TeamEntry.buildTeamWithId(teamId),null,null,null,null);
        if (c==null || !c.moveToFirst()){
            Team team = fdc.getTeam(String.valueOf(teamId));
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.TeamEntry._ID,teamId);
            values.put(DatabaseContract.TeamEntry.NAME_COL, team.getName());
            values.put(DatabaseContract.TeamEntry.SHORT_NAME_COL, team.getShortName());
            values.put(DatabaseContract.TeamEntry.CREST_URL_COL, team.getCrestUrl());
            Uri newItemUri = getContentResolver().insert(DatabaseContract.TeamEntry.CONTENT_URI,values);
            c = getContentResolver().query(newItemUri,null,null,null,null);
        }

        if (c.moveToFirst()) {
            String crestUrl = c.getString(c.getColumnIndex(DatabaseContract.TeamEntry.CREST_URL_COL));
            v.put(column,crestUrl);
            Log.i(TAG,column+": "+crestUrl);
        }else{
            Log.e(TAG, "Empty response for " + column);
        }
        c.close();
    }

    void getLeagueInfo(FootballDataClient fdc, long leagueId, ContentValues v) throws IOException {
        Cursor c = getContentResolver().query(
                DatabaseContract.LeagueEntry.buildLeagueWithId(leagueId),
                null, null, null, null
        );
        if (c==null || !c.moveToFirst()){
            League league = fdc.getLeague(String.valueOf(leagueId));
            if (league!=null){
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.LeagueEntry._ID, leagueId);
                values.put(DatabaseContract.LeagueEntry.NAME_COL, league.getCaption());
                values.put(DatabaseContract.LeagueEntry.SHORT_NAME_COL, league.getLeague());
                values.put(DatabaseContract.LeagueEntry.YEAR_COL, league.getYear());
                Uri newItemUri = getContentResolver().insert(DatabaseContract.LeagueEntry.CONTENT_URI,values);
                c = getContentResolver().query(newItemUri,null,null,null,null);
            }
        }

        if (c.moveToFirst()) {
            String leagueName = c.getString(c.getColumnIndex(DatabaseContract.LeagueEntry.NAME_COL));
            v.put(DatabaseContract.ScoresEntry.LEAGUE_COL, leagueName);
            v.put(DatabaseContract.ScoresEntry.LEAGUE_ID_COL, leagueId);
            Log.i(TAG, "League: " + leagueName + ", " + leagueId);
        }else{
            Log.e(TAG, "Empty response for league");
            v.put(DatabaseContract.ScoresEntry.LEAGUE_COL, getString(R.string.league_not_known));
            v.put(DatabaseContract.ScoresEntry.LEAGUE_ID_COL, -1);
        }
        c.close();
    }
}

