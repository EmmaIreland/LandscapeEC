package landscapeEC.locality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;


public class Location<T> implements Serializable {
    private static final long serialVersionUID = 8006609685140336153L;
    private final T position;
    private List<Individual> individuals;
    private List<Individual> pendingIndividuals;
    private Problem problem;
    private ViralClauseCounter viralClauseCounter;
    
    public Location(T aPosition, Problem aProblem) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
        pendingIndividuals = new ArrayList<Individual>();
        problem = aProblem;
        viralClauseCounter = new ViralClauseCounter(this);
    }

    public Location(T position) {
        this(position, null);
    }
    
    public T getPosition() {
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
    
    public ViralClauseCounter getViralClauseCounter() {
        return viralClauseCounter;
    }

}
