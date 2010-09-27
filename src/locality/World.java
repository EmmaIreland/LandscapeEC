package locality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
    private boolean toroidal = false;
    private Map<Position,Location> worldMap;
    private Integer[] dimensions;

    public World(Integer[] dimensions, boolean isToroidal) {
        toroidal = isToroidal;
        this.dimensions = dimensions.clone();

        worldMap = new HashMap<Position,Location>();

        int numDimensions = dimensions.length;
        Position end = new Position(dimensions);

        Position start = new Position();
        for (int i = 0; i < numDimensions; i++) {
            start.add(0);
        }

        Position position;
        LocationIterator iter = new LocationIterator(start, end, this);
        while (iter.hasNext()) {
            position = iter.next();
            worldMap.put(position, new Location(position));
        }
    }

    public Location getLocation(Position position) {
        return worldMap.get(position);
    }

    public int getNumLocations() {
        return worldMap.size();
    }
    
    public boolean isToroidal() {
    	return toroidal;
    }

    public List<Position> getNeighborhood(Position position, int radius) {
        List<Position> positions = new ArrayList<Position>();
        LocationIterator iter = new LocationIterator(position, radius, this);
        while (iter.hasNext()) {
            positions.add(iter.next());
        }
        return positions;
    }

	public Integer[] getDimensions() {
		return dimensions;
	}
}
