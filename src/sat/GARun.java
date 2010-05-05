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
import util.SharedPRNG;

public class GARun {

    private MutationOperator mutationOperator = new MutationOperator();
    private SelectionOperator selectionOperator = new SelectionOperator();
    private CrossoverOperator crossoverOperator = new CrossoverOperator();
    
    public void run() throws FileNotFoundException, IOException {
        PopulationManager popManager = new PopulationManager();
       
        SatParser satParser = new SatParser();
        SatInstance satInstance = 
            satParser.parseInstance(new FileReader(new File(StringParameter.PROBLEM_FILE.getValue())));
        SatEvaluator satEvaluator =  new SatEvaluator();
        
        List<Individual> population = popManager.generatePopulation();
        
        for(int i=0; i<IntParameter.NUM_GENERATIONS.getValue(); i++) {
            List<Individual> newPopulation = new ArrayList<Individual>();
            
            List<Individual> elite = popManager.getElite(population, DoubleParameter.ELITE_PROPORTION.getValue(), satInstance, satEvaluator);
            
            newPopulation.addAll(elite);
            
            List<Individual> crossoverPop = popManager.crossover(population, selectionOperator, crossoverOperator);
            
            newPopulation.addAll(popManager.mutatePopulation(crossoverPop, mutationOperator));
            
            population = newPopulation;
            
            Individual bestIndividual = elite.get(elite.size()-1);
            System.out.println("Some individual: " + bestIndividual);
            System.out.println("   Fitness: " + satEvaluator.evaluate(satInstance, bestIndividual));
            
        }
        
    }

}
