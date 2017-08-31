package servlets;

import GameComponents.SessionToken;
import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.persistence.EntityExistsException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

import static servlets.Util.ServletUtils.SetError;


/**
 * Created by dan on 3/13/2017.
 */
@WebServlet(name = "FaceBookLoginServlet")
public class FaceBookLoginServlet extends javax.servlet.http.HttpServlet {
    private static Gson gson = new Gson();

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            String accessToken = request.getParameter("accessToken");
            String userEmail = request.getParameter("userEmail");
            String userName = request.getParameter("userName");
            SessionToken token;

            token = DatabaseFacade.FaceBookLogin(accessToken, userEmail, userName);
            if (token.GetToken().equals("")) {
                response.setStatus(400);
            } else {
                out.println(gson.toJson(token.GetToken()));
                out.flush();
            }
        } catch (NonUniqueResultException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "username already in use");
        } catch (EntityExistsException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Email already in use");
        }
        catch (Exception e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Server has encountered an unknow error");
        }
    }
}
