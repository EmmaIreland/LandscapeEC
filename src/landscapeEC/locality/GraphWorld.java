package landscapeEC.locality;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class GraphWorld implements Iterable<Location>, Serializable, World<Integer> {

	private LinkedHashMap<Integer, Location> Locations;
	
	public GraphWorld(File file){
		//check no locations have no neighbors
		parseData(file);
	}
	
	public Location getLocation(Integer position){
		return Locations.get(position);
	}
	
	public List<Integer> getNeighborhood(Integer position, int radius){
		//TODO Fill in
		return null;
	}
	
	@Override
	public Iterator<Location> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void parseData(File file){
		//TODO Fill
	}

	@Override
	public void setLocationProblem(Integer position, Problem problem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumLocations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Individual> getIndividualsAt(Integer p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
