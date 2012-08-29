/*package landscapeEC.core.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Individual;
import landscapeEC.util.FrequencyCounter;

public class DiversityCounterUpdater extends RecursiveAction{
	private FrequencyCounter<Individual> fc;

	public DiversityCounterUpdater(FrequencyCounter<Individual> fc){
		this.fc = fc;
	}

	@Override
	protected void compute() {
		Iterator<Individual> iter = fc.keys();
		while(iter.hasNext()){
			Individual individual = iter.next();
			DiversityCalculator.addIndividuals(individual, fc.getCount(individual));
		}
	}

} */
