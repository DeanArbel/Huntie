package servlets;

import GameComponents.SessionToken;
import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

import static servlets.Util.ServletUtils.SetError;


/**
 * Created by dan on 2/24/2017.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends javax.servlet.http.HttpServlet{
    private static Gson gson = new Gson();

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String userEmail = request.getParameter("email");
            String password = request.getParameter("password");
            PrintWriter out = response.getWriter();
            SessionToken token;
            if (userEmail == null || password == null) {
                SetError(response, 410, "user Email or password are void");
            } else {
                token = DatabaseFacade.Login(userEmail, password);//TODO get facade here and then facade.Login
                if (!token.GetToken().equals("")) {
                    out.println(gson.toJson(token.GetToken()));
                    response.setStatus(200);
                    DatabaseFacade.EndTransaction();
                } else {
                    DatabaseFacade.RollbackTransaction();
                    SetError(response, 420, "UserName/Password entered incorrect");
                }
            }
        } catch (Exception e) {
            SetError(response, 400, "Login failed, please try again");
        }
    }
}