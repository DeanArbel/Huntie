package GameComponents;

import Util.Enums.GameStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 18/2/2017.
 */
public class Game {
    private final String r_GameId;
    private final List<String> r_Managers = new ArrayList<>();
    private final List<Team> r_Teams = new ArrayList<>();
    private final List<List<Riddle>> r_Riddles = new ArrayList<>();
    private int m_MaxPlayers = 10;
    private int m_MaxPayersInTeam = 1;
    private String m_GameArea;
    private GameStatus m_GameStatus = GameStatus.IN_CREATION;
    private boolean m_IsTeamGame = false;

    public Game(String i_GameId, String i_ManagerId) {
        r_GameId = i_GameId;
        r_Managers.add(i_ManagerId);
    }

    public String getGameId() {
        return r_GameId;
    }
}
