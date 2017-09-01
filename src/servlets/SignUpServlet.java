package servlets;

/**
 * Created by dan on 3/14/2017.
 */

import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.persistence.EntityExistsException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

import static servlets.Util.ServletUtils.SetError;


@WebServlet(name = "SignUpServlet")
public class SignUpServlet extends javax.servlet.http.HttpServlet  {
    private static Gson gson = new Gson();

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        response.setContentType("application/json");
        try {
            String userEmail = request.getParameter("email");
            String password = request.getParameter("password");
            String username = request.getParameter("username");
            PrintWriter out = response.getWriter();
            int res = DatabaseFacade.SignUp(userEmail, password, username);
            out.println(gson.toJson(res));
            out.flush();
            response.setStatus(200);

        } catch (NonUniqueResultException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Username already in use");
        } catch (EntityExistsException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Email already in use");
        }
        catch (Exception e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response,  400, "Server has encountered an unknown error");
        }
    }
}