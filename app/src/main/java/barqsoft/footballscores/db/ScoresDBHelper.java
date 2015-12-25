package barqsoft.footballscores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import barqsoft.footballscores.db.DatabaseContract.ScoresEntry;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper{
    public static final String TAG = ScoresDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;

    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        final String CREATE_SCORES_SQL = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + ScoresEntry._ID + " INTEGER PRIMARY KEY,"
                + ScoresEntry.DATE_COL + " TEXT NOT NULL,"
                + ScoresEntry.TIME_COL + " INTEGER NOT NULL,"
                + ScoresEntry.HOME_COL + " TEXT NOT NULL,"
                + ScoresEntry.AWAY_COL + " TEXT NOT NULL,"
                + ScoresEntry.LEAGUE_COL + " TEXT NOT NULL,"
                + ScoresEntry.LEAGUE_ID_COL + " INTEGER NOT NULL,"
                + ScoresEntry.HOME_GOALS_COL + " TEXT NOT NULL,"
                + ScoresEntry.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoresEntry.MATCH_ID + " INTEGER NOT NULL,"
                + ScoresEntry.MATCH_DAY + " INTEGER NOT NULL,"
                + ScoresEntry.HOME_CREST+ " TEXT,"
                + ScoresEntry.AWAY_CREST+ " TEXT,"
                + " UNIQUE ("+ ScoresEntry.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        Log.i(TAG, "Create table "+DatabaseContract.SCORES_TABLE);
        db.execSQL(CREATE_SCORES_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.i(TAG, "Drop table "+DatabaseContract.SCORES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
        onCreate(db);
    }
}
