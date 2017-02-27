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
    private Game m_UnpublishedGame = null;

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

    public Game getUnpublishedGame() {
        return m_UnpublishedGame;
    }

    public void setUnpublishedGame(Game i_UnpublishedGame) {
        this.m_UnpublishedGame = i_UnpublishedGame;
    }

    public String getEmailAddress() {
        return m_EmailAddress;
    }

    public void setEmailAddress(String m_EmailAddress) {
        this.m_EmailAddress = m_EmailAddress;
    }

    public void DeleteUnpublishedGame() {
        if (m_UnpublishedGame != null) {
            DatabaseFacade.DeleteGame(m_UnpublishedGame.GetGameId());
        }

        m_UnpublishedGame = null;
    }
}