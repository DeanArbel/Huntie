package GameComponents;

import Util.DatabaseFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
public class User {
    private final String r_UserId;
    private final Map<String, Game> r_Games = new HashMap<>();
    private String m_UserName;
    private String m_Password;
    private String m_EmailAddress;
    private Game m_UnformedGame = null;

    public String getUserId() {
        return r_UserId;
    }

    public User(String i_UserId) {
        r_UserId = i_UserId;
    }

    public String getUserName() {
        return m_UserName;
    }

    public void setUserName(String m_UserName) {
        this.m_UserName = m_UserName;
    }

    public String getPassword() {
        return m_Password;
    }

    public void setPassword(String m_Password) {
        this.m_Password = m_Password;
    }

    public Game getUnformedGame() {
        return m_UnformedGame;
    }

    public void setUnformedGame(Game i_UnformedGame) {
        this.m_UnformedGame = i_UnformedGame;
    }

    public String getEmailAddress() {
        return m_EmailAddress;
    }

    public void setEmailAddress(String m_EmailAddress) {
        this.m_EmailAddress = m_EmailAddress;
    }

    public void DeleteUnformedGame() {
        if (m_UnformedGame != null) {
            DatabaseFacade.DeleteGame(m_UnformedGame.getGameId());
        }

        m_UnformedGame = null;
    }
}