package locality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sat.Individual;
import sat.IndividualComparator;

public class Location {
    private final Position position;
    private List<Individual> individuals;
    private List<Individual> pendingIndividuals;
    private IndividualComparator locationComparator;
    
    public Location(Position aPosition, IndividualComparator comparator) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
        pendingIndividuals = new ArrayList<Individual>();
        locationComparator = comparator;
    }

    public Position getPosition() {
        return position;
    }
    
    public int getNumIndividuals() {
        return individuals.size();
    }
    
    public IndividualComparator getComparator() {
    	return locationComparator;
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
        pendingIndividuals = new ArrayList<Individual>();
    }

    public void removeAll(List<Individual> individualsToRemove) {
        individuals.removeAll(individualsToRemove);
    }

	public void addFromPendingIndividuals() {
		individuals.addAll(pendingIndividuals);
		pendingIndividuals = new ArrayList<Individual>();
	}
}
