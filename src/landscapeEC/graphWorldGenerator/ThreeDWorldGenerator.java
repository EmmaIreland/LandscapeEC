package landscapeEC.graphWorldGenerator;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class ThreeDWorldGenerator {

	private int worldSize;
	private int worldSizeSq;
	private LinkedList<LinkedList<Integer>> listOfNeighborhoods;

	public static void main(String[] args) throws Exception{
		ThreeDWorldGenerator gen = new ThreeDWorldGenerator(3);
		gen.generate3DWorld();
	}

	private ThreeDWorldGenerator(int worldSize) {
		this.worldSize = worldSize;
		worldSizeSq = (int) java.lang.Math.pow(worldSize, 2);
	}

	private void generate3DWorld () {
		listOfNeighborhoods = new LinkedList<LinkedList<Integer>>();

		for (int i = 0; i < java.lang.Math.pow(worldSize, 3); i++) {
			listOfNeighborhoods.add(this.addLocations(i));
		}

		String name = "graphWorldFiles/3DWorldSize" + worldSize;
		this.writeYAMLFile(name);

	}

	private void writeYAMLFile(String name) {
		Writer output = null;
		File file = new File(name);
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < java.lang.Math.pow(worldSize, 3); i++) {
			String header = "--- # " + i + "\n";
			try {
				output.write(header);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String body = listOfNeighborhoods.get(i) + "\n\n";
			try {
				output.write(body);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private LinkedList<Integer> addLocations (int location) {
		LinkedList<Integer> loc = new LinkedList<Integer>();
 		int x = location % worldSize;
		int y = (location/worldSize) % worldSize;
		int z = (location/worldSizeSq) % worldSize;
		
		for (int i = Math.max(x-1, 0); i <= Math.min(x+1, worldSize - 1); ++i){
			for (int j = Math.max(y-1, 0); j <= Math.min(y+1, worldSize - 1); ++j){
				for (int k = Math.max(z-1, 0); k <= Math.min(z+1, worldSize - 1); ++k){
					loc.add(i+worldSize*j+worldSizeSq*k);
				}
			}
		}
		loc.removeFirstOccurrence(location);
		return loc;

		//		LinkedList<Integer> loc = this.addAllLocations(location);
		//		return this.removeInvalidLocations(location, loc);
	}

	private LinkedList<Integer> removeInvalidLocations(int location,	LinkedList<Integer> locations) {
		LinkedList<Integer> loc = locations;
		//checks if number is on outer edge
		if ((location+1 % (java.lang.Math.pow(worldSize, 3))) <= worldSize) {
			loc.removeFirstOccurrence(location-worldSize);
			loc.removeFirstOccurrence(location-worldSize-1);
			loc.removeFirstOccurrence(location-worldSize+1);
			loc.removeFirstOccurrence(location-worldSize-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+1+worldSizeSq);

		}

		//checks if number is on inner edge
		if ((location+5+1 % (java.lang.Math.pow(worldSize, 3))) <= worldSize) {
			loc.removeFirstOccurrence(location+worldSize);
			loc.removeFirstOccurrence(location+worldSize-1);
			loc.removeFirstOccurrence(location+worldSize+1);
			loc.removeFirstOccurrence(location+worldSize-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+1+worldSizeSq);
		}

		//checks if number is on left edge
		if ((location % worldSize) == 0) {
			loc.removeFirstOccurrence(location-1);
			loc.removeFirstOccurrence(loc.add(location+worldSize-1));
			loc.removeFirstOccurrence(loc.add(location-worldSize-1));
			loc.removeFirstOccurrence(location-1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-1+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1+worldSizeSq);
		}
		//checks if number is on right edge
		if ((location + 1 % worldSize) == 0) {
			loc.removeFirstOccurrence(location+1);
			loc.removeFirstOccurrence(location+worldSize+1);
			loc.removeFirstOccurrence(location-worldSize+1);
			loc.removeFirstOccurrence(location+1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-1+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1+worldSizeSq);
		}
		//checks if something is on the top
		if (location <= worldSizeSq-1) {
			loc.removeFirstOccurrence(location-worldSizeSq);
			loc.removeFirstOccurrence(location+1-worldSizeSq);
			loc.removeFirstOccurrence(location-1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+1-worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+1-worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1-worldSizeSq);
		}

		if (location + 1 >= (java.lang.Math.pow(worldSize, 3) - worldSize)) {
			loc.removeFirstOccurrence(location+worldSizeSq);
			loc.removeFirstOccurrence(location+1+worldSizeSq);
			loc.removeFirstOccurrence(location-1+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize+1+worldSizeSq);
			loc.removeFirstOccurrence(location+worldSize-1+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize+1+worldSizeSq);
			loc.removeFirstOccurrence(location-worldSize-1+worldSizeSq);
		}

		return loc;
	}
	private LinkedList<Integer> addAllLocations (int location) {
		LinkedList<Integer> loc = new LinkedList<Integer>();
		//add all spots to list
		//left and right nodes
		loc.add(location+1);
		loc.add(location-1);
		//lower and lower left and right nodes
		loc.add(location+worldSize);
		loc.add(location+worldSize+1);
		loc.add(location+worldSize-1);
		//upper and upper left and right nodes
		loc.add(location-worldSize);
		loc.add(location-worldSize+1);
		loc.add(location-worldSize-1);

		//above layer
		//left and right nodes
		loc.add(location-worldSizeSq);
		loc.add(location+1-worldSizeSq);
		loc.add(location-1-worldSizeSq);
		//lower and lower left and right nodes
		loc.add(location+worldSize-worldSizeSq);
		loc.add(location+worldSize+1-worldSizeSq);
		loc.add(location+worldSize-1-worldSizeSq);
		//upper and upper left and right nodes
		loc.add(location-worldSize-worldSizeSq);
		loc.add(location-worldSize+1-worldSizeSq);
		loc.add(location-worldSize-1-worldSizeSq);

		//below layer
		//left and right nodes
		loc.add(location+worldSizeSq);
		loc.add(location+1+worldSizeSq);
		loc.add(location-1+worldSizeSq);
		//lower and lower left and right nodes
		loc.add(location+worldSize+worldSizeSq);
		loc.add(location+worldSize+1+worldSizeSq);
		loc.add(location+worldSize-1+worldSizeSq);
		//upper and upper left and right nodes
		loc.add(location-worldSize+worldSizeSq);
		loc.add(location-worldSize+1+worldSizeSq);
		loc.add(location-worldSize-1+worldSizeSq);

		return loc;
	}



}

