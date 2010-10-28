package locality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parameters.IntArrayParameter;

import sat.Individual;
import sat.IndividualComparator;
import sat.SatInstance;

public class World implements Iterable<Vector> {
    private boolean toroidal = false;
    private Map<Vector,Location> worldMap;
    private Vector dimensions;

    public World(Vector dimensions, boolean isToroidal, SatInstance satInstance) {
        toroidal = isToroidal;
        this.dimensions = new Vector(dimensions);

        worldMap = new HashMap<Vector,Location>();

        int numDimensions = dimensions.size();
        Vector end = new Vector(dimensions);

        Vector start = new Vector();
        for (int i = 0; i < numDimensions; i++) {
            start.add(0);
        }
        
        ManhattanDistanceGradiantGeography geography = new ManhattanDistanceGradiantGeography();

        LocationIterator iter = new LocationIterator(start, end, this);
        while (iter.hasNext()) {
            Vector position = iter.next();

            final SatInstance subInstance = geography.getSubInstance(dimensions, satInstance, position, this);
            IndividualComparator locationComparator = new IndividualComparator(subInstance);

            worldMap.put(position, new Location(position, locationComparator));
        }
    }

    public Location getLocation(Vector position) {
        return worldMap.get(position);
    }

    public Location getStartingLocation() {
        Integer[] startingLocation = IntArrayParameter.STARTING_LOCATION.getValue();
        Vector position = new Vector(startingLocation);

        return worldMap.get(position);
    }

    public int getNumLocations() {
        return worldMap.size();
    }

    public boolean isToroidal() {
        return toroidal;
    }

    public List<Vector> getNeighborhood(Vector position, int radius) {
        List<Vector> positions = new ArrayList<Vector>();
        LocationIterator iter = new LocationIterator(position, radius, this);
        while (iter.hasNext()) {
            positions.add(iter.next());
        }
        return positions;
    }

    public Vector getDimensions() {
        return dimensions;
    }

    @Override
    public LocationIterator iterator() {
        return new LocationIterator(this);
    }

    public List<Individual> getIndividualsAt(Vector p) {
        return getLocation(p).getIndividuals();
    }

    private int getBiggestDimension() {
        int biggestDim = 0;
        for (int i = 0; i < dimensions.size(); i++) {
            if (dimensions.get(i) > biggestDim) {
                biggestDim = dimensions.get(i);
            }
        }

        return biggestDim - 1;
    }
}
