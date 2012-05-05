package landscapeEC.problem.sat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import landscapeEC.core.GARun;
import landscapeEC.locality.Location;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;
import landscapeEC.util.FrequencyCounter;

public class JadedEvaluator extends Evaluator implements Observer {
    private static FrequencyCounter<Integer> fc = new FrequencyCounter<Integer>();
    private static boolean hasSeenWorld = false;
    private static int maxGlobalPop = 1;
    //private static int maxDamping = -1;
    //private static double dampedFitness = 0.0;
    
    @Override
    public void generationData(GARun run){
        int generationNumber = run.getGenerationNumber();
        if(!hasSeenWorld){
            int i=0;
            for(Location l : run.getWorld()){
                i++;
            }
            maxGlobalPop = i*IntParameter.CARRYING_CAPACITY.getValue();
        }
        /*if(generationNumber>100 && generationNumber%25==0){
            System.out.println("Max damping: "+maxDamping+" Damped fitness: "+dampedFitness);
            maxDamping = 0;
            dampedFitness = 0.0;
        }*/
        
        fc.reset();
    }
    
    @Override
    public double doEvaluation(Problem problem, Individual individual) {
        int identity=0;
        int count = 0;
        SatInstance satInstance = (SatInstance) problem;
        if (satInstance.getNumClauses() == 0) {
            return 1.0;
        }

        int clausesSolved = 0;
        int[] array = new int[satInstance.getNumClauses()];
        int i = 0;
        for (Clause clause : satInstance) {
            if (clause.satisfiedBy(individual)) {
                clausesSolved++;
                array[i]=1;
            } else {
                array[i]=0;
            }
            i++;
        }
        identity = Arrays.hashCode(array);
        fc.addItem(identity);
        count = fc.getCount(identity);
        if(clausesSolved == satInstance.getNumClauses())
            return 1.0;
        double result = (double) ((double)clausesSolved /(double) satInstance.getNumClauses())-((double)count/(double)maxGlobalPop);
        /*if(count > maxDamping){
            maxDamping = count;
            dampedFitness = result;
        }*/
        return result;
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
         SatInstance satInstance = (SatInstance) problem;
        if (satInstance.getNumClauses() == 0) {
            return "";
        }
        
        String bitString = "";

        for (Clause clause : satInstance) {
            if (clause.satisfiedBy(individual)) {
                bitString += "1";
            } else  {
                bitString += "0";
            }
        }
        
        return bitString;
    }

}
