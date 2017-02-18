package servlets.GameCreator;

import Util.DatabaseFacade;
import servlets.Util.SessionUtils;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by Dean on 18/2/2017.
 */
@WebServlet(name = "NewGameSevlet")
public class NewGameServlet extends javax.servlet.http.HttpServlet {
    protected void doPut(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        //TODO: Fix this
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        if (userid == null) {
            response.sendRedirect("index.jsp");
        } else {
            //TODO: Adjust username or id depending on login implementation
            DatabaseFacade.CreateNewGame(userid);
            response.sendRedirect("Manager/GameType.html");
        }
    }
}
