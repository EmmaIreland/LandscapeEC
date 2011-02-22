package landscapeEC.sat;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleArrayParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.sat.operators.CrossoverOperator;
import landscapeEC.sat.operators.MutationOperator;
import landscapeEC.sat.operators.SelectionOperator;
import landscapeEC.util.SharedPRNG;

public class GARun {

    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;
    private CrossoverOperator crossoverOperator;

    private IndividualComparator comparator;

    private PopulationManager popManager;
    private SatInstance satInstance;

    private World world;

    private List<Observer> observers = new ArrayList<Observer>();
    private int successes;
    private String propertiesFilename;
    private FileWriter writer;
    private double bestOverallFitness;
    
    private double[] intervalFitnesses;

    public GARun(String propertiesFilename) {
        this.propertiesFilename = propertiesFilename;
    }

    public void run() throws Exception {
        mutationOperator = getMutationOperator();
        selectionOperator = getSelectionOperator();
        crossoverOperator = getCrossoverOperator();

        intervalFitnesses = new double[getReportingIntervals().length];   
        
        SatParser satParser = new SatParser();
        satInstance = satParser.parseInstance(new FileReader(new File(StringParameter.PROBLEM_FILE.getValue())));

        comparator = new IndividualComparator(satInstance);

        popManager = new PopulationManager();

        int numRuns = IntParameter.NUM_RUNS.getValue();

        setupObservers();
        
        generateRFile(successes);

        successes = 0;
        for (int i = 0; i < numRuns; i++) {
            SharedPRNG.updateGenerator();
            
            System.out.println("\nRUN " + (i + 1) + "\n");

            Arrays.fill(intervalFitnesses, Double.NaN);
            
            try {
                if (runGenerations(i)) {
                    addRunToRFile(true, SatEvaluator.getNumEvaluations(), 1.0);
                    successes++;
                } else {
                    addRunToRFile(false, IntParameter.NUM_EVALS_TO_DO.getValue(), bestOverallFitness);
                }
            } catch (EmptyWorldException e) {
                System.err.println("All individuals died!");
            }

            SatEvaluator.resetEvaluationsCounter();
        }

        System.out.println(successes + "/" + numRuns + " runs successful");
        
        closeRFile();
    }

    private void addRunToRFile(boolean success, int completedEvaluations, double bestFitness) throws IOException {
        Set<String> propertyNames = GlobalParameters.getParameterNames();
        for(String name:propertyNames) {
            if(GlobalParameters.isSet(name)) {
                writer.write(GlobalParameters.getStringValue(name).replaceAll(" ", "") + " ");
            }
        }
        
        for(int i = 0; i < intervalFitnesses.length; i++) {
        	double fitness = intervalFitnesses[i];
        	if (Double.isNaN(fitness)) {
        		fitness = 1.0;
        	}
			writer.write(fitness+ " ");
        }
        
        writer.write(success+" " + completedEvaluations + " " + bestFitness + "\n");
    }

    private void generateRFile(int successes) throws IOException {
        writer = new FileWriter(new File(propertiesFilename+".R"));
        
        Set<String> propertyNames = GlobalParameters.getParameterNames();
        for(String name:propertyNames) {
            if(GlobalParameters.isSet(name)) {
                writer.write(name + " ");
            }
        }
        for(int i = 0; i < getReportingIntervals().length; i++) {
        	writer.write("INTERVAL_"+getReportingIntervals()[i]+ " ");
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

        String observerNames[] = StringParameter.OBSERVERS.getValue().split(",");

        for (String observerName : observerNames) {
            Class<Observer> obs = (Class<Observer>) Class.forName(observerName);
            Constructor<Observer> cons = obs.getConstructor();
            Observer instance = (Observer) cons.newInstance();
            observers.add(instance);
        }
    }

    private boolean runGenerations(int currentRun) throws Exception {
        List<Individual> population = popManager.generatePopulation(satInstance);

        world = new World(new Vector(IntArrayParameter.WORLD_DIMENSIONS.getValue()), BooleanParameter.TOROIDAL.getValue(), satInstance);

        world.clear();

        SeedType seedType = SeedType.valueOf(StringParameter.STARTING_POPULATION.getValue());

        switch (seedType) {
        case ORIGIN:
            world.getOrigin().setIndividuals(population);
            break;
        case EVERYWHERE:
            fillLocations(world);
            break;
        case CORNERS:
            Vector topLeft = Vector.origin(world.getDimensions().size());
            Vector bottomRight = world.getDimensions().minusToAll(1);
            Vector topRight = Vector.getCorner(bottomRight, topLeft);
            Vector bottomLeft = Vector.getCorner(topLeft, bottomRight);
            List<Vector> vectors = Arrays.asList(topLeft, bottomLeft, topRight, bottomRight);
            fillLocations(vectors);
            break;
        }

        int i = 0;
        bestOverallFitness = 0.0;
        Individual bestIndividual = null;
        while (SatEvaluator.getNumEvaluations() < IntParameter.NUM_EVALS_TO_DO.getValue()) {
            processAllLocations();

            for (Observer o : observers) {
                o.generationData(i, world, satInstance, successes);
            }

            bestIndividual = world.findBestIndividual(comparator);
            // System.out.println("Generation " + (i + 1));
            // System.out.println("   Best individual: " + bestIndividual);
            bestOverallFitness = SatEvaluator.evaluate(satInstance, bestIndividual);
            // System.out.println("   Best fitness: " + bestFitness);
            
            double[] reportingIntervals = getReportingIntervals();
            for (int j = 0; j < reportingIntervals.length; j++) {
            	if(SatEvaluator.getNumEvaluations() > reportingIntervals[j]*IntParameter.NUM_EVALS_TO_DO.getValue() && Double.isNaN(intervalFitnesses[j])) {
            		intervalFitnesses[j] = bestOverallFitness;
            		SnapShot.saveSnapShot(propertiesFilename + ".run" + currentRun + ".part" + j, world, satInstance);
            	}
            }
            
            if (bestOverallFitness == 1.0) {
                System.out.println("Best Fitness: " + bestOverallFitness);
                SatEvaluator.printUnsolvedClauses(satInstance, bestIndividual);
                System.out.println("SUCCESS");
                return true;
            }

            i++;
        }

        System.out.println("Best Fitness: " + bestOverallFitness);
        SatEvaluator.printUnsolvedClauses(satInstance, bestIndividual);
        System.out.println("FAILURE");

        return false;
    }

    private void fillLocations(Iterable<Vector> vectors) {
        for (Vector v : vectors) {
            Location l = world.getLocation(v);
            l.setIndividuals(popManager.generatePopulation(satInstance));
        }
    }

    private void processAllLocations() {
        updateFitnesses();

        performMigration();
        addFromPendingIndividuals();

        performDraconianReaper();
        setFromPendingIndividuals();

        performElitism();
        performReproduction();
        setFromPendingIndividuals();
    }

    private void updateFitnesses() {
        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            for (Individual individual : locationIndividuals) {
                double fitness = SatEvaluator.evaluate(comparator.getInstance(), individual);
                individual.setGlobalFitness(fitness);
            }
        }
    }

