package GameComponents;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;

/**
 * Created by Dean on 18/2/2017.
 */
@Entity
public class Riddle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int m_Id;

    public static final int MIN_APPEARANCE = 1;
    public static final int MAX_APPEARANCE = 99;
    private String m_Name;
    private int m_AppearanceNumber;
    private boolean m_IsTextType;
    private String m_TextQuestion;
    private String m_OptionalQuestionImage;
    private String m_Answer;
    //private Set<String> r_SolvedByUserList = new HashSet<>();
    //private Set<String> r_SolvedByTeamList = new HashSet<>();

    @OneToMany
    private List<User> m_SolvedByUser = new ArrayList<>();

    @OneToMany
    private List<Team> m_SolvedByTeam = new ArrayList<>();

    public int getId(){return m_Id;}

    //public void setId(int i_Id){m_Id=i_Id;}

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

    public void UserSolvedRiddle(User i_Solver, Team i_SolverTeam) {//string String
        //r_SolvedByUserList.add(i_Solver);
        m_SolvedByUser.add(i_Solver);
        //r_SolvedByTeamList.add(i_SolverTeam);
        m_SolvedByTeam.add(i_SolverTeam);
    }

    public boolean IsSolvedPlayer(User i_UserId) {
        return m_SolvedByUser.contains(i_UserId);
    }//r_SolvedByUserList.contains(i_UserId);

    public boolean IsSolvedByTeam(Team i_TeamName) {
        return m_SolvedByTeam.contains(i_TeamName);
    }//r_SolvedByTeamList.contains(i_TeamName);

    public boolean CheckAnswer(String i_Answer) {
        if (m_IsTextType) {
            return checkTextAnswer(i_Answer);
        }
        return i_Answer.equals("true");
    }

    private boolean checkTextAnswer(String i_Answer) {
        return m_Answer.toLowerCase().equals(i_Answer.toLowerCase());
    }

    public static Comparator<Riddle> RiddleeComparator = new Comparator<Riddle>() {

        public int compare(Riddle r1, Riddle r2) {
            int res = r1.getAppearanceNumber() - r2.getAppearanceNumber();

            if(res == 0) {
                return res;
            }
            return res > 0 ? 1:-1;
            //ascending order
            //return StudentName1.compareTo(StudentName2);


            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };
}
