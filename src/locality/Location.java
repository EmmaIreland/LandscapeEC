package locality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sat.Individual;
import sat.IndividualComparator;

public class Location {
    private final Vector position;
    private List<Individual> individuals;
    private List<Individual> pendingIndividuals;
    private IndividualComparator locationComparator;
    
    public Location(Vector aPosition, IndividualComparator comparator) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
        pendingIndividuals = new ArrayList<Individual>();
        locationComparator = comparator;
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
    
    public IndividualComparator getComparator() {
        if (hasNoComparator()) {
            throw new IllegalStateException("Comparator undefined for position " + position);
        }
    	return locationComparator;
    }

	public boolean hasNoComparator() {
		return locationComparator == null;
	}
    
    public void setComparator(IndividualComparator locationComparator) {
        this.locationComparator = locationComparator;
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
