package servlets.GameCreator;

import GameComponents.Game;
import GameComponents.Level;
import GameComponents.Riddle;
import GameComponents.User;
import Util.DatabaseFacade;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static servlets.Util.ServletUtils.SetError;


/**
 * Created by Dean on 18/2/2017.
 */
@MultipartConfig
@WebServlet(name = "UnpublishedGameComponentsServlet")
public class UnpublishedGameComponentsServlet extends HttpServlet {
    private static Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String username = SessionUtils.getUsername(request);
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //Game game = DatabaseFacade.getGame(DatabaseFacade.GetUser(userid).GetUnpublishedGame().GetGameId());
                DatabaseFacade.UpdateToken(token);
                User user = DatabaseFacade.GetUserFromToken(token);
                Game game = DatabaseFacade.getGame(user.GetUnpublishedGame().GetGameId());
                if (game != null) {
                    handlePostRequest(request, response, game, user.GetEmailAddress());
                    out.println(gson.toJson(game.GetGameId()));
                } else {
                    throw new ServletException( "Game not found");
                }
                DatabaseFacade.EndTransaction();
            }
            catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //String username = SessionUtils.getUsername(request);
        String token = request.getParameter("token");
        if (token == null || !DatabaseFacade.IsTokenValid(token)) {
            response.sendRedirect("index.jsp"); //TODO: Change this according to login system
        } else {
            try (PrintWriter out = response.getWriter()) {
                //Game game = DatabaseFacade.GetUser(userid).GetUnpublishedGame();
                DatabaseFacade.UpdateToken(token);
                User user = DatabaseFacade.GetUserFromToken(token);
                Game game = user.GetUnpublishedGame();
                if (game == null) {
                    //game = DatabaseFacade.CreateNewGame(userid);
                    game = DatabaseFacade.CreateNewGame(user.GetEmailAddress());
                }

                handleGetRequest(request, out, game);
                DatabaseFacade.EndTransaction();
            } catch (Exception e) {
                DatabaseFacade.RollbackTransaction();
                SetError(response, 400, e.getMessage());
            }
        }
    }

    private void handleGetRequest(HttpServletRequest request, PrintWriter out, Game game) throws ServletException {
        String requestType = request.getParameter("requestType");
        switch (requestType) {
            case "GameType":
                out.print("[" + gson.toJson(game.IsTeamGame()) + ", ");
                out.print(gson.toJson(game.GetMaxPlayers()) +", ");
                out.print(gson.toJson(game.GetMaxPlayersInTeam()) +", ");
                out.println(gson.toJson(game.GetTeamNames()) + "]");
                out.flush();
                break;
            case "GameBuilder":
                out.println(gson.toJson(game.GetLevels()));
                break;
            default:
                throw new ServletException("No request was sent");
        }
    }

    private void handlePostRequest(HttpServletRequest request, HttpServletResponse response, Game game, String userid)
            throws ServletException, IOException{
        String requestType = request.getParameter("requestType");
        switch (requestType) {
            case "GameType":
                handleGameTypeRequest(request, game);
                break;
            case "GameBuilder":
                handleGameBuilderRequest(request, game);
                break;
            case "GameSettings":
                handleGameSettingsRequest(request, game, userid);
                break;
            default:
                throw new ServletException("No request was sent");
        }
        response.setStatus(200);
    }

    private void handleGameSettingsRequest(HttpServletRequest request, Game game, String userid) {
        HashMap<String, Object> settingsMap = gson.fromJson(request.getParameter("settings"), HashMap.class);
        game.SetStartDate(new Date(((Double)settingsMap.get("startTime")).longValue()));
        game.SetDuration(((Double)settingsMap.get("duration")));
        game.AddTreasureLevel((String)settingsMap.get("treasureType"), (String)settingsMap.get("treasureLocation"));
        game.SetGameName((String)settingsMap.get("gameName"));
        game.PublishGame();
        //TODO: Add check that game is really ready for publish
        //DatabaseFacade.GetUser(userid).SetUnpublishedGame(null);
        DatabaseFacade.GetUser(userid).SetUnpublishedGame(null);
    }

    private void handleGameTypeRequest(HttpServletRequest request, Game game) {
        boolean isTeamGame = request.getParameter("gameType").equals("Team Game");
        Integer maxPlayers;
        Integer maxPlayersInTeam;
        Map teamMap;
        if (isTeamGame) {
            maxPlayersInTeam = Integer.parseInt(request.getParameter("maxPlayersInTeam"));
            teamMap = gson.fromJson(request.getParameter("teams"), HashMap.class);
            maxPlayers = maxPlayersInTeam * teamMap.size();
        }
        else {
            maxPlayersInTeam = 1;
            teamMap = new HashMap();
            teamMap.put("Players", null);
            maxPlayers = Integer.parseInt(request.getParameter("maxPlayers"));
        }

        game.SetMaxPayersInTeam(maxPlayersInTeam);
        game.SetMaxPlayers(maxPlayers);
        game.SetIsTeamGame(isTeamGame);
        game.SetTeamNames(teamMap.keySet());
    }

    private void handleGameBuilderRequest(HttpServletRequest request, Game game) throws ServletException, IOException {
        String gameBuilderRequest = request.getParameter("action");
        HashMap<String, Object> riddleMap = gson.fromJson(request.getParameter("riddle"), HashMap.class);
        if ("delete".equals(gameBuilderRequest)) {
            deleteRiddle(game, ((Double)riddleMap.get("level")).intValue(), ((Double)riddleMap.get("index")).intValue());
        }
        else if ("add".equals(gameBuilderRequest)) {
            Integer levelIndex = ((Double)riddleMap.get("level")).intValue();
            Level level = game.GetLevel(levelIndex);
            Riddle riddle = buildRiddle(riddleMap);
            level.AddRiddle(riddle);
        }
        else {
            throw new ServletException("No action was given");
        }
    }

    private Riddle buildRiddle(Map i_Riddle) throws ServletException {
        Riddle riddle = new Riddle();
        boolean isTextType = !"Photo".equals(i_Riddle.get("type"));
        int appearanceNumber = ((Double)i_Riddle.get("level")).intValue();
        String answer = isTextType ? (String)i_Riddle.get("answer") : ((String)i_Riddle.get("answer"));
        String name = (String)i_Riddle.get("name");
        String questionText = (String)i_Riddle.get("questionText");
        String optionalImage = ((String)i_Riddle.get("questionOptionalImage"));
        String location = ((String)i_Riddle.get("location"));
        if (name == null || questionText == null || answer == null && appearanceNumber > Riddle.MAX_APPEARANCE && appearanceNumber < Riddle.MIN_APPEARANCE) {
            throw new ServletException("Received illegal parameters");
        }
        riddle.setName(name);
        riddle.setAppearanceNumber(appearanceNumber);
        riddle.setTextQuestion(questionText);
        riddle.setIsTextType(isTextType);
        riddle.SetOptionalQuestionImage(optionalImage);
        riddle.setAnswer(answer);
        riddle.setM_Location(location);
        DatabaseFacade.PersistObject(riddle);
        return riddle;
    }

    private void deleteRiddle(Game game, int appearanceNumber, int index) {
        game.DeleteRiddle(appearanceNumber,index);
    }
}
