package GameComponents;

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
}
