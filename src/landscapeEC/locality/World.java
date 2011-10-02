package landscapeEC.locality;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.locality.geography.Geography;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.sat.EmptyWorldException;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.Individual;
import landscapeEC.problem.sat.IndividualComparator;


public class World implements Iterable<Vector>, Serializable {
    private static final long serialVersionUID = 8032708223600669849L;
    private boolean toroidal = false;
    private Map<Vector,Location> worldMap;
    private Vector dimensions;

    public World(Vector dimensions, boolean isToroidal) throws Exception {
        toroidal = isToroidal;
        this.dimensions = new Vector(dimensions);
        
        worldMap = new HashMap<Vector,Location>();
        for (Vector position : this) {
            worldMap.put(position, new Location(position));
        }
        
        Geography geography = createGeography();
        
        geography.generateGeography(this);
    }
    
    @SuppressWarnings("unchecked")
	private Geography createGeography() throws Exception {
		String geographyName = StringParameter.GEOGRAPHY_TYPE.getValue();

		Class<Geography> geography = (Class<Geography>) Class.forName(geographyName);
		Constructor<Geography> cons = geography.getConstructor();
		Geography instance = cons.newInstance();
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

    private Individual findBestInCell(IndividualComparator comparator,
	    Vector position) {
	return Collections.max(getIndividualsAt(position), comparator);
    }

    public Individual findBestIndividual() {
        IndividualComparator comparator = GlobalSatInstance.getComparator();
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for (Vector p : this) {
            if (getLocation(p).getNumIndividuals() > 0) {
                bestFromCells.add(findBestInCell(comparator, p));
            }
        }
        if (bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        return Collections.max(bestFromCells, comparator);
    }

	public List<Vector> makeShell(final Vector position, final int radius) {
		List<Vector> result = new ArrayList<Vector>();
		Vector currentPosition = new Vector(position.getCoordinates());
		int lockedDimension = 0;
		while(lockedDimension < dimensions.size()){
			
			currentPosition.set(lockedDimension, position.get(lockedDimension)-radius);
		    result.addAll(finishMakingShell(currentPosition, lockedDimension, radius));
			
			currentPosition.set(lockedDimension, position.get(lockedDimension)+radius);
			result.addAll(finishMakingShell(currentPosition, lockedDimension, radius));
			
			currentPosition.set(lockedDimension, position.get(lockedDimension));
			lockedDimension++;
		}
		if(dimensions.size()==1){
			currentPosition.set(0, position.get(0)-radius);
			result.add(currentPosition.clone());
			currentPosition.set(0, position.get(0)+radius);
			result.add(currentPosition.clone());
			//I don't know why, but without this, the shell code doesn't work for one dimension.
		}
		/*
		 * In order to make a shell with no repetition, take the following steps
		 * (using a three-dimensional example) For a shell radius 2, centered on
		 * origin: Start with (2,x,y) and (-2,x,y), locking 2 and -2 in place
		 * and allowing x and y to go through their ranges of allowed values,
		 * [-2,2]. Then, move to (x,2,y) and (x,-2,y) and repeat, ONLY ALLOWING
		 * X THE RANGE [-1,1]. Repeat this for as many dimensions as are
		 * present, only allowing dimension tokens to the left to attain
		 * [-r+1,r-1]
		 * 
		 * In short, dimension tokens to the LEFT of the locked dimension may
		 * only do most of their range. This is to avoid including certain
		 * positions multiple times.
		 */
		System.out.println(result.toString());
		return result;
	}
	
    private List<Vector> finishMakingShell(final Vector position, int lockedDimension, int radius) {
    	List<Vector> result = new ArrayList<Vector>();
    	int currentDimension = 0;
    	int leftRange = radius-1;
    	int scan = -1;
    	Vector currentPosition = new Vector(position.getCoordinates());
    	while(currentDimension < lockedDimension){
    		scan = currentPosition.get(currentDimension)-leftRange;
    		currentPosition.set(currentDimension, scan);
    		while(currentPosition.get(currentDimension)<=position.get(currentDimension)+leftRange){
    			result.add(currentPosition.clone());
    			scan++;
    			currentPosition.set(currentDimension, scan);
    		}
    		currentDimension++;
    	}
    	currentDimension++;
    	while(currentDimension<dimensions.size()){
    		scan = currentPosition.get(currentDimension)-radius;
    		currentPosition.set(currentDimension, scan);
    		while(currentPosition.get(currentDimension)<=position.get(currentDimension)+radius){
    			result.add(currentPosition.clone());
    			scan++;
    			currentPosition.set(currentDimension, scan);
    		}
    		currentDimension++;
    	}
		return result;
	}

	public List<Individual> getSpeciesNeighborhood(Vector p) {
        IndividualComparator comparator = GlobalSatInstance.getComparator();
	int[] speciesBits = findBestInCell(comparator, p).getBits();
	for(int rad=0; (rad<dimensions.size()/2); rad++){
	    
	}
	return getIndividualsAt(p);
    }
}
