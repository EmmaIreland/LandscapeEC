package locality;

import java.util.HashMap;
import java.util.Map;

public class World {
	private boolean toroidal = false;
	private Map<String, Location> worldMap; 
	
	public World(int[] dimensions, boolean isToroidal) {
		toroidal = isToroidal;
		
		worldMap = new HashMap<String, Location>();
		
		int numDimensions = dimensions.length;
		int[] dimensionIters = new int[dimensions.length];
		for(int i = 0; i < numDimensions; i++) {
			dimensionIters[i] = 0;
		}
		Location location;
		// Here we create the Locations based on the world's dimensions
		while(dimensionIters[numDimensions] < dimensions[numDimensions]) {
			location = new Location(dimensionIters);
			worldMap.put(location.getPositionString(), location);
			dimensionIters[0]++;
			
			for(int i = 0; i < numDimensions; i++) {
				if(dimensionIters[i] >= dimensions[i]) {
					dimensionIters[i] = 0;
					dimensionIters[i+1]++;
				}
			}
		}
	}
}
