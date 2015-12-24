package barqsoft.footballscores.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */
public class League {
    @SerializedName("_links")
    @Expose
    private Links links;

    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("league")
    @Expose
    private String league;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("numberOfTeams")
    @Expose
    private Integer numberOfTeams;
    @SerializedName("numberOfGames")
    @Expose
    private Integer numberOfGames;
    @SerializedName("lastUpdated")
    @Expose
    private String lastUpdated;

    public Links getLinks(){
        return links;
    }

    public void setLinks(Links links){
        this.links=links;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(Integer numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public Integer getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(Integer numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