    private void performDraconianReaper() {

        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            for (Individual individual : locationIndividuals) {
                SatInstance locationInstance = world.getLocation(position).getComparator().getInstance();
                double fitness = SatEvaluator.evaluate(locationInstance, individual);
                int numClauses = locationInstance.getNumClauses();
                int correctClauses = (int) Math.round(fitness * numClauses);
                if (numClauses == correctClauses) {
                    world.getLocation(position).addToPendingIndividuals(individual);
                }
            }
        }
    }

    private void performMigration() {

        double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
        int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();

        if (migrationProbability <= 0 || migrationDistance <= 0)
            return;

        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);
            List<Individual> individualsToRemove = new ArrayList<Individual>();

            for (Individual i : locationIndividuals) {
                if (SharedPRNG.instance().nextDouble() < migrationProbability) {
                    individualsToRemove.add(i);
                    List<Vector> neighborhood = world.getNeighborhood(position, migrationDistance);
                    neighborhood.remove(position);
                    Vector newPosition;
                    try {
                        newPosition = neighborhood.get(SharedPRNG.instance().nextInt(neighborhood.size()));
                    } catch (IndexOutOfBoundsException e) {
                        throw new MigrationInWorldOfSizeOneException(e);
                    }
                    world.getLocation(newPosition).addToPendingIndividuals(i);
                }
            }
            world.getLocation(position).removeAll(individualsToRemove);
        }
    }

    private void performElitism() {
        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            if (!locationIndividuals.isEmpty()) {
                List<Individual> elite = popManager.getElite(locationIndividuals, DoubleParameter.ELITE_PROPORTION.getValue(), comparator);

                world.getLocation(position).addToPendingIndividuals(elite);
            }
        }
    }

    private void performReproduction() {
        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            if (locationIndividuals.size() >= IntParameter.TOURNAMENT_SIZE.getValue()) {
                List<Individual> crossoverPop = popManager.crossover(locationIndividuals, selectionOperator, crossoverOperator, comparator);

                List<Individual> mutatedPopulation = popManager.mutatePopulation(crossoverPop, mutationOperator);

                world.getLocation(position).addToPendingIndividuals(mutatedPopulation);
            } else if (BooleanParameter.PROMOTE_SMALL_POPULATIONS.getValue()) {
                List<Individual> copiedPopulation = new ArrayList<Individual>();
                for (Individual individual : locationIndividuals) {
                    copiedPopulation.add(individual);
                }

                List<Individual> mutatedPopulation = popManager.mutatePopulation(copiedPopulation, mutationOperator);

                world.getLocation(position).addToPendingIndividuals(mutatedPopulation);                
            }
        }
    }

    private void setFromPendingIndividuals() {
        for (Vector position : world) {
            world.getLocation(position).setFromPendingIndividuals();
            // assert world.getLocation(position).getNumIndividuals() <=
            // IntParameter.CARRYING_CAPACITY.getValue();
        }
    }

    private void addFromPendingIndividuals() {
        for (Vector position : world) {
            world.getLocation(position).addFromPendingIndividuals();
        }
    }

    private MutationOperator getMutationOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.MUTATION_OPERATOR.getValue();

        Class<MutationOperator> eval = (Class<MutationOperator>) Class.forName(evaluatorName);
        Constructor<MutationOperator> factory = eval.getConstructor();
        MutationOperator instance = factory.newInstance();
        return instance;
    }

    private SelectionOperator getSelectionOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.SELECTION_OPERATOR.getValue();

        Class<SelectionOperator> eval = (Class<SelectionOperator>) Class.forName(evaluatorName);
        Constructor<SelectionOperator> factory = eval.getConstructor();
        SelectionOperator instance = factory.newInstance();
        return instance;
    }

    private CrossoverOperator getCrossoverOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.CROSSOVER_OPERATOR.getValue();

        Class<CrossoverOperator> eval = (Class<CrossoverOperator>) Class.forName(evaluatorName);
        Constructor<CrossoverOperator> factory = eval.getConstructor();
        CrossoverOperator instance = factory.newInstance();
        return instance;
    }
}
