package barqsoft.footballscores.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */
public class Links {
    public static final String KEY = "href";

    @SerializedName("self")
    @Expose
    private Map<String,String> self;

    @SerializedName("soccerseason")
    @Expose
    private Map<String,String> soccerseason;

    @SerializedName("homeTeam")
    @Expose
    private Map<String,String> homeTeam;

    @SerializedName("awayTeam")
    @Expose
    private Map<String,String> awayTeam;

    @SerializedName("teams")
    @Expose
    private Map<String,String> teams;

    @SerializedName("fixtures")
    @Expose
    private Map<String,String> fixtures;

    @SerializedName("leagueTable")
    @Expose
    private Map<String,String> leagueTable;

    @SerializedName("players")
    @Expose
    private Map<String,String> players;

    public String getSelf(){
        return self.get(KEY);
    }

    public String getSoccerSeason(){
        return soccerseason.get(KEY);
    }

    public String getHomeTeam(){
        return  homeTeam.get(KEY);
    }

    public String getAwayTeam(){
        return awayTeam.get(KEY);
    }

    public String getTeams(){
        return teams.get(KEY);
    }

    public String getFixtures(){
        return fixtures.get(KEY);
    }

    public String getLeagueTable(){
        return leagueTable.get(KEY);
    }

    public String getPlayers(){
        return players.get(KEY);
    }
}
