package servlets.Util;

import Util.DatabaseFacade;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Dean on 18/2/2017.
 */
public class ServletUtils {
    public static void SetError(HttpServletResponse response, int i_ErrorNum, String errorMessage){
        response.setStatus(i_ErrorNum);
        response.addHeader("errorText", errorMessage);
    }

    public static void AssertUserInDatabase(String i_UserId) throws ServletException {
        //TODO: Change implementation upon login implementation
        DatabaseFacade.createUser("tmp", "tmp");
    }
}