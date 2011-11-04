package landscapeEC.problem.sat.operators;

import java.util.HashMap;
import java.util.List;

import landscapeEC.locality.Location;
import landscapeEC.locality.ShellMaker;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class LocalizedMutation implements MutationOperator, Observer {
    HashMap<Vector, Integer> speciesConcentrationMap = new HashMap<Vector, Integer>();
    ShellMaker shellMaker = null;

    @Override
    public Individual mutate(Individual ind, Object... parameters) {
        int[] bits = ind.getBits();
        double mutationRate=0;
        if(parameters[0] instanceof Vector){
            mutationRate = (DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length)*speciesConcentrationMap.get(parameters[0]);
        }
        else {
            mutationRate = DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length;
        }
        
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
    public void generationData(int generationNumber, World world, int successes) {
        shellMaker = new ShellMaker(world);
        for(Vector position : world){
            List<Vector> temp=world.getSpeciesNeighborhood(position);
            for(Vector square : temp){
                Integer current = speciesConcentrationMap.get(square);
                current++;
                speciesConcentrationMap.put(square, current);
            }
        }
    }
}
