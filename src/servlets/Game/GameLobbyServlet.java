package servlets.Game;

import GameComponents.Game;
import GameComponents.Riddle;
import GameComponents.User;
import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static servlets.Util.ServletUtils.SetError;

/**
 * Created by Dean on 28/02/2017.
 */
@WebServlet(name = "GameLobbyServlet")
public class GameLobbyServlet extends HttpServlet {
    private static Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //ServletUtils.AssertUserInDatabase(user);
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                DatabaseFacade.UpdateToken(token);
                User user = DatabaseFacade.GetUserFromToken(token);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(request, out, user, game);
                DatabaseFacade.EndTransaction();
            } catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(HttpServletRequest request, PrintWriter out, User user, Game game) throws Exception {
        String playerReq = request.getParameter("request");
        if (!game.IsPlayerInGame(user)) {
            throw new Exception("Player not in game");
        }
        switch(playerReq) {
            case "getGameInfo":
                handleGameInfoRequest(out, user, game);
                break;
            case "getPlayerTables":
                handlePlayerTablesRequest(out, user, game);
        }
    }

    private void handlePlayerTablesRequest(PrintWriter out, User user, Game game) {
        Map<String, Object> responseData = new HashMap();
        responseData.put("isTeamGame", game.IsTeamGame());
        if (game.IsTeamGame()) {
            responseData.put("otherTeamsScore", game.GetOtherTeamsScore(user));
            responseData.put("myTeamName", game.GetPlayerTeamName(user));
            responseData.put("myTeamScore", game.GetPlayerTeamScore(user));
        }
        out.println(gson.toJson(responseData));
    }

    private void handleGameInfoRequest(PrintWriter out, User user, Game game) {
        Date now = new Date();
        Date endTime = game.GetEndTime();
        boolean playerHasWon = game.HasPlayerWon(user);
        Map<String, Object> responseData = new HashMap();
        Map<String, String> riddlesNameAndLocations = new HashMap<>();
        Map<String, Integer> riddleNamesAndIds = new HashMap<>();
        if (!playerHasWon && endTime.after(now)) {
            for (Riddle riddle : game.GetUserRiddlesToSolve(user)) {
                riddlesNameAndLocations.put(riddle.getName(), riddle.getM_Location());
                riddleNamesAndIds.put(riddle.getName(), riddle.getId());
            }
        }
        responseData.put("playerHasWon", playerHasWon);
        if (!playerHasWon) {
            int teamLevel = game.GetTeamLevelIndex(user);
            responseData.put("myLevel", teamLevel);
            responseData.put("riddlesNamesAndLocations", riddlesNameAndLocations);
            responseData.put("riddlesNamesAndIds", riddleNamesAndIds);
            responseData.put("isTreasureLevel", game.IsTreasureLevel(teamLevel - 1));
        }
        responseData.put("treasureType", game.GetTreasureType());
        responseData.put("gameName", game.GetGameName());
        responseData.put("startTime", game.GetStartTime().getTime());
        responseData.put("endTime", game.GetEndTime().getTime());
        out.println(gson.toJson(responseData));
    }
}