package landscapeEC.graphWorldGenerator;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class ThreeDWorldGenerator {

	private int WorldSize;
	private int WorldSizeSq;
	private LinkedList<LinkedList<Integer>> listOfNeighborhoods;

	public static void main(String[] args) throws Exception{
		ThreeDWorldGenerator gen = new ThreeDWorldGenerator(3);
		gen.generate3DWorld();
	}

	private ThreeDWorldGenerator(int worldSize) {
		WorldSize = worldSize;
		WorldSizeSq = (int) java.lang.Math.pow(WorldSize, 2);
	}

	private void generate3DWorld () {
		listOfNeighborhoods = new LinkedList<LinkedList<Integer>>();
		for (int i = 0; i <= java.lang.Math.pow(WorldSize, 3); i++) {
			listOfNeighborhoods.add(this.addLocations(i));
		}

		String name = "graphWorldFiles/3DWorldSize" + WorldSize;
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
		for (int i = 0; i <= java.lang.Math.pow(WorldSize, 3); i++) {
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
		LinkedList<Integer> loc = this.addAllLocations(location);
		return this.removeInvalidLocations(location, loc);
	}

	private LinkedList<Integer> removeInvalidLocations(int location,	LinkedList<Integer> locations) {
		LinkedList<Integer> loc = locations;
		//checks if number is on outer edge
		if ((location+1 % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
			loc.removeFirstOccurrence(location-WorldSize);
			loc.removeFirstOccurrence(location-WorldSize-1);
			loc.removeFirstOccurrence(location-WorldSize+1);
			loc.removeFirstOccurrence(location-WorldSize-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+1+WorldSizeSq);

		}

		//checks if number is on inner edge
		if ((location+5+1 % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
			loc.removeFirstOccurrence(location+WorldSize);
			loc.removeFirstOccurrence(location+WorldSize-1);
			loc.removeFirstOccurrence(location+WorldSize+1);
			loc.removeFirstOccurrence(location+WorldSize-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+1+WorldSizeSq);
		}

		//checks if number is on left edge
		if ((location % WorldSize) == 0) {
			loc.removeFirstOccurrence(location-1);
			loc.removeFirstOccurrence(loc.add(location+WorldSize-1));
			loc.removeFirstOccurrence(loc.add(location-WorldSize-1));
			loc.removeFirstOccurrence(location-1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-1+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1+WorldSizeSq);
		}
		//checks if number is on right edge
		if ((location + 1 % WorldSize) == 0) {
			loc.removeFirstOccurrence(location+1);
			loc.removeFirstOccurrence(location+WorldSize+1);
			loc.removeFirstOccurrence(location-WorldSize+1);
			loc.removeFirstOccurrence(location+1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-1+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1+WorldSizeSq);
		}
		//checks if something is on the top
		if (location <= WorldSizeSq-1) {
			loc.removeFirstOccurrence(location-WorldSizeSq);
			loc.removeFirstOccurrence(location+1-WorldSizeSq);
			loc.removeFirstOccurrence(location-1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+1-WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+1-WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1-WorldSizeSq);
		}

		if (location + 1 >= (java.lang.Math.pow(WorldSize, 3) - WorldSize)) {
			loc.removeFirstOccurrence(location+WorldSizeSq);
			loc.removeFirstOccurrence(location+1+WorldSizeSq);
			loc.removeFirstOccurrence(location-1+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize+1+WorldSizeSq);
			loc.removeFirstOccurrence(location+WorldSize-1+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize+1+WorldSizeSq);
			loc.removeFirstOccurrence(location-WorldSize-1+WorldSizeSq);
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
		loc.add(location+WorldSize);
		loc.add(location+WorldSize+1);
		loc.add(location+WorldSize-1);
		//upper and upper left and right nodes
		loc.add(location-WorldSize);
		loc.add(location-WorldSize+1);
		loc.add(location-WorldSize-1);

		//above layer
		//left and right nodes
		loc.add(location-WorldSizeSq);
		loc.add(location+1-WorldSizeSq);
		loc.add(location-1-WorldSizeSq);
		//lower and lower left and right nodes
		loc.add(location+WorldSize-WorldSizeSq);
		loc.add(location+WorldSize+1-WorldSizeSq);
		loc.add(location+WorldSize-1-WorldSizeSq);
		//upper and upper left and right nodes
		loc.add(location-WorldSize-WorldSizeSq);
		loc.add(location-WorldSize+1-WorldSizeSq);
		loc.add(location-WorldSize-1-WorldSizeSq);

		//below layer
		//left and right nodes
		loc.add(location+WorldSizeSq);
		loc.add(location+1+WorldSizeSq);
		loc.add(location-1+WorldSizeSq);
		//lower and lower left and right nodes
		loc.add(location+WorldSize+WorldSizeSq);
		loc.add(location+WorldSize+1+WorldSizeSq);
		loc.add(location+WorldSize-1+WorldSizeSq);
		//upper and upper left and right nodes
		loc.add(location-WorldSize+WorldSizeSq);
		loc.add(location-WorldSize+1+WorldSizeSq);
		loc.add(location-WorldSize-1+WorldSizeSq);

		return loc;
	}



}

