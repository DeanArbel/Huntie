package servlets.GameCreator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dean on 18/2/2017.
 */
@WebServlet(name = "UnpublishedGameComponentsServlet")
public class UnpublishedGameComponentsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String gameName = request.getParameter("gameName");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            if (gameName != null) {
                if (isGameExist(gameName)) {
                    GameEntry gameEntry = getGameEntry(gameName);
                    handleRequest(request, response, gameEntry);
                    String json = gson.toJson(gameEntry);
                    out.println(json);
                    out.flush();
                } else {
                    setError(response, "Game not found");
                }
            }
        }
        catch (Exception e) {
            setError(response, e.getMessage());
        }
    }
}
