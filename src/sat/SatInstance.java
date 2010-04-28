package sat;

public class SatInstance {
    private int numVariables;
    private int numClauses;
    private ClauseList cnf = new ClauseList();

    public int getNumVariables() {
        return numVariables;
    }

    public int getNumClauses() {
        //TODO Fix this to actually count
        return numClauses;
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }

    public void setNumClauses(int numClauses) {
        this.numClauses = numClauses;
    }

    public ClauseList getClauseList(){
        return cnf;
    }
    
    public void addClause(Clause newClause){
        cnf.addClause(newClause);
    }
}
