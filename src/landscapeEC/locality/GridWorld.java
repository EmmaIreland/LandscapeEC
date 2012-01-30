package landscapeEC.locality;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import landscapeEC.locality.geography.Geography;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;

public class GridWorld implements Serializable, World<Vector> {
    private static final long serialVersionUID = 8032708223600669849L;
    private boolean toroidal = false;
    private Map<Vector, Location> worldMap;
    private Vector dimensions;

    public GridWorld(Vector dimensions, boolean isToroidal) throws Exception {
        toroidal = isToroidal;
        this.dimensions = new Vector(dimensions);

        worldMap = new HashMap<Vector, Location>();
        Integer[] array = new Integer[dimensions.size()];
        
        //generateLocations is now how we scale gridWorlds to
        //an arbitrary number of dimensions.
        generateLocations(0, array);

        Geography geography = createGeography();

        geography.generateGeography(this);
    }
    
    private void generateLocations(int current, Integer[] partial){
        if(current<dimensions.size()){
            for(int i=0; i<dimensions.get(current); i++){
                partial[current]=i;
                generateLocations(current+1, partial);
            }
        }
        else{
            Vector vector = new Vector(partial);
            Location<Vector> position = new Location<Vector>(vector, GlobalProblem.getProblem());
            worldMap.put(position.getPosition(), new Location<Vector>(position.getPosition()));
        }
    }

    @SuppressWarnings("unchecked")
    private Geography createGeography() throws Exception {
        String geographyName = StringParameter.GEOGRAPHY_TYPE.getValue();

        Class<Geography> geography = (Class<Geography>) Class
                .forName(geographyName);
        Constructor<Geography> cons = geography.getConstructor();
        Geography instance = cons.newInstance();
        return instance;
    }

    public void setLocationProblem(Vector position, Problem problem) {
        getLocation(position).setProblem(problem);
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
        GridWorldIterator iter = new GridWorldIterator(position, radius, this);
        while (iter.hasNext()) {
            positions.add(iter.next().getPosition());
        }
        return positions;
    }

    public Vector getDimensions() {
        return dimensions;
    }

    @Override
    public Iterator<Location<Vector>> iterator() {
        return new GridWorldIterator(this);
    }

    public List<Individual> getIndividualsAt(Vector p) {
        return getLocation(p).getIndividuals();
    }

    public void clear() {
        for (Location<Vector> l : this) {
            getLocation(l.getPosition()).setIndividuals(new ArrayList<Individual>());
        }
    }

    private Individual findBestInCell(IndividualComparator comparator,
            Vector position) {
        return Collections.max(getIndividualsAt(position), comparator);
    }

    public Individual findBestIndividual() {
        IndividualComparator comparator = IndividualComparator.getComparator();
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for (Location<Vector> l : this) {
            if (getLocation(l.getPosition()).getNumIndividuals() > 0) {
                bestFromCells.add(findBestInCell(comparator, l.getPosition()));
            }
        }
        if (bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        return Collections.max(bestFromCells, comparator);
    }

}
