package landscapeEC.locality;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;

public interface World<T> extends Iterable<Location<T>>, Serializable {

    public void setLocationProblem(T position, Problem problem);

    public int getNumLocations();

    // TODO This is making the return type List<Graphworld> which is bad?
    public List<T> getNeighborhood(Object position, int radius);

    public List<Individual> getIndividualsAt(T p);

    public void clear();

    @Override
    public Iterator<Location<T>> iterator();

    public Location<T> getLocation(Object p);

    public Location<T> getOrigin();

    public Individual findBestIndividual();

    public List<Location<T>> getCorners();

}