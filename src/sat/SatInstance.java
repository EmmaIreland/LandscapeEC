package sat;

public class SatInstance {
    private int numVariables;
    private ClauseList cnf = new ClauseList();

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
    
    public void addClause(Clause newClause){
        cnf.addClause(newClause);
    }
}
