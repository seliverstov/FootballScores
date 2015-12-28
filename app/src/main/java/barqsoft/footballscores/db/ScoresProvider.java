package barqsoft.footballscores.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider
{
    private static ScoresDBHelper mOpenHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final int TEAMS = 200;
    private static final int TEAMS_WITH_ID = 201;
    private static final int LEAGUES = 300;
    private static final int LEAGUES_WITH_ID = 301;
    private UriMatcher mUriMatcher = buildUriMatcher();

    private static final String SCORES_BY_LEAGUE = DatabaseContract.ScoresEntry.LEAGUE_COL + " = ?";

    private static final String SCORES_BY_DATE =
            DatabaseContract.ScoresEntry.DATE_COL + " LIKE ?";

    private static final String SCORES_BY_ID =
            DatabaseContract.ScoresEntry.MATCH_ID + " = ?";

    private static final String TEAMS_BY_ID =
            DatabaseContract.ScoresEntry._ID + " = ?";

    private static final String LEAGUES_BY_ID =
            DatabaseContract.ScoresEntry._ID + " = ?";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority =  DatabaseContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DatabaseContract.SCORES_PATH , MATCHES);
        matcher.addURI(authority, DatabaseContract.SCORES_PATH+"/league" , MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, DatabaseContract.SCORES_PATH+"/id" , MATCHES_WITH_ID);
        matcher.addURI(authority, DatabaseContract.SCORES_PATH+"/date" , MATCHES_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.TEAMS_PATH, TEAMS);
        matcher.addURI(authority, DatabaseContract.TEAMS_PATH+"/#" , TEAMS_WITH_ID);
        matcher.addURI(authority, DatabaseContract.LEAGUES_PATH, LEAGUES);
        matcher.addURI(authority, DatabaseContract.LEAGUES_PATH+"/#" , LEAGUES_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        return 0;
    }

    @Override
    public String getType(Uri uri){
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.ScoresEntry.SCORES_CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.ScoresEntry.SCORES_CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.ScoresEntry.SCORES_CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.ScoresEntry.SCORES_CONTENT_TYPE;
            case TEAMS:
                return DatabaseContract.TeamEntry.TEAMS_CONTENT_TYPE;
            case TEAMS_WITH_ID:
                return DatabaseContract.TeamEntry.TEAMS_CONTENT_ITEM_TYPE;
            case LEAGUES:
                return DatabaseContract.LeagueEntry.LEAGUES_CONTENT_TYPE;
            case LEAGUES_WITH_ID:
                return DatabaseContract.LeagueEntry.LEAGUES_CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        switch (mUriMatcher.match(uri)){
            case MATCHES:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            DatabaseContract.SCORES_TABLE,
                            projection,null,null,null,null,sortOrder);
                    break;
            case MATCHES_WITH_DATE:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,SCORES_BY_DATE,selectionArgs,null,null,sortOrder);
                    break;
            case MATCHES_WITH_ID:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,SCORES_BY_ID,selectionArgs,null,null,sortOrder);
                    break;
            case MATCHES_WITH_LEAGUE:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,SCORES_BY_LEAGUE,selectionArgs,null,null,sortOrder);
                    break;
            case TEAMS:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            DatabaseContract.TEAMS_TABLE,
                            projection,selection,selectionArgs,null,null,sortOrder);
                    break;
            case TEAMS_WITH_ID:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.TEAMS_TABLE,
                        projection,TEAMS_BY_ID,new String[]{String.valueOf(ContentUris.parseId(uri))},null,null,null);
                    break;
            case LEAGUES:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            DatabaseContract.LEAGUES_TABLE,
                            projection,selection,selectionArgs,null,null,sortOrder);
                    break;
            case LEAGUES_WITH_ID:
                    retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.LEAGUES_TABLE,
                        projection,LEAGUES_BY_ID,new String[]{String.valueOf(ContentUris.parseId(uri))},null,null,null);
                    break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (mUriMatcher.match(uri)){
            case TEAMS: {
                long _id = db.insertWithOnConflict(DatabaseContract.TEAMS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                returnUri = ContentUris.withAppendedId(DatabaseContract.TeamEntry.CONTENT_URI, _id);
                break;
            }
            case LEAGUES: {
                long _id = db.insertWithOnConflict(DatabaseContract.LEAGUES_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                returnUri = ContentUris.withAppendedId(DatabaseContract.LeagueEntry.CONTENT_URI, _id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)){
            case MATCHES:
                db.beginTransaction();
                int returncount = 0;
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returncount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
