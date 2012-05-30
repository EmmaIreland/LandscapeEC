package landscapeEC.core.threads;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import landscapeEC.locality.Location;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.FrequencyCounter;

public class ForkDiversityCounter extends RecursiveTask {
	private Location[] locations;
	private static final int THRESHOLD = IntParameter.SPLIT_THRESHOLD.getValue();
	
	public ForkDiversityCounter(Location[] locations){
		this.locations = locations;
	}

	@Override
	protected FrequencyCounter<Individual> compute() {
		if(locations.length > THRESHOLD){
			int mid = locations.length/2;
			ForkDiversityCounter a = new ForkDiversityCounter(Arrays.copyOfRange(locations, 0, mid));
			a.fork();
			ForkDiversityCounter b = new ForkDiversityCounter(Arrays.copyOfRange(locations, mid, locations.length));
			//return a fusion of a.join() and b.compute()
			FrequencyCounter result = b.compute();
			b.compute().addCounter((FrequencyCounter)a.join());
			return result;
		} else {
			return doWork();
		}
	}

	private FrequencyCounter<Individual> doWork() {
		FrequencyCounter<Individual> result = new FrequencyCounter<Individual>();
		for(Location location : locations){
			List<Individual> individuals = location.getIndividuals();
			for(Individual i : individuals){
				if (individuals.size() > 0) {
				    Individual bestIndividual = (Individual) Collections.max(location.getIndividuals(), IndividualComparator.getComparator());
				    DiversityCalculator.addBestIndividual(bestIndividual);
				}
				result.addItem(i);
			}
		}
		return result;
	}

}
