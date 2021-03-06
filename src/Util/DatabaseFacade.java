package Util;

import GameComponents.*;
import GameComponents.Utils.RandomString;

import javax.persistence.*;

/**
 * Created by Dean on 18/2/2017.
 */
public final class DatabaseFacade {
    private static EntityManagerFactory m_HuntieEntityManagerFactory = Persistence.createEntityManagerFactory("Huntie.odb");
    private static EntityManager m_HuntieEntityManager;
    private static int m_Sessions = 0;
   // private Timer timer = new Timer().scheduleAtFixedRate(()->refrashTokens(),(long)3,(long)3);
//        ses.scheduleWithFixedDelay(new Runnable() {
//        @Override
//        public void run() {
//            System.out.println(new Date());
//        }
//    }, 0, 1, TimeUnit.SECONDS);

//    public DatabaseFacade(){
//        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
//        ses.scheduleWithFixedDelay(()->refrashTokens(),30,60,TimeUnit.MINUTES);
//    }

    public static User GetUser(String i_UserEmail) {
        User user;
        refreshEntityManagerAndTransAction();
        user = m_HuntieEntityManager.find(User.class, i_UserEmail);
        if (user != null) {
            m_HuntieEntityManager.persist(user);
        }
        return user;
    }

    public static Game getGame(Integer i_GameId) {
        Game game = null;
        if (i_GameId != null) {
            refreshEntityManagerAndTransAction();
            game = m_HuntieEntityManager.find(Game.class, i_GameId);
            m_HuntieEntityManager.persist(game);
        }

        return game;
    }

    public static User createUser(String i_UserName, String i_UserPassword, String i_Email) throws Exception {
        User newUser = new User();

        if (!IsUserNameUnique(i_UserName)) {
            throw new NonUniqueResultException("User name is already taken");
        }
        newUser.SetUserName(i_UserName);
        newUser.setPassword(i_UserPassword);
        if (!IsEmailUnique(i_Email)) {
            throw new EntityExistsException("Email is already taken");
        }
        newUser.SetEmailAddress(i_Email);
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.persist(newUser);
        EndTransaction();
        return newUser;
    }

    public static String GetUserName(String i_UserEmail) {
        return GetUser(i_UserEmail).GetUserName();
    }

    public static int Verify(String i_UserEmail, String i_UserPassword){
        return 0;
    }

    public static boolean DoesUserHaveAnUnpublishedGame(String i_UserEmail) {
        return GetUser(i_UserEmail).GetUnpublishedGame() != null;
    }

    //TODO: Adjust username or id depending on login implementation
    public static Game CreateNewGame(String i_UserEmail) {
        Game newGame = null;

        User gameCreator = GetUser(i_UserEmail);
        if (gameCreator != null) {
            DeleteGame(gameCreator.GetUnpublishedGame());
            newGame = new Game(gameCreator);
            m_HuntieEntityManager.persist(newGame);
            gameCreator.SetUnpublishedGame(newGame);
            gameCreator.AddGameToManagerList(newGame);
            EndTransaction();
        }

        return newGame;
    }


    public static Team CreateNewTeam() {
        Team team = new Team();
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.persist(team);
        return team;
    }

    public static Riddle CreateNewRiddle() {
        Riddle riddle = new Riddle();
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.persist(riddle);
        return riddle;
    }

    public static Level CreateNewLevel(int i_LevelIdx, boolean i_TreasureLevel) {
        Level level = new Level(i_LevelIdx, i_TreasureLevel);
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.persist(level);
        return level;
    }

    public static void EndTransaction() {
        EntityTransaction transaction = m_HuntieEntityManager.getTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
        if (--m_Sessions == 0) {
            m_HuntieEntityManager.close();
        }
    }

    public static void DeleteGame(Game i_Game) {//was String
        if(i_Game != null) {
            refreshEntityManagerAndTransAction();
            m_HuntieEntityManager.persist(i_Game);
            m_HuntieEntityManager.remove(i_Game);
        }
    }

    public static boolean IsUserNameUnique(String i_UserName) {
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
        //m_HuntieEntityManager.close();
        return nameIsUnique;
    }

