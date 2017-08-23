package servlets;

/**
 * Created by dan on 3/14/2017.
 */

import Util.DatabaseFacade;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "SignUpServlet")
public class SignUpServlet extends javax.servlet.http.HttpServlet  {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String userEmail = request.getParameter("email");
        String password = request.getParameter("password");
        String username = request.getParameter("username");
        PrintWriter out = response.getWriter();
        DatabaseFacade databaseFacade = (DatabaseFacade) getServletContext().getAttribute("databaseFacade");
        int res = databaseFacade.SignUp(userEmail,password,username);

        if(res == 0){
            out.print(res);
            out.flush();
        }
        else if(res == -1){
            SetError(response,400,"Email already in use");
        }
        else{
            SetError(response,400,"username already in use");
        }
    }
}
