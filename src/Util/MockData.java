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
            game.SetTeamNames(new HashSet<String>(Arrays.asList("Team Water", "Team Ground", "Team Air")));
            int riddleLevel = 0;
            for (int i = 0; i < 3; i++) {
                game.AddRiddle(createMockRiddle(riddleLevel, "The First Riddle"));
                game.AddRiddle(createMockRiddle(riddleLevel, "Clocktower Conundrum"));
                game.AddRiddle(createMockRiddle(riddleLevel, "Find the Flee"));
                game.AddRiddle(createMockRiddle(riddleLevel, "All Aboard"));
                game.AddRiddle(createMockRiddle(riddleLevel, "Quest for Knowledge"));
                game.AddRiddle(createMockRiddle(riddleLevel++, "The Special Dish"));
            }
            game.SetStartDate(new Date());
            game.SetDuration(5.0f);
            game.SetMaxPayersInTeam(3);
            game.SetMaxPlayers(20);
            game.SetTreasureType("Treasure Chest");
            game.SetGameName("Danny's Treasure Hunt");
            game.PublishGame();

            String userToSolveRiddles = createMockUser("Moshe");
            game.AddPlayer(userToSolveRiddles, 0);
            String userToAlsoSolveriddles = createMockUser("Ron");
            game.AddPlayer(userToAlsoSolveriddles, 1);
            game.AddPlayer(createMockUser("Dorit"), 0);
            game.AddPlayer(createMockUser("Naor"), 1);
            //game.AddPlayer(createMockUser("Amy"), 2);
            String userInMyTeam = createMockUser("Amy");
            game.AddPlayer(userInMyTeam, 2);
            game.TryToSolveRiddle(userInMyTeam, 0, "Three");
            for (int i = 0; i < 6; i++) {
                game.TryToSolveRiddle(userToSolveRiddles, 0, "Three");
                game.TryToSolveRiddle(userToAlsoSolveriddles, 0, "Three");
            }
            for (int i = 0; i < 6; i++) {
                game.TryToSolveRiddle(userToSolveRiddles, 0, "Three");
            }
            DatabaseFacade.GetUser("1").SetUnpublishedGame(null);
        }
    }

    private static Riddle createMockRiddle(int riddleLevel, String riddleName) {
        Riddle riddle = new Riddle();
        riddle.setIsTextType(true);
        riddle.setName(riddleName);
        riddle.setAppearanceNumber(riddleLevel);
        riddle.setTextQuestion("How many heads does Cereberus have?");
        riddle.setAnswer("Three");
        return riddle;
    }

    private static String createMockUser(String userName) throws Exception {
        return DatabaseFacade.createUser(userName, "123", "mock@email.com" + mockUserNumber++).GetUserId();
    }
}