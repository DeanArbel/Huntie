package GameComponents;

import Util.DatabaseFacade;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
public class Team {
    private final String r_TeamName;
    private final Map<String, Pair<Integer, Integer>> r_PlayerRiddleLevel;
    private Pair<Integer, Integer> m_TeamRiddleLevel; // In TeamGame this field is used for player score. In single it is used for checking player riddle level

    public Team(String i_TeamName) {
        r_TeamName = i_TeamName;
        r_PlayerRiddleLevel = new HashMap<>();
    }

    public String GetTeamName() {
        return r_TeamName;
    }

    public boolean IsPlayerInTeam(String i_userId) {
        return r_PlayerRiddleLevel.containsKey(i_userId);
    }

    public int Count() {
        return r_PlayerRiddleLevel.size();
    }

    public void AddPlayer(String i_PlayerToAdd, int i_RiddleCount) {
        r_PlayerRiddleLevel.put(i_PlayerToAdd, new Pair<>(0, i_RiddleCount));
    }

    public void InitTeam(int i_RiddleCount) {
        m_TeamRiddleLevel = new Pair<>(0, i_RiddleCount);
    }

    public int GetPlayerRiddleLevel(String i_UserId) {
        return r_PlayerRiddleLevel.get(i_UserId).getKey();
    }

    public int GetPlayerSolvedRiddlesInLevel(String i_UserId) {
        return r_PlayerRiddleLevel.get(i_UserId).getValue();
    }

    public int GetTeamRiddleLevel() {
        return m_TeamRiddleLevel.getKey();
    }

    /**
     * @param i_UserId
     * @param i_NextRiddleLevelSize
     * @return Player solved all the riddles of the game
     */
    public boolean PlayerSolvedRiddle(String i_UserId, Integer i_NextRiddleLevelSize) {
        boolean playerHasWon = false;
        Pair<Integer, Integer> playerPrevRiddleLevel = r_PlayerRiddleLevel.get(i_UserId);
        if (playerPrevRiddleLevel.getValue() == 1) {
            r_PlayerRiddleLevel.put(i_UserId, new Pair<>(playerPrevRiddleLevel.getKey() + 1, i_NextRiddleLevelSize));
            if (i_NextRiddleLevelSize == null) {
                playerHasWon = true;
            }
        }
        else {
            r_PlayerRiddleLevel.put(i_UserId, new Pair<>(playerPrevRiddleLevel.getKey(), playerPrevRiddleLevel.getValue() - 1));
        }

        return playerHasWon;
    }

    /**
     * @param i_NextRiddleLevelSize
     * @return Team solved all the riddles of the game
     */
    public boolean TeamSolvedRiddle(String i_UserId, Integer i_NextRiddleLevelSize) {
        boolean teamHasWon = false;
        if (m_TeamRiddleLevel.getValue() == 1) {
            m_TeamRiddleLevel = new Pair<>(m_TeamRiddleLevel.getKey() + 1, i_NextRiddleLevelSize);
            if (i_NextRiddleLevelSize == null) {
                teamHasWon = true;
            }
        }
        else {
            m_TeamRiddleLevel = new Pair<>(m_TeamRiddleLevel.getKey(), m_TeamRiddleLevel.getValue() - 1);
        }
        r_PlayerRiddleLevel.put(i_UserId, new Pair<>(0, r_PlayerRiddleLevel.get(i_UserId).getValue() + 1));

        return teamHasWon;
    }

    public Map<String,Integer> GetTeamScores() {
        Map<String,Integer> teamScores = new HashMap<>();
        for (Map.Entry<String, Pair<Integer, Integer>> entry : r_PlayerRiddleLevel.entrySet()) {
            teamScores.put(DatabaseFacade.GetUserName(entry.getKey()), entry.getValue().getValue());
        }

        return teamScores;
    }
}
