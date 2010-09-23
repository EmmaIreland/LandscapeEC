package locality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
	private boolean toroidal = false;
	private Map<List<Integer>, Location> worldMap; 
	
	public World(int[] dimensions, boolean isToroidal) {
		toroidal = isToroidal;
		
		worldMap = new HashMap<List<Integer>, Location>();
		
		int numDimensions = dimensions.length;
		List<Integer> end = makePosition(dimensions);
		
		List<Integer> start = new ArrayList<Integer>();
		for(int i = 0; i < numDimensions; i++) {
			start.add(0);
		}
		
		List<Integer> position;
		LocationIterator iter = new LocationIterator(start, end);
		while(iter.hasNext()) {
			position = iter.next();
			worldMap.put(position, new Location(position));
		}
	}

	private List<Integer> makePosition(int[] dimensionIters) {
		List<Integer> result = new ArrayList<Integer>();
		for (Integer v : dimensionIters) {
			result.add(v);
		}
		return result;
	}

	public Location getLocation(List<Integer> position) {
		return worldMap.get(position);
	}

	public int getNumLocations() {
		return worldMap.size();
	}

    public List<Location> getNeighborhood(List<Integer> position, int radius) {
        throw new UnsupportedOperationException();
    }
}
