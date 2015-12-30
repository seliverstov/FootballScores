package barqsoft.footballscores.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract
{
    public static final String SCORES_TABLE = "scores_table";
    public static final String TEAMS_TABLE = "teams_table";
    public static final String LEAGUES_TABLE = "leagues_table";

    public static final class TeamEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TEAMS_PATH).build();
        public static final String NAME_COL="name";
        public static final String SHORT_NAME_COL="short_name";
        public static final String CREST_URL_COL="crest_url";

        public static final String TEAMS_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAMS_PATH;

        public static final String TEAMS_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAMS_PATH;

        public static Uri buildTeamWithId(long id){
            return BASE_CONTENT_URI.buildUpon().appendPath(TEAMS_PATH).appendPath(String.valueOf(id)).build();
        }
    }

    public static final class LeagueEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(LEAGUES_PATH).build();
        public static final String NAME_COL = "name";
        public static final String SHORT_NAME_COL = "short_name";
        public static final String YEAR_COL = "year";

        public static final String LEAGUES_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + LEAGUES_PATH;

        public static final String LEAGUES_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + LEAGUES_PATH;

        public static Uri buildLeagueWithId(long id){
            return BASE_CONTENT_URI.buildUpon().appendPath(LEAGUES_PATH).appendPath(String.valueOf(id)).build();
        }
    }

    public static final class ScoresEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SCORES_PATH).build();

        public static final String LEAGUE_COL = "league";
        public static final String LEAGUE_ID_COL = "league_id";
        public static final String DATE_COL = "matchTime";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";
        public static final String HOME_CREST = "home_crest";
        public static final String AWAY_CREST = "away_crest";

        public static final String SCORES_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH;

        public static final String SCORES_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH;

        /*public static Uri buildScoreWithLeague(){
            return BASE_CONTENT_URI.buildUpon().appendPath(SCORES_PATH).appendPath("league").build();
        }
        public static Uri buildScoreWithId(){
            return BASE_CONTENT_URI.buildUpon().appendPath(SCORES_PATH).appendPath("id").build();
        }*/
        public static Uri buildScoreWithDate(){
            return BASE_CONTENT_URI.buildUpon().appendPath(SCORES_PATH).appendPath("date").build();
        }
    }

    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final String SCORES_PATH = "scores";
    public static final String TEAMS_PATH = "teams";
    public static final String LEAGUES_PATH = "leagues";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
}
