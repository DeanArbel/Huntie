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
    private String m_OptionalQuestionImage;
    private String m_Answer;
    private String m_Location;
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

    public String getAnswer() {
        return m_Answer;
    }

    public void setAnswer(String m_TextAnswer) {
        this.m_Answer = m_TextAnswer;
    }

    public String GetOptionalQuestionImage() {
        return m_OptionalQuestionImage;
    }

    public void SetOptionalQuestionImage(String i_Image) {
        this.m_OptionalQuestionImage = i_Image;
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

    public boolean CheckAnswer(String i_Answer) {
        if (m_IsTextType) {
            return checkTextAnswer(i_Answer);
        }
        return i_Answer.equals("true");
    }

    private boolean checkTextAnswer(String i_Answer) {
        return m_Answer.toLowerCase().equals(i_Answer.toLowerCase());
    }

    public String getM_Location() {
        return m_Location;
    }

    public void setM_Location(String m_Location) {
        this.m_Location = m_Location;
    }
}
