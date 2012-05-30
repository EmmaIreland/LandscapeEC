package landscapeEC.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.IndividualFactory;
import landscapeEC.problem.sat.operators.CrossoverOperator;
import landscapeEC.problem.sat.operators.MutationOperator;
import landscapeEC.problem.sat.operators.SelectionOperator;



public class PopulationManager {
    

    public List<Individual> getElite(List<Individual> startingIndividuals, double eliteProportion) {
    	List<Individual> individuals = new ArrayList<Individual>(startingIndividuals);
        Collections.sort(individuals, IndividualComparator.getComparator());
        
        List<Individual> result = new ArrayList<Individual>();
        int populationSize = individuals.size();
		int eliteSize = getEliteSize(populationSize, eliteProportion);
        for(int i=0; i<eliteSize; i++) {
            result.add(individuals.get(populationSize-1-i));
        }
        return result;
    }

    private int getEliteSize(int populationSize, double eliteProportion) {
        return (int)Math.ceil(populationSize*eliteProportion);
    }

    public List<Individual> generatePopulation() {
        List<Individual> population = new ArrayList<Individual>();
        for(int i=0;i<IntParameter.CARRYING_CAPACITY.getValue();i++){
            population.add(IndividualFactory.getInstance(GlobalProblem.getProblem().getBitStringSize()));
        }
        return population;
    }

    public List<Individual> mutatePopulation(List<Individual> population, MutationOperator mutationOperator, Object... parameters) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        for(Individual ind:population){
            Individual newIndividual = mutationOperator.mutate(ind, parameters);
            
            newPopulation.add(newIndividual);
        }
        return newPopulation;
        
    }

    public List<Individual> crossover(List<Individual> population, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        int numChildren = getNumberOfNeededChildren(population.size());
        
        for(int i=0; i<numChildren; i++) {
            List<Individual> parents = selectionOperator.selectParents(population, IndividualComparator.getComparator());
            Individual individual = crossoverOperator.crossover(parents);
            newPopulation.add(individual);
        }
                
        return newPopulation;
    }
    
    public List<Individual> forkedCrossover(List<Individual> population, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, int numChildren) {
        List<Individual> newPopulation = new ArrayList<Individual>();
        
        for(int i=0; i<numChildren; i++) {
            List<Individual> parents = selectionOperator.selectParents(population, IndividualComparator.getComparator());
            Individual individual = crossoverOperator.crossover(parents);
            newPopulation.add(individual);
        }
                
        return newPopulation;
    }
    
    public int getNumberOfNeededChildren(int populationSize) {
        int carryingCapacity = IntParameter.CARRYING_CAPACITY.getValue();
        int eliteSize = getEliteSize(populationSize, DoubleParameter.ELITE_PROPORTION.getValue());
        
        int maxChildren = (int) (populationSize*DoubleParameter.REPRODUCTION_RATE.getValue());
        return Math.min(carryingCapacity-eliteSize, maxChildren);
    }

}
