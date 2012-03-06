package landscapeEC.locality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;

public class GraphWorld implements Serializable, World<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<Integer, Location<Integer>> locations;
	private LinkedHashMap<Integer, List<Integer>> neighborhoods;

	public GraphWorld() throws Exception {
		
		File file = new File(StringParameter.FILE.getValue());

		Yaml yaml = new Yaml();
		InputStream input = null;
		locations = new LinkedHashMap<Integer, Location<Integer>>();
		neighborhoods = new LinkedHashMap<Integer, List<Integer>>();

		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		    throw new RuntimeException(e);
		}

		int counter = 0;
		for (Object data : yaml.loadAll(input)) {
			processData(data.toString(), counter);
			counter++;
		}

		List<String> list = (List<String>) yaml.load(input);
		if(locations.get(0) == null) {
			throw new Exception("A Graph World must have a 0th location"); 
		}
		
		//System.out.println(locations);
		//System.out.println(neighborhoods);
	}


	private void processData(String data, Integer locNum) {
	    //System.out.println("Processing line: " + data);
		if(data.startsWith("[")) {
			data = data.substring(1, data.length()-1);
			String[] splitData = data.split(" ");
			ArrayList<Integer> intSplitData = new ArrayList<Integer>();

			for(int i = 0; i < splitData.length; i++){
				intSplitData.add(Integer.parseInt(splitData[i]));
			}
			Location<Integer> newSpot = new Location<Integer>(locNum, GlobalProblem.getProblem());
			locations.put(locNum, newSpot);
			//System.out.println("locNum = " + locNum);
			//System.out.println("intSplitData = " + intSplitData);
			neighborhoods.put(locNum, intSplitData);
		}
	}

	@Override
	public Location<Integer> getLocation(Object position){
	    return locations.get(position);
	}

	public List<Integer> getNeighborhood(Object position, int radius){
	    //TODO currently assume that we will only get a neighborhood of 1.
	    Integer pos = (Integer) position;
	    
	    if(radius != 1){
	        throw new UnsupportedOperationException("Neighborhood size of not 1 is not yet supported");
	    }
	    return neighborhoods.get(position);
	}

	@Override
	public Iterator<Location<Integer>> iterator() {
		return new GraphWorldIterator(this);
	}


	@Override
	public void setLocationProblem(Integer position, Problem problem) {
		getLocation(position).setProblem(problem);

	}

	@Override
	public int getNumLocations() {
		return locations.size();
	}

	@Override
	public List<Individual> getIndividualsAt(Integer p) {
		return locations.get(p).getIndividuals();
	}

	@Override
	public void clear() {
		for (Location<Integer> l : this) {
			getLocation(l.getPosition()).setIndividuals(new ArrayList<Individual>());
		}

	}

	public boolean has(int i) {
		return locations.containsKey(i);
	}


	@Override
	public Location<Integer> getOrigin() {
		return locations.get(0);
	}


	@Override
	public Individual findBestIndividual() {
		IndividualComparator comparator = IndividualComparator.getComparator();
        List<Individual> bestFromCells = new ArrayList<Individual>();
       // System.out.println("Location 0: " + getLocation(0));
//        System.out.println(findBestInCell(comparator, 0).toString());
       // System.out.println("Processing location " + this.getLocation(0));
        for (Location<Integer> l : this) {
        //    System.out.println("Processing location " + l);
            if (getLocation(l.getPosition()).getNumIndividuals() > 0) {
                bestFromCells.add(findBestInCell(comparator, l.getPosition()));
            //    System.out.println(findBestInCell(comparator, l.getPosition()).toString());
            }
        }
        if (bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        //System.out.println(this.getLocation(0).getIndividuals().toString());
        return Collections.max(bestFromCells, comparator);
	}
	
	private Individual findBestInCell(IndividualComparator comparator,
            Integer position) {
        return Collections.max(getIndividualsAt(position), comparator);
    }

}
