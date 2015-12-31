package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
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
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.rest.FootballDataClient;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Match;
import barqsoft.footballscores.rest.model.MatchResult;
import barqsoft.footballscores.rest.model.Team;

import static android.content.ContentResolver.*;

/**
 * Created by a.g.seliverstov on 30.12.2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    public static final String ACCOUNT_TYPE = "footballscores.barqsoft";

    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext().getApplicationContext());
        Intent messageIntent = new Intent(MainActivity.ACTION_UPDATE_SCORES);
        try {
            FootballDataClient fdc = new FootballDataClient(getContext().getString(R.string.api_key));
            getMatches(fdc, FootballDataClient.DEFAULT_NEXT_TIMEFRAME, provider);
            getMatches(fdc, FootballDataClient.DEFAULT_PAST_TIMEFRAME, provider);
            //messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES, getContext().getString(R.string.scored_updated));
            Intent widgetIntent = new Intent(MainActivity.ACTION_SCORES_UPDATED);
            getContext().sendBroadcast(widgetIntent);
        }catch(Exception e){
            Log.e(TAG,e.getMessage(),e);
            messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES,getContext().getString(R.string.server_error));
        }
        broadcastManager.sendBroadcast(messageIntent);
    }

    public static void init(Context context){
        getAccount(context);
    }

    public static void syncNow(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(SYNC_EXTRAS_MANUAL, true);

        Account account = getAccount(context);
        if (account!=null)
            requestSync(account, DatabaseContract.CONTENT_AUTHORITY, bundle);
    }

    public static Account getAccount(Context context){
        AccountManager accountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),ACCOUNT_TYPE);

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.i(TAG, "Account doesn't exist");
                return null;
            }
            SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

            ContentResolver.setSyncAutomatically(newAccount, DatabaseContract.CONTENT_AUTHORITY, true);

            syncNow(context);
        }

        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getAccount(context);
        String authority = DatabaseContract.CONTENT_AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private void getMatches(FootballDataClient fdc,String timeFrame, ContentProviderClient provider){
        try {
            List<Match> matches = fdc.listMatches(timeFrame);
            if (matches==null || matches.size()==0) return;
            Log.i(TAG, "Fetched "+matches.size()+" records");
            ContentValues[] vals = new ContentValues[matches.size()];
            for(int i=0;i<matches.size();i++){
                Match m = matches.get(i);
                ContentValues v = new ContentValues();
                v.put(DatabaseContract.ScoresEntry._ID, ContentUris.parseId(Uri.parse(m.getLinks().getSelf())));
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
                getLeagueInfo(fdc,leagueId,v,provider);

                long homeTeamId = ContentUris.parseId(Uri.parse(m.getLinks().getHomeTeam()));
                getTeamInfo(fdc,homeTeamId, DatabaseContract.ScoresEntry.HOME_CREST, v,provider);

                long awayTeamId = ContentUris.parseId(Uri.parse(m.getLinks().getAwayTeam()));
                getTeamInfo(fdc,awayTeamId, DatabaseContract.ScoresEntry.AWAY_CREST, v,provider);

                SimpleDateFormat sdf = new SimpleDateFormat(FootballDataClient.DEFAULT_DATE_FORMAT, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone(FootballDataClient.DEFAULT_TIME_ZONE));
                Date date = sdf.parse(m.getDate());

                SimpleDateFormat dateFormat = new SimpleDateFormat(getContext().getString(R.string.date_format), Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat(getContext().getString(R.string.time_format), Locale.getDefault());
                timeFormat.setTimeZone(TimeZone.getDefault());
                v.put(DatabaseContract.ScoresEntry.DATE_COL, dateFormat.format(date));
                v.put(DatabaseContract.ScoresEntry.TIME_COL, timeFormat.format(date));
                vals[i]=v;
            }
            int insCnt = provider.bulkInsert(DatabaseContract.ScoresEntry.CONTENT_URI, vals);
            Log.i(TAG, "Inserted "+insCnt+" records");
        } catch (IOException | ParseException | RemoteException e) {
            Log.e(TAG, e.getMessage(),e);
        }
    }

    void getTeamInfo(FootballDataClient fdc, long teamId, String column, ContentValues v, ContentProviderClient provider) throws IOException, RemoteException {
        Cursor c = provider.query(DatabaseContract.TeamEntry.buildTeamWithId(teamId), null, null, null, null);
        if (c==null || !c.moveToFirst()){
            Team team = fdc.getTeam(String.valueOf(teamId));
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.TeamEntry._ID,teamId);
            values.put(DatabaseContract.TeamEntry.NAME_COL, team.getName());
            values.put(DatabaseContract.TeamEntry.SHORT_NAME_COL, team.getShortName());
            values.put(DatabaseContract.TeamEntry.CREST_URL_COL, team.getCrestUrl());
            Uri newItemUri = provider.insert(DatabaseContract.TeamEntry.CONTENT_URI, values);
            c = provider.query(newItemUri, null, null, null, null);
        }

        if (c.moveToFirst()) {
            String crestUrl = c.getString(c.getColumnIndex(DatabaseContract.TeamEntry.CREST_URL_COL));
            v.put(column, crestUrl);
            //Log.i(TAG,column+": "+crestUrl);
        }else{
            Log.e(TAG, "Empty response for " + column);
        }
        c.close();
    }

    void getLeagueInfo(FootballDataClient fdc, long leagueId, ContentValues v, ContentProviderClient provider) throws IOException, RemoteException {
        Cursor c = provider.query(
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
                Uri newItemUri = provider.insert(DatabaseContract.LeagueEntry.CONTENT_URI, values);
                c = provider.query(newItemUri, null, null, null, null);
            }
        }

        if (c.moveToFirst()) {
            String leagueName = c.getString(c.getColumnIndex(DatabaseContract.LeagueEntry.NAME_COL));
            v.put(DatabaseContract.ScoresEntry.LEAGUE_COL, leagueName);
            v.put(DatabaseContract.ScoresEntry.LEAGUE_ID_COL, leagueId);
            //Log.i(TAG, "League: " + leagueName + ", " + leagueId);
        }else{
            Log.e(TAG, "Empty response for league");
            v.put(DatabaseContract.ScoresEntry.LEAGUE_COL, getContext().getString(R.string.league_not_known));
            v.put(DatabaseContract.ScoresEntry.LEAGUE_ID_COL, -1);
        }
        c.close();
    }
}
