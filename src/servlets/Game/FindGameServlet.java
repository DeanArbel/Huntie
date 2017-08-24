package servlets.Game;

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
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dean on 27/02/2017.
 */
@WebServlet(name = "FindGameServlet")
public class FindGameServlet extends HttpServlet {
    private static Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                DatabaseFacade databaseFacade = (DatabaseFacade) getServletContext().getAttribute("databaseFacade");
                Game game = databaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handleGetRequest(out, userid, game);
            } catch (Exception e) {
//                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(PrintWriter out, String userEmail, Game game) throws ServletException, IOException {
        Map<String, String> dataMap = new HashMap();
        dataMap.put("gameCode", Integer.toString(game.GetGameId()));
        //        if (game.IsUserManager(userId)) {
//            //response.getWriter().println(gson.toJson(Constants.SITE_URL + "Manager/Menu.html?gameCode=" + game.GetGameId()));
//        } //TODO: Enable this and make the one below else if
        if (game.IsPlayerInGame(userEmail)) {
            dataMap.put("url", "/Player/GameLobby.html");
        } else if (!game.IsGameFull()) {
            dataMap.put("url", "/Player/JoinGame.html");
        } else {
            throw new ServletException("Game is full");
        }
        out.println(gson.toJson(dataMap));
    }
}
