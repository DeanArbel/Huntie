package servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

import static Util.DatabaseFacade.Login;

/**
 * Created by dan on 2/24/2017.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String userEmail = request.getParameter("email");
        String password = request.getParameter("password");
        int token;

        if(userEmail == null || password == null){
            response.setStatus(400);
        }
        else{
            token = Login(userEmail,password);
            if(token != 0){

            }
            else{
                response.setStatus(400);
            }
        }
    }
}
