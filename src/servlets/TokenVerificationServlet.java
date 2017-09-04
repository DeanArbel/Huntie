package servlets;

import Util.DatabaseFacade;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet(name = "TokenVerificationServlet")
public class TokenVerificationServlet extends javax.servlet.http.HttpServlet{
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
//        try {
            String accessToken = request.getParameter("access token");

            if(accessToken != null && DatabaseFacade.IsTokenValid(accessToken)) {
                response.setStatus(200);
            }
            else{
                response.setStatus(403);
            }
//        } catch (NonUniqueResultException e) {
//            DatabaseFacade.RollbackTransaction();
//            SetError(response, 400, "username already in use");
//        } catch (EntityExistsException e) {
//            DatabaseFacade.RollbackTransaction();
//            SetError(response, 400, "Email already in use");
//        } catch (Exception e) {
//            DatabaseFacade.RollbackTransaction();
//            SetError(response, 400, "Server has encountered an unknow error");
//        }
    }
}
