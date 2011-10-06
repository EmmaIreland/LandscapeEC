package landscapeEC.problem.sat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import landscapeEC.util.SharedPRNG;

public class SatInstance implements Iterable<Clause>, Serializable {
    private static final long serialVersionUID = 3401366560852023162L;
    private int numVariables;
    private LinkedHashSet<Clause> clauseList = new LinkedHashSet<Clause>();

    public SatInstance() {
    	//Do nothing!
    }
    
    public SatInstance(int numVars) {
    	numVariables = numVars;
    }
    
    public int getNumVariables() {
        return numVariables;
    }

    public int getNumClauses() {
        return clauseList.size();
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }
    
    public void addClause(Clause newClause){
        clauseList.add(newClause);
    }

    public SatInstance getSubInstance(double percentage) {
        int numClauses = (int) Math.ceil(clauseList.size() * percentage);
        
        SatInstance subInstance = new SatInstance();
        ArrayList<Clause> clauses = new ArrayList<Clause>(clauseList); //This should preserve the order of the clauses
        
        for (int i = 0; i < numClauses; i++) {
            subInstance.addClause(clauses.get(i));
        }

        return subInstance;
    }
    
    public Boolean contains(Clause c) {
        return clauseList.contains(c);
    }

    public Set<Clause> getClauses() {
        return Collections.unmodifiableSet(clauseList);
    }

    public void shuffleClauses() {
        ArrayList<Clause> shuffledClauses = new ArrayList<Clause>(clauseList);
        Collections.shuffle(shuffledClauses, SharedPRNG.instance());
        
        clauseList.clear();
        clauseList.addAll(shuffledClauses);
    }
    
    @Override
    public Iterator<Clause> iterator() {
        return clauseList.iterator();
    }
    
    @Override
    public String toString() {
        return
            "Num variables: " + numVariables + "\n" +
            "Clauses: " + clauseList;
    }
}
