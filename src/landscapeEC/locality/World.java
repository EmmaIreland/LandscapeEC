package landscapeEC.locality;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.locality.geography.Geography;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.SatInstance;



public class World implements Iterable<Vector> {
    private boolean toroidal = false;
    private Map<Vector,Location> worldMap;
    private Vector dimensions;

    public World(Vector dimensions, boolean isToroidal, SatInstance satInstance) throws Exception {
        toroidal = isToroidal;
        this.dimensions = new Vector(dimensions);

        worldMap = new HashMap<Vector,Location>();
        for (Vector position : this) {
            worldMap.put(position, new Location(position));
        }
        
        Geography geography = createGeography();
        
        geography.generateGeography(satInstance, this);
    }
    
    @SuppressWarnings("unchecked")
	private Geography createGeography() throws Exception {
		String geographyName = StringParameter.GEOGRAPHY_TYPE.getValue();

		Class<Geography> geography = (Class<Geography>) Class.forName(geographyName);
		Constructor<Geography> cons = geography.getConstructor();
		Geography instance = (Geography) cons.newInstance();
		return instance;
	}

    public void setLocationComparator(Vector position,
            IndividualComparator locationComparator) {
        getLocation(position).setComparator(locationComparator);
    }

    public Location getLocation(Vector position) {
        return worldMap.get(position);
    }
    
    public Location getOrigin() {
        Vector position = Vector.origin(dimensions.size());

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

    public void clear() {
        for (Vector p : this) {
            getLocation(p).setIndividuals(new ArrayList<Individual>());
        }
    }
}
