package barqsoft.footballscores.rest;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Match;
import barqsoft.footballscores.rest.model.Team;

/**
 * Created by a.g.seliverstov on 24.12.2015.
 */
public class FootbalDataClientTest extends AndroidTestCase {
    private static final String TAG = FootbalDataClientTest.class.getSimpleName();

    FootbalDataClient client;
    public void setUp(){
        client = new FootbalDataClient(getContext().getString(R.string.api_key));
    }

    public void testListMatches() throws IOException {
        List<Match> matches = client.listMatches(FootbalDataClient.DEFAULT_NEXT_TIMEFRAME);
        assertNotNull(matches);
        for(Match m: matches){
            assertNotNull(m.getAwayTeamId());
            assertNotNull(m.getAwayTeamName());
            assertNotNull(m.getDate());
            assertNotNull(m.getHomeTeamId());
            assertNotNull(m.getHomeTeamName());
            assertNotNull(m.getId());
            assertNotNull(m.getMatchday());
            assertNotNull(m.getSoccerseasonId());
            assertNotNull(m.getMatchResult());
        }
    }

    public void testListSeasons() throws IOException {
        List<League> leagues = client.listLeagues("2015");
        assertNotNull(leagues);
        for(League l: leagues){
            assertNotNull(l.getCaption());
            assertNotNull(l.getLastUpdated());
            assertNotNull(l.getLeague());
            assertNotNull(l.getNumberOfGames());
            assertNotNull(l.getNumberOfTeams());
            assertNotNull(l.getYear());
        }
    }

    public void testGetTeam() throws IOException {
        Team team = client.getTeam("66");
        assertNotNull(team);
        assertNotNull(team.getCrestUrl());
        assertNotNull(team.getName());
        assertNotNull(team.getShortName());
        assertNotNull(team.getSquadMarketValue());
    }
}
