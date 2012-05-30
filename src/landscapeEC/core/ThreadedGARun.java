package landscapeEC.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import landscapeEC.core.threads.ForkDiversityCounter;
import landscapeEC.core.threads.ForkLocationProcessor;
import landscapeEC.core.threads.ForkMigrator;
import landscapeEC.core.threads.ForkedDiversityCounter;
import landscapeEC.locality.EmptyWorldException;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.MigrationInWorldOfSizeOneException;
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
import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;
import landscapeEC.util.FrequencyCounter;
import landscapeEC.util.ParameterClassLoader;
import landscapeEC.util.SharedPRNG;

public class ThreadedGARun extends GARun{

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
	
	private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private ExecutorService threads = new ThreadPoolExecutor(2, 16, 15, TimeUnit.MILLISECONDS, workQueue);
	@SuppressWarnings("rawtypes")
	private Location[] locations;
	
	
	//Testing stuff
	private long startTime;
	private boolean haventSaid=true;
	private List<Long> longs = new ArrayList<Long>();

	public ThreadedGARun(String propertiesFilename) {
		super(propertiesFilename);
	}

	public void run() throws Exception {
		
		startTime=System.currentTimeMillis();

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
		System.out.println("Average time to 1M evals: "+(result/longs.size()));
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
/*
 * *********************************************************************
 * 
 * 								START OF MAIN FUNCTION
 * 
 * *********************************************************************
 */	
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
		
		//************************************START OF MAIN LOOP***********************************
		
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

		//************************************END OF MAIN LOOP***********************************

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
	
	/*
	 * *********************************************************************
	 * 
	 * 								END OF MAIN FUNCTION
	 * 
	 * *********************************************************************
	 */	

	private void fillAllLocations() {
		for (Location location : world) {
			location.setIndividuals(popManager.generatePopulation());
		}
	}

	private void processAllLocations() {
		if(GlobalProblem.getEvaluator().getNumEvaluations()>1000000 && haventSaid){
			haventSaid=false;
			longs.add((System.currentTimeMillis()-startTime));
		}
		
		
		updateDiversityCounts();
		if (BooleanParameter.VIRAL_CLAUSES.getValue()) {
		    viralClauseCounter.updateViralClauses(world);
		}
		
		performMigration();
		addFromPendingIndividuals();
		threadByLocations();
		while(!(forkPool.getActiveThreadCount()==0)){
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
	
	/*private void performMigration(){
		ForkMigrator fm = new ForkMigrator(locations, world);
		forkPool.invoke(fm);
	}*/
	
	private void performMigration() {
	    double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
	    int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();

	    if (migrationProbability <= 0 || migrationDistance <= 0)
	        return;
	    
	    //TODO It may be possible to reduce the code duplication here, but we can't right now until we refactor
	    //    the way world.getNeighborhood works (right now it needs to take a Vector or an Integer)
	    //if (StringParameter.WORLD_TYPE.getValue().contains("GridWorld")) {
	        //GridWorld gridWorld = (GridWorld) world;
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
//
//	    } else {
//	        GraphWorld graphWorld = (GraphWorld) world;
//	        for (Location<?> location : graphWorld) {
//	            List<Individual> locationIndividuals = location.getIndividuals();
//	            List<Individual> individualsToRemove = new ArrayList<Individual>();
//
//	            for (Individual i : locationIndividuals) {
//	                if (SharedPRNG.instance().nextDouble() < migrationProbability) {
//	                    individualsToRemove.add(i);
//	                    List<Integer> neighborhood = graphWorld.getNeighborhood((Integer) location.getPosition(), migrationDistance);
//	                    neighborhood.remove(location);
//	                    Integer newPosition;
//	                    try {
//	                        newPosition = neighborhood.get(SharedPRNG.instance().nextInt(neighborhood.size()));
//	                        Location<Integer> newLocation = graphWorld.getLocation(newPosition);
//	                        newLocation.addToPendingIndividuals(i);
//	                    } catch (IndexOutOfBoundsException e) {
//	                        throw new MigrationInWorldOfSizeOneException(e);
//	                    }
//	                }
//	            }
//	            location.removeAll(individualsToRemove);
//	        }
//	    }
	}

	private void updateDiversityCounts() {
		//DiversityCalculator.reset();
		//ForkDiversityCounter fdc = new ForkDiversityCounter(locations);
		//DiversityCalculator.addCounter((FrequencyCounter<Individual>)forkPool.invoke(fdc));
		DiversityCalculator.reset();
		ForkedDiversityCounter fdc = new ForkedDiversityCounter(locations);
		forkPool.execute(fdc);
	}
	

	private void setFromPendingIndividuals() {

		for (Location location : world) {
			location.setFromPendingIndividuals();
		}
	}


	private void addFromPendingIndividuals() {

		for (Location location : world) {
			synchronized(location){
				location.addFromPendingIndividuals();
			}
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
