package landscapeEC.problem.sat.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import landscapeEC.locality.Location;
import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.locality.GridWorld;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.SharedPRNG;

public final class LocalizedMutation implements MutationOperator, Observer {
    static Hashtable<Vector, Integer> speciesConcentrationMap = new Hashtable<Vector, Integer>();
    static Hashtable<Vector, int[]> speciesMap = new Hashtable<Vector, int[]>();
    static ShellMaker shellMaker;
    GridWorld world;
    private int maxRad = 0;

    @Override
    public Individual mutate(Individual ind, Object... parameters) {
        int[] bits = ind.getBits();
        double mutationRate = (DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length)*speciesConcentrationMap.get(((Location)parameters[0]).getPosition());
        for (int i = 0; i < bits.length; i++) {
            if (SharedPRNG.instance().nextDouble() < mutationRate) {
                bits[i] = flipBit(bits[i]);
            }
        }
        return new Individual(bits);
    }

    private int flipBit(int bit) {
        if (bit == 0)
            return 1;
        return 0;
    }

    @Override
    public void generationData(int generationNumber, GridWorld newWorld, int successes) {
        if(maxRad>1){
        System.out.println("MaxRad for generation "+generationNumber+":"+maxRad);
        }
        maxRad=0;
        this.world = newWorld;
        resetConcentrationMap();
        generateSpeciesMap();
        shellMaker = new ShellMaker(world);
        for (Location location : world) {
            if (speciesMap.get(location)!=null) {
                List<Vector> temp = getSpeciesNeighborhood(location.getPosition());
                for (Vector v : temp) {
                    Integer currentValue = speciesConcentrationMap.get(v);
                    speciesConcentrationMap.put(v, currentValue+1);
                }
            }
        }
    }
    

    private void generateSpeciesMap() {
        for(Location l : world){
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
        for(Location location : world){
            speciesConcentrationMap.put(location.getPosition(), 1);
        }
    }
}
