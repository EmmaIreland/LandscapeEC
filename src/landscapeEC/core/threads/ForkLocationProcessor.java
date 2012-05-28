package landscapeEC.core.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import landscapeEC.core.PopulationManager;
import landscapeEC.locality.Location;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;
import landscapeEC.problem.sat.operators.CrossoverOperator;
import landscapeEC.problem.sat.operators.MutationOperator;
import landscapeEC.problem.sat.operators.SelectionOperator;
import landscapeEC.util.ParameterClassLoader;

public class ForkLocationProcessor extends RecursiveAction {
	Location[] locations;
	private PopulationManager popManager;
	private Evaluator evaluator;
	private static final int THRESHOLD = 150;
	
	public ForkLocationProcessor(Location[] locations){
		this.locations = locations;
	}
	
	@Override
	protected void compute() {
		if(locations.length > THRESHOLD){
			int mid = locations.length/2;
			ForkLocationProcessor a = new ForkLocationProcessor(Arrays.copyOfRange(locations, 0, mid));
			a.fork();
			ForkLocationProcessor b = new ForkLocationProcessor(Arrays.copyOfRange(locations, mid, locations.length));
			b.compute();
		} else {
			doWork();
		}
	}
	
	protected void doWork(){
		popManager = new PopulationManager();
		evaluator = GlobalProblem.getEvaluator();
		performDraconianReaper();
		performElitism();
		performReproduction();
	}
	
	private void performDraconianReaper() {

	    for (Location location : locations) {
	        // Location location = world.getLocation(position);
	        List<Individual> locationIndividuals = location.getIndividuals();

	        for (Individual individual : locationIndividuals) {
	            Problem locationProblem = location.getProblem();
	            doReaperEffect(individual, location, evaluator.solvesSubProblem(individual, locationProblem));
	        }
			location.setFromPendingIndividuals();
	    }
	}

	private void doReaperEffect(Individual individual, Location location, boolean satisfiesRequirements) {
		if ("FITNESS".equals(StringParameter.REAPER_EFFECT.getValue())) {
			if (!satisfiesRequirements) {
				individual.updateLocalFitness(location.getProblem().getDifficulty());
			}
			location.addToPendingIndividuals(individual);
		} else {
			if (satisfiesRequirements) {
				location.addToPendingIndividuals(individual);
			}
		}
	}
	
	private void performElitism() {
		for (Location location : locations) {
			List<Individual> locationIndividuals = location.getIndividuals();

			if (!locationIndividuals.isEmpty()) {
				List<Individual> elite = popManager.getElite(
						locationIndividuals,
						DoubleParameter.ELITE_PROPORTION.getValue());

				location.addToPendingIndividuals(elite);

			}
		}
	}
	
	private void performReproduction() {
		MutationOperator mutationOperator = null;
		SelectionOperator selectionOperator = null;
		CrossoverOperator crossoverOperator = null;
		try{
		mutationOperator = ParameterClassLoader
		.loadClass(StringParameter.MUTATION_OPERATOR);
		selectionOperator = ParameterClassLoader
		.loadClass(StringParameter.SELECTION_OPERATOR);
		crossoverOperator = ParameterClassLoader
		.loadClass(StringParameter.CROSSOVER_OPERATOR);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("A thread failed to load some parameter.");
		}
		for (Location location : locations) {
			List<Individual> locationIndividuals = location.getIndividuals();

			if (locationIndividuals.size() >= IntParameter.TOURNAMENT_SIZE
					.getValue()) {
				List<Individual> crossoverPop = popManager.crossover(
						locationIndividuals, selectionOperator,
						crossoverOperator);

				List<Individual> mutatedPopulation = popManager
				.mutatePopulation(crossoverPop, mutationOperator,
						location);

				location.addToPendingIndividuals(mutatedPopulation);
				location.setFromPendingIndividuals();
			} else if (BooleanParameter.PROMOTE_SMALL_POPULATIONS.getValue()) {
				List<Individual> copiedPopulation = new ArrayList<Individual>();
				for (Individual individual : locationIndividuals) {
					copiedPopulation.add(individual);
				}

				List<Individual> mutatedPopulation = popManager
				.mutatePopulation(copiedPopulation, mutationOperator,
						location);

				location.addToPendingIndividuals(mutatedPopulation);
				location.setFromPendingIndividuals();
			}
		}
	}

}