    public static boolean IsEmailUnique(String i_Email) {
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
        //m_HuntieEntityManager.close();
        return emailIsUnique;
    }

    public static SessionToken Login(String i_UserEmail, String i_UserPassword){
        SessionToken token = new SessionToken();
        token.SetUser(null);
        token.SetToken("");
        User user;

        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        user = m_HuntieEntityManager.find(User.class, i_UserEmail);
        if(user != null) {
            if(user.GetPassword().equals(i_UserPassword)){//new
                token = generateSessionToken(user);
            }
        }

        return token;
    }

    private static SessionToken generateSessionToken(User i_User){
        RandomString sessionToken = new RandomString();
        SessionToken token = new SessionToken();
        token.SetToken(sessionToken.nextString());
        token.SetUser(i_User);
        token.UpdateExpirationTime();

        refreshEntityManagerAndTransAction();
        while(m_HuntieEntityManager.contains(token)) {
            token.SetToken(sessionToken.nextString());
        }
        m_HuntieEntityManager.persist(token);

        return token;
    }

    public static SessionToken FaceBookLogin(String i_AccessToken, String i_UserEmail, String i_UserName) throws Exception {//todo modify
        SessionToken token;

        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        User user = m_HuntieEntityManager.find(User.class, i_UserEmail);
        if(user == null) {
            SignUp(i_UserEmail,i_AccessToken,i_UserName);
        }
        token = generateSessionToken(user);
        return token;
    }

    public static int SignUp(String i_UserEmail, String i_UserPassword, String i_UserName) throws Exception{
        createUser(i_UserName, i_UserPassword, i_UserEmail);
        return 0;
    }

    public static void Close(){
        m_HuntieEntityManagerFactory.close();
    }

    public static Boolean IsTokenValid(String i_Token){
        boolean res = false;


        refreshEntityManagerAndTransAction();
        SessionToken token = m_HuntieEntityManager.find(SessionToken.class, i_Token);
        if(token != null){
            res = !token.IsExpiried();
        }
        EndTransaction();

        return res;
    }

    public static void UpdateToken(String i_Token){
        if(IsTokenValid(i_Token)){
            refreshEntityManagerAndTransAction();
            SessionToken token = m_HuntieEntityManager.find(SessionToken.class, i_Token);
            token.UpdateExpirationTime();
            m_HuntieEntityManager.persist(token);
            EndTransaction();
        }
    }

    private static void refrashTokens(){
        m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        TypedQuery<SessionToken> query = m_HuntieEntityManager.createQuery("select s from SessionToken s ", SessionToken.class);
        m_HuntieEntityManager.getTransaction().begin();
        for(SessionToken sessionToken:query.getResultList()){
            if(sessionToken.IsExpiried()){
                m_HuntieEntityManager.remove(sessionToken);
            }
        }
        m_HuntieEntityManager.getTransaction().commit();
        m_HuntieEntityManager.close();
    }

    public static User GetUserFromToken(String i_Token){
        refreshEntityManagerAndTransAction();
        SessionToken token = m_HuntieEntityManager.find(SessionToken.class, i_Token);

        return token.GetUser();
    }

    private static void refreshEntityManagerAndTransAction() {
        if (m_HuntieEntityManager == null || !m_HuntieEntityManager.isOpen()) {
            m_HuntieEntityManager = m_HuntieEntityManagerFactory.createEntityManager();
        }
        m_Sessions++;
        EntityTransaction transaction = m_HuntieEntityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
    }

    public static void PersistObject(Object i_NewObject) {
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.persist(i_NewObject);
    }

    public static void RemoveObject(Object i_RemovableObject) {
        refreshEntityManagerAndTransAction();
        m_HuntieEntityManager.remove(i_RemovableObject);
    }

    public static void RollbackTransaction() {
        if (m_HuntieEntityManager != null && m_HuntieEntityManager.isOpen()) {
            EntityTransaction transaction = m_HuntieEntityManager.getTransaction();
            if (transaction != null && transaction.isActive()) {
                m_HuntieEntityManager.getTransaction().rollback();
            }
            if (--m_Sessions == 0) {
                m_HuntieEntityManager.close();
            }
        }
    }
}