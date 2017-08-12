package GameComponents;

import Util.DatabaseFacade;
import Util.Enums.GameStatus;

import java.util.*;

/**
 * Created by Dean on 18/2/2017.
 */
public class Game {
    private final String r_GameId;
    private final List<String> r_Managers = new ArrayList<>();
    private final List<Team> r_Teams = new ArrayList<>();
    private List<List<Riddle>> m_Riddles = new ArrayList<>(Riddle.MAX_APPEARANCE + 1);
    private int m_MaxPlayers = 20;
    private int m_MaxPayersInTeam = 2;
    private int m_PlayersInGame;
    private String m_GameName;
    private String m_GameArea;
    private String m_TreasureType;
    private Date m_StartDate;
    private Date m_EndDate;
    private GameStatus m_GameStatus = GameStatus.IN_CREATION;
    private boolean m_IsTeamGame = false;

    public Game(String i_GameId, String i_ManagerId) {
        r_GameId = i_GameId;
        r_Managers.add(i_ManagerId);
    }

    public String GetGameName() {
        return m_GameName;
    }

    public void SetGameName(String i_GameName) {
        if (i_GameName == null || i_GameName.isEmpty()) {
            m_GameName = "Game " + r_GameId;
        }
        else {
            m_GameName = i_GameName;
        }
    }

    public String GetGameId() {
        return r_GameId;
    }

    public int GetMaxPlayers() {
        return m_MaxPlayers;
    }

    public void SetMaxPlayers(int i_MaxPlayers) {
        this.m_MaxPlayers = i_MaxPlayers;
    }

    public int GetMaxPlayersInTeam() {
        return m_MaxPayersInTeam;
    }

    public void SetMaxPayersInTeam(int i_MaxPayersInTeam) {
        this.m_MaxPayersInTeam = i_MaxPayersInTeam;
    }

    public Collection<Team> GetTeams() {
        return r_Teams;
    }

    public List<String> GetTeamNames() {
        List<String> teamNames = new ArrayList<>();
        for(Team team : r_Teams) {
            teamNames.add(team.GetTeamName());
        }

        return teamNames;
    }

    public boolean IsTeamGame() {
        return m_IsTeamGame;
    }

    public void SetIsTeamGame(boolean i_IsTeamGame) {
        this.m_IsTeamGame = i_IsTeamGame;
    }

    public void SetTeamNames(Set<String> teamNames) {
        r_Teams.clear();
        for(String team : teamNames) {
            r_Teams.add(new Team(team));
        }
    }

    public List<List<Riddle>> GetRiddles() {
        return m_Riddles;
    }

    public void ClearRiddles() { m_Riddles.clear(); }

    public void AddRiddle(Riddle riddle) {
        int riddleIndex = riddle.getAppearanceNumber();
        if (m_Riddles.size() <= riddleIndex) {
            for (int i = m_Riddles.size(); i < riddleIndex; i++) {
                m_Riddles.add(i, null);
            }
            m_Riddles.add(riddleIndex, new ArrayList<>());
        }
        else if (m_Riddles.get(riddleIndex) == null) {
            m_Riddles.set(riddleIndex, new ArrayList<>());
        }
        m_Riddles.get(riddleIndex).add(riddle);
    }

    public void DeleteRiddle(int appearanceNumber, int index) {
        m_Riddles.get(appearanceNumber).remove(index);
    }

    public void AddPlayer(String i_PlayerToAdd, int i_TeamIdx) {
        //TODO: In the future add not manager check
        if (!IsGameFull()) {
            Team team = r_Teams.get(i_TeamIdx);
            User player = DatabaseFacade.GetUser(i_PlayerToAdd);
            if (m_IsTeamGame && team.Count() >= m_MaxPayersInTeam) {
                throw new ArrayIndexOutOfBoundsException("Team has reached max size");
            }
            else {
                int riddleLevel = m_IsTeamGame ? 0 : m_Riddles.get(0).size();
                team.AddPlayer(i_PlayerToAdd, riddleLevel);
                m_PlayersInGame++;
            }
            player.JoinGameAsPlayer(r_GameId, m_GameName);
        }
        else {
            throw new ArrayIndexOutOfBoundsException("Game has reached max player size");
        }
    }

