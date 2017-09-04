package servlets;

import GameComponents.SessionToken;
import GameComponents.Utils.FaceBookLoginResponse;
import Util.DatabaseFacade;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.persistence.EntityExistsException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
            String userID = request.getParameter("userID");
            String loginURL = "https://graph.facebook.com/v2.10/" + userID + "?access_token=" + accessToken + "&locale=en_US&fields=name,email";
            FaceBookLoginResponse faceBookLoginResponse = sendGet(loginURL);

            SessionToken token;

            if(faceBookLoginResponse.IsResponseValid()) {
                token = DatabaseFacade.FaceBookLogin(accessToken, faceBookLoginResponse.getEmail(), faceBookLoginResponse.getUserName());//userEmail, userName
                if (token.GetToken().equals("")) {
                    response.setStatus(403);//400
                } else {
                    out.println(gson.toJson(token.GetToken()));
                    out.flush();
                }
            }
            else{
                response.setStatus(403);
            }
        } catch (NonUniqueResultException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "username already in use");
        } catch (EntityExistsException e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Email already in use");
        } catch (Exception e) {
            DatabaseFacade.RollbackTransaction();
            SetError(response, 400, "Server has encountered an unknow error");
        }
    }


    private FaceBookLoginResponse sendGet(String url) throws Exception {


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

//        //add request header
//        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(response.toString()).getAsJsonObject();
        return new FaceBookLoginResponse(o.get("email").getAsString(),o.get("name").getAsString(),Integer.toString(responseCode));
    }
}