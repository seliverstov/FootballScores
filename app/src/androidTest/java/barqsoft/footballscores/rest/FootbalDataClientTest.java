package barqsoft.footballscores.rest;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.rest.model.League;
import barqsoft.footballscores.rest.model.Links;
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
        List<Match> matches = client.listMatches("n7");
        assertNotNull(matches);
        Log.i(TAG, "Matches size: "+matches.size());
        for(Match m: matches){
            assertNotNull(m.getStatus());
            assertNotNull(m.getAwayTeamName());
            assertNotNull(m.getDate());
            assertNotNull(m.getHomeTeamName());
            assertNotNull(m.getMatchday());
            assertNotNull(m.getMatchResult());
            Links links = m.getLinks();
            assertNotNull(links);
            assertNotNull(links.getSelf());
            assertNotNull(links.getSoccerSeason());
            assertNotNull(links.getHomeTeam());
            assertNotNull(links.getAwayTeam());
        }
    }

    public void testListLeagues() throws IOException {
        List<League> leagues = client.listLeagues("2015");
        assertNotNull(leagues);
        assertEquals(12, leagues.size());
        for(League l: leagues){
            assertNotNull(l.getCaption());
            assertNotNull(l.getLastUpdated());
            assertNotNull(l.getLeague());
            assertNotNull(l.getNumberOfGames());
            assertNotNull(l.getNumberOfTeams());
            assertNotNull(l.getYear());
            Links links = l.getLinks();
            assertNotNull(links);
            assertNotNull(links.getSelf());
            assertNotNull(links.getTeams());
            assertNotNull(links.getFixtures());
            assertNotNull(links.getLeagueTable());
        }
    }

    public void testGetLeague() throws IOException {
        League league = client.getLeague("398");
        assertNotNull(league);
        assertEquals("Premier League 2015/16", league.getCaption());
        assertEquals("PL", league.getLeague());
        assertEquals("2015",league.getYear());
        assertEquals(new Integer(20),league.getNumberOfTeams());
        assertEquals(new Integer(380),league.getNumberOfGames());
        assertEquals("2015-12-21T21:26:51Z", league.getLastUpdated());
        Links links = league.getLinks();
        assertNotNull(links);
        assertEquals("http://api.football-data.org/v1/soccerseasons/398", links.getSelf());
        assertEquals("http://api.football-data.org/v1/soccerseasons/398/teams",links.getTeams());
        assertEquals("http://api.football-data.org/v1/soccerseasons/398/fixtures",links.getFixtures());
        assertEquals("http://api.football-data.org/v1/soccerseasons/398/leagueTable",links.getLeagueTable());
    }

    public void testGetTeam() throws IOException {
        Team team = client.getTeam("66");
        assertNotNull(team);
        assertEquals("http://upload.wikimedia.org/wikipedia/de/d/da/Manchester_United_FC.svg", team.getCrestUrl());
        assertNotNull("Manchester United FC", team.getName());
        assertNotNull("ManU",team.getShortName());
        assertNotNull("377,250,000 €",team.getSquadMarketValue());
        assertNotNull("MUFC",team.getCodel());
        Links links = team.getLinks();
        assertNotNull(links);
        assertEquals("http://api.football-data.org/v1/teams/66",links.getSelf());
        assertEquals("http://api.football-data.org/v1/teams/66/fixtures",links.getFixtures());
        assertEquals("http://api.football-data.org/v1/teams/66/players",links.getPlayers());
    }
}
