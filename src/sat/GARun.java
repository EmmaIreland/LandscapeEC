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

public class GARun {

    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;
    private CrossoverOperator crossoverOperator;
    
    private IndividualComparator comparator;
    
    private PopulationManager popManager;
    private SatInstance satInstance;
    private SatEvaluator satEvaluator;
    
    private World world;
    
    public void run() throws FileNotFoundException, IOException, SecurityException, IllegalArgumentException,
        ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        mutationOperator = getMutationOperator();
        selectionOperator = getSelectionOperator();
        crossoverOperator = getCrossoverOperator();
       
        SatParser satParser = new SatParser();
        satInstance = 
            satParser.parseInstance(new FileReader(new File(StringParameter.PROBLEM_FILE.getValue())));
        satEvaluator =  new SatEvaluator();
        
        comparator = new IndividualComparator(satInstance, satEvaluator);
        
        popManager = new PopulationManager();
        
        world = new World(IntArrayParameter.WORLD_DIMENSIONS.getValue(), BooleanParameter.TOROIDAL.getValue());
        
        int numRuns = IntParameter.NUM_RUNS.getValue();
        
        int successes = 0;
        for(int i=0; i<numRuns; i++) {
            System.out.println("\nRUN " + (i+1) + "\n");
            if(runGeneration()) {
                successes++;
            }
        }
        
        System.out.println(successes + "/" + numRuns + " runs successful");
    }

    private boolean runGeneration() {
        List<Individual> population = popManager.generatePopulation(satInstance);
        world.getStartingLocation().setIndividuals(population);
        
        for(int i=0; i<IntParameter.NUM_GENERATIONS.getValue(); i++) {
        	
        	population = new ArrayList<Individual>();
        	
        	processAllLocations(population);
        	
            Individual bestIndividual = Collections.max(population, comparator);
            System.out.println("Generation " + (i+1));
            System.out.println("   Best individual: " + bestIndividual);
            double bestFitness = satEvaluator.evaluate(satInstance, bestIndividual);
            System.out.println("   Best fitness: " + bestFitness);
            if(bestFitness == 1.0) {
                System.out.println("SUCCESS");
                return true;
            }
        }
        
        System.out.println("FAILURE");
        return false;
    }

	private void processAllLocations(List<Individual> population) {
		/*TODO  There is a potential problem with this code:
		 * If we add in new children individuals as we iterate through all the locations in the world,
		 * there is a possibility that we will run the process on the children of the current
		 * generation, instead of processing them when we are suppoed to.
		 * 
		 * Perhaps we will want to add the children to the world AFTER the current generation has
		 * finished processing?
		 */
		
		for(Position position: world) {
			List<Individual> locationIndividuals = world.getLocation(position).getIndividuals();
			
			if(!locationIndividuals.isEmpty()) {
				List<Individual> newPopulation = new ArrayList<Individual>();

				List<Individual> elite = popManager.getElite(locationIndividuals, DoubleParameter.ELITE_PROPORTION.getValue(), comparator);

				newPopulation.addAll(elite);

				List<Individual> crossoverPop = popManager.crossover(locationIndividuals, selectionOperator, crossoverOperator, comparator);

				newPopulation.addAll(popManager.mutatePopulation(crossoverPop, mutationOperator));

				world.getLocation(position).setIndividuals(newPopulation);
				
				population.addAll(newPopulation);
			}
		}
	}
    
    private MutationOperator getMutationOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException,
        IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.MUTATION_OPERATOR.getValue();
        
        Class<MutationOperator> eval = (Class<MutationOperator>) Class.forName(evaluatorName);
        Constructor<MutationOperator> factory = eval.getConstructor();
        MutationOperator instance = factory.newInstance();
        return instance;  
    }
    
    private SelectionOperator getSelectionOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException,
        IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.SELECTION_OPERATOR.getValue();
        
        Class<SelectionOperator> eval = (Class<SelectionOperator>) Class.forName(evaluatorName);
        Constructor<SelectionOperator> factory = eval.getConstructor();
        SelectionOperator instance = factory.newInstance();
        return instance;  
    }
    
    private CrossoverOperator getCrossoverOperator() throws ClassNotFoundException, SecurityException, NoSuchMethodException,
        IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String evaluatorName = StringParameter.CROSSOVER_OPERATOR.getValue();
        
        Class<CrossoverOperator> eval = (Class<CrossoverOperator>) Class.forName(evaluatorName);
        Constructor<CrossoverOperator> factory = eval.getConstructor();
        CrossoverOperator instance = factory.newInstance();
        return instance;  
    }

}