    public Date GetStartTime() {
        return m_StartDate;
    }

    public Date GetEndTime() { return m_EndDate; }

    public void SetStartDate(Date i_Date) {
        this.m_StartDate = i_Date;
    }

    public void SetDuration(double i_Duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(m_StartDate);
        calendar.add(Calendar.HOUR_OF_DAY, (int)i_Duration);
        calendar.add(Calendar.MINUTE, (int)((((int)i_Duration) - i_Duration)*60));
        m_EndDate = calendar.getTime();
    }

    public String GetTreasureType() {
        return m_TreasureType;
    }

    public void SetTreasureType(String i_TreasureType) {
        this.m_TreasureType = i_TreasureType;
    }

    public GameStatus GetGameStatus() {
        return m_GameStatus;
    }

    public void SetGameStatus(GameStatus i_GameStatus) {
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

    public boolean IsUserManager(String i_UserId) {
        boolean result = false;
        for (String userId : r_Managers) {
            if (i_UserId.equals(userId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean IsGameFull() {
        return m_PlayersInGame >= m_MaxPlayers;
    }

    public int GetRiddlesCount(int i_Idx) {
        return m_Riddles.get(i_Idx).size();
    }

    public void PublishGame() {
        List<List<Riddle>> condensedRiddles = new ArrayList<>();
        for (List<Riddle> riddles: m_Riddles) {
            if (riddles != null) {
                condensedRiddles.add(riddles);
            }
        }
        m_Riddles = condensedRiddles;
        if (m_IsTeamGame) {
            int firstLevelRiddlesLength = m_Riddles.get(0).size();
            for(Team team : r_Teams) {
                team.InitTeam(firstLevelRiddlesLength);
            }
        }
        for (String managerId : r_Managers) {
            DatabaseFacade.GetUser(managerId).AddGameToManagerList(r_GameId, m_GameName);
        }

        m_GameStatus = GameStatus.CREATION_COMPLETE;
    }

    public List<Riddle> GetUserRiddlesToSolve(String i_UserId) {
        List<Riddle> riddlesToSolve;
        if (m_IsTeamGame) {
            riddlesToSolve = getUserRiddlesToSolveTeam(i_UserId);
        }
        else {
            riddlesToSolve = getUserRiddlesToSolveSolo(i_UserId);
        }

        return riddlesToSolve;
    }

    public Riddle GetUserRiddleByIndex(String i_UserId, int i_Index) {
        return GetUserRiddlesToSolve(i_UserId).get(i_Index);
    }

    public boolean TryToSolveRiddle(String i_UserId, int i_Index, String i_Answer) {
        List<Riddle> riddles = GetUserRiddlesToSolve(i_UserId);
        Riddle riddleToBeSolved = riddles.get(i_Index);
        boolean userSolvedRiddle = false;
        assertRiddleCanBeSolved(riddleToBeSolved, i_UserId); // Al
        if (riddleToBeSolved.CheckAnswer(i_Answer)) {
            Team playerTeam = getPlayerTeam(i_UserId);
            if (m_IsTeamGame) {
                // Updates riddles solved and to be solved for team
                if (!riddles.get(i_Index).IsSolvedByTeam(playerTeam.GetTeamName())) {
                    int nextLevel = playerTeam.GetTeamRiddleLevel() + 1;
                    Integer nextRiddleSetSize = m_Riddles.size() > nextLevel ? m_Riddles.get(nextLevel).size() : null;
                    playerTeam.TeamSolvedRiddle(i_UserId, nextRiddleSetSize);
                }
            }
            // Updates riddles solved and to be solved for player
            else {
                Integer nextRiddleSetSize = playerTeam.GetPlayerRiddleLevel(i_UserId) + 1;
                nextRiddleSetSize = m_Riddles.size() > nextRiddleSetSize ? nextRiddleSetSize : null;
                playerTeam.PlayerSolvedRiddle(i_UserId, nextRiddleSetSize);
            }

            riddleToBeSolved.UserSolvedRiddle(i_UserId, playerTeam.GetTeamName());
            userSolvedRiddle = true;
        }

        return userSolvedRiddle;
    }

    private void assertRiddleCanBeSolved(Riddle i_RiddleToBeSolved, String i_UserId) {
        if (i_RiddleToBeSolved.IsSolvedPlayer(i_UserId)) {
            throw new IllegalStateException("User had already solved the riddle!");
        }
    }

    private List<Riddle> getUserRiddlesToSolveSolo(String i_UserId) {
        List<Riddle> riddlesToSolve = new ArrayList<>();
        Team playerTeam = getPlayerTeam(i_UserId);
        if (!playerTeam.HasPlayerWon(i_UserId)) {
            Integer riddleLevel = playerTeam.GetPlayerRiddleLevel(i_UserId);
            for (Riddle riddle : m_Riddles.get(riddleLevel)) {
                if (!riddle.IsSolvedPlayer(i_UserId)) {
                    riddlesToSolve.add(riddle);
                }
            }
        }

        return riddlesToSolve;
    }

    private List<Riddle> getUserRiddlesToSolveTeam(String i_UserId) {
        List<Riddle> riddlesToSolve = new ArrayList<>();
        Team playerTeam = getPlayerTeam(i_UserId);
        if (!playerTeam.HasTeamWon()) {
            for (Riddle riddle : m_Riddles.get(playerTeam.GetTeamRiddleLevel())) {
                if (!riddle.IsSolvedByTeam(playerTeam.GetTeamName())) {
                    riddlesToSolve.add(riddle);
                }
            }
        }

        return riddlesToSolve;
    }

    private int getTeamRiddleLevel(String i_TeamName) {
        int teamRiddleLevel = -1;
        for (Team team : r_Teams) {
            if (team.GetTeamName().equals(i_TeamName)) {
                teamRiddleLevel = team.GetTeamRiddleLevel();
            }
        }
        //TODO: Change this so it throws exception if team not found
        return teamRiddleLevel;
    }

    private Integer getPlayerRiddleLevel(String i_UserId) {
        Team playerTeam = getPlayerTeam(i_UserId);
        return playerTeam.GetPlayerRiddleLevel(i_UserId);
    }

    private Team getPlayerTeam(String i_UserId) {
        Team playerTeam = null;
        for (Team team : r_Teams) {
            if (team.IsPlayerInTeam(i_UserId)) {
                playerTeam = team;
                break;
            }
        }

        return playerTeam;
    }

    public Map<String,Integer> GetPlayerTeamScore(String i_Userid) {
        Team team = getPlayerTeam(i_Userid);
        return team.GetTeamScores();
    }

    public Map<String, Integer> GetOtherTeamsScore(String i_Userid) {
        Team playerTeam = getPlayerTeam(i_Userid);
        Map<String, Integer> teamsScores = new HashMap<>();
        for (Team team : r_Teams) {
            if (team != playerTeam) {
                teamsScores.put(team.GetTeamName(), team.GetTeamRiddleLevel());
            }
        }

        return teamsScores;
    }

    public boolean HasPlayerWon(String i_UserId) {
        Team playerTeam = getPlayerTeam(i_UserId);
        if (m_IsTeamGame) {
            return playerTeam.HasTeamWon();
        }
        else {
            return playerTeam.HasPlayerWon(i_UserId);
        }
    }

    public String GetPlayerTeamName(String i_Userid) {
        Team team = getPlayerTeam(i_Userid);
        return team.GetTeamName();
    }
}
