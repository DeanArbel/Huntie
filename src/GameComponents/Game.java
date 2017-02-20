package GameComponents;

import Util.Enums.GameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Dean on 18/2/2017.
 */
public class Game {
    private final String r_GameId;
    private final List<String> r_Managers = new ArrayList<>();
    private final List<Team> r_Teams = new ArrayList<>();
    private final List<List<Riddle>> r_Riddles = new ArrayList<>();
    private int m_MaxPlayers = 20;
    private int m_MaxPayersInTeam = 2;
    private String m_GameArea;
    private GameStatus m_GameStatus = GameStatus.IN_CREATION;
    private boolean m_IsTeamGame = true;

    public Game(String i_GameId, String i_ManagerId) {
        r_GameId = i_GameId;
        r_Managers.add(i_ManagerId);
    }

    public String getGameId() {
        return r_GameId;
    }

    public int getMaxPlayers() {
        return m_MaxPlayers;
    }

    public void setMaxPlayers(int i_MaxPlayers) {
        this.m_MaxPlayers = i_MaxPlayers;
    }

    public int getMaxPayersInTeam() {
        return m_MaxPayersInTeam;
    }

    public void setMaxPayersInTeam(int i_MaxPayersInTeam) {
        this.m_MaxPayersInTeam = i_MaxPayersInTeam;
    }

    public List<Team> getTeams() {
        return r_Teams;
    }

    public List<String> getTeamNames() {
        List<String> teamNames = new ArrayList<>();
        for(Team team : r_Teams) {
            teamNames.add(team.getTeamName());
        }

        return teamNames;
    }

    public boolean isTeamGame() {
        return m_IsTeamGame;
    }

    public void setIsTeamGame(boolean i_IsTeamGame) {
        this.m_IsTeamGame = i_IsTeamGame;
    }

    public void setTeamNames(Set<String> teamNames) {
        r_Teams.clear();
        for(String team : teamNames) {
            r_Teams.add(new Team(team));
        }
    }

    public List<List<Riddle>> getRiddles() {
        return r_Riddles;
    }
}
