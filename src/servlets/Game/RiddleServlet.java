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
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("/home.html");
        } else {
            try (PrintWriter out = response.getWriter()) {
               // ServletUtils.AssertUserInDatabase(user);
                DatabaseFacade.UpdateToken(token);
                User user = DatabaseFacade.GetUserFromToken(token);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                if (game == null) {
                    throw new ServletException("No game was found");
                }
                String riddleCode = request.getParameter("riddleCode");
                Integer intRiddleCode = riddleCode != null ? Integer.parseInt(riddleCode) : null;
                handlePostRequest(request, user, out, game, game.GetUserRiddleById(intRiddleCode ,user));
                DatabaseFacade.EndTransaction();
            } catch (Exception e) {
                    DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handlePostRequest(HttpServletRequest request, User user, PrintWriter out, Game game, Riddle riddle) {
        String answer = request.getParameter("answer");
        if (answer == null) {
            answer = new String();
        }
        out.println(gson.toJson(game.TryToSolveRiddle(user, riddle, answer)));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //String username = SessionUtils.getUsername(request);
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("/index.jsp");
        } else {
            try (PrintWriter out = response.getWriter()) {
                //ServletUtils.AssertUserInDatabase(user);
                //Game game = DatabaseFacade.getGame(request.getParameter("gameCode"));
                DatabaseFacade.UpdateToken(token);
                Game game = DatabaseFacade.getGame(Integer.parseInt(request.getParameter("gameCode")));
                User user = DatabaseFacade.GetUserFromToken(token);
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