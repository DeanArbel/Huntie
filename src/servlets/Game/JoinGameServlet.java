package servlets.Game;

import GameComponents.Game;
import GameComponents.Team;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static servlets.Util.ServletUtils.SetError;

/**
 * Created by Dean on 26/02/2017.
 */
@WebServlet(name = "JoinGameServlet")
public class JoinGameServlet extends HttpServlet {
    private static Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try {
                Game game = DatabaseFacade.getGame(DatabaseFacade.getUser(userid).getUnpublishedGame().GetGameId());
                if (game != null) {
                    game.AddPlayer(userid, Integer.parseInt(request.getParameter("teamIndex")));
                } else {
                    SetError(response, 400, "Game not found");
                }
            }
            catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            ServletUtils.AssertUserInDatabase(userid);
            try (PrintWriter out = response.getWriter()) {
                Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(out, userid, game);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(PrintWriter out, String userId, Game game) throws ServletException {
        Map<String, Object> outputMap = new HashMap<>();
        boolean isPlayerInGame = game.IsPlayerInGame(userId);
        outputMap.put("isPlayerInGame", isPlayerInGame);
        if (!isPlayerInGame) {
            String errMsg = getPlayerJoinGameErrors(userId, game);
            outputMap.put("errMsg", errMsg);
            if (errMsg.isEmpty()) {
                outputMap.put("maxPlayersInTeam", game.GetMaxPlayersInTeam());
                List<Team> teams = game.GetTeams();
                List<Map<String, String>> teamsMap = new ArrayList<>();
                for (Team team : teams) {
                    Map<String, String> teamMap = new HashMap<>();
                    teamMap.put("name", team.getTeamName());
                    teamMap.put("count", Integer.toString(team.Count()));
                    teamsMap.add(teamMap);
                }
                outputMap.put("teams", teamsMap);
            }
        }
        out.println(gson.toJson(outputMap));
    }

    private String getPlayerJoinGameErrors(String userId, Game game) {
        String errMsg = "";
        //TODO: Complete this
        return errMsg;
    }
}
