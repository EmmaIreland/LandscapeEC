package sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import parameters.DoubleParameter;
import parameters.IntParameter;
import parameters.StringParameter;

public class GARun {

    private MutationOperator mutationOperator = new PointMutation();
    private SelectionOperator selectionOperator = new RandomSelection();
    private CrossoverOperator crossoverOperator = new UniformCrossover();
    
    private PopulationManager popManager;
    private SatInstance satInstance;
    private SatEvaluator satEvaluator;
    
    public void run() throws FileNotFoundException, IOException {
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

}
