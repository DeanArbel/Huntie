package servlets.Game;

import GameComponents.Game;
import GameComponents.Team;
import Util.DatabaseFacade;
import com.google.gson.Gson;

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
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try {
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                DatabaseFacade.UpdateToken(token);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game != null) {
                    game.AddPlayer(DatabaseFacade.GetUserFromToken(token),Integer.parseInt((request.getParameter("teamIndex"))));
                } else {
                    throw new ServletException("Game not found");
                }
                DatabaseFacade.EndTransaction();
            }
            catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //String username = SessionUtils.getUsername(request);
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                DatabaseFacade.IsTokenValid(token);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(out, DatabaseFacade.GetUserFromToken(token).GetEmailAddress(), game);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            } finally {
                DatabaseFacade.EndTransaction();
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
                List<Map<String, String>> teamsMap = new ArrayList<>();
                for (Team team : game.GetTeams()) {
                    Map<String, String> teamMap = new HashMap<>();
                    teamMap.put("name", team.GetTeamName());
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
