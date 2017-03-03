package GameComponents;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dean on 18/2/2017.
 */
public class Riddle {
    public static final int MIN_APPEARANCE = 1;
    public static final int MAX_APPEARANCE = 99;
    private String m_Name;
    private int m_AppearanceNumber;
    private boolean m_IsTextType;
    private String m_TextQuestion;
    //private ??? m_QuestionImage;
    private String m_TextAnswer;
    private Set<String> r_SolvedByUserList = new HashSet<>();
    private Set<String> r_SolvedByTeamList = new HashSet<>();

    public String getName() {
        return m_Name;
    }

    public void setName(String m_Name) {
        this.m_Name = m_Name;
    }

    public int getAppearanceNumber() {
        return m_AppearanceNumber;
    }

    public void setAppearanceNumber(int m_AppearanceNumber) {
        this.m_AppearanceNumber = m_AppearanceNumber;
    }

    public boolean isIsTextType() {
        return m_IsTextType;
    }

    public void setIsTextType(boolean m_IsTextType) {
        this.m_IsTextType = m_IsTextType;
    }

    public String getTextQuestion() {
        return m_TextQuestion;
    }

    public void setTextQuestion(String m_TextQuestion) {
        this.m_TextQuestion = m_TextQuestion;
    }

    public String getTextAnswer() {
        return m_TextAnswer;
    }

    public void setTextAnswer(String m_TextAnswer) {
        this.m_TextAnswer = m_TextAnswer;
    }

    public void UserSolvedRiddle(String i_Solver, String i_SolverTeam) {
        r_SolvedByUserList.add(i_Solver);
        r_SolvedByTeamList.add(i_SolverTeam);
    }

    public boolean IsSolvedPlayer(String i_UserId) {
        return r_SolvedByUserList.contains(i_UserId);
    }

    public boolean IsSolvedByTeam(String i_TeamName) {
        return r_SolvedByTeamList.contains(i_TeamName);
    }

    public boolean CheckTextAnswer(String i_Answer) {
        return m_TextAnswer.toLowerCase().equals(i_Answer.toLowerCase());
    }
}
