package servlets.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Dean on 18/2/2017.
 */
public class SessionUtils {
    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        //TODO: Replace the logics here depending on the way login is implemented
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
}
