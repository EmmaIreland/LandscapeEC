package landscapeEC.graphWorldGenerator;

import java.io.File;
import java.util.List;


public class ThreeDWorldGenerator {
	
	private int WorldSize;
	private List<List<Integer>> listOfNeighborhoods;
	
	public File generate3DWorld (int worldSize) {
		WorldSize = worldSize;
		for (int i = 0; i >= java.lang.Math.pow(WorldSize, 3); i++) {
			//add and check top layer
			//add and check middle layer
			//add and check bottom layer
			//do all at once?
		}
		
		return null;
	}
	
	private boolean checkLocation (int location) {
		boolean validity = true;
		//checks if number is on outer
		if ((location % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
			validity = false;
		}
		//checks if number is on inner edge
		if ((location+5 % (java.lang.Math.pow(WorldSize, 3))) <= WorldSize) {
			validity = false;
		}
		//checks if number is on left edge
		if ((location-1 % WorldSize) == 0) {
			validity = false;
		}
		if ((location % WorldSize) == 0) {
			validity = false;
		}
		
		return validity;
	}
	
}

