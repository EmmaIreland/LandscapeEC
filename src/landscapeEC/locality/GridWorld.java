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
        if(dimensions.size()>1){
            Integer[] array = new Integer[2];
            //This needs a way to scale to an arbitrary number of dimensions, but I
            //do not care to work the solution out right now, because it is finals
            //week and I am very busy otherwise.
            for (int x=0; x<dimensions.get(0); x++) {
                for(int y=0; y<dimensions.get(1); y++){
                    array[0]=x;
                    array[1]=y;
                    Vector vector = new Vector(array);
                    Location position = new Location(vector, GlobalProblem.getProblem());
                    worldMap.put(position.getPosition(), new Location(position.getPosition()));
                }
            }
        }
        else{
            for (int x=0; x<dimensions.get(0); x++) {
                Integer[] array = new Integer[1];
                array[0]=x;
                Vector vector = new Vector(array);
                Location position = new Location(vector, GlobalProblem.getProblem());
                worldMap.put(position.getPosition(), new Location(position.getPosition()));
            }
        }

        Geography geography = createGeography();

        geography.generateGeography(this);
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
    public Iterator<Location> iterator() {
        return new GridWorldIterator(this);
    }

    public List<Individual> getIndividualsAt(Vector p) {
        return getLocation(p).getIndividuals();
    }

    public void clear() {
        for (Location l : this) {
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
        for (Location l : this) {
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
