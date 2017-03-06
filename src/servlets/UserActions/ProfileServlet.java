package servlets.UserActions;

import GameComponents.User;
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
import java.util.HashMap;
import java.util.Map;

import static servlets.Util.ServletUtils.SetError;

/**
 * Created by Dean on 06/03/2017.
 */
@WebServlet(name = "ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private static Gson gson = new Gson();

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
                ServletUtils.AssertUserInDatabase(userid);
                handleGetRequest(out, userid);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(PrintWriter out, String userid) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, String> createdGames = new HashMap<>();
        Map<String, String> playedGames = new HashMap<>();
        User user = DatabaseFacade.GetUser(userid);
        for (Map.Entry<String, String> game : user.GetManagedGames().entrySet()) {
            createdGames.put(game.getKey(), game.getValue());
        }
        for (Map.Entry<String, String> game : user.GetPlayedGames().entrySet()) {
            playedGames.put(game.getKey(), game.getValue());
        }
        responseMap.put("username", user.GetUserName());
        responseMap.put("email", user.GetEmailAddress());
        responseMap.put("createdGames", createdGames);
        responseMap.put("playedGames", playedGames);
        out.println(gson.toJson(responseMap));
    }
}
