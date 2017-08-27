package servlets.GameCreator;

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
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        //TODO: Fix this
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try {
                if (DatabaseFacade.DoesUserHaveAnUnpublishedGame(userid)) {
                    if ("true".equals(request.getParameter("createNewGame"))) {
                        createNewGame(response, userid);
                    } else {
                        SetError(response, 499, "User already has a game");
                    }
                } else {
                    createNewGame(response, userid);
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
        i_Response.sendRedirect("/Manager/GameType.html");
    }
}