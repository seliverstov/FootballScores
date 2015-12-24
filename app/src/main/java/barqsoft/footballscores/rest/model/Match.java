package barqsoft.footballscores.rest.model;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Match {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("soccerseasonId")
    @Expose
    private Integer soccerseasonId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("matchday")
    @Expose
    private Integer matchday;
    @SerializedName("homeTeamName")
    @Expose
    private String homeTeamName;
    @SerializedName("homeTeamId")
    @Expose
    private Integer homeTeamId;
    @SerializedName("awayTeamName")
    @Expose
    private String awayTeamName;
    @SerializedName("awayTeamId")
    @Expose
    private Integer awayTeamId;
    @SerializedName("result")
    @Expose
    private MatchResult matchResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSoccerseasonId() {
        return soccerseasonId;
    }

    public void setSoccerseasonId(Integer soccerseasonId) {
        this.soccerseasonId = soccerseasonId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public Integer getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Integer homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Integer getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Integer awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

}
