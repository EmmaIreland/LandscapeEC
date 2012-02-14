package landscapeEC.problem.sat.operators.localizedMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.problem.IndividualComparator;

public class BiggestBox implements ConcentrationRanker {
    static Hashtable<Vector, Integer> speciesConcentrationMap = new Hashtable<Vector, Integer>();
    static Hashtable<Vector, int[]> speciesMap = new Hashtable<Vector, int[]>();
    private static BiggestBox instance;
    static ShellMaker shellMaker;
    GridWorld world;
    private int maxRad = 0;
    private boolean mapExists = false;
    
    protected BiggestBox(){
        //do absolutely nothing
    }

    public static BiggestBox getInstance() {
        if(instance == null) {
           instance = new BiggestBox();
        }
        return instance;
     }

    @Override
    public int getAmp(Vector vector) {
        if(!mapExists){
            generateConcentrationMap();
        }
	return speciesConcentrationMap.get(vector);
    }

    @Override
    public void initialize(GridWorld newWorld, int generationNumber) {
        if(maxRad>1){
            System.out.println("MaxRad for generation "+generationNumber+":"+maxRad);
        }
        maxRad=0;
        this.world = newWorld;
        resetConcentrationMap();
        mapExists = false;
    }

    private void generateConcentrationMap() {
        generateSpeciesMap();
        shellMaker = new ShellMaker(world);
        for (Location<Vector> location : world) {
            if (speciesMap.get(location.getPosition())!=null) {
                List<Vector> temp = getSpeciesNeighborhood(location.getPosition());
                for (Vector v : temp) {
                     Integer currentValue = speciesConcentrationMap.get(v);
                     speciesConcentrationMap.put(v, currentValue+1);
                }
            }
        }
        mapExists=true;
    }
    
    private void generateSpeciesMap() {
        for(Location<Vector> l : world){
            if(!world.getIndividualsAt(l.getPosition()).isEmpty())
            speciesMap.put(l.getPosition(), findBestInCell(l.getPosition()));
        }
    }

    private List<Vector> getSpeciesNeighborhood(Vector position) {
        List<Vector> neighborhood = new ArrayList<Vector>();
        neighborhood.add(position);
        int[] speciesBits = speciesMap.get(position);
        boolean match = true;
        List<Vector> candidateShell;
        for(int rad=1; rad<Collections.max(world.getDimensions().coordinates) && match; rad++){
            candidateShell = shellMaker.makeShell(position, rad);
            for(Vector v : candidateShell){
                
                if(speciesMap.get(v) == null || Arrays.equals(speciesBits, speciesMap.get(v))){
                    continue;
                }
                match = false;
                break;
            }
            
            if(match){
                neighborhood.addAll(candidateShell);
            }
            if(rad>maxRad){
                maxRad = rad;
            }
        }
        return neighborhood;
    }

    private int[] findBestInCell(Vector position) {
        if (world.getIndividualsAt(position).isEmpty()) {
            return null;
        }
        IndividualComparator comparator = IndividualComparator.getComparator();
        return Collections.max(world.getIndividualsAt(position), comparator).getBits();
    }

    public void resetConcentrationMap(){
        speciesConcentrationMap.clear();
        for(Location<Vector> location : world){
            speciesConcentrationMap.put(location.getPosition(), 1);
        }
    }

    @Override
    public Hashtable<Vector, Integer> getConcentrationMap() {
        return speciesConcentrationMap;
    }

}
