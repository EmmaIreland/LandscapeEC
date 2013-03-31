package landscapeEC.locality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;
import landscapeEC.util.YamlLoader;

public class GraphWorld implements World<Integer> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private LinkedHashMap<Integer, Location<Integer>> locations;
    private LinkedHashMap<Integer, List<Integer>> neighborhoods;
    private ArrayList<Location<Integer>> corners;

    public GraphWorld() throws Exception {

        YamlLoader yamlLoader = new YamlLoader(
                StringParameter.GRAPHWORLD_FILE.getValue());

        locations = yamlLoader.getLocations();
        neighborhoods = yamlLoader.getNeighborhoods();
        corners = new ArrayList<Location<Integer>>();

        for (Integer i : yamlLoader.getRawCorners()) {
            corners.add(this.getLocation(i));
        }

        /*
         * File file = new File(StringParameter.GRAPHWORLD_FILE.getValue());
         * 
         * Yaml yaml = new Yaml(); InputStream input = null; locations = new
         * LinkedHashMap<Integer, Location<Integer>>(); neighborhoods = new
         * LinkedHashMap<Integer, List<Integer>>();
         * 
         * try { input = new FileInputStream(file); } catch
         * (FileNotFoundException e) { throw new RuntimeException(e); }
         * 
         * int counter = 0; for (Object data : yaml.loadAll(input)) {
         * processData(data.toString(), counter); counter++; }
         * 
         * List<String> list = (List<String>) yaml.load(input);
         * if(locations.get(0) == null) { throw new
         * Exception("A Graph World must have a 0th location"); }
         */

    }

    /*
     * private void processData(String data, Integer locNum) {
     * if(data.startsWith("[Corners")) { corners = new ArrayList(); data =
     * data.substring(9, data.length()-1); String[] splitData = data.split(" ");
     * ArrayList<Integer> intSplitData = new ArrayList<Integer>();
     * 
     * for(int i = 0; i < splitData.length; i++){
     * intSplitData.add(Integer.parseInt(splitData[i])); } for (Integer i :
     * intSplitData) { corners.add(this.getLocation(i)); } } else
     * if(data.startsWith("[")) { data = data.substring(1, data.length()-1);
     * String[] splitData = data.split(" "); ArrayList<Integer> intSplitData =
     * new ArrayList<Integer>();
     * 
     * for(int i = 0; i < splitData.length; i++){
     * intSplitData.add(Integer.parseInt(splitData[i])); } Location<Integer>
     * newSpot = new Location<Integer>(locNum,
     * GlobalProblem.getProblem().getSubProblem(0)); locations.put(locNum,
     * newSpot); neighborhoods.put(locNum, intSplitData); }
     * 
     * }
     */

    @Override
    public Location<Integer> getLocation(Object position) {
        return locations.get(position);
    }

    @Override
    public List<Integer> getNeighborhood(Object position, int radius) {
        // TODO currently assume that we will only get a neighborhood of 1.
        if (radius != 1) {
            throw new UnsupportedOperationException(
                    "Neighborhood size of not 1 is not yet supported");
        }
        return neighborhoods.get(position);
    }

    @Override
    public Iterator<Location<Integer>> iterator() {
        return new GraphWorldIterator(this);
    }

    @Override
    public void setLocationProblem(Integer position, Problem problem) {
        getLocation(position).setProblem(problem);

    }

    @Override
    public int getNumLocations() {
        return locations.size();
    }

    @Override
    public List<Individual> getIndividualsAt(Integer p) {
        return locations.get(p).getIndividuals();
    }

    @Override
    public void clear() {
        for (Location<Integer> l : this) {
            getLocation(l.getPosition()).setIndividuals(
                    new ArrayList<Individual>());
        }

    }

    public boolean has(int i) {
        return locations.containsKey(i);
    }

    @Override
    public Location<Integer> getOrigin() {
        return locations.get(0);
    }

    @Override
    public Individual findBestIndividual() {
        IndividualComparator comparator = IndividualComparator.getComparator();
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for (Location<Integer> l : this) {
            if (getLocation(l.getPosition()).getNumIndividuals() > 0) {
                bestFromCells.add(findBestInCell(comparator, l.getPosition()));
            }
        }
        if (bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        return Collections.max(bestFromCells, comparator);
    }

    private Individual findBestInCell(IndividualComparator comparator,
            Integer position) {
        return Collections.max(getIndividualsAt(position), comparator);
    }

    @Override
    public ArrayList<Location<Integer>> getCorners() {
        return corners;
    }

}
