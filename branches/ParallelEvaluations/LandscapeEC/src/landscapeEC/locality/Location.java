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
    
    public Location(T aPosition, Problem aProblem) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
        pendingIndividuals = new ArrayList<Individual>();
        problem = aProblem;
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
    
    public int[] getAverageIndividual() {
    	
        List<Individual> listOfInd = new ArrayList<Individual>();
        listOfInd.addAll(this.getIndividuals());
        int[] displayString = new int[this.getProblem().getBitStringSize()];
        for(int i=0; i<this.getProblem().getBitStringSize(); i++){
            int count = 0;
            for(int j=0; j<listOfInd.size(); j++){
                if(listOfInd.get(j).getBit(i)==0){
                    count++;
                } else {
                    count--;
                }
            }
            if(count > 0){
                displayString[i] = 0;
            } else {
                displayString[i] = 1;
            }
        }
        
        return displayString;
    	
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
    
    public String toString() {
        return position.toString() + " (with " + individuals.size() + " individuals)";
    }

}
