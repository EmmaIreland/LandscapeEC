package landscapeEC.sat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SatInstance implements Serializable{
    private static final long serialVersionUID = 3401366560852023162L;
    private int numVariables;
    private ClauseList cnf = new ClauseList();

    public SatInstance() {
    	//Do nothing!
    }
    
    public SatInstance(ClauseList clauseList, int numVars) {
    	cnf = clauseList;
    	numVariables = numVars;
    }
    
    public int getNumVariables() {
        return numVariables;
    }

    public int getNumClauses() {
        return cnf.getNumClauses();
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }

    public ClauseList getClauseList(){
        return cnf;
    }
    
    public SatInstance getSubInstance(double percentage) {
    	return new SatInstance(cnf.getSubClauseList(percentage), numVariables);
    }
    
    public void addClause(Clause newClause){
        cnf.addClause(newClause);
    }
    
    @Override
    public String toString() {
        return
            "Num variables: " + numVariables + "\n" +
            "Clauses: " + cnf;
    }
}
