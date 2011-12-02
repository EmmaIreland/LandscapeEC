package landscapeEC.locality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.util.SharedPRNG;


public class Location implements Serializable {
    private static final long serialVersionUID = 8006609685140336153L;
    private final Vector position;
    private List<Individual> individuals;
    private List<Individual> pendingIndividuals;
    private Problem problem;
    private Map<Clause, Integer> viralClauses = new HashMap<Clause, Integer>();
    
    public Location(Vector aPosition, Problem aProblem) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
        pendingIndividuals = new ArrayList<Individual>();
        problem = aProblem;
    }

    public Location(Vector position) {
        this(position, null);
    }

    public Vector getPosition() {
        return position;
    }
    
    public int getNumIndividuals() {
        return individuals.size();
    }
    
    public Problem getProblem() {
        return problem;
    }

    public boolean hasNoProblem() {
	return problem == null;
    }

    public void setProblem(Problem aProblem) {
        problem = aProblem;
    }
    
    public void setIndividuals(List<Individual> individuals) {
    	this.individuals = new ArrayList<Individual>(individuals);
    }
    
    public List<Individual> getIndividuals() {
    	return Collections.unmodifiableList(individuals);
    }

    public void addIndividuals(List<Individual> newIndividuals) {
        individuals.addAll(newIndividuals);
    }

    public void addToPendingIndividuals(List<Individual> newIndividuals) {
        pendingIndividuals.addAll(newIndividuals);
    }

    public void addToPendingIndividuals(Individual i) {
        pendingIndividuals.add(i);
    }

    public void setFromPendingIndividuals() {
        individuals = new ArrayList<Individual>(pendingIndividuals);
        pendingIndividuals.clear();
    }

    public void removeAll(List<Individual> individualsToRemove) {
        individuals.removeAll(individualsToRemove);
    }

    public void addFromPendingIndividuals() {
        individuals.addAll(pendingIndividuals);
        pendingIndividuals.clear();
    }

    public void updateViralClauses(List<Clause> unsolvedClauses, GridWorld world) {
        for (Clause clause : unsolvedClauses) {
            int count = 0;
            if (viralClauses.get(clause) == null) {
                viralClauses.put(clause, 1);
                count = 1;
            } else {
                count = viralClauses.get(clause) + 1;
                viralClauses.put(clause, count);
            }

            if(count % IntParameter.VIRAL_CLAUSE_THRESHOLD.getValue() == 0) {
                if (count < IntParameter.VIRAL_CLAUSE_THRESHOLD.getValue() * 8) {
                    spreadViralClause(world, clause);
                }
            }
        }
    }

    private void spreadViralClause(GridWorld world, Clause clause) {
        //System.out.println("Viral Clause limit reached for " + position.toString());

        List<Vector> neighborhood = world.getNeighborhood(position, 1);
        neighborhood.remove(position); //We don't want to spread to the same location
        Collections.shuffle(neighborhood, SharedPRNG.instance());

        for (Vector pos : neighborhood) {
            SatInstance locationProblem = (SatInstance) world.getLocation(pos).getProblem();

            //Only add clause to a location that does not contain it
            if (!locationProblem.contains(clause)) {
                locationProblem.addViralClause(clause);
                break;
            }
        }
    }

}
