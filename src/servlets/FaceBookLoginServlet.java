package servlets;

import GameComponents.SessionToken;
import Util.DatabaseFacade;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by dan on 3/13/2017.
 */
@WebServlet(name = "FaceBookLoginServlet")
public class FaceBookLoginServlet extends javax.servlet.http.HttpServlet{
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String accessToken = request.getParameter("accessToken");
        String userEmail = request.getParameter("userEmail");
        String userName = request.getParameter("userName");
        DatabaseFacade databaseFacade = (DatabaseFacade) getServletContext().getAttribute("databaseFacade");
        SessionToken token;

        token = databaseFacade.FaceBookLogin(accessToken, userEmail, userName);
        if(token.GetToken().equals("")){
            response.setStatus(400);
        }
        else{
            out.print(token.GetToken());
            out.flush();
        }
    }
}
