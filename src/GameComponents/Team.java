package GameComponents;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dean on 18/2/2017.
 */
public class Team {
    private final String r_TeamName;
    private final Set<String> r_TeamMembers = new HashSet<>();

    public Team(String i_TeamName) {
        r_TeamName = i_TeamName;
    }

    public String getTeamName() {
        return r_TeamName;
    }

    public boolean IsPlayerInTeam(String i_userId) {
        return r_TeamMembers.contains(i_userId);
    }

    public int Count() {
        return r_TeamMembers.size();
    }

    public int GetSize() {
        return r_TeamMembers.size();
    }

    public void AddPlayer(String i_PlayerToAdd) {
        r_TeamMembers.add(i_PlayerToAdd);
    }
}
