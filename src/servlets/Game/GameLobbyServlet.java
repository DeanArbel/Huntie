package servlets.Game;

import GameComponents.Game;
import GameComponents.Riddle;
import Util.DatabaseFacade;
import com.google.gson.Gson;
import servlets.Util.ServletUtils;

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
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                ServletUtils.AssertUserInDatabase(userid);
                Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(request, out, userid, game);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(HttpServletRequest request, PrintWriter out, String userid, Game game) {
        String playerReq = request.getParameter("request");
        switch(playerReq) {
            case "getGameInfo":
                handleGameInfoRequest(out, userid, game);
                break;
            case "getPlayerTables":
                handlePlayerTablesRequest(out, userid, game);
        }
    }

    private void handlePlayerTablesRequest(PrintWriter out, String userid, Game game) {
        Map<String, Object> responseData = new HashMap();
        responseData.put("isTeamGame", game.IsTeamGame());
        if (game.IsTeamGame()) {
            responseData.put("myTeamScore", game.GetPlayerTeamScore(userid));
            responseData.put("otherTeamsScore", game.GetOtherTeamsScore(userid));
        }
        out.println(gson.toJson(responseData));
    }

    private void handleGameInfoRequest(PrintWriter out, String userid, Game game) {
        Date now = new Date();
        Date endTime = game.GetEndTime();
        boolean playerHasWon = game.HasPlayerWon(userid);
        Map<String, Object> responseData = new HashMap();
        List<String> riddlesNames = new ArrayList();
        if (!playerHasWon && endTime.after(now)) {
            for (Riddle riddle : game.GetUserRiddlesToSolve(userid)) {
                riddlesNames.add(riddle.getName());
            }
        }
        responseData.put("playerHasWon", playerHasWon);
        responseData.put("riddlesNames", riddlesNames);
        responseData.put("gameName", game.GetGameName());
        responseData.put("startTime", game.GetStartTime());
        responseData.put("endTime", game.GetEndTime());
        out.println(gson.toJson(responseData));
    }
}