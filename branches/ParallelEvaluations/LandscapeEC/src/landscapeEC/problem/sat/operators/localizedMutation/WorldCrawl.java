package landscapeEC.problem.sat.operators.localizedMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.FrequencyCounter;
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
public class WorldCrawl implements ConcentrationRanker {
    //Necessary for singletonizing
    private static WorldCrawl instance = null;
    
    //A reference to the GridWorld object
    static private GridWorld world;
    
    //Where The Information Lives
    static Hashtable<Vector, Integer> speciesConcentrationMap = new Hashtable<Vector, Integer>();
    static FrequencyCounter<Integer> speciesCount = new FrequencyCounter<Integer>();

    //Mechanically necessary stuff
    static List<Vector> unprocessed = new ArrayList<Vector>();
    private ArrayList<Vector> directions = new ArrayList<Vector>();
    private boolean mapExists = false;
    
    //Testing variables
    private int maxGroupSize = 0;
    private int globalMaxSpeciesPop = 0;
    private int[] maxGroupSpecies;
    
    protected WorldCrawl(){
        //Oh yes, this looks gross.
        //Deal wid it.
        Integer[] set = new Integer[2];
        set[0] = -1;
        set[1] = 0;
        Vector left = new Vector(set);
        set[0] = 1;
        Vector right = new Vector(set);
        set[0] = 0;
        set[1] = -1;
        Vector down = new Vector(set);
        set[1] = 1;
        Vector up = new Vector(set);
        directions.add(right);
        directions.add(up);
        directions.add(left);
        directions.add(down);
    }

    public static WorldCrawl getInstance() {
        if(instance == null) {
           instance = new WorldCrawl();
        }
        return instance;
     }

    @Override
    public int getAmp(Vector vector) {
        if (!mapExists)
            generateConcentrationMap();
        if (speciesConcentrationMap.containsKey(vector))
            return speciesConcentrationMap.get(vector);
        return 1;
    }

    @Override
    public void initialize(GridWorld newWorld, int generationNumber) {
        if(generationNumber % 100 == 1){
            System.out.println("Max group size seen at generation "+(generationNumber-1)+": "+maxGroupSize);
            if((globalMaxSpeciesPop-maxGroupSize) > 0)
            System.out.println("There were "+(globalMaxSpeciesPop-maxGroupSize)+" other cells held by this species at the time.");
            maxGroupSize = 0;
            maxGroupSpecies = null;
            globalMaxSpeciesPop = 0;
        }
        world = newWorld;
        speciesConcentrationMap.clear();
        speciesCount.reset();
        mapExists = false;
        
    }

    private void generateConcentrationMap() {
        boolean updated = false;
        Vector current;
        setupUnprocessed();
        List<Vector> currentGroup;
        while (!unprocessed.isEmpty()) {
            current = unprocessed.get(unprocessed.size() - 1);
            currentGroup = crawl(current);
            for (Vector v : currentGroup) {
                speciesConcentrationMap.put(v, currentGroup.size());
            }
            int[] species = findBestInCell(current);
            speciesCount.addItem(Arrays.hashCode(species), currentGroup.size());
            if(currentGroup.size() > maxGroupSize){
                maxGroupSize = currentGroup.size();
                maxGroupSpecies = species;
                updated = true;
            }
        }
        setupUnprocessed();
        for(Vector v : unprocessed){
            int x = (speciesCount.getCount(Arrays.hashCode(findBestInCell(v)))/2);
            speciesConcentrationMap.put(v, Math.max(speciesConcentrationMap.get(v), x));
        }
        if(updated){
            globalMaxSpeciesPop = speciesCount.getCount(Arrays.hashCode(maxGroupSpecies));
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
        int[] species = findBestInCell(current);
        for (Vector v : directions) {
            Vector next = current.plus(v);
            if (unprocessed.contains(next)
                    && Arrays.equals(findBestInCell(next), species)) {
                //System.out.println("Processing " + next + " via " + v);
                result.addAll(crawl(next));
            }
        }
        return result;
    }
    
    // Use the version of this that already exists!
    private int[] findBestInCell(Vector position) {
        if (world.getIndividualsAt(position).isEmpty()) {
            return null;
        }
        IndividualComparator comparator = IndividualComparator.getComparator();
        return Collections.max(world.getIndividualsAt(position), comparator)
                .getBits();
    }

    @Override
    public Hashtable<Vector, Integer> getConcentrationMap() {
        return speciesConcentrationMap;
    }

}
