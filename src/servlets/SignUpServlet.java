package servlets;

/**
 * Created by dan on 3/14/2017.
 */

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import static Util.DatabaseFacade.SignUp;

@WebServlet(name = "SignUpServlet")
public class SignUpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        String userEmail = request.getParameter("email");
        String password = request.getParameter("password");
        String userame = request.getParameter("username");
        int res = SignUp(userEmail,password,userame);

        if(res == 0){

        }
        else if(res == -1){

        }
        else{

        }
    }
}
