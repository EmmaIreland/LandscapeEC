package landscapeEC.problem.sat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Problem;
import landscapeEC.problem.SeparableProblem;
import landscapeEC.util.SharedPRNG;

public class SatInstance implements Iterable<Clause>, Serializable, Problem, SeparableProblem<Clause> {
    private static final long serialVersionUID = 3401366560852023162L;
    private int numVariables;
    private LinkedHashSet<Clause> clauseList = new LinkedHashSet<Clause>();
    private double difficulty;

    public SatInstance(double difficulty) {
    	this.difficulty = difficulty;
    }
    
    public SatInstance(List<Clause> listOfClauses, double difficulty) {
        this.difficulty = difficulty;
        
        clauseList.addAll(listOfClauses);
    }
    
    public SatInstance(int numVars, double difficulty) {
    	numVariables = numVars;
    	this.difficulty = difficulty;
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
    
    @Override
    public double getDifficulty() {
        return difficulty;
    }
    
    public void addViralClause(Clause clause) {
        if (!clauseList.contains(clause) && clauseList.size() > 0) {
            clauseList.add(clause); //add new clause

            //Update Difficulty
            SatInstance globalProblem = (SatInstance) GlobalProblem.getProblem();
            double increment = (1 / (double) globalProblem.getNumClauses());
            difficulty += increment;
        }
    }
    
    @Override
    public Problem getSubProblem(double difficulty) {
        return getSubProblem(difficulty, 0.0);
    }
    
    public Problem getSubProblem(double difficulty, double offset) {
        //Offset defines the clause on which the subInstance starts
        int numClauses = (int) Math.ceil(clauseList.size() * difficulty);
        double newDifficulty = this.difficulty * difficulty;
        
        ArrayList<Clause> clauses = new ArrayList<Clause>(clauseList); //This should preserve the order of the clauses
        ArrayList<Clause> clausesToAdd = new ArrayList<Clause>();
        
        int current = (int) Math.ceil(clauseList.size() * offset);
        for (int i = 0; i < numClauses; i++) {
            if(current >= clauses.size()) {
                current = 0;
            }
            clausesToAdd.add(clauses.get(current));
            current++;
        }
        
        SatInstance subInstance = new SatInstance(clausesToAdd, newDifficulty);
        
        return subInstance;
    }
    
    @Override
    public SeparableProblem<Clause> crossover(SeparableProblem<Clause> firstParent, SeparableProblem<Clause> secondParent, int noiseStrength) {
        SatInstance globalSatInstance = (SatInstance) GlobalProblem.getProblem();
        int NumClauses = globalSatInstance.getNumClauses();

        SatInstance firstInstance = (SatInstance) firstParent;
        SatInstance secondInstance = (SatInstance) secondParent;
        
        ArrayList<Clause> clauses = new ArrayList<Clause>();
        
        for (Clause clause : globalSatInstance.getClauses()) {
            // Decide whether or not this clause will be mutated according to
            // Geography Noise Strength
            // Then if a clauseList contains a clause and mutate=true, then we
            // don't add it
            // If a clauseList doesn't contain a clause and mutate=true then we
            // add it
            // Reverse for when mutate is false
            boolean mutate = SharedPRNG.instance().nextDouble() < (noiseStrength / (double) NumClauses);
            if (SharedPRNG.instance().nextBoolean()) {
                if (firstInstance.contains(clause) != mutate) {
                    clauses.add(clause);
                }
            } else {
                if (secondInstance.contains(clause) != mutate) {
                    clauses.add(clause);
                }
            }
        }
        
        SatInstance globalProblem = (SatInstance) GlobalProblem.getProblem();
        double newDifficulty = clauses.size() / ((double) globalProblem.getNumClauses());
        
        SatInstance newSatInstance = new SatInstance(clauses, newDifficulty);

        return newSatInstance;
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
    
    public SatInstance intersection(SatInstance satInstance) {
        //Returns the intersection between the two "sets" of clauses represented 
        //by this instance and the one provided
        Set<Clause> resultSet = (Set<Clause>) clauseList.clone();
        Set<?> secondSet = (Set<Clause>) satInstance.getClauses();
        
        resultSet.retainAll(secondSet);
        SatInstance globalSatInstance = (SatInstance) GlobalProblem.getProblem();
        double newDifficulty = resultSet.size()/(double)globalSatInstance.getNumClauses();
        SatInstance result = new SatInstance(new ArrayList<Clause>(resultSet), newDifficulty);
        
        return result;
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
