package servlets.GameCreator;

import GameComponents.User;
import Util.DatabaseFacade;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static servlets.Util.ServletUtils.SetError;


/**
 * Created by Dean on 18/2/2017.
 */
@WebServlet(name = "NewGameServlet")
public class NewGameServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            SetError(response, 480, "User has to login");
        } else {
            try {
                User user = DatabaseFacade.GetUserFromToken(token);
                if (DatabaseFacade.DoesUserHaveAnUnpublishedGame(user.GetEmailAddress())) {
                    if ("true".equals(request.getParameter("createNewGame"))) {
                        createNewGame(response, user.GetEmailAddress());
                    } else {
                        SetError(response, 499, "User already has a game");
                    }
                } else {
                    createNewGame(response, user.GetEmailAddress());
                }
            }
            catch(Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void createNewGame(HttpServletResponse i_Response, String i_UserId) throws IOException {
        DatabaseFacade.CreateNewGame(i_UserId);
        i_Response.sendRedirect("/Manager/game-type.html");
    }
}