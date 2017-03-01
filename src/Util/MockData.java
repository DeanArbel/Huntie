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

    public static void CreateMockTeamGame() {
        if (!isMockTeamGameSet) {
            isMockTeamGameSet = true;
            Game game = DatabaseFacade.CreateNewGame("1");
            game.SetIsTeamGame(true);
            game.SetTeamNames(new HashSet<String>(Arrays.asList("Team Blue", "Team Yellow", "Team Red")));
            int riddleLevel = 0;
            for (int i = 0; i < 10; i++) {
                game.AddRiddle(createMockRiddle(riddleLevel++));
            }
            game.SetDuration(5.0f);
            game.SetMaxPayersInTeam(2);
            game.SetMaxPlayers(20);
            game.SetStartDate(new Date());
            game.SetTreasureType("Treasure Chest");
            game.PublishGame();
            DatabaseFacade.getUser("1").setUnpublishedGame(null);
        }
    }

    private static Riddle createMockRiddle(int riddleLevel) {
        Riddle riddle = new Riddle();
        riddle.setIsTextType(true);
        riddle.setName("Cerberus Heads");
        riddle.setAppearanceNumber(riddleLevel);
        riddle.setTextAnswer("How many heads does Cereberus have?");
        riddle.setTextAnswer("Three");
        return riddle;
    }
}