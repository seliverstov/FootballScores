package barqsoft.footballscores.rest;

import java.util.List;

import barqsoft.footballscores.rest.model.Fixtures;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Team;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */
public interface FootbalDataService {


    @GET("/v1/fixtures/")
    Call<Fixtures> getFixtures(@Query("timeFrame") String timeFrame, @Header("X-Auth-Token") String apiKey);

    @GET("/v1/teams/{id}")
    Call<Team> getTeam(@Path("id") String id,@Header("X-Auth-Token") String apiKey);

    @GET("/v1/soccerseasons")
    Call<List<League>> getSeasons(@Query("season") String season, @Header("X-Auth-Token") String apiKey);
}
