package Util;

import GameComponents.Game;
import GameComponents.Riddle;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by Dean on 28/02/2017.
 */
public class MockData {
    private static boolean isMockTeamGameSet = false;
    private static int mockUserNumber = 1;

    public static void CreateMockTeamGame() throws Exception {
        if (!isMockTeamGameSet) {
            isMockTeamGameSet = true;
            Game game = DatabaseFacade.CreateNewGame("1");
            game.SetIsTeamGame(true);
            game.SetTeamNames(new HashSet<String>(Arrays.asList("Team Blue", "Team Yellow", "Team Red")));
            int riddleLevel = 0;
            for (int i = 0; i < 3; i++) {
                game.AddRiddle(createMockRiddle(riddleLevel));
                game.AddRiddle(createMockRiddle(riddleLevel++));
            }
            game.SetStartDate(new Date());
            game.SetDuration(5.0f);
            game.SetMaxPayersInTeam(2);
            game.SetMaxPlayers(20);
            game.SetTreasureType("Treasure Chest");
            game.SetGameName("Greek Mythology Game");
            game.PublishGame();

            String userToSolveRiddles = createMockUser();
            game.AddPlayer(userToSolveRiddles, 0);
            game.AddPlayer(createMockUser(), 0);
            game.AddPlayer(createMockUser(), 1);
            game.TryToSolveTextRiddle(userToSolveRiddles, 0, "three");
            game.TryToSolveTextRiddle(userToSolveRiddles, 0, "three");
            DatabaseFacade.GetUser("1").SetUnpublishedGame(null);
        }
    }

    private static Riddle createMockRiddle(int riddleLevel) {
        Riddle riddle = new Riddle();
        riddle.setIsTextType(true);
        riddle.setName("Cerberus Heads");
        riddle.setAppearanceNumber(riddleLevel);
        riddle.setTextQuestion("How many heads does Cereberus have?");
        riddle.setAnswer("Three");
        return riddle;
    }

    private static String createMockUser() throws Exception {
        return DatabaseFacade.createUser("Mock User " + mockUserNumber, "123", "mock@email.com" + mockUserNumber++).GetUserId();
    }
}