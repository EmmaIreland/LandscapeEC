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

import observers.MapVisualizer;
import observers.Observer;

import locality.Location;
import locality.Position;
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
    private SatEvaluator satEvaluator;

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
        satEvaluator = new SatEvaluator();

        comparator = new IndividualComparator(satInstance, satEvaluator);

        popManager = new PopulationManager();

        world = new World(IntArrayParameter.WORLD_DIMENSIONS.getValue(),
                BooleanParameter.TOROIDAL.getValue());

        int numRuns = IntParameter.NUM_RUNS.getValue();
        
        setupObservers();

        int successes = 0;
        for (int i = 0; i < numRuns; i++) {
            System.out.println("\nRUN " + (i + 1) + "\n");
            if (runGeneration()) {
                successes++;
            }
        }

        System.out.println(successes + "/" + numRuns + " runs successful");
    }

    private void setupObservers() {
        // TODO generalize this to load from parameters (not just use a hard-coded visualizer)
        observers.add(new MapVisualizer(world));
    }

    private boolean runGeneration() {
        List<Individual> population = popManager.generatePopulation(satInstance);
        for(Position p:world) {
            world.getLocation(p).setIndividuals(new ArrayList<Individual>());
        }
        world.getStartingLocation().setIndividuals(population);

        for (int i = 0; i < IntParameter.NUM_GENERATIONS.getValue(); i++) {
            processAllLocations();
            
            for(Observer o:observers) {
                o.generationData(world, satEvaluator, satInstance);
            }

            Individual bestIndividual = findBestIndividual();
            System.out.println("Generation " + (i + 1));
            System.out.println("   Best individual: " + bestIndividual);
            double bestFitness = satEvaluator.evaluate(satInstance,
                    bestIndividual);
            System.out.println("   Best fitness: " + bestFitness);
            if (bestFitness == 1.0) {
                System.out.println("SUCCESS");
                return true;
            }
        }

        System.out.println("FAILURE");
        return false;
    }
    
    private Individual findBestIndividual() {
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for(Position p:world) {
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
        performElitism();
        performReproduction();
        setFromPendingIndividuals();
    }
    
    private void performMigration() {
        double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
        int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();
        
        if(migrationProbability <= 0 || migrationDistance <= 0) return;
        
        for (Position position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);
            List<Individual> individualsToRemove = new ArrayList<Individual>();
            
            for(Individual i:locationIndividuals) {
                if(SharedPRNG.instance().nextDouble() < migrationProbability) {
                    individualsToRemove.add(i);
                    List<Position> neighborhood = world.getNeighborhood(position, migrationDistance);
                    neighborhood.remove(position);
                    Position newPosition;
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
        for (Position position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            if (!locationIndividuals.isEmpty()) {
                List<Individual> elite = popManager.getElite(
                        locationIndividuals, DoubleParameter.ELITE_PROPORTION.getValue(), comparator);
                
                world.getLocation(position).addToPendingIndividuals(elite);
            }
        }
    }
    
    private void performReproduction() {
        for (Position position : world) {
            List<Individual> locationIndividuals = world.getIndividualsAt(position);

            if (locationIndividuals.size() >= IntParameter.TOURNAMENT_SIZE.getValue()) {
                List<Individual> crossoverPop = popManager.crossover(
                        locationIndividuals, selectionOperator,
                        crossoverOperator, comparator);

                List<Individual> mutatedPopulation = popManager.mutatePopulation(crossoverPop, mutationOperator);

                world.getLocation(position).addToPendingIndividuals(mutatedPopulation);
            }
        }
    }
    
    private void setFromPendingIndividuals() {
        for (Position position : world) {
            world.getLocation(position).setFromPendingIndividuals();
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
