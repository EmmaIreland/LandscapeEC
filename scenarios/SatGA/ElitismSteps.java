package SatGA;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.PopulationManager;
import landscapeEC.sat.SatInstance;
import landscapeEC.sat.SatParser;

import org.jbehave.scenario.steps.Steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;


public class ElitismSteps extends Steps {
    private PopulationManager populationManager;
    private double eliteProportion;
    private SatInstance satInstance;
    private List<Individual> individuals = new ArrayList<Individual>();

    @Given("a population manager")
    public void constructPopulationManager() {
        populationManager = new PopulationManager();
    }

    @Given("a SAT evaluator")
    public void constructSATEvaluator() {
    }

    @When("I have a population of $population")
    public void getPopulation(String population) {
        String[] lines = population.split("\n");
        for (String line : lines) {
            Individual individual = new Individual(line);
            individuals.add(individual);
        }
    }

    @When("I have an elite proportion of $eliteProportion")
    public void getEliteProportion(double proportion) {
        eliteProportion = proportion;
    }

    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser = new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        satInstance = satParser.parseInstance(stringReader);
    }

    @Then("the elite set should be $eliteSet")
    public void getEliteSet(String eliteSet) {
        List<Individual> expectedElite = new ArrayList<Individual>();

        String[] lines = eliteSet.split("\n");
        for (String line : lines) {
            Individual individual = new Individual(line);
            expectedElite.add(individual);
        }
        
        for(Individual individual : individuals) {
            individual.setGlobalFitness(satInstance);
        }

        IndividualComparator comparator = new IndividualComparator(satInstance);
        List<Individual> actualElite = populationManager.getElite(individuals, eliteProportion, comparator);

        assertEquals("Expected elite and actual elite are different sizes", expectedElite.size(), actualElite.size());

        for (Individual individual : expectedElite) {
            assertTrue("Expected elite has an element not in actual elite", contains(individual, actualElite));
        }
        for (Individual individual : actualElite) {
            assertTrue("Actual elite has an element not in expected elite", contains(individual, actualElite));
        }
    }

    private boolean contains(Individual target, List<Individual> actualElite) {
        for (Individual individual : actualElite) {
            if (Arrays.equals(individual.getBits(), target.getBits())) {
                return true;
            }
        }

        return false;
    }
}
