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
        User user = DatabaseFacade.GetUser("1");
        if (user == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
               // ServletUtils.AssertUserInDatabase(user);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                handlePostRequest(request, user, out, game, game.GetUserRiddleById(Integer.parseInt(request.getParameter("riddleCode")),user));
                DatabaseFacade.EndTransaction();
            } catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handlePostRequest(HttpServletRequest request, User user, PrintWriter out, Game game, Riddle riddle) {
        //TODO: Handle photo riddle

        out.println(gson.toJson(game.TryToSolveRiddle(user, riddle, request.getParameter("answer"))));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //String username = SessionUtils.getUsername(request);
        User user = DatabaseFacade.GetUser("1");
        if (user == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //ServletUtils.AssertUserInDatabase(user);
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                Riddle riddle = game.GetUserRiddleById(Integer.parseInt(request.getParameter("riddleCode")),user);
                handleGetRequest(out, user, riddle);
                DatabaseFacade.EndTransaction();
                // TODO: Prevent user from accessing riddle unless he's in area
            } catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(PrintWriter out, User user, Riddle riddle) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", riddle.getName());
        dataMap.put("question", riddle.getTextQuestion());
        dataMap.put("optionalImage", riddle.GetOptionalQuestionImage());
        if (!riddle.isIsTextType()) {
            dataMap.put("answer", riddle.getAnswer());
        }
        out.println(gson.toJson(dataMap));
    }
}