/*package landscapeEC.core.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import landscapeEC.core.PopulationManager;
import landscapeEC.locality.Location;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.IntArrayParameter;
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
	private static int maxPop;
	private static int IND_THRESHOLD;
	private static final int LOC_THRESHOLD = Math.max(IntArrayParameter.WORLD_DIMENSIONS.getValue()[0]*IntArrayParameter.WORLD_DIMENSIONS.getValue()[1]/Runtime.getRuntime().availableProcessors(), 1);
	public ForkLocationProcessor(Location[] locations){
		maxPop = IntArrayParameter.WORLD_DIMENSIONS.getValue()[0]*IntArrayParameter.WORLD_DIMENSIONS.getValue()[1]*IntParameter.CARRYING_CAPACITY.getValue();
		IND_THRESHOLD = maxPop/Runtime.getRuntime().availableProcessors();
		this.locations = locations;
	}
	
	@Override
	protected void compute() {
		if(locations.length > LOC_THRESHOLD){
			int mid = locations.length/2;
			ForkLocationProcessor a = new ForkLocationProcessor(Arrays.copyOfRange(locations, 0, mid));
			a.fork();
			ForkLocationProcessor b = new ForkLocationProcessor(Arrays.copyOfRange(locations, mid, locations.length));
			b.compute();
		} else {
			if(IntParameter.CARRYING_CAPACITY.getValue()<IND_THRESHOLD){
				doWork();
			} else {
				threadByIndividuals();
			}
		}
	}
	
	protected void threadByIndividuals(){
		for(Location location : locations){
			if(location.getIndividuals().size()!=0){
				List<Individual> individuals = location.getIndividuals();
				ForkIndividualProcessor fip = new ForkIndividualProcessor(individuals, location, IND_THRESHOLD);
				List<Individual> newIndividuals = fip.compute();
				location.setIndividuals(newIndividuals);
			}
		}
	}
	
	protected void doWork(){
		popManager = new PopulationManager();
		evaluator = GlobalProblem.getEvaluator();
		performDraconianReaper();
		setFromPendingIndividuals();
		performElitism();
		performReproduction();
		setFromPendingIndividuals();
	}
	
	private void setFromPendingIndividuals(){
		for(Location location : locations){
			synchronized(location){
				location.setFromPendingIndividuals();
			}
		}
	}
	
	private void performDraconianReaper() {

	    for (Location location : locations) {
	        // Location location = world.getLocation(position);
	        List<Individual> locationIndividuals = location.getIndividuals();

	        for (Individual individual : locationIndividuals) {
	            Problem locationProblem = location.getProblem();
	            doReaperEffect(individual, location, evaluator.solvesSubProblem(individual, locationProblem));
	        }
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
			} else if (BooleanParameter.PROMOTE_SMALL_POPULATIONS.getValue()) {
				List<Individual> copiedPopulation = new ArrayList<Individual>();
				for (Individual individual : locationIndividuals) {
					copiedPopulation.add(individual);
				}

				List<Individual> mutatedPopulation = popManager
				.mutatePopulation(copiedPopulation, mutationOperator,
						location);

				location.addToPendingIndividuals(mutatedPopulation);
			}
		}
	}

}
*/