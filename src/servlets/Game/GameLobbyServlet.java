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
import java.util.*;

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
        //String username = SessionUtils.getUsername(request);
        DatabaseFacade databaseFacade = (DatabaseFacade) getServletContext().getAttribute("databaseFacade");
        User user = databaseFacade.GetUser(request.getParameter("userEmail"));
        if (user == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //ServletUtils.AssertUserInDatabase(user);
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                Game game = databaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(request, out, user, game);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(HttpServletRequest request, PrintWriter out, User user, Game game) {
        String playerReq = request.getParameter("request");
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
            responseData.put("myTeamScore", game.GetPlayerTeamScore(user));
            responseData.put("otherTeamsScore", game.GetOtherTeamsScore(user));
            responseData.put("myTeamName", game.GetPlayerTeamName(user));
            responseData.put("myTeamLevel", game.GetTeamLevel(user));
        }
        out.println(gson.toJson(responseData));
    }

    private void handleGameInfoRequest(PrintWriter out, User user, Game game) {
        Date now = new Date();
        Date endTime = game.GetEndTime();
        boolean playerHasWon = game.HasPlayerWon(user);
        Map<String, Object> responseData = new HashMap();
        Map<String, String> riddlesNameAndLocations = new HashMap<>();
        if (!playerHasWon && endTime.after(now)) {
            for (Riddle riddle : game.GetUserRiddlesToSolve(user)) {
                riddlesNameAndLocations.put(riddle.getName(), riddle.getM_Location());
            }
        }
        responseData.put("playerHasWon", playerHasWon);
        responseData.put("riddlesNameAndLocations", riddlesNameAndLocations);
        responseData.put("gameName", game.GetGameName());
        responseData.put("startTime", game.GetStartTime());
        responseData.put("endTime", game.GetEndTime());
        out.println(gson.toJson(responseData));
    }
}