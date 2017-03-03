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
import java.util.HashMap;
import java.util.Map;

import static servlets.Util.ServletUtils.SetError;

/**
 * Created by Dean on 03/03/2017.
 */
@WebServlet(name = "RiddleServlet")
public class RiddleServlet extends HttpServlet {
    private static Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                handlePostRequest(request, userid, out, game);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handlePostRequest(HttpServletRequest request, String userid, PrintWriter out, Game game) {
        //TODO: Handle photo riddle
        out.println(gson.toJson(game.TryToSolveTextRiddle(userid, Integer.parseInt(request.getParameter("riddleCode")), request.getParameter("answer"))));
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
                Riddle riddle = game.GetUserRiddleByIndex(userid, Integer.parseInt(request.getParameter("riddleCode")));
                handleGetRequest(out, userid, riddle);
            } catch (Exception e) {
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(PrintWriter out, String userid, Riddle riddle) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", riddle.getName());
        dataMap.put("question", riddle.getTextQuestion());
        dataMap.put("optionalImage", riddle.GetOptionalQuestionImage());
        out.println(gson.toJson(dataMap));
    }
}