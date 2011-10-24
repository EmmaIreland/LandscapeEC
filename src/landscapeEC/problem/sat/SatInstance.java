package landscapeEC.problem.sat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Problem;
import landscapeEC.util.SharedPRNG;

public class SatInstance implements Iterable<Clause>, Serializable, Problem {
    private static final long serialVersionUID = 3401366560852023162L;
    private int numVariables;
    private LinkedHashSet<Clause> clauseList = new LinkedHashSet<Clause>();

    public SatInstance() {
    	//Do nothing!
    }
    
    public SatInstance(int numVars) {
    	numVariables = numVars;
    }
    
    @Override
    public int getBitStringSize() {
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

    @Override
    public Problem getSubProblem(double difficulty) {
        int numClauses = (int) Math.ceil(clauseList.size() * difficulty);
        
        SatInstance subInstance = new SatInstance();
        ArrayList<Clause> clauses = new ArrayList<Clause>(clauseList); //This should preserve the order of the clauses
        
        for (int i = 0; i < numClauses; i++) {
            subInstance.addClause(clauses.get(i));
        }

        return subInstance;
    }
    
    @Override
    public Evaluator getEvaluator() {
        return new SatEvaluator();
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
