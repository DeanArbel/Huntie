package servlets.GameCreator;

import Util.DatabaseFacade;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by Dean on 18/2/2017.
 */
@WebServlet(name = "NewGameSevlet")
public class NewGameServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        //TODO: Fix this
        //String username = SessionUtils.getUsername(request);
        String userid = "1";
        DatabaseFacade.createUser("tmp", "tmp");
        if (userid == null) {
            response.sendRedirect("index.jsp");
        } else {
//            try (PrintWriter out = response.getWriter()) {
//                Gson gson = new Gson();
//                if (gameName != null) {
//                    if (isGameExist(gameName)) {
//                        GameEntry gameEntry = getGameEntry(gameName);
//                        handleRequest(request, response, gameEntry);
//                        String json = gson.toJson(gameEntry);
//                        out.println(json);
//                        out.flush();
//                    } else {
//                        setError(response, "Game not found");
//                    }
//                }
//            }
//            catch (Exception e) {
//                setError(response, e.getMessage());
//            }
            //TODO: Adjust username or id depending on login implementation
            DatabaseFacade.CreateNewGame(userid);
            response.sendRedirect("/Manager/GameType.html");
        }
    }
}
