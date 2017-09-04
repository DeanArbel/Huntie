package GameComponents.Utils;

public class FaceBookLoginResponse {
    private String m_Email;
    private String m_UserName;
    private String m_Response;

    public FaceBookLoginResponse(String i_Email, String i_UserName, String i_Response){
        m_Email = i_Email;
        m_Response = i_Response;
        m_UserName = i_UserName;
    }

    public String getEmail() {
        return m_Email;
    }

    public String getUserName() {
        return m_UserName;
    }

    public Boolean IsResponseValid(){
        return m_Response.equals("200");
    }
}
