package landscapeEC.locality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public class GraphWorld implements Serializable, World<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<Integer, Location> locations;

	public GraphWorld(File file) {

		Yaml yaml = new Yaml();
		InputStream input = null;
		try {
			input = new FileInputStream("graphWorldFiles/testGraphWorld");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int counter = 1;
		for (Object data : yaml.loadAll(input)) {
			processData(data.toString(), counter);
			System.out.println(data);
			System.out.println("Location " + counter +" processed.");
			counter++;
		}

		List<String> list = (List<String>) yaml.load(input);
		System.out.println(list);

	}

	private void processData(String data, int locNum) {
		if(data.startsWith("[")) {
			data = data.substring(1, data.length()-1);
			
			
		}
	}

	public Location getLocation(Integer position){
		return locations.get(position);
	}

	public List<Integer> getNeighborhood(Integer position, int radius){
		//currently assume that we will only get a neighborhood of 1.
		//TODO we need to address if we want locations to have their neighborhoods or not.

		/*if(radius != 1){
			throw new UnsupportedOperationException("Neighborhood size of not 1 is not yet supported, bitch at Nick");
		}
		List<Location> locationList = locations.get(position).getNeighbors();*/
		return null;
	}

	@Override
	public Iterator<Location<Integer>> iterator() {
		return new GraphWorldIterator(this);
	}

	private void parseData(File file){
		Yaml yaml = new Yaml();

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
		locations = new LinkedHashMap<Integer, Location>();

	}

	public boolean has(int i) {
		return locations.containsValue(i);
	}

}
