package landscapeEC.problem.sat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class CachingSatEvaluator extends SatEvaluator {
    
    Map<Integer, int[]> map = new LocalMap<Integer, int[]>();
    long built=0;
    long fetched=0;
    
    @Override
    public double doEvaluation(Problem problem, Individual individual) {
        int bitHash = Arrays.hashCode(individual.getBits());
        if(!map.containsKey(bitHash)){
            buildEntry(individual);
        }
        fetched++;
        int[] bits = map.get(bitHash);
        SatInstance satInstance = (SatInstance) problem;
        if (satInstance.getNumClauses() == 0) {
            return 1.0;
        }
        
        int clausesSolved = 0;
        int i = 0;
        
        while(i<bits.length){
            clausesSolved+=bits[i];
            i++;
        }
        
        return (double) clausesSolved / satInstance.getNumClauses();
    }
    
    private void buildEntry(Individual ind){
        built++;
        SatInstance satInstance = (SatInstance)GlobalProblem.getProblem();
        int[] bits = new int[satInstance.getNumClauses()];
        int i = 0;
        for (Clause clause : satInstance) {
            if (clause.satisfiedBy(ind)) {
                bits[i] = 1;
            } else  {
                bits[i] = 0;
            }
            i++;
        }
        map.put(Arrays.hashCode(ind.getBits()), bits);
        if(built % 10000 == 0){
            System.out.println((double)((double)built/(double)fetched));
        }
    }

    @Override
    public boolean solvesSubProblem(Individual individual, Problem locationProblem) {
        double fitness = evaluate(locationProblem, individual);
        int numClauses = ((SatInstance) locationProblem).getNumClauses();
        return 1 - fitness < 1/(2.0 * numClauses);
    }
    
    public List<Clause> getUnsolvedClauses(Individual individual, Problem locationProblem) {
        SatInstance satInstance = (SatInstance) locationProblem;
        List<Clause> unsolvedClauses = new ArrayList<Clause>();
        
        for (Clause clause : satInstance) {
            if (!clause.satisfiedBy(individual)) {
                unsolvedClauses.add(clause);
            }
        }
        
        return unsolvedClauses;
    }
    
    
    @Override
    public String getResultString(Problem problem, Individual individual) {
        int bitHash = Arrays.hashCode(individual.getBits());
        if(!map.containsKey(bitHash)){
            buildEntry(individual);
        }
        int[] bits = map.get(bitHash);
        fetched++;
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<((SatInstance)GlobalProblem.getProblem()).getNumClauses(); i++){
            if(bits[i]==1){
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }
    private class LocalMap<K, V> extends LinkedHashMap<K, V>{
        
        private final int MAX_ENTRIES = IntArrayParameter.WORLD_DIMENSIONS.getValue()[0]*
        IntArrayParameter.WORLD_DIMENSIONS.getValue()[1]*
        IntParameter.CARRYING_CAPACITY.getValue();
        
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest){
            return size() > MAX_ENTRIES;
        }
    }

}
