/*package landscapeEC.core.threads;

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

public class ForkIndividualProcessor extends RecursiveTask {
	private List<Individual> individuals;
	private PopulationManager popManager;
	private Evaluator evaluator;
	private Location location;
	private List<Individual> pendingIndividuals;
	private Problem locationProblem;
	private int numIndividuals;
	private static int IND_THRESHOLD;
	
	public ForkIndividualProcessor(List<Individual> individuals, Location location, int IND_THRESHOLD){
		this.individuals = individuals;
		this.location = location;
		this.IND_THRESHOLD=IND_THRESHOLD;
	}

	@Override
	protected List<Individual> compute() {
		if(individuals.size()>IND_THRESHOLD){
			int mid = individuals.size()/2;
			int i = 0;
			List<Individual> aList = new ArrayList<Individual>();
			while(i<mid){
				aList.add(individuals.get(i));
				i++;
			}
			ForkIndividualProcessor a = new ForkIndividualProcessor(aList, location, IND_THRESHOLD);
			a.fork();
			List<Individual> bList = new ArrayList<Individual>();
			while(i<individuals.size()){
				bList.add(individuals.get(i));
				i++;
			}
			ForkIndividualProcessor b = new ForkIndividualProcessor(bList, location, IND_THRESHOLD);
			List<Individual> result = b.compute();
			result.addAll((List<Individual>)a.join());
			return result;
		}
		else {
			return doWork();
		}
	}

	protected List<Individual> doWork() {
        locationProblem = location.getProblem();
		popManager = new PopulationManager();
		evaluator = GlobalProblem.getEvaluator();
		
		pendingIndividuals = new ArrayList<Individual>(individuals.size());
		numIndividuals = individuals.size();
		performDraconianReaper();
		setFromPendingIndividuals();
		performElitism();
		performReproduction();
		setFromPendingIndividuals();
		
		return individuals;
	}
	
	private void setFromPendingIndividuals(){
		individuals = new ArrayList<Individual>(pendingIndividuals);
		pendingIndividuals.clear();
	}
	
	private void addToPendingIndividuals(Individual individual){
		pendingIndividuals.add(individual);
	}
	
	private void addToPendingIndividuals(List<Individual> individuals){
		pendingIndividuals.addAll(individuals);
	}
	
	private void performDraconianReaper() {
		for(Individual individual : individuals){
            doReaperEffect(individual, evaluator.solvesSubProblem(individual, locationProblem));
        }
	}

	private void doReaperEffect(Individual individual, boolean satisfiesRequirements) {
		if ("FITNESS".equals(StringParameter.REAPER_EFFECT.getValue())) {
			if (!satisfiesRequirements) {
				individual.updateLocalFitness(locationProblem.getDifficulty());
			}
			addToPendingIndividuals(individual);
		} else {
			if (satisfiesRequirements) {
				addToPendingIndividuals(individual);
			}
		}
	}
	
	private void performElitism() {
		List<Individual> elite = 
			popManager.getElite(individuals ,DoubleParameter.ELITE_PROPORTION.getValue());
			addToPendingIndividuals(elite);
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
			if (individuals.size() >= IntParameter.TOURNAMENT_SIZE
					.getValue()) {
				int numIndividuals = individuals.size();
				int maxChildren = (int) (numIndividuals*DoubleParameter.REPRODUCTION_RATE.getValue());
		        int numChildren = Math.min(numIndividuals-pendingIndividuals.size(), maxChildren);
		        
				List<Individual> crossoverPop = popManager.forkedCrossover(
						individuals, selectionOperator,
						crossoverOperator, numChildren);

				List<Individual> mutatedPopulation = popManager
				.mutatePopulation(crossoverPop, mutationOperator,
						individuals);
				addToPendingIndividuals(mutatedPopulation);
			} else if (BooleanParameter.PROMOTE_SMALL_POPULATIONS.getValue()) {
				List<Individual> copiedPopulation = new ArrayList<Individual>();
				for (Individual individual : individuals) {
					copiedPopulation.add(individual);
				}

				List<Individual> mutatedPopulation = popManager
				.mutatePopulation(copiedPopulation, mutationOperator,
						individuals);
				addToPendingIndividuals(mutatedPopulation);
			}
		}
	} */