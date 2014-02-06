package landscapeEC.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import landscapeEC.locality.Location;
import landscapeEC.problem.GlobalProblem;

import org.yaml.snakeyaml.Yaml;
 
public class YamlLoader {

	File file;
	Yaml yaml;
	InputStream input;
	private LinkedHashMap<Integer, Location<Integer>> locations = new LinkedHashMap<Integer, Location<Integer>>();
	private LinkedHashMap<Integer, List<Integer>> neighborhoods = new LinkedHashMap<Integer, List<Integer>>();
	private ArrayList<Integer> corners = new ArrayList<Integer>();

	public YamlLoader(String fileStr) {

		file = new File(fileStr);

		yaml = new Yaml();
		input = null;
		

		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		this.getDataFromFile();

	}

	public void getDataFromFile() {

		int counter = 0;
		for (Object data : yaml.loadAll(input)) {
			processData(data.toString(), counter);
			counter++;
		}
	}

	private void processData(String data, Integer locNum) {
		if(data.startsWith("[Corners")) {
			data = data.substring(9, data.length()-1);
			String[] splitData = data.split(" ");

			for(int i = 0; i < splitData.length; i++){
				corners.add(Integer.parseInt(splitData[i]));
			}
		} else if(data.startsWith("[")) {
			data = data.substring(1, data.length()-1);
			String[] splitData = data.split(" ");
			ArrayList<Integer> intSplitData = new ArrayList<Integer>();

			for(int i = 0; i < splitData.length; i++){
				intSplitData.add(Integer.parseInt(splitData[i]));
			}
			Location<Integer> newSpot = new Location<Integer>(locNum, GlobalProblem.getProblem().getSubProblem(0));
			locations.put(locNum, newSpot);
			neighborhoods.put(locNum, intSplitData);
		}
	}
	
	
	public LinkedHashMap<Integer, Location<Integer>> getLocations() {
		return locations;
	}
	
	public LinkedHashMap<Integer, List<Integer>> getNeighborhoods() {
		return neighborhoods;
	}
	
	//Note, this function doesn't return a list of the locations, just the integers
	//that point at them.
	public List<Integer> getRawCorners() {
		return corners;
	}
}
