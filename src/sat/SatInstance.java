package sat;

public class SatInstance {
    private int numVariables;
    private int numClauses;
    private CNF cnf;

    public int getNumVariables() {
        return numVariables;
    }

    public int getNumClauses() {
        return numClauses;
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }

    public void setNumClauses(int numClauses) {
        this.numClauses = numClauses;
    }

}
