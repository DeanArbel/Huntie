package servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

import static Util.DatabaseFacade.Login;
import static servlets.Util.ServletUtils.SetError;

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
        int token;

        if(userEmail == null || password == null){

            SetError(response,410,"user Email or password are void");
        }
        else{
            token = Login(userEmail,password);
            if(token != 0){
                out.print(token);
                out.flush();
            }
            else{
                SetError(response,420,"UserName/Password entered incorrect");
            }
        }
    }
}
