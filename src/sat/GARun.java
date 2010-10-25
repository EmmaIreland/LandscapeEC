package sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import observers.Observer;
import observers.vis.*;

import locality.Vector;
import locality.World;

import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.IntArrayParameter;
import parameters.IntParameter;
import parameters.StringParameter;
import sat.operators.CrossoverOperator;
import sat.operators.MutationOperator;
import sat.operators.SelectionOperator;
import util.SharedPRNG;

public class GARun {

    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;
    private CrossoverOperator crossoverOperator;

    private IndividualComparator comparator;

    private PopulationManager popManager;
    private SatInstance satInstance;

    private World world;
    
    private List<Observer> observers = new ArrayList<Observer>();

    public void run() throws FileNotFoundException, IOException,
            SecurityException, IllegalArgumentException,
            ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        mutationOperator = getMutationOperator();
        selectionOperator = getSelectionOperator();
        crossoverOperator = getCrossoverOperator();

        SatParser satParser = new SatParser();
        satInstance = satParser.parseInstance(new FileReader(new File(
                StringParameter.PROBLEM_FILE.getValue())));

        comparator = new IndividualComparator(satInstance);

        popManager = new PopulationManager();

        world = new World(new Vector(IntArrayParameter.WORLD_DIMENSIONS.getValue()),
                BooleanParameter.TOROIDAL.getValue(), satInstance);

        int numRuns = IntParameter.NUM_RUNS.getValue();
        
        setupObservers();

        int successes = 0;
        for (int i = 0; i < numRuns; i++) {
            System.out.println("\nRUN " + (i + 1) + "\n");
            if (runGeneration()) {
                successes++;
            }
            SatEvaluator.resetEvaluationsCounter();
        }

        System.out.println(successes + "/" + numRuns + " runs successful");
    }

    private void setupObservers() {
        // TODO generalize this to load from parameters (not just use a hard-coded visualizer)
        observers.add(new Console());
        observers.add(new MapVisualizer(world));
        observers.add(new DataDisplay());
    }

    private boolean runGeneration() {
        List<Individual> population = popManager.generatePopulation(satInstance);
        for(Vector p:world) {
            world.getLocation(p).setIndividuals(new ArrayList<Individual>());
        }
        world.getStartingLocation().setIndividuals(population);

        int i = 0;
        while (SatEvaluator.getNumEvaluations() < IntParameter.NUM_EVALS_TO_DO.getValue()) {
            processAllLocations();
            
            for(Observer o:observers) {
                o.generationData(i, world, satInstance);
            }

            Individual bestIndividual = findBestIndividual();
            System.out.println("Generation " + (i + 1));
            //System.out.println("   Best individual: " + bestIndividual);
            double bestFitness = SatEvaluator.evaluate(satInstance, bestIndividual);
            //System.out.println("   Best fitness: " + bestFitness);
            if (bestFitness == 1.0) {
                System.out.println("SUCCESS");
                return true;
            }
			
            i++;
        }

        System.out.println("FAILURE");
        
        return false;
    }
    
    private Individual findBestIndividual() {
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for(Vector p:world) {
            if(world.getLocation(p).getNumIndividuals() > 0) {
                bestFromCells.add(Collections.max(world.getIndividualsAt(p), comparator));
            }
        }
        if(bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        return Collections.max(bestFromCells, comparator);
    }

    private void processAllLocations() {
        performMigration();
        addFromPendingIndividuals();
        
        performDraconianReaper();
        setFromPendingIndividuals();
        
        performElitism();
        performReproduction();
        setFromPendingIndividuals();
    }

	private void performDraconianReaper() {
		int numKilled = 0;
		
		for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);
            
            for(Individual individual : locationIndividuals) {
            	SatInstance locationInstance = world.getLocation(position).getComparator().getInstance();
				double fitness = SatEvaluator.evaluate(locationInstance, individual);
            	int numClauses = locationInstance.getNumClauses();
				int correctClauses = (int) Math.round(fitness * numClauses);
            	if (numClauses == correctClauses) {
            		world.getLocation(position).addToPendingIndividuals(individual);
            	} else {
            		numKilled++;
            	}
            }
        }
		
		System.out.println("Killed: " + numKilled);
	}

	private void performMigration() {
		int numMigrated = 0;
		
        double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
        int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();
        
        if(migrationProbability <= 0 || migrationDistance <= 0) return;
        
        for (Vector position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);
            List<Individual> individualsToRemove = new ArrayList<Individual>();
            
            for(Individual i:locationIndividuals) {
                if(SharedPRNG.instance().nextDouble() < migrationProbability) {
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
                    
                    numMigrated++;
                }
            }
            world.getLocation(position).removeAll(individualsToRemove);
        }
        
        System.out.println("Moved : " + numMigrated);
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
            }
        }
    }
    
    private void setFromPendingIndividuals() {
        for (Vector position : world) {
            world.getLocation(position).setFromPendingIndividuals();
            //assert world.getLocation(position).getNumIndividuals() <= IntParameter.CARRYING_CAPACITY.getValue();
        }
    }
    
    private void addFromPendingIndividuals() {
        for (Vector position : world) {
            world.getLocation(position).addFromPendingIndividuals();
        }
    }

    private MutationOperator getMutationOperator()
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        String evaluatorName = StringParameter.MUTATION_OPERATOR.getValue();

        Class<MutationOperator> eval = (Class<MutationOperator>) Class.forName(evaluatorName);
        Constructor<MutationOperator> factory = eval.getConstructor();
        MutationOperator instance = factory.newInstance();
        return instance;
    }

    private SelectionOperator getSelectionOperator()
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        String evaluatorName = StringParameter.SELECTION_OPERATOR.getValue();

        Class<SelectionOperator> eval = (Class<SelectionOperator>) Class.forName(evaluatorName);
        Constructor<SelectionOperator> factory = eval.getConstructor();
        SelectionOperator instance = factory.newInstance();
        return instance;
    }

    private CrossoverOperator getCrossoverOperator()
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        String evaluatorName = StringParameter.CROSSOVER_OPERATOR.getValue();

        Class<CrossoverOperator> eval = (Class<CrossoverOperator>) Class.forName(evaluatorName);
        Constructor<CrossoverOperator> factory = eval.getConstructor();
        CrossoverOperator instance = factory.newInstance();
        return instance;
    }
}
