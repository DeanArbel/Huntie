package Util;

import GameComponents.Game;
import GameComponents.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 18/2/2017.
 */
public class DatabaseFacade {
    private static final Map<String, Game> sr_Games = new HashMap<>();
    private static final Map<String, User> sr_Users = new HashMap<>();
    private static final Map<String, String> sr_UseerMap = new HashMap<>();
    private static int s_CurrentGameId = 1;
    private static int s_CurrentUserId = 1;

    public static User GetUser(String i_UserId) {
        return sr_Users.get(i_UserId);
    }

    public static Game getGame(String i_GameId) {
        return sr_Games.get(i_GameId);
    }

    public static User createUser(String i_UserName, String i_UserPassword, String i_Email) throws Exception {
        User newUser = new User(String.valueOf(s_CurrentUserId));
        newUser.SetUserName(i_UserName);
        newUser.setPassword(i_UserPassword);
        newUser.SetEmailAddress(i_Email);
        sr_Users.put(newUser.GetUserId(), newUser);
        s_CurrentUserId++;
        MockData.CreateMockTeamGame(); //TODO: Remove this line

        return newUser;
    }

    public static String GetUserName(String i_UserId) {
        return GetUser(i_UserId).GetUserName();
    }

    public static int Verify(String i_UserEmail, String i_UserPassword){
        return 0;
    }

    public static boolean DoesUserHaveAnUnpublishedGame(String i_UserId) {
        return sr_Users.get(i_UserId).GetUnpublishedGame() != null;
    }

    //TODO: Adjust username or id depending on login implementation
    public static Game CreateNewGame(String i_UserId) {
        Game newGame = null;
        User gameCreator = sr_Users.get(i_UserId);
        if (gameCreator != null) {
            gameCreator.DeleteUnpublishedGame();
            newGame = new Game(String.valueOf(s_CurrentGameId++), i_UserId);
            sr_Games.put(newGame.GetGameId(), newGame);
            gameCreator.SetUnpublishedGame(newGame);
        }

        return newGame;
    }

    public static void DeleteGame(String i_GameId) {
        sr_Games.remove(i_GameId);
    }

    public static boolean IsUserNameUnique(String i_UserName) {
        boolean nameIsUnique = true;
        for (User user : sr_Users.values()) {
            if (user.GetUserName().toLowerCase().equals(i_UserName.toLowerCase())) {
                nameIsUnique = false;
                break;
            }
        }
        return nameIsUnique;
    }

    public static boolean IsEmailUnique(String i_Email) {
        boolean emailIsUnique = true;
        for (User user : sr_Users.values()) {
            if (user.GetEmailAddress().toLowerCase().equals(i_Email.toLowerCase())) {
                emailIsUnique = false;
                break;
            }
        }
        return emailIsUnique;
    }
}