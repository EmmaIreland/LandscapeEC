package landscapeEC.graphWorldGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ThreeDWorldGenerator {
	
	private int WorldSize;
	private int WorldSizeSq;
	private List<List<Integer>> listOfNeighborhoods;
	
	public File generate3DWorld (int worldSize) {
		WorldSize = worldSize;
		WorldSizeSq = (int) java.lang.Math.pow(WorldSize, 2);
		for (int i = 0; i >= java.lang.Math.pow(WorldSize, 3); i++) {
			//add and check top layer
			//add and check middle layer
			//add and check bottom layer
			//do all at once?
		}
		
		return null;
	}
	
	private List<Integer> addLocations (int location) {
		List<Integer> loc = new ArrayList<Integer>();
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
		
		//END adding locations
		
		
		
		//checks if number is on outer
		if ((location % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
			
		}
		//checks if number is on inner edge
		if ((location+5 % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
		}
		//checks if number is on left edge
		if ((location-1 % WorldSize) == 0) {
		}
		if ((location % WorldSize) == 0) {
		}
		
		return null;
	}
	

	
}

