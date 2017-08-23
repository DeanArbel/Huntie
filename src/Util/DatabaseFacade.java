package Util;

import GameComponents.Game;
import GameComponents.User;

import javax.persistence.*;

/**
 * Created by Dean on 18/2/2017.
 */
public class DatabaseFacade {
    private EntityManagerFactory m_HuntieEntityManagerFactory = Persistence.createEntityManagerFactory("$objectdb/db/Huntie.odb");
    private EntityManager m_HuntieEntityManager;

    public User GetUser(String i_UserEmail) {
        User user;

        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        user = m_HuntieEntityManager.find(User.class, i_UserEmail);
        m_HuntieEntityManager.close();
        return user;
    }

    public Game getGame(int i_GameId) {//was String i_GameId
        Game game;

        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        game = m_HuntieEntityManager.find(Game.class, i_GameId);
        m_HuntieEntityManager.close();
        return game;
    }

    public User createUser(String i_UserName, String i_UserPassword, String i_Email) throws Exception {
        User newUser = new User();

        if (!IsUserNameUnique(i_UserName)) {
            throw new Exception("User name is already taken");
        }
        newUser.SetUserName(i_UserName);
        newUser.setPassword(i_UserPassword);
        if (!IsEmailUnique(i_Email)) {
            throw new Exception("Email is already taken");
        }
        newUser.SetEmailAddress(i_Email);
        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        m_HuntieEntityManager.getTransaction().begin();
        m_HuntieEntityManager.persist(newUser);
        m_HuntieEntityManager.getTransaction().commit();
        m_HuntieEntityManager.close();
        MockData.CreateMockTeamGame(); //TODO: Remove this line

        return newUser;
    }

    public String GetUserName(String i_UserEmail) {
        return GetUser(i_UserEmail).GetUserName();
    }

    public static int Verify(String i_UserEmail, String i_UserPassword){
        return 0;
    }

    public boolean DoesUserHaveAnUnpublishedGame(String i_UserEmail) {
        return GetUser(i_UserEmail).GetUnpublishedGame() != null;
    }

    //TODO: Adjust username or id depending on login implementation
    public Game CreateNewGame(String i_UserEmail) {
        Game newGame = null;

        User gameCreator = GetUser(i_UserEmail);
        if (gameCreator != null) {
           // gameCreator.DeleteUnpublishedGame();
            DeleteGame(gameCreator.GetUnpublishedGame().GetGameId());
            newGame = new Game(gameCreator);//String.valueOf(m_CurrentGameId++),
            m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
            m_HuntieEntityManager.getTransaction().begin();
            m_HuntieEntityManager.persist(newGame);
            gameCreator.SetUnpublishedGame(newGame);
            m_HuntieEntityManager.getTransaction().commit();
            m_HuntieEntityManager.close();
        }

        return newGame;
    }

    public void DeleteGame(int i_GameId) {//was String
        Game game = getGame(i_GameId);
        if(game != null) {
            m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
            m_HuntieEntityManager.getTransaction().begin();
            m_HuntieEntityManager.remove(game);
            m_HuntieEntityManager.getTransaction().commit();
            m_HuntieEntityManager.close();
        }
    }

    public boolean IsUserNameUnique(String i_UserName) {
        boolean nameIsUnique;
//        for (User user : sr_Users.values()) {
//            if (user.GetUserName().toLowerCase().equals(i_UserName.toLowerCase())) {
//                nameIsUnique = false;
//                break;
//            }
//        }
//        return nameIsUnique;
        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        TypedQuery<User> query = m_HuntieEntityManager.createQuery("select u from User u where u.m_UserName = :name", User.class);
        nameIsUnique = query.setParameter("name",i_UserName).getResultList().isEmpty();
        m_HuntieEntityManager.close();
        return nameIsUnique;
    }

    public boolean IsEmailUnique(String i_Email) {
        boolean emailIsUnique;
//        for (User user : sr_Users.values()) {
//            if (user.GetEmailAddress().toLowerCase().equals(i_Email.toLowerCase())) {
//                emailIsUnique = false;
//                break;
//            }
//        }
//        return emailIsUnique;
        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        TypedQuery<User> query = m_HuntieEntityManager.createQuery("select u from User u where u.m_EmailAddress = :email", User.class);
        emailIsUnique = query.setParameter("email",i_Email).getResultList().isEmpty();
        m_HuntieEntityManager.close();
        return emailIsUnique;
    }

    public int Login(String i_UserEmail, String i_UserPassword){
        int token=0;
        User user;

        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        user = m_HuntieEntityManager.find(User.class, i_UserEmail);
        m_HuntieEntityManager.close();
        if(user != null) {
            if(user.GetPassword().equals(i_UserPassword)){//new
                token = generateToken();
            }
        }

        return token;
    }

    private static int generateToken(){
        return 1;
    }

//    public int FaceBookLogin(String i_AccessToken, Integer i_UserID){//todo modify
//        int token = 0;
//
//        if(!sr_UserMap.containsKey(i_AccessToken)) {
//            User user = new User(i_UserID.toString());
//            m_UserEntityManager.getTransaction().begin();
//            m_UserEntityManager.persist(user);
//            m_UserEntityManager.getTransaction().commit();
////            sr_Users.put(i_AccessToken,user);
//        }
//        token = generateToken();
//        return token;
//    }

    public int SignUp(String i_UserEmail, String i_UserPassword, String i_UserName){
        //if(!IsEmailUnique(i_UserEmail)){
          //  return -1;
        //}

        //if(!IsUserNameUnique(i_UserName)){
          //  return -2;
        //}

        try {
            User user = createUser(i_UserName, i_UserPassword, i_UserEmail);
            m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
            if(!m_HuntieEntityManager.contains(user)){
                m_HuntieEntityManager.getTransaction().begin();
                m_HuntieEntityManager.persist(user);
                m_HuntieEntityManager.getTransaction().commit();
            }
            m_HuntieEntityManager.close();
        }
        catch (Exception e){

        }
        return 0;
    }

    public void Close(){
        m_HuntieEntityManagerFactory.close();
    }
}