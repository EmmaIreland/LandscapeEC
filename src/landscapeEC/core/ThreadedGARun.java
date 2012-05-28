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
import java.util.concurrent.ForkJoinPool;

import landscapeEC.core.threads.ForkDiversityCounter;
import landscapeEC.core.threads.ForkLocationProcessor;
import landscapeEC.core.threads.ForkMigrator;
import landscapeEC.locality.EmptyWorldException;
import landscapeEC.locality.GraphWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.MigrationInWorldOfSizeOneException;
import landscapeEC.locality.Vector;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.ViralClauseCounter;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleArrayParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.DiversityCalculator;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.operators.CrossoverOperator;
import landscapeEC.problem.sat.operators.MutationOperator;
import landscapeEC.problem.sat.operators.SelectionOperator;
import landscapeEC.util.FrequencyCounter;
import landscapeEC.util.ParameterClassLoader;
import landscapeEC.util.SharedPRNG;

public class ThreadedGARun extends GARun{

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

	private double[] intervalFitnesses;
	private double[] intervalDiversities;
	
	private ForkJoinPool forkPool = new ForkJoinPool();
	private Location[] locations;

	public ThreadedGARun(String propertiesFilename) {
		super(propertiesFilename);
	}

	public void run() throws Exception {
		mutationOperator = ParameterClassLoader
		.loadClass(StringParameter.MUTATION_OPERATOR);
		selectionOperator = ParameterClassLoader
		.loadClass(StringParameter.SELECTION_OPERATOR);
		crossoverOperator = ParameterClassLoader
		.loadClass(StringParameter.CROSSOVER_OPERATOR);

		intervalFitnesses = new double[getReportingIntervals().length];
		intervalDiversities = new double[getReportingIntervals().length];

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

			Arrays.fill(intervalFitnesses, Double.NaN);

			try {
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
			double fitness = intervalFitnesses[i];
			if (Double.isNaN(fitness)) {
				fitness = 1.0;
			}
			writer.write(fitness + " ");
		}

		for (int i = 0; i < intervalDiversities.length; i++) {
			double diversity = intervalDiversities[i];
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
		int numLocations=0;
		for(Location l : world){
			numLocations++;
		}
		locations = new Location[numLocations];
		numLocations=0;
		for(Location l : world){
			locations[numLocations]=l;
			numLocations++;
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
		    for (int j = 0; j < reportingIntervals.length; j++) {
		        if (evaluator.getNumEvaluations() > reportingIntervals[j]
		                                                               * IntParameter.NUM_EVALS_TO_DO.getValue()
		                                                               && Double.isNaN(intervalFitnesses[j])) {
		            intervalFitnesses[j] = bestOverallFitness;
		            intervalDiversities[j] = DiversityCalculator
		            .calculateResultStringDiversity();
		            SnapShot.saveSnapShot(propertiesFilename + ".run"
		                    + currentRun + ".part" + j, world);
		        }
		    }

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

	private void fillAllLocations() {
		for (Location location : world) {
			location.setIndividuals(popManager.generatePopulation());
		}
	}

	private void processAllLocations() {
		updateDiversityCounts();
		if (BooleanParameter.VIRAL_CLAUSES.getValue()) {
		    viralClauseCounter.updateViralClauses(world);
		}
		
		performMigration();
		addFromPendingIndividuals();
		
		threadByLocations();
		while(!forkPool.isQuiescent()){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void threadByLocations(){
		ForkLocationProcessor flp = new ForkLocationProcessor(locations);
		forkPool.invoke(flp);
	}
	
	private void performMigration(){
		ForkMigrator fm = new ForkMigrator(locations, world);
		forkPool.invoke(fm);
	}

	private void updateDiversityCounts() {
		DiversityCalculator.reset();
		ForkDiversityCounter fdc = new ForkDiversityCounter(locations);
		DiversityCalculator.addCounter((FrequencyCounter<Individual>)forkPool.invoke(fdc));
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
