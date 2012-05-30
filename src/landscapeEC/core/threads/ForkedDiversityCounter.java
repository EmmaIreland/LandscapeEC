package landscapeEC.core.threads;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import landscapeEC.locality.Location;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;

public class ForkedDiversityCounter extends RecursiveAction{
	
	private Location[] locations;
	
	public ForkedDiversityCounter(Location[] locations){
		this.locations = locations;
	}
	
	@Override
	protected void compute() {
		updateDiversityCounts();
	}
	
	private void updateDiversityCounts() {
		DiversityCalculator.reset();
		for (Location location : locations) {
			List<Individual> locationIndividuals = location.getIndividuals();
			if (locationIndividuals.size() > 0) {
			    Individual bestIndividual = (Individual) Collections.max(location.getIndividuals(), IndividualComparator.getComparator());
			    DiversityCalculator.addBestIndividual(bestIndividual);
			}
			for (Individual individual : locationIndividuals) {
				DiversityCalculator.addIndividual(individual);
			}
		}


	}

}
