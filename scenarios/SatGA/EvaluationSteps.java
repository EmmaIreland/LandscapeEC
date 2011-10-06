package SatGA;
import java.io.IOException;
import java.io.StringReader;

import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import static junit.framework.Assert.*;

import org.jbehave.scenario.steps.Steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.Named;


public class EvaluationSteps extends Steps {
    private String bitString;
    private SatInstance satInstance;

    @Given("a sat evaluator")
    public void constructParser() {
    }
    
    @When("I have a bitstring of <bitstring>")
    public void getBitString(@Named("bitstring") String string) {
        bitString=string;
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        this.satInstance = satParser.parseInstance(stringReader);        
    }
    
    @Then("the fitness should be <fitness>")
    public void confirmEvaluation(@Named("fitness") double expectedFitness) {
        double actualFitness = SatEvaluator.evaluate(satInstance, new Individual(bitString));
        
        assertEquals(expectedFitness, actualFitness);
    }

}
