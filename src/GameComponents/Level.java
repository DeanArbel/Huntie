package GameComponents;

import Util.DatabaseFacade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dan on 8/16/2017.
 */
@Entity
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int m_Id;

    private int m_Index;

    @OneToMany
    private List<Riddle> m_Riddles;

    public Level(int i_Index) {
        this();
        m_Index = i_Index;
    }

    public Level() {
        m_Riddles = new ArrayList<>();
    }

    public void AddRiddle(Riddle i_Riddle){
        m_Riddles.add(i_Riddle);
    }

    public boolean IsRiddleInLevel(Riddle i_Riddle){return m_Riddles.contains(i_Riddle);}

    public void RemoveRiddle(Riddle i_Riddle){
        m_Riddles.remove(i_Riddle);
    }

    public void RemoveRiddle(int i_AppearanceNumber){
        Riddle riddleToRemove = m_Riddles.get(i_AppearanceNumber);
        DatabaseFacade.PersistObject(this);
        DatabaseFacade.PersistObject(riddleToRemove);
        m_Riddles.remove(riddleToRemove);
        DatabaseFacade.RemoveObject(riddleToRemove);
    }

    public List<Riddle> GetRiddlesNotSolvedByPlayer(User i_User) {
        return getPlayerRiddles(i_User, false);
    }

    public List<Riddle> GetRiddleSolvedByTeam(Team i_Team){return  getTeamRiddles(i_Team,true);}

    public List<Riddle> GetRiddlesSolvedByPlayer(User i_User) {
        return getPlayerRiddles(i_User, true);
    }

    private List<Riddle> getPlayerRiddles(User i_User, boolean i_Solved) {
        List<Riddle> playerRiddles = new ArrayList<>();
        for (Riddle riddle : m_Riddles) {
            if (riddle.IsSolvedPlayer(i_User) == i_Solved) {
                playerRiddles.add(riddle);
            }
        }

        return playerRiddles;
    }

    private List<Riddle> getTeamRiddles(Team i_Team, boolean i_Solved) {
        List<Riddle> teamRiddles = new ArrayList<>();
        for (Riddle riddle : m_Riddles) {
            if (riddle.IsSolvedByTeam(i_Team) == i_Solved) {
                teamRiddles.add(riddle);
            }
        }

        return teamRiddles;
    }

    public Riddle GetRiddle(Integer i_AppearanceNumber){
        return m_Riddles.get(i_AppearanceNumber);
    }

    public List<Riddle> GetRiddlesNotSolvedByTeam(Team i_Team) {
        return getTeamRiddles(i_Team,false);
    }

    public void SortRiddles(){
        Collections.sort(m_Riddles,Riddle.RiddleeComparator);
    }

//    private int sortRiddlesHelper(Riddle i_Riddle1,Riddle i_Riddle2){
//        int res = i_Riddle1.getAppearanceNumber() - i_Riddle2.getAppearanceNumber();
//
//        if(res == 0) {
//            return res;
//        }
//        return res > 0 ? 1:-1;
//    }

    public int GetIndex() {
        return m_Index;
    }

    public void SetIndex(int m_Index) {
        this.m_Index = m_Index;
    }

    public void ClearRiddles(){
        m_Riddles.clear();
    }

    public int GetRiddlesCount(){
        return m_Riddles.size();
    }

    public Riddle GetRiddleById(Integer i_id) {
        for (Riddle riddle : m_Riddles) {
            if (riddle.getId() == i_id) {
                return riddle;
            }
        }

        throw new NullPointerException("Riddle not found");
    }
}
