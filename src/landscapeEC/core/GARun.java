package landscapeEC.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import landscapeEC.locality.EmptyWorldException;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.MigrationInWorldOfSizeOneException;
import landscapeEC.locality.Vector;
import landscapeEC.locality.ViralClauseCounter;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleArrayParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;
import landscapeEC.problem.sat.operators.CrossoverOperator;
import landscapeEC.problem.sat.operators.MutationOperator;
import landscapeEC.problem.sat.operators.SelectionOperator;
import landscapeEC.util.ParameterClassLoader;
import landscapeEC.util.SharedPRNG;

public class GARun {

	private MutationOperator mutationOperator;
	private SelectionOperator selectionOperator;
	private CrossoverOperator crossoverOperator;

	private PopulationManager popManager;
	private Evaluator evaluator;

	private World<?> world;
	
	private ViralClauseCounter viralClauseCounter;

	private List<Observer> observers = new ArrayList<Observer>();
	private int successes;
	private int generationNumber;
	private String propertiesFilename;
	private FileWriter writer;
	private double bestOverallFitness;
	
	private boolean runGenerations = true;

	private Double[] intervalFitnesses;
	private Double[] intervalDiversities;
	
	private long startTime;
	private boolean haventSaid=true;
	private List<Long> longs = new ArrayList<Long>();

