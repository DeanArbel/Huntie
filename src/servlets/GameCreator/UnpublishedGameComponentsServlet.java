package servlets.GameCreator;

import GameComponents.Game;
import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static servlets.Util.ServletUtils.SetError;

/**
 * Created by Dean on 18/2/2017.
 */
@WebServlet(name = "UnpublishedGameComponentsServlet")
public class UnpublishedGameComponentsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                Game game = DatabaseFacade.getGame(DatabaseFacade.getUser(userid).getUnpublishedGame().getGameId());
                if (game != null) {
                    handleRequest(request, out, game);
                } else {
                    SetError(response, 400, "Game not found");
                }
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleRequest(HttpServletRequest request, PrintWriter out, Game game) throws ServletException {
        String requestType = request.getParameter("requestType");
        Gson gson = new Gson();
        switch (requestType) {
            case "GameType":
                out.println(gson.toJson(game.isTeamGame()));
                out.println(gson.toJson(game.getMaxPlayers()));
                out.println(gson.toJson(game.getMaxPayersInTeam()));
                out.println(gson.toJson(game.getTeamNames()));
            default:
                throw new ServletException("No request was sent");
        }
    }
}
