package servlets.Game;

import GameComponents.Game;
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
            ServletUtils.AssertUserInDatabase(userid);
            try (PrintWriter out = response.getWriter()) {
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
            case "getRiddles":
                out.println(gson.toJson(game.GetUserRiddlesToSolve(userid)));
                break;
        }
    }
}