	public GARun(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	public void run() throws Exception {
		mutationOperator = ParameterClassLoader
		.loadClass(StringParameter.MUTATION_OPERATOR);
		selectionOperator = ParameterClassLoader
		.loadClass(StringParameter.SELECTION_OPERATOR);
		crossoverOperator = ParameterClassLoader
		.loadClass(StringParameter.CROSSOVER_OPERATOR);

		intervalFitnesses = new Double[getReportingIntervals().length];
		intervalDiversities = new Double[getReportingIntervals().length];

		ProblemParser problemParser = ParameterClassLoader
		.loadClass(StringParameter.PROBLEM_PARSER);
		Problem problem = problemParser.parseProblem(new FileReader(new File(
				StringParameter.PROBLEM_FILE.getValue())));

		GlobalProblem.setProblem(problem);
		GlobalProblem.setEvaluator((Evaluator) ParameterClassLoader
				.loadClass(StringParameter.PROBLEM_EVALUATOR));
		evaluator = GlobalProblem.getEvaluator();

		popManager = new PopulationManager();

		int numRuns = IntParameter.NUM_RUNS.getValue();

		setupObservers();

		generateRFile(successes);

		successes = 0;
		for (int i = 0; i < numRuns; i++) {
			System.out.println("\nRUN " + (i + 1) + "\n");

			Arrays.fill(intervalFitnesses, (Double) null);

			try {
				startTime=System.currentTimeMillis();
				haventSaid=true;
				if (runGenerations(i)) {
					addRunToRFile(true, evaluator.getNumEvaluations(), 1.0);
					successes++;
				} else {
					addRunToRFile(false,
							IntParameter.NUM_EVALS_TO_DO.getValue(),
							bestOverallFitness);
				}
			} catch (EmptyWorldException e) {
				System.err.println("All individuals died!");
			}

			evaluator.resetEvaluationsCounter();
			SharedPRNG.updateGenerator(); // Generate a new seed for each run
		}

		System.out.println(successes + "/" + numRuns + " runs successful");
		long result = 0;
		for(long l : longs){
			result+=l;
		}
//		System.out.println("Average time to 1M evals: "+(result/longs.size()));
		closeRFile();
	}

	private void addRunToRFile(boolean success, int completedEvaluations,
			double bestFitness) throws IOException {
		Set<String> propertyNames = GlobalParameters.getParameterNames();
		for (String name : propertyNames) {
			if (GlobalParameters.isSet(name)) {
				writer.write(GlobalParameters.getStringValue(name).replaceAll(
						" ", "")
						+ " ");
			}
		}

		for (int i = 0; i < intervalFitnesses.length; i++) {
			Double fitness = intervalFitnesses[i];
			if (fitness == null) {
				fitness = 1.0;
			}
			writer.write(fitness + " ");
		}

		for (int i = 0; i < intervalDiversities.length; i++) {
			Double diversity = intervalDiversities[i];
			writer.write(diversity + " ");
		}

		writer.write(success + " " + completedEvaluations + " " + bestFitness
				+ "\n");
		writer.flush();
	}

	private void generateRFile(int successes) throws IOException {
		writer = new FileWriter(new File(propertiesFilename + ".R"));

		Set<String> propertyNames = GlobalParameters.getParameterNames();
		for (String name : propertyNames) {
			if (GlobalParameters.isSet(name)) {
				writer.write(name + " ");
			}
		}
		for (int i = 0; i < getReportingIntervals().length; i++) {
			writer.write("INTERVAL_" + getReportingIntervals()[i] + "_FITNESS ");
		}
		for (int i = 0; i < getReportingIntervals().length; i++) {
			writer.write("INTERVAL_" + getReportingIntervals()[i]
			                                                   + "_DIVERSITY ");
		}
		writer.write("SUCCESS COMPLETED_EVALS BEST_FITNESS\n");
	}

	private double[] getReportingIntervals() {
		return DoubleArrayParameter.REPORTING_INTERVALS.getValue();
	}

	private void closeRFile() throws IOException {
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	private void setupObservers() throws Exception {
		if (!GlobalParameters.isSet(StringParameter.OBSERVERS.toString()))
			return;

		String observerNames[] = StringParameter.OBSERVERS.getValue()
		.split(",");

		for (String observerName : observerNames) {
			Class<Observer> obs = (Class<Observer>) Class.forName(observerName);
			Constructor<Observer> cons = obs.getConstructor();
			Observer instance = cons.newInstance();
			observers.add(instance);
		}
	}

	private boolean runGenerations(int currentRun) throws Exception {
		List<Individual> population = popManager.generatePopulation();

		world = ParameterClassLoader.loadClass(StringParameter.WORLD_TYPE);
		world.clear();
		
		viralClauseCounter = new ViralClauseCounter();

		SeedType seedType = SeedType
		.valueOf(StringParameter.STARTING_POPULATION.getValue());

		switch (seedType) {
		case ORIGIN:
			world.getOrigin().setIndividuals(population);
			break;
		case EVERYWHERE:
			fillAllLocations();
			break;
		case CORNERS:
			for(Location l : world.getCorners()) {
				l.setIndividuals(popManager.generatePopulation());
			}
			break;
		}
		generationNumber = 0;
		bestOverallFitness = 0.0;
		Individual bestIndividual = null;
		// initialize observers before the run starts

		for (Observer o : observers) {
		    o.generationData(this);
		}
		while (evaluator.getNumEvaluations() < IntParameter.NUM_EVALS_TO_DO.getValue()) {
		    if(runGenerations) {
		        processAllLocations();
		        generationNumber++;
		    }

		    for (Observer o : observers) {
		        o.generationData(this);
		    }
		    bestIndividual = world.findBestIndividual();
		    bestOverallFitness = bestIndividual.getGlobalFitness();
		    double[] reportingIntervals = getReportingIntervals();
		    
		    checkForReportingInterval(currentRun);
		    
		    if (BooleanParameter.QUIT_ON_SUCCESS.getValue()
		            && bestOverallFitness == 1.0) {
		        System.out.println("Best Fitness: " + bestOverallFitness);
		        // This will be removed during refactoring
		        System.out.println("SUCCESS");
		        return true;
		    }

		    
		}


		if (bestOverallFitness == 1.0) {
			System.out.println("Best Fitness: " + bestOverallFitness);
			// This will be removed during refactoring
			System.out.println("SUCCESS");
			return true;
		}

		System.out.println("Best Fitness: " + bestOverallFitness);
		// This will be removed during refactoring
		System.out.println("FAILURE");
		return false;
	}

	private void checkForReportingInterval(int currentRun){
	    double[] reportingIntervals = getReportingIntervals();
            for (int j = 0; j < reportingIntervals.length; j++) {
                if (evaluator.getNumEvaluations() > reportingIntervals[j]
                                                                       * IntParameter.NUM_EVALS_TO_DO.getValue()
                                                                       && intervalFitnesses[j] == null) {
                    intervalFitnesses[j] = bestOverallFitness;
                    intervalDiversities[j] = DiversityCalculator
                    .calculateResultStringDiversity();
                    printIndividualsInLocation(0, 0);
                    printIndividualsInLocation(0, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
	}

    private void printIndividualsInLocation(int x, int y) {
        List<Integer> origin = new ArrayList<Integer>();
        System.out.println("Individuals at x " + x+ "y "+y);
        origin.add(x);
        origin.add(y);
        for (Individual i : ((GridWorld) world).getIndividualsAt(new Vector(origin))) {
            System.out.println(i);
        }
    }
	
	private void fillAllLocations() {
		for (Location location : world) {
			location.setIndividuals(popManager.generatePopulation());
		}
	}

	private void processAllLocations() {
		if(GlobalProblem.getEvaluator().getNumEvaluations()>1000000 && haventSaid){
			haventSaid=false;
			System.out.println("Evals/sec: "+(GlobalProblem.getEvaluator().getNumEvaluations()/((System.currentTimeMillis()-startTime)/1000)));
		}
		
		if(BooleanParameter.VIRAL_CLAUSES.getValue() || !BooleanParameter.SKIP_DIVERSITY.getValue())
			updateDiversityCounts();
		if (BooleanParameter.VIRAL_CLAUSES.getValue()) {
		    viralClauseCounter.updateViralClauses(world);
		}
		
		performMigration();
		addFromPendingIndividuals();

		performDraconianReaper();
		setFromPendingIndividuals();
		performElitism();
		performReproduction();
		setFromPendingIndividuals();
	}

	private void updateDiversityCounts() {
		DiversityCalculator.reset();
		for (Location location : world) {
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

	private void performDraconianReaper() {

	    for (Location location : world) {
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

	private void performMigration() {
	    double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
	    int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();

	    if (migrationProbability <= 0 || migrationDistance <= 0)
	        return;

	        for (Location<?> location : world) {
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
	                        newLocation.addToPendingIndividuals(i);
	                    } catch (IndexOutOfBoundsException e) {
	                        throw new MigrationInWorldOfSizeOneException(e);
	                    }
	                }
	            }
	            location.removeAll(individualsToRemove);
	        }

	}

	private void performElitism() {
		for (Location location : world) {
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

		for (Location location : world) {
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

	private void setFromPendingIndividuals() {

		for (Location location : world) {
			location.setFromPendingIndividuals();
		}
	}


	private void addFromPendingIndividuals() {

		for (Location location : world) {
			location.addFromPendingIndividuals();
		}
	}
	
	public World<?> getWorld() {
	    return world;
	}
	
	public int getGenerationNumber() {
	    return generationNumber;
	}
	
	public int getNumSucesses() {
	    return successes;
	}
	
	public void allowGenerationRunning(boolean flag) {
	    runGenerations = flag;
	}
}
