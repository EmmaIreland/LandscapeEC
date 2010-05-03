package SatGA;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jbehave.scenario.steps.Steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.Named;

import sat.Individual;
import sat.PopulationManager;
import sat.SatEvaluator;
import sat.SatInstance;
import sat.SatParser;

public class ElitismSteps extends Steps {
    private PopulationManager populationManager;
    private double eliteProportion;
    private SatInstance satInstance;
    private List<Individual> individuals = new ArrayList<Individual>();
    private SatEvaluator satEvaluator;

    @Given("a population manager")
    public void constructPopulationManager() {
        populationManager = new PopulationManager();
    }
    
    @Given("a SAT evaluator")
    public void constructSATEvaluator() {
        satEvaluator = new SatEvaluator();
    }
    
    @When("I have a population of $population")
    public void getPopulation(String population) {
        String[] lines = population.split("\n");
        for(String line:lines) {
            Individual individual = new Individual(line);
            individuals.add(individual);
        }
    }
    
    @When("I have an elite proportion of $eliteProportion")
    public void getEliteProportion(double eliteProportion) {
        this.eliteProportion = eliteProportion;
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        satInstance = satParser.parseInstance(stringReader);        
    }
    
    @Then("the elite set should be $eliteSet")
    public void getEliteSet(String eliteSet) {
        List<Individual> expectedElite = new ArrayList<Individual>();

        String[] lines = eliteSet.split("\n");
        for(String line:lines) {
            Individual individual = new Individual(line);
            expectedElite.add(individual);
        }
        
        List<Individual> actualElite = populationManager.getElite(individuals, eliteProportion, satInstance, satEvaluator);

        assertEquals("Expected elite and actual elite are different sizes", expectedElite.size(), actualElite.size());
        
        System.out.println("Expected elite: \n" + expectedElite);
        System.out.println("Actual elite: \n" + actualElite);
        
        for(Individual individual:expectedElite) {
            assertTrue("Expected elite has an element not in actual elite", actualElite.contains(individual));
        }
        for(Individual individual:actualElite) {
            assertTrue("Actual elite has an element not in expected elite", expectedElite.contains(individual));
        }
    }

}
