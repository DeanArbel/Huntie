package GameComponents;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
public class Team {
    private final String r_TeamName;
    private final Map<String, User> r_TeamMembers = new HashMap<>();

    public Team(String i_TeamName) {
        r_TeamName = i_TeamName;
    }

    public String getTeamName() {
        return r_TeamName;
    }

    public boolean IsPlayerInTeam(String i_userId) {
        return r_TeamMembers.get(i_userId) != null;
    }

    public int Count() {
        return r_TeamMembers.size();
    }
}
