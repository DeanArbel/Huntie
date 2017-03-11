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
        response.setCharacterEncoding("UTF-8");
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                ServletUtils.AssertUserInDatabase(userid);
                handleGetRequest(request.getParameter("request"), out, userid);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(String i_RequestType, PrintWriter out, String i_Userid) throws ServletException {
        Map<String, Object> responseMap = new HashMap<>();
        User user = DatabaseFacade.GetUser(i_Userid);
        switch (i_RequestType) {
            case "Tables":
                Map<String, String> createdGames = new HashMap<>();
                Map<String, String> playedGames = new HashMap<>();
                for (Map.Entry<String, String> game : user.GetManagedGames().entrySet()) {
                    createdGames.put(game.getKey(), game.getValue());
                }
                for (Map.Entry<String, String> game : user.GetPlayedGames().entrySet()) {
                    playedGames.put(game.getKey(), game.getValue());
                }
                responseMap.put("myGames", createdGames);
                responseMap.put("playedGames", playedGames);
                break;
            case "UserInfo":
                responseMap.put("username", user.GetUserName());
                responseMap.put("email", user.GetEmailAddress());
                break;
            default:
                throw new ServletException("Did not receive proper request");
        }

        out.println(gson.toJson(responseMap));
    }
}
