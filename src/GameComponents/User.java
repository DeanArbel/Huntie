package GameComponents;

import Util.DatabaseFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
public class User {
    private final String r_UserId;
    private final Map<String, String> r_ManagedGames = new HashMap<>();
    private final Map<String, String> r_PlayedGames = new HashMap<>();
    private String m_UserName;
    private String m_Password;
    private String m_EmailAddress;
    private Game m_UnpublishedGame = null;

    public String GetUserId() {
        return r_UserId;
    }

    public User(String i_UserId) {
        r_UserId = i_UserId;
    }

    public String GetUserName() {
        return m_UserName;
    }

    public void SetUserName(String i_UserName) throws Exception {
        if (!DatabaseFacade.IsUserNameUnique(i_UserName)) {
            throw new Exception("User name is already taken");
        }
        this.m_UserName = i_UserName;
    }

    public String GetPassword() {
        return m_Password;
    }

    public void setPassword(String m_Password) {
        this.m_Password = m_Password;
    }

    public Game GetUnpublishedGame() {
        return m_UnpublishedGame;
    }

    public void SetUnpublishedGame(Game i_UnpublishedGame) {
        this.m_UnpublishedGame = i_UnpublishedGame;
    }

    public String GetEmailAddress() {
        return m_EmailAddress;
    }

    public void SetEmailAddress(String i_EmailAddress) throws Exception {
        if (!DatabaseFacade.IsEmailUnique(i_EmailAddress)) {
            throw new Exception("Email is already taken");
        }
        this.m_EmailAddress = i_EmailAddress;
    }

    public Map<String, String> GetManagedGames() {
        return r_ManagedGames;
    }

    public Map<String, String> GetPlayedGames() {
        return r_PlayedGames;
    }

    public void DeleteUnpublishedGame() {
        if (m_UnpublishedGame != null) {
            DatabaseFacade.DeleteGame(m_UnpublishedGame.GetGameId());
        }

        m_UnpublishedGame = null;
    }

    public void JoinGameAsPlayer(String i_GameId, String i_GameName) {
        r_PlayedGames.put(i_GameId, i_GameName);
    }

    public void AddGameToManagerList(String i_GameId, String i_GameName) {
        r_PlayedGames.remove(i_GameId);
        r_ManagedGames.put(i_GameId, i_GameName);
    }
}