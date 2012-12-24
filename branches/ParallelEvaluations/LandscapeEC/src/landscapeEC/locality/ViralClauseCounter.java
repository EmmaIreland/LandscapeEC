package landscapeEC.locality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.util.SharedPRNG;

public class ViralClauseCounter implements Serializable {
    private static final long serialVersionUID = -5156231581336681271L;
    private Set<Clause> viralClauses = new HashSet<Clause>(); 
    private int numGenerationsForCurrentOptimum = 0;
    private String currentLocalOptimumResultString = "";
    private Clause currentUnsolvedClause;
    
    public void updateViralClauses(World<?> world) {
        Evaluator evaluator = GlobalProblem.getEvaluatorType();
        if (!(evaluator instanceof SatEvaluator)) {
          throw new RuntimeException("Viral Clauses is currently only supported under 3SAT");
        }
        
        SatEvaluator satEvaluator = (SatEvaluator) evaluator;
        
        doViralClauseSpread(world);
        doLocalOptimumCheck(world, satEvaluator);
    }
    
    private void doLocalOptimumCheck(World<?> world, SatEvaluator evaluator) {
        //Find best individual with highest number of instances in population
        Set<Individual> bestIndividuals = DiversityCalculator.getBestIndividuals();
        Individual bestCommon = (Individual) bestIndividuals.toArray()[0];
        double bestPercentage = 0.0;
        for(Individual individual : bestIndividuals) {
            String resultString = evaluator.getResultString(individual);
            double individualPercentage = DiversityCalculator.resultStringPercentage(resultString);
            if(individualPercentage > bestPercentage) {
                bestCommon = individual;
                bestPercentage = individualPercentage;
            }
        }
        
        SatInstance globalProblem = (SatInstance) GlobalProblem.getProblem();
        
        String commonResultString = evaluator.getResultString(bestCommon);
        if(commonResultString.equals(currentLocalOptimumResultString)) {
            //it is the same as last generation's local optimum
            numGenerationsForCurrentOptimum++;
        } else {
            //a new local optimum
            numGenerationsForCurrentOptimum = 0;
            currentLocalOptimumResultString = commonResultString;

            List<Clause> unsolved = evaluator.getUnsolvedClauses(bestCommon, globalProblem);
            currentUnsolvedClause = unsolved.get(0);
        }

        //check if current local optimum has passed the threshold
        if(numGenerationsForCurrentOptimum > IntParameter.VIRAL_CLAUSE_THRESHOLD.getValue()) {
            //Create new viral clause for local optimum and start spreading it
            if (!viralClauses.contains(currentUnsolvedClause)) {
                addNewViralClause(world, currentUnsolvedClause);
            }
        }
        
        //System.out.println("Local optimum generation count: " + numGenerationsForCurrentOptimum);
    }
    
    private void addNewViralClause(World<?> world, Clause clause) {
        //Get a random location and add the new viral clause to it
        int locationNumber = SharedPRNG.instance().nextInt(world.getNumLocations());
        int currentLocation = 0;
        
        //Iterate over locations until we get to the location
        for (Location<?> location : world) {
            SatInstance locationProblem = (SatInstance) location.getProblem();
            if(currentLocation == locationNumber) {
                locationProblem.addViralClause(clause);
                viralClauses.add(clause);
                break;
            }
            currentLocation++;
        }
        
        System.out.println("Adding a new viral clause: " + clause.getId());
    }

    private void doViralClauseSpread(World<?> world) {
        //If there are no viral clauses, do nothing
        if (viralClauses.size() == 0) {
            return;
        }
        
        List<SatInstance> locationProblemsToUpdate  = new ArrayList<SatInstance>();

        //Iterate over every location and spread viral clauses
        for (Location<?> location : world) {
            SatInstance locationInstance = (SatInstance) location.getProblem();

            if(hasAllViralClauses(locationInstance)) {
                //Spread viral clauses to location's neighborhood
                List<?> neighborhood = world.getNeighborhood(location.getPosition(), 1);
                neighborhood.remove(location.getPosition()); //We don't want to spread to the same location
                Collections.shuffle(neighborhood, SharedPRNG.instance());

                for (Object pos : neighborhood) {
                    SatInstance locationProblem = (SatInstance) world.getLocation(pos).getProblem();

                    //Only add clause to a location that does not contain it
                    if (!hasAllViralClauses(locationProblem)) {
                        locationProblemsToUpdate.add(locationProblem);
                        //System.out.println("Spreading viral clause at: " + location.getPosition());
                        break;
                    }
                }
            }
        }
        
        for(SatInstance problem : locationProblemsToUpdate) {
            for (Clause clause : viralClauses) {
                problem.addViralClause(clause);
            }
        }
    }
    
    private boolean hasAllViralClauses(SatInstance locationProblem) {
        for (Clause clause : viralClauses) {
            if (!locationProblem.contains(clause)) {
                return false;
            }
        }
        
        return true;
    }
}
