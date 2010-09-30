package sat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import parameters.DoubleParameter;
import parameters.IntParameter;
import sat.operators.CrossoverOperator;
import sat.operators.MutationOperator;
import sat.operators.SelectionOperator;


public class PopulationManager {
    

    public List<Individual> getElite(List<Individual> individuals, double eliteProportion, IndividualComparator comparator) {
        Collections.sort(individuals, comparator);
        
        List<Individual> result = new ArrayList<Individual>();
        int eliteSize = (int)Math.ceil(individuals.size()*eliteProportion);
        for(int i=0; i<eliteSize; i++) {
            result.add(individuals.get(individuals.size()-1-i));
        }
        return result;
    }

    public List<Individual> generatePopulation(SatInstance satInstance) {
        List<Individual> population = new ArrayList<Individual>();
        for(int i=0;i<IntParameter.CARRYING_CAPACITY.getValue();i++){
            population.add(IndividualFactory.getInstance(satInstance.getNumVariables()));
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
            SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, IndividualComparator comparator) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        int numChildren = getNumberOfNeededChildren();
        
        for(int i=0; i<numChildren; i++) {
            List<Individual> parents = selectionOperator.selectParents(population, comparator);
            Individual individual = crossoverOperator.crossover(parents);
            newPopulation.add(individual);
        }
                
        return newPopulation;
    }
    
    public int getNumberOfNeededChildren() {
        int carryingCapacity = IntParameter.CARRYING_CAPACITY.getValue();
        double eliteProportion = DoubleParameter.ELITE_PROPORTION.getValue();
        int eliteSize = (int)Math.ceil(carryingCapacity*eliteProportion);
        return carryingCapacity-eliteSize;
    }

}
