package landscapeEC.problem.sat.operators;


import java.util.Hashtable;

import landscapeEC.locality.Location;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Vector;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.operators.localizedMutation.BiggestBox;
import landscapeEC.problem.sat.operators.localizedMutation.ConcentrationRanker;
import landscapeEC.problem.sat.operators.localizedMutation.LocalityType;
import landscapeEC.problem.sat.operators.localizedMutation.WorldCrawl;
import landscapeEC.util.SharedPRNG;

public final class LocalizedMutation implements MutationOperator, Observer {
    //This is a _really janky_ way to handle switching between various implementations of locality.
    //It will be switched to a parameter-reader as soon as, well, I finish doing that.
    LocalityType rankerType = LocalityType.valueOf(StringParameter.LOCALITY_TYPE.getValue());
    ConcentrationRanker amplifier;
    public LocalizedMutation(){
        switch (rankerType) {
        case BIGGEST_BOX:
            amplifier = BiggestBox.getInstance();
            break;
        case WORLDCRAWL:
            amplifier = WorldCrawl.getInstance();
            break;
        }
    }
        

    @Override
    public Individual mutate(Individual ind, Object... parameters) {
        int[] bits = ind.getBits();
        double mutationRate = (DoubleParameter.AVERAGE_MUTATIONS.getValue()/bits.length)
        /*this amplification factor is currently ridiculously high.
          remember to resolve that inside the classes you extract.*/
        *amplifier.getAmp(((Location<Vector>)parameters[0]).getPosition());
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
	amplifier.initialize(newWorld, generationNumber);
    }
    
    public ConcentrationRanker getAmplifier(){
        return amplifier;
    }
    
    public Hashtable<Vector, Integer> getConcentrationMap(){
        return amplifier.getConcentrationMap();
    }
}
