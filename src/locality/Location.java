package locality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sat.Individual;

public class Location {
    private final Position position;
    private List<Individual> individuals;
    
    public Location(Position aPosition) {
        position = aPosition;
        individuals = new ArrayList<Individual>();
    }

    public Position getPosition() {
        return position;
    }
    
    public int getNumIndividuals() {
        return individuals.size();
    }
    
    public void setIndividuals(List<Individual> individuals) {
    	this.individuals = new ArrayList<Individual>(individuals);
    }
    
    public List<Individual> getIndividuals() {
    	return Collections.unmodifiableList(individuals);
    }
}
