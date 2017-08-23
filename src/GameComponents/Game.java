package GameComponents;

import Util.Enums.GameStatus;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Dean on 18/2/2017.
 */
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int m_GameId;//was final String

    @OneToMany
    private final List<User> r_Managers = new ArrayList<>();

    @OneToMany
    private final List<Team> r_Teams = new ArrayList<>();

    @OneToMany
    private List<Level> m_Levels = new ArrayList<>(Riddle.MAX_APPEARANCE + 1);

    private int m_MaxPlayers = 20;
    private int m_MaxPayersInTeam = 2;
    private int m_PlayersInGame;
    private String m_GameName;
    private String m_TreasureType;
    private Date m_StartDate;
    private Date m_EndDate;
    private GameStatus m_GameStatus = GameStatus.IN_CREATION;
    private boolean m_IsTeamGame = false;

    public Game(User i_Manager) {//String i_GameId,
        r_Managers.add(i_Manager);
    }

    public Game(){}

    public String GetGameName() {
        return m_GameName;
    }

    public void SetGameName(String i_GameName) {
        if (i_GameName == null || i_GameName.isEmpty()) {
            m_GameName = "Game " + m_GameId;
        }
        else {
            m_GameName = i_GameName;
        }
    }

    public int GetGameId() {
        return m_GameId;
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
        Team tempTeam;
        r_Teams.clear();
        for(String team : teamNames) {
            tempTeam = new Team();
            tempTeam.SetTeamName(team);
            r_Teams.add(tempTeam);
        }
    }

    public List<Level> GetRiddles() {
        return m_Levels;
    }

    public void ClearRiddles() { m_Levels.clear(); }

    public void ClearLevel(int i_LevelNum){
        for(Level level:m_Levels){
            if(level.GetIndex()==i_LevelNum){
                level.ClearRiddles();
                break;
            }
        }
    }

    public void RemoveLevel(int i_LevelNum){
        for(Level level:m_Levels){
            if(level.GetIndex() == i_LevelNum){
                m_Levels.remove(level);
                break;
            }
        }
    }

    public void AddRiddle(Riddle riddle, int i_LevelNum) {
        Level level = findOrAddLevel(i_LevelNum, true);
        if(!level.IsRiddleInLevel(riddle)) {
            level.AddRiddle(riddle);
            level.SortRiddles();
        }
//        int riddleIndex = riddle.getAppearanceNumber();
//        if (m_Levels.size() <= riddleIndex) {
//            for (int i = m_Levels.size(); i < riddleIndex; i++) {
//                m_Levels.add(i, null);
//            }
//            m_Levels.add(riddleIndex, new ArrayList<>());
//        }
//        else if (m_Levels.get(riddleIndex) == null) {
//            m_Levels.set(riddleIndex, new ArrayList<>());
//        }
//        m_Levels.get(riddleIndex).add(riddle);
    }

    private Level findOrAddLevel(int i_LevelNum, boolean i_IsAddOn){
        for(Level level:m_Levels){
            if(level.GetIndex() == i_LevelNum)
                return level;
        }

        Level level = null;
        if(i_IsAddOn) {
            level = new Level();
            level.SetIndex(i_LevelNum);
        }
        return level;
    }

    public void AddLevel(Level i_Level){
        if(!m_Levels.contains(i_Level)) {
            m_Levels.add(i_Level);
        }
    }

    public void DeleteRiddle(int i_AppearanceNumber, int i_LevelNum) {
        Level level = findOrAddLevel(i_LevelNum,false);
        if(level != null){
            level.RemoveRiddle(i_AppearanceNumber);
        }
//        m_Levels.get(appearanceNumber).remove(index);
    }

    public void AddPlayer(User i_PlayerToAdd, int i_TeamIdx) {
        //TODO: In the future add not manager check
        if (!IsGameFull()) {
            Team team = r_Teams.get(i_TeamIdx);
            //User player = DatabaseFacade.GetUser(i_PlayerToAdd);
            if (m_IsTeamGame && team.Count() >= m_MaxPayersInTeam) {
                throw new ArrayIndexOutOfBoundsException("Team has reached max size");
            }
            else if (m_IsTeamGame) {
//                int riddleLevel = m_IsTeamGame ? 0 : m_Levels.get(0).size();
                team.AddPlayer(i_PlayerToAdd, m_Levels.get(0));//was i_playerToAdd
                m_PlayersInGame++;
            }
            i_PlayerToAdd.JoinGameAsPlayer(this);//was m_GameId
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

    public boolean IsPlayerInGame(User i_User) {
        boolean result = false;
        for (Team team : r_Teams) {
            if (team.IsPlayerInTeam(i_User)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public Boolean IsPlayerInGame(String i_UserId){
        boolean result = false;
        for (Team team : r_Teams) {
            if (team.IsPlayerInTeam(i_UserId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean IsUserManager(String i_UserEmail) {
        boolean result = false;
        for (User user : r_Managers) {
            if (i_UserEmail.equals(user.GetEmailAddress())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean IsGameFull() {
        return m_PlayersInGame >= m_MaxPlayers;
    }

    public int GetRiddlesInLevelCount(int i_Idx) {
        return m_Levels.get(i_Idx).GetRiddlesCount();
    }

    public int GetRiddlesCount(){
        int buffer=0;

        for(int i=0;i<m_Levels.size();i++){
            buffer+=GetRiddlesInLevelCount(i);
        }

        return buffer;
    }

    public void PublishGame() {
        List<Level> condensedRiddles = new ArrayList<>();
        for (Level level: m_Levels) {
            if (level != null && level.GetRiddlesCount() > 0) {
                condensedRiddles.add(level);
            }
        }
        m_Levels = condensedRiddles;
//        if (m_IsTeamGame) {
//            int firstLevelRiddlesLength = m_Levels.get(0).GetRiddlesCount();
//            for(Team team : r_Teams) {
//                team.InitTeam(firstLevelRiddlesLength);
//            }
//        }
        for (User manager : r_Managers) {
           // DatabaseFacade.GetUser(manager.GetEmailAddress()).AddGameToManagerList(m_GameId, m_GameName);//todo recheck
            manager.AddGameToManagerList(this);
        }

        m_GameStatus = GameStatus.CREATION_COMPLETE;
    }

    public List<Riddle> GetUserRiddlesToSolve(User i_User) {
        List<Riddle> riddlesToSolve;
        if (m_IsTeamGame) {
            riddlesToSolve = getUserRiddlesToSolveTeam(i_User);
        }
        else {
            riddlesToSolve = getUserRiddlesToSolveIndividual(i_User);
        }

        return riddlesToSolve;
    }

//    public Riddle GetUserRiddleByIndex(User i_User, int i_Index) {//String i_UserId
//        return GetUserRiddlesToSolve(i_UserId).get(i_Index);
//    }

    public boolean TryToSolveRiddle(User i_User, Riddle i_Riddle, String i_Answer) {
        boolean userSolvedRiddle = false;
        assertRiddleCanBeSolved(i_Riddle, i_User); // Al
        if (i_Riddle.CheckAnswer(i_Answer)) {
            Team playerTeam = getPlayerTeam(i_User);
            int nextLevel = playerTeam.GetTeamRiddleLevel() + 1;
            if (m_IsTeamGame) {
                // Updates riddles solved and to be solved for team
                if (i_Riddle.IsSolvedByTeam(playerTeam)) {//was playerTeam.GetTeamName()
                    //Integer nextRiddleSetSize = m_Levels.size() > nextLevel ? m_Levels.get(nextLevel).size() : null;
                    playerTeam.TeamSolvedRiddle(m_Levels.get(nextLevel));
                }
            }
            // Updates riddles solved and to be solved for player
            else {
                //Integer nextRiddleSetSize = playerTeam.GetPlayerRiddleLevel(i_User) + 1;
                //nextRiddleSetSize = m_Levels.size() > nextRiddleSetSize ? nextRiddleSetSize : null;
                playerTeam.PlayerSolvedRiddle(i_User, m_Levels.get(nextLevel));
            }

            i_Riddle.UserSolvedRiddle(i_User, playerTeam);//was i_UserId, playerTeam.GetTeamName()
            userSolvedRiddle = true;
        }

        return userSolvedRiddle;
    }

    private void assertRiddleCanBeSolved(Riddle i_RiddleToBeSolved, User i_User) {
        if (i_RiddleToBeSolved.IsSolvedPlayer(i_User)) {
            throw new IllegalStateException("User had already solved the riddle!");
        }
    }

    private List<Riddle> getUserRiddlesToSolveIndividual(User i_User) {
        List<Riddle> riddlesToSolve = new ArrayList<>();
        Team playerTeam = getPlayerTeam(i_User);
        if (!playerTeam.HasPlayerWon(i_User)) {
            Integer riddleLevel = playerTeam.GetPlayerRiddleLevel(i_User);
            return m_Levels.get(riddleLevel).GetRiddlesNotSolvedByPlayer(i_User);
        }

        return riddlesToSolve;
    }

    private List<Riddle> getUserRiddlesToSolveTeam(User i_User) {
        List<Riddle> riddlesToSolve = new ArrayList<>();
        Team playerTeam = getPlayerTeam(i_User);
        if (!playerTeam.HasTeamWon()) {
            return m_Levels.get(getTeamRiddleLevel(playerTeam.GetTeamName())).GetRiddlesNotSolvedByTeam(playerTeam);
        }

        return riddlesToSolve;
    }

    private int getTeamRiddleLevel(String i_TeamName) {
        int teamRiddleLevel = -1;
        for (Team team : r_Teams) {
            if (team.GetTeamName().equals(i_TeamName)) {
                teamRiddleLevel = team.GetTeamRiddleLevel();
                break;
            }
        }
        //TODO: Change this so it throws exception if team not found
        return teamRiddleLevel;
    }

//    private Integer getPlayerRiddleLevel(String i_UserId) {
//        Team playerTeam = getPlayerTeam(i_UserId);
//        return playerTeam.GetPlayerRiddleLevel(i_UserId);
//    }

    private Team getPlayerTeam(User i_User) {
        Team playerTeam = null;
        for (Team team : r_Teams) {
            if (team.IsPlayerInTeam(i_User)) {
                playerTeam = team;
                break;
            }
        }

        return playerTeam;
    }

    public Map<User,Integer> GetPlayerTeamScore(User i_User) {///TODO change to ScoreCurrentLevel or remove
        Team team = getPlayerTeam(i_User);
        return team.GetTeamScoresForCurrentLevel();
    }

    public int GetPlayerScore(User i_User){
        int score = 0;

        for(Level level:m_Levels){
            score += level.GetRiddlesSolvedByPlayer(i_User).size();
        }

        return score;
    }

    public int GetTeamScore(Team i_Team){
        int score = 0;

        for(Level level:m_Levels){
            score += level.GetRiddleSolvedByTeam(i_Team).size();
        }

        return score;
    }

    public Map<String, Integer> GetOtherTeamsScore(User i_User) {//TODO change score to level?
        Team playerTeam = getPlayerTeam(i_User);
        Map<String, Integer> teamsScores = new HashMap<>();
        for (Team team : r_Teams) {
            if (team != playerTeam) {
                teamsScores.put(team.GetTeamName(), team.GetTeamRiddleLevel());
            }
        }

        return teamsScores;
    }

    public boolean HasPlayerWon(User i_User) {
        Team playerTeam = getPlayerTeam(i_User);
        if (m_IsTeamGame) {
            return playerTeam.HasTeamWon();
        }
        else {
            return playerTeam.HasPlayerWon(i_User);
        }
    }

    public Riddle GetUserRiddleById(Integer i_AppearanceNumber, User i_User){
        Team team = getPlayerTeam(i_User);

        return m_Levels.get(team.GetPlayerRiddleLevel(i_User)).GetRiddle(i_AppearanceNumber);
    }
}
