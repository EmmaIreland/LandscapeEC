package SatGA;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

import org.jbehave.scenario.steps.Steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.Named;

import sat.ClauseList;
import sat.Individual;
import sat.SatEvaluator;
import sat.SatInstance;
import sat.SatParser;

public class EvaluationSteps extends Steps {
    private SatEvaluator satEvaluator;
    private String bitString;
    private SatInstance satInstance;

    @Given("a sat evaluator")
    public void constructParser() {
        satEvaluator = new SatEvaluator();
    }
    
    @When("I have a bitstring of <bitstring>")
    public void getBitString(@Named("bitstring") String bitString) {
        this.bitString=bitString;
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        this.satInstance = satParser.parseInstance(stringReader);        
    }
    
    @Then("the fitness should be <fitness>")
    public void confirmEvaluation(@Named("fitness") double expectedFitness) {
        double actualFitness = satEvaluator.evaluate(satInstance, new Individual(bitString));
        
        assertEquals(expectedFitness, actualFitness);
    }

}
