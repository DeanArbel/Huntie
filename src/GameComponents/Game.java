package GameComponents;

import Util.Enums.GameStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Dean on 18/2/2017.
 */
public class Game {
    private final String r_GameId;
    private final List<String> r_Managers = new ArrayList<>();
    private final List<Team> r_Teams = new ArrayList<>();
    private final List<List<Riddle>> r_Riddles = new ArrayList<>(Riddle.MAX_APPEARANCE + 1);
    private int m_MaxPlayers = 20;
    private int m_MaxPayersInTeam = 2;
    private String m_GameArea;
    private String m_TreasureType;
    private Date m_StartDate;
    private float m_Duration;
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

    public int getMaxPlayersInTeam() {
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

    public void ClearRiddles() { r_Riddles.clear(); }

    public void AddRiddle(Riddle riddle) {
        int riddleIndex = riddle.getAppearanceNumber();
        if (r_Riddles.size() <= riddleIndex) {
            for (int i = r_Riddles.size(); i < riddleIndex; i++) {
                r_Riddles.add(i, null);
            }
            r_Riddles.add(riddleIndex, new ArrayList<>());
        }
        else if (r_Riddles.get(riddleIndex) == null) {
            r_Riddles.add(riddleIndex, new ArrayList<>());
        }
        r_Riddles.get(riddleIndex).add(riddle);
    }

    public void DeleteRiddle(int appearanceNumber, int index) {
        r_Riddles.get(appearanceNumber).remove(index);
    }

    public Date getStartDate() {
        return m_StartDate;
    }

    public void setStartDate(Date i_Date) {
        this.m_StartDate = i_Date;
    }

    public float getDuration() {
        return m_Duration;
    }

    public void setDuration(float i_Duration) {
        this.m_Duration = i_Duration;
    }

    public String getTreasureType() {
        return m_TreasureType;
    }

    public void setTreasureType(String i_TreasureType) {
        this.m_TreasureType = i_TreasureType;
    }

    public GameStatus getGameStatus() {
        return m_GameStatus;
    }

    public void setGameStatus(GameStatus i_GameStatus) {
        this.m_GameStatus = i_GameStatus;
    }

    public boolean IsPlayerInGame(String i_UserId) {
        boolean result = false;
        for (Team team : r_Teams) {
            if (team.IsPlayerInTeam(i_UserId)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
