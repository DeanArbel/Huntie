package GameComponents;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dan on 8/23/2017.
 */
@Entity
public class SessionToken {

    @Id
    private String m_Token;

    @OneToOne
    private User m_User;

    private Date m_ExpirationTime = new Date();


    public String GetToken(){return m_Token;}

    public void SetToken(String i_Token){m_Token = i_Token;}

    public User GetUser(){return  m_User;}

    public void SetUser(User i_User){m_User = i_User;}

    public Boolean IsExpiried(){
        return !m_ExpirationTime.after(new Date());
    }

    public void UpdateExpirationTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 3);
        m_ExpirationTime = calendar.getTime();
    }
}
