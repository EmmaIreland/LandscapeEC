

/*
package landscapeEC.core.threads;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import landscapeEC.locality.Location;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.util.FrequencyCounter;

public class ForkDiversityCounter extends RecursiveTask {
	private Location[] locations;
	private static final int LOC_THRESHOLD = Math.max(IntArrayParameter.WORLD_DIMENSIONS.getValue()[0]*IntArrayParameter.WORLD_DIMENSIONS.getValue()[1]/Runtime.getRuntime().availableProcessors(), 1);
	private static int IND_THRESHOLD;
	private Individual[] individuals;
	
	public ForkDiversityCounter(Location[] locations){
		this.locations = locations;
	}
	
	public ForkDiversityCounter(Individual[] individuals){
		this.individuals = individuals;
		int maxPop = IntArrayParameter.WORLD_DIMENSIONS.getValue()[0]*IntArrayParameter.WORLD_DIMENSIONS.getValue()[1]*IntParameter.CARRYING_CAPACITY.getValue();
		IND_THRESHOLD = maxPop/Runtime.getRuntime().availableProcessors();
	}

	@Override
	protected FrequencyCounter<Individual> compute() {
		if(individuals!=null){
			if(individuals.length > IND_THRESHOLD){
				int mid = individuals.length/2;
				ForkDiversityCounter a = new ForkDiversityCounter(Arrays.copyOfRange(individuals, 0, mid));
				a.fork();
				ForkDiversityCounter b = new ForkDiversityCounter(Arrays.copyOfRange(individuals, mid, individuals.length));
				FrequencyCounter result = b.compute();
				result.addCounter((FrequencyCounter)a.join());
				return result;
			}
			else {
				doIndividuals();
			}
		}
		if(locations.length > LOC_THRESHOLD){
			int mid = locations.length/2;
			ForkDiversityCounter a = new ForkDiversityCounter(Arrays.copyOfRange(locations, 0, mid));
			a.fork();
			ForkDiversityCounter b = new ForkDiversityCounter(Arrays.copyOfRange(locations, mid, locations.length));
			//return a fusion of a.join() and b.compute()
			FrequencyCounter result = b.compute();
			result.addCounter((FrequencyCounter)a.join());
			return result;
		} else {
			return doWork();
		}
	}
	
	private FrequencyCounter<Individual> doIndividuals(){
		FrequencyCounter<Individual> result = new FrequencyCounter<Individual>();
		for(Individual i : individuals){
			result.addItem(i);
		}
		return result;
	}

	private FrequencyCounter<Individual> doWork() {
		FrequencyCounter<Individual> result = new FrequencyCounter<Individual>();
		for(Location location : locations){
			List<Individual> individuals = location.getIndividuals();
			for(Individual i : individuals){
				/*if (individuals.size() > 0) {
				    Individual bestIndividual = (Individual) Collections.max(location.getIndividuals(), IndividualComparator.getComparator());
				    DiversityCalculator.addBestIndividual(bestIndividual);
				    
				    This line does two things -
				     It makes ViralClauses work
				     It makes threading performance gobble on the fat one.
				}*/ /*
				result.addItem(i);
			}
		}
		return result;
	}

}
*/