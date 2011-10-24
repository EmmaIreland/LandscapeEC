package landscapeEC.locality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;


public class Location implements Serializable {
    private static final long serialVersionUID = 8006609685140336153L;
    private final Vector position;
    private List<Individual> individuals;
    private List<Individual> pendingIndividuals;
    private Problem problem;
    private double difficulty;
    
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
    
    //This is deprecated because we want to move difficulty to be entirely within problem.
    @Deprecated
    public void setProblem(Problem aProblem, double difficulty) {
        problem = aProblem;
        this.difficulty = difficulty;
    }
    
    public double getDifficulty(){
        return difficulty;
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

}
