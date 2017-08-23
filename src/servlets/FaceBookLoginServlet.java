package servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

import static Util.DatabaseFacade.FaceBookLogin;

/**
 * Created by dan on 3/13/2017.
 */
@WebServlet(name = "FaceBookLoginServlet")
public class FaceBookLoginServlet extends javax.servlet.http.HttpServlet{
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String accessToken = request.getParameter("accessToken");
        String userID = request.getParameter("userID");
        int token;

        token = FaceBookLogin(accessToken, Integer.parseInt(userID));
        if(token == 0){
            response.setStatus(400);
        }
    }
}
