package GameComponents;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 18/2/2017.
 */
@Entity
public class User {
//    @Id
//    private final String r_UserId;

    @OneToMany
    private List<Game> m_ManagedGames = new ArrayList<>();

    @OneToMany
    private List<Game> m_PlayedGames = new ArrayList<>();

    private String m_UserName;
    private String m_Password;

    @Id
    private String m_EmailAddress;

    @OneToOne
    private Game m_UnpublishedGame = null;

//    public String GetUserId() {
//        return r_UserId;
//    }
//
//    public User(String i_UserId) {
//        r_UserId = i_UserId;
//    }

    public String GetUserName() {
        return m_UserName;
    }

    public void SetUserName(String i_UserName) throws Exception {
//        if (!DatabaseFacade.IsUserNameUnique(i_UserName)) {
//            throw new Exception("User name is already taken");
//        }
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
//        if (!DatabaseFacade.IsEmailUnique(i_EmailAddress)) {
//            throw new Exception("Email is already taken");
//        }
        this.m_EmailAddress = i_EmailAddress;
    }

    public List<Game> GetManagedGames() {
        return m_ManagedGames;
    }

    public List<Game> GetPlayedGames() {
        return m_PlayedGames;
    }

//    public void DeleteUnpublishedGame() {//TODO not belong here
//        if (m_UnpublishedGame != null) {
//            DatabaseFacade.DeleteGame(m_UnpublishedGame.GetGameId());
//        }
//
//        m_UnpublishedGame = null;
//    }

    public void JoinGameAsPlayer(Game i_Game) {
        m_PlayedGames.add(i_Game);
    }

    public void AddGameToManagerList(Game i_Game) {
        m_PlayedGames.remove(i_Game);
        m_ManagedGames.add(i_Game);
    }
}