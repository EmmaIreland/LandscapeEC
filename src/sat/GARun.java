package sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import parameters.DoubleParameter;
import parameters.IntParameter;
import parameters.StringParameter;
import sat.operators.CrossoverOperator;
import sat.operators.MutationOperator;
import sat.operators.SelectionOperator;

public class GARun {

    private MutationOperator mutationOperator;
    private SelectionOperator selectionOperator;
    private CrossoverOperator crossoverOperator;
    
    private PopulationManager popManager;
    private SatInstance satInstance;
    private SatEvaluator satEvaluator;
    
    public void run() throws FileNotFoundException, IOException, SecurityException, IllegalArgumentException,
        ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        mutationOperator = getMutationOperator();
        selectionOperator = getSelectionOperator();
        crossoverOperator = getCrossoverOperator();
        
        popManager = new PopulationManager();
       
        SatParser satParser = new SatParser();
        satInstance = 
            satParser.parseInstance(new FileReader(new File(StringParameter.PROBLEM_FILE.getValue())));
        satEvaluator =  new SatEvaluator();
        
        for(int i=0; i<IntParameter.NUM_RUNS.getValue(); i++) {
            System.out.println("\nRUN " + (i+1) + "\n");
            runGeneration();
        }        
    }

    private void runGeneration() {
        List<Individual> population = popManager.generatePopulation();
        
        for(int i=0; i<IntParameter.NUM_GENERATIONS.getValue(); i++) {
            List<Individual> newPopulation = new ArrayList<Individual>();
            
            List<Individual> elite = popManager.getElite(population, DoubleParameter.ELITE_PROPORTION.getValue(), satInstance, satEvaluator);
            
            newPopulation.addAll(elite);
            
            List<Individual> crossoverPop = popManager.crossover(population, selectionOperator, crossoverOperator);
            
            newPopulation.addAll(popManager.mutatePopulation(crossoverPop, mutationOperator));
            
            population = newPopulation;
            
            Individual bestIndividual = elite.get(elite.size()-1);
            System.out.println("Generation " + (i+1));
            System.out.println("   Best individual: " + bestIndividual);
            double bestFitness = satEvaluator.evaluate(satInstance, bestIndividual);
            System.out.println("   Best fitness: " + bestFitness);
            if(bestFitness == 1.0) {
                System.out.println("SUCCESS");
                return;
            }
        }
        
        System.out.println("FAILURE");
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
