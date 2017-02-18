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
    private static int s_CurrentGameId = 1;
    private static int s_CurrentUserId = 1;

    public static User getUser(String i_UserId) {
        return sr_Users.get(i_UserId);
    }

    public static Game getGame(String i_GameId) {
        return sr_Games.get(i_GameId);
    }

    public static User createUser(String i_UserName, String i_UserPassword) {
        User newUser = new User(String.valueOf(s_CurrentUserId++));
        newUser.setUserName(i_UserName);
        newUser.setPassword(i_UserPassword);
        sr_Users.put(newUser.getUserId(), newUser);

        return newUser;
    }

    public static Game CreateNewGame(String i_UserId) {
        Game newGame = null;
        User gameCreator = sr_Users.get(i_UserId);
        if (gameCreator != null) {
            gameCreator.DeleteUnformedGame();
            newGame = new Game(String.valueOf(s_CurrentGameId++), i_UserId);
            sr_Games.put(newGame.getGameId(), newGame);
            gameCreator.setUnformedGame(newGame);
        }

        return newGame;
    }

    public static void DeleteGame(String i_GameId) {
        sr_Games.remove(i_GameId);
    }
}
