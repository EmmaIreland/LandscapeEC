package landscapeEC.locality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class GraphWorld implements Serializable, World<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<Integer, Location<Integer>> locations;
	private LinkedHashMap<Integer, List<Integer>> neighborhoods;

	public GraphWorld(File file) throws Exception {

		Yaml yaml = new Yaml();
		InputStream input = null;
		locations = new LinkedHashMap<Integer, Location<Integer>>();
		neighborhoods = new LinkedHashMap<Integer, List<Integer>>();

		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	}


	private void processData(String data, Integer locNum) {
		if(data.startsWith("[")) {
			data = data.substring(1, data.length()-1);
			String[] splitData = data.split(" ");
			ArrayList<Integer> intSplitData = new ArrayList<Integer>();

			for(int i = 0; i >= splitData.length; i++){
				intSplitData.add(Integer.parseInt(splitData[i]));
			}
			Location<Integer> newSpot = new Location<Integer>(locNum);
			locations.put(locNum, newSpot);
			neighborhoods.put(locNum, intSplitData);
		}
	}

	public Location<Integer> getLocation(Integer position){
		return locations.get(position);
	}

	public List<Integer> getNeighborhood(Integer position, int radius){
		//TODO currently assume that we will only get a neighborhood of 1.

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
		return locations.containsValue(i);
	}


	@Override
	public Location<Integer> getOrigin() {
		return locations.get(0);
	}

}
