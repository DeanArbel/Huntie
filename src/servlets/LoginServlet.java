package servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by dan on 2/24/2017.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String userEmail = request.getParameter("email");
        String password = request.getParameter("password");

        if(userEmail == null || password == null){
            response.setStatus(400);
        }
//
//        if(!isValidUser(userEmail)){
//            response.setStatus(400);
//        }


    }
}
