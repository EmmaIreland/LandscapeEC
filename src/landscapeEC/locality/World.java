package landscapeEC.locality;

import java.util.Iterator;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public interface World<T> extends Iterable<Location> {

	public void setLocationProblem(T position, Problem problem);
	
	public int getNumLocations();
	
	public List<T> getNeighborhood(T position, int radius);
	
	public List<Individual> getIndividualsAt(T p);
	
	public void clear();
	
 	public Iterator<Location> iterator();
 	
 	public Location getLocation(T p);
	
}