package servlets;

import GameComponents.SessionToken;
import Util.DatabaseFacade;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by dan on 2/24/2017.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends javax.servlet.http.HttpServlet{
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String userEmail = request.getParameter("email");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();
        SessionToken token;

        if(userEmail == null || password == null){

            SetError(response,410,"user Email or password are void");
        }
        else{
            DatabaseFacade databaseFacade = (DatabaseFacade) getServletContext().getAttribute("databaseFacade");
            token = databaseFacade.Login(userEmail,password);//TODO get facade here and then facade.Login
            if(!token.GetToken().equals("")){
                out.print(token.GetToken());
                out.flush();
            }
            else{
                SetError(response,420,"UserName/Password entered incorrect");
            }
        }
    }
}
