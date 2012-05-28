package landscapeEC.core.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import landscapeEC.locality.Location;
import landscapeEC.locality.MigrationInWorldOfSizeOneException;
import landscapeEC.locality.World;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.Individual;
import landscapeEC.util.SharedPRNG;

public class ForkMigrator extends RecursiveAction {
	private Location[] locations;
	private World world;
	private static final int THRESHOLD = 150;
	
	public ForkMigrator(Location[] locations, World world){
		this.locations = locations;
		this.world = world;
	}

	@Override
	protected void compute() {
		if(locations.length > THRESHOLD){
			int mid = locations.length/2;
			ForkMigrator a = new ForkMigrator(Arrays.copyOfRange(locations, 0, mid), world);
			a.fork();
			ForkMigrator b = new ForkMigrator(Arrays.copyOfRange(locations, mid, locations.length), world);
			b.compute();
		} else {
			performMigration();
		}
		
	}
	private void performMigration() {
	    double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
	    int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();

	    if (migrationProbability <= 0 || migrationDistance <= 0)
	        return;
	    
	    //TODO It may be possible to reduce the code duplication here, but we can't right now until we refactor
	    //    the way world.getNeighborhood works (right now it needs to take a Vector or an Integer)
	    //if (StringParameter.WORLD_TYPE.getValue().contains("GridWorld")) {
	        //GridWorld gridWorld = (GridWorld) world;
	        for (Location<?> location : locations) {
	            List<Individual> locationIndividuals = location.getIndividuals();
	            List<Individual> individualsToRemove = new ArrayList<Individual>();

	            for (Individual i : locationIndividuals) {
	                if (SharedPRNG.instance().nextDouble() < migrationProbability) {
	                    individualsToRemove.add(i);
	                    List<?> neighborhood = world.getNeighborhood(location.getPosition(), migrationDistance);
	                    neighborhood.remove(location);
	                    Object newPosition;
	                    try {
	                        newPosition = neighborhood.get(SharedPRNG.instance().nextInt(neighborhood.size()));
	                        Location<?> newLocation = world.getLocation(newPosition);
	                        synchronized(newLocation){
	                        	newLocation.addToPendingIndividuals(i);
	                        }
	                    } catch (IndexOutOfBoundsException e) {
	                        throw new MigrationInWorldOfSizeOneException(e);
	                    }
	                }
	            }
	            location.removeAll(individualsToRemove);
	        }
	}

}
