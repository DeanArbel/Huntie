package GameComponents;

import javax.ejb.Local;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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

    private LocalTime m_ExpirationTime = LocalTime.now();


    public String GetToken(){return m_Token;}

    public void SetToken(String i_Token){m_Token = i_Token;}

    public User GetUser(){return  m_User;}

    public void SetUser(User i_User){m_User = i_User;}

    public Boolean IsExpiried(){
        return !m_ExpirationTime.isAfter(LocalTime.now());
    }

    public void UpdateExpirationTime(){
        m_ExpirationTime = LocalTime.now();
        m_ExpirationTime = m_ExpirationTime.plusHours(3);
    }
}
