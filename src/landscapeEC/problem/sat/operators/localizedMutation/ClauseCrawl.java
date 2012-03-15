package landscapeEC.problem.sat.operators.localizedMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
/*
 * Future work:
 * 
 * Re-implement "waiting list", where everything touched by worldCrawl itself
 * is added either to the current group, or a line to be selected next for a
 * worldCrawl start-point.  Current solution not ideal, but not terrible.
 * 
 * TESTING
 * 
 * See how to get maxGroupSpecies and maxGroupSize out of here, perhaps even
 * added to the visualizer-console.
 * 
 * Is there an efficient way to flip unprocessed around?  Constantly .getting
 * (unprocessed.size()-1) seems inefficient.  And kinda gross.
 * 
 */
public class ClauseCrawl implements ConcentrationRanker {
    private static ClauseCrawl instance = null;
    static private GridWorld world;
    private int maxGroupSize = 0;
    static List<Vector> unprocessed = new ArrayList<Vector>();
    static Hashtable<Vector, Integer> speciesConcentrationMap = new Hashtable<Vector, Integer>();

    private Vector left;
    private Vector right;
    private Vector up;
    private Vector down;
    private ArrayList<Vector> directions = new ArrayList<Vector>();
    private boolean mapExists = false;
    private int biggestGroupSeen;
	private Hashtable<Vector, char[]> speciesMap = new Hashtable<Vector, char[]>();
    
    protected ClauseCrawl(){
        biggestGroupSeen = 0;
        Integer[] set = new Integer[2];
        set[0] = -1;
        set[1] = 0;
        left = new Vector(set);
        set[0] = 1;
        right = new Vector(set);
        set[0] = 0;
        set[1] = -1;
        down = new Vector(set);
        set[1] = 1;
        up = new Vector(set);
        directions.add(right);
        directions.add(up);
        directions.add(left);
        directions.add(down);
    }

    public static ClauseCrawl getInstance() {
        if(instance == null) {
           instance = new ClauseCrawl();
        }
        return instance;
     }

    @Override
    public int getAmp(Vector vector) {
        if (!mapExists)
            generateConcentrationMap();
        if (speciesConcentrationMap.containsKey(vector))
            return speciesConcentrationMap.get(vector);
        System.out.println("getAmp called for " + vector
                + " - Found no entry in concentrationMap.");
        return 1;
    }

    @Override
    public void initialize(GridWorld newWorld, int generationNumber) {
        maxGroupSize = 0;
        world = newWorld;
        mapExists = false;
    }
    
    private void generateSpeciesMap(){
    	Evaluator evaluator = GlobalProblem.getEvaluator();
    	for(Vector v : unprocessed){
    		speciesMap.put(v, evaluator.getResultString(findBestInCell(v)).toCharArray());
    	}
    }

    private void generateConcentrationMap() {
        Vector current;
        setupUnprocessed();
    	generateSpeciesMap();
        List<Vector> currentGroup;
        while (!unprocessed.isEmpty()) {
            current = unprocessed.get(unprocessed.size() - 1);
            currentGroup = crawl(current);
            if(currentGroup.size() > biggestGroupSeen){
                biggestGroupSeen = currentGroup.size();
                System.out.println("Biggest group yet: "+biggestGroupSeen);
            }
            for (Vector v : currentGroup) {
                speciesConcentrationMap.put(v, currentGroup.size());
            }
        }
        mapExists = true;
    }

    private void setupUnprocessed() {
        unprocessed.clear();
        for (Location<Vector> l : world) {
            if (!l.getIndividuals().isEmpty()) {
                unprocessed.add(l.getPosition());
            }
        }
    }

    private List<Vector> crawl(Vector current) {
        unprocessed.remove(current);
        List<Vector> result = new ArrayList<Vector>();
        result.add(current);
        char[] species = speciesMap.get(current);
        for (Vector v : directions) {
            Vector next = current.plus(v);
            if (unprocessed.contains(next)
                    && Arrays.equals(speciesMap.get(next), species)) {
                //System.out.println("Processing " + next + " via " + v);
                result.addAll(crawl(next));
            }
        }
        return result;
    }

    @Override
    public Hashtable<Vector, Integer> getConcentrationMap() {
        return speciesConcentrationMap;
    }
    
    private Individual findBestInCell(Vector position) {
        if (world.getIndividualsAt(position).isEmpty()) {
            return null;
        }
        return Collections.max(world.getIndividualsAt(position), IndividualComparator.getComparator());
    }

}
