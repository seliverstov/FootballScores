package barqsoft.footballscores.rest;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import barqsoft.footballscores.rest.model.Fixtures;
import barqsoft.footballscores.rest.model.Match;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Team;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */
public class FootbalDataClient {
    private static final String TAG = FootbalDataClient.class.getSimpleName();
    private String apiKey;
    private FootbalDataService service;
    public static final String DEFAULT_PAST_TIMEFRAME = "p2";
    public static final String DEFAULT_NEXT_TIMEFRAME = "n2";
    public static final String BASE_URL = "http://api.football-data.org";

    public FootbalDataClient(String apiKey){
        this.apiKey=apiKey;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(FootbalDataService.class);
    }

    public List<Match> listMatches(String timeFrame) throws IOException {
        Call<Fixtures> call = service.getFixtures(timeFrame, apiKey);
        Response<Fixtures> res = call.execute();
        Log.i(TAG, res.raw().request().urlString());
        Fixtures result = res.body();
        return (result!=null)?result.getMatches():null;
    }

    public List<League> listLeagues(String season) throws IOException {
        Call<List<League>> call = service.getSeasons(season, apiKey);
        Response<List<League>> res = call.execute();
        Log.i(TAG, res.raw().request().urlString());
        return res.body();
    }

    public Team getTeam(String id) throws IOException {
        Call<Team> call = service.getTeam(id,apiKey);
        Response<Team> res = call.execute();
        Log.i(TAG, res.raw().request().urlString());
        return res.body();
    }
}
