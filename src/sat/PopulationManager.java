package sat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import parameters.DoubleParameter;
import parameters.IntParameter;


public class PopulationManager {
    
    private IndividualFactory factory = new IndividualFactory();
    

    public List<Individual> getElite(List<Individual> individuals, double eliteProportion,
            SatInstance satInstance, SatEvaluator satEvaluator) {
        IndividualComparator comparator = new IndividualComparator(satInstance, satEvaluator);
        Collections.sort(individuals, comparator);
        
        List<Individual> result = new ArrayList<Individual>();
        int eliteSize = (int)Math.ceil(individuals.size()*eliteProportion);
        for(int i=0; i<eliteSize; i++) {
            result.add(individuals.get(i));
        }
        return result;
    }

    public List<Individual> generatePopulation() {
        List<Individual> population = new ArrayList<Individual>();
        for(int i=0;i<IntParameter.POOL_SIZE.getValue();i++){
            population.add(factory.getInstance());
        }
        return population;
    }

    public List<Individual> mutatePopulation(List<Individual> population, MutationOperator mutationOperator) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        for(Individual ind:population){
            Individual newIndividual = mutationOperator.mutate(ind);
            
            newPopulation.add(newIndividual);
        }
        return newPopulation;
        
    }

    public List<Individual> crossover(List<Individual> population,
            SelectionOperator selectionOperator, CrossoverOperator crossoverOperator) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        for(int i=0; i<getNumberOfNeededChildren(); i++) {
            List<Individual> parents = selectionOperator.selectParents(population);
            Individual individual = crossoverOperator.crossover(parents);
            newPopulation.add(individual);
        }
        
        return newPopulation;
    }
    
    public int getNumberOfNeededChildren() {
        int poolSize = IntParameter.POOL_SIZE.getValue();
        double eliteProportion = DoubleParameter.ELITE_PROPORTION.getValue();
        int eliteSize = (int)Math.ceil(poolSize*eliteProportion);
        return poolSize-eliteSize;
    }

}
