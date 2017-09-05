package GameComponents;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int m_TeamId;

    private String m_TeamName;

    @OneToMany
    private Map<User,Level> m_PlayerLevels;

    @ManyToOne
    private Level m_TeamRiddleLevel; // In TeamGame this field is used for player score. In single it is used for checking player riddle level

    @OneToMany
    private List<Riddle> m_UnsolvedTeamRiddles = new ArrayList<>();

    public Team() {
        m_PlayerLevels = new HashMap<>();
    }

    public void SetTeamName(String i_TeamName){m_TeamName = i_TeamName;}

    public String GetTeamName() {
        return m_TeamName;
    }

    public boolean IsPlayerInTeam(User i_user) {
        return m_PlayerLevels.containsKey(i_user);
    }

    public  Boolean IsPlayerInTeam(String i_UserEmail){
        Boolean res = false;

        for(Map.Entry<User,Level> entry: m_PlayerLevels.entrySet()){
            res = entry.getKey().GetEmailAddress().equals(i_UserEmail);
            if(res){
                break;
            }
        }

        return res;
    }

    public int Count() {
        return m_PlayerLevels.size();
    }

    public void AddPlayer(User i_PlayerToAdd, Level i_Level) {
        if (m_TeamRiddleLevel == null) {
            m_TeamRiddleLevel = i_Level;
        }
        m_PlayerLevels.put(i_PlayerToAdd, i_Level);
    }

    public int GetPlayerRiddleLevel(User i_User) {
        return m_PlayerLevels.get(i_User).GetIndex();
    }

//    public int GetPlayerRiddleLevel(String i_UserId) {
//        return r_PlayerRiddleLevel.get(i_UserId).getValue();
//    }
//
//    public int GetPlayerSolvedRiddlesInLevel(String i_UserId) {
//        return r_PlayerRiddleLevel.get(i_UserId).getValue();
//    }
//    public int GetPlayerSolvedRiddlesInLevel(String i_UserId) {
//        return r_PlayerRiddleLevel.get(i_UserId).getValue();
//    }

    public int GetTeamRiddleLevel() {
        if (m_TeamRiddleLevel == null) {
            return 0;
        }
        return m_TeamRiddleLevel.GetIndex();
    }

    public boolean PlayerSolvedRiddle(User i_User, Riddle i_Riddle, Level i_NextLevel) {
        boolean playerHasCompletedGame = false;
        Level playerPrevRiddleLevel = m_PlayerLevels.get(i_User);
        if (!i_Riddle.IsSolvedByTeam(this)) {
            playerHasCompletedGame = TeamSolvedRiddle(i_NextLevel, i_Riddle);
        }
        i_Riddle.UserSolvedRiddle(i_User, this);
        if (playerPrevRiddleLevel.GetRiddlesNotSolvedByPlayer(i_User).isEmpty()) {
            m_PlayerLevels.put(i_User, i_NextLevel);
            playerHasCompletedGame = i_NextLevel == null;
        }

        return playerHasCompletedGame;
    }

    public boolean TeamSolvedRiddle(Level i_NextLevel, Riddle i_Riddle) {
        boolean teamHasWon = false;
        if (m_UnsolvedTeamRiddles.isEmpty()) {
            m_UnsolvedTeamRiddles = m_TeamRiddleLevel.GetRiddlesNotSolvedByTeam(this);
        }
        if (m_UnsolvedTeamRiddles.contains(i_Riddle)) {
            m_UnsolvedTeamRiddles.remove(i_Riddle);
        }
        if (m_UnsolvedTeamRiddles.isEmpty()) {
            m_TeamRiddleLevel = i_NextLevel;
            teamHasWon = i_NextLevel == null;
            if (!teamHasWon) {
                m_UnsolvedTeamRiddles = m_TeamRiddleLevel.GetRiddlesNotSolvedByTeam(this);
            }
        }

        return teamHasWon;
    }

    public Map<String,Integer> GetTeamScoresForCurrentLevel() {//Returns Team Scores fo
        Map<String,Integer> teamScores = new HashMap<>();
        for (Map.Entry<User, Level> entry : m_PlayerLevels.entrySet()) {
            User user = entry.getKey();
            teamScores.put(user.GetUserName(), entry.getValue().GetRiddlesSolvedByPlayer(user).size());
        }

        return teamScores;
    }

    public boolean HasTeamWon() {
        return m_TeamRiddleLevel == null;
    }

    public boolean HasPlayerWon(User i_User) {
        return m_PlayerLevels.get(i_User) == null;
    }
}