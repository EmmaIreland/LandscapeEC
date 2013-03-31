package landscapeEC.locality;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import landscapeEC.locality.geography.Geography;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;

public class GridWorld implements World<Vector> {
    private static final long serialVersionUID = 8032708223600669849L;
    private boolean toroidal = false;
    private Map<Vector, Location<Vector>> worldMap;
    private Vector dimensions;

    public GridWorld() throws Exception {
        this(new Vector(IntArrayParameter.WORLD_DIMENSIONS.getValue()),
                BooleanParameter.TOROIDAL.getValue());
    }

    public GridWorld(Vector dimensions, boolean isTorodial) throws Exception {
        // Specifically used only for tests
        toroidal = isTorodial;
        this.dimensions = dimensions;

        worldMap = new HashMap<Vector, Location<Vector>>();
        Integer[] array = new Integer[dimensions.size()];

        // generateLocations is now how we scale gridWorlds to
        // an arbitrary number of dimensions.
        generateLocations(0, array);

        Geography geography = createGeography();

        geography.generateGeography(this);
    }

    private void generateLocations(int current, Integer[] partial) {
        if (current < dimensions.size()) {
            for (int i = 0; i < dimensions.get(current); i++) {
                partial[current] = i;
                generateLocations(current + 1, partial);
            }
        } else {
            Vector vector = new Vector(partial);
            // TODO This might break everything
            Location<Vector> position = new Location<Vector>(vector,
                    GlobalProblem.getProblem().getSubProblem(0));
            worldMap.put(position.getPosition(),
                    new Location<Vector>(position.getPosition()));
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

    @Override
    public void setLocationProblem(Vector position, Problem problem) {
        getLocation(position).setProblem(problem);
    }

    @Override
    public Location<Vector> getLocation(Object position) {
        return worldMap.get(position);
    }

    @Override
    public Location<Vector> getOrigin() {
        Vector position = Vector.origin(dimensions.size());

        return worldMap.get(position);
    }

    @Override
    public int getNumLocations() {
        return worldMap.size();
    }

    public boolean isToroidal() {
        return toroidal;
    }

    @Override
    public List<Vector> getNeighborhood(Object position, int radius) {
        Vector pos = (Vector) position;
        List<Vector> positions = new ArrayList<Vector>();
        GridWorldIterator iter = new GridWorldIterator(pos, radius, this);
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

    @Override
    public List<Individual> getIndividualsAt(Vector p) {
        return getLocation(p).getIndividuals();
    }

    @Override
    public void clear() {
        for (Location<Vector> l : this) {
            getLocation(l.getPosition()).setIndividuals(
                    new ArrayList<Individual>());
        }
    }

    private Individual findBestInCell(IndividualComparator comparator,
            Vector position) {
        try {
            return Collections.max(getIndividualsAt(position), comparator);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException at position "
                    + position.toString());
            return null;
        }
    }

    @Override
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

    @Override
    public List<Location<Vector>> getCorners() {
        ArrayList<Location<Vector>> corners = new ArrayList<Location<Vector>>();
        Location<Vector> topLeft = this.getLocation(Vector.origin((this)
                .getDimensions().size()));
        Location<Vector> bottomRight = this.getLocation(this.getDimensions()
                .minusToAll(1));
        Location<Vector> topRight = this.getLocation(Vector.getCorner(
                bottomRight.getPosition(), topLeft.getPosition()));
        Location<Vector> bottomLeft = this.getLocation(Vector.getCorner(
                topLeft.getPosition(), bottomRight.getPosition()));
        corners.add(topLeft);
        corners.add(bottomRight);
        corners.add(topRight);
        corners.add(bottomLeft);
        return corners;
    }

}
