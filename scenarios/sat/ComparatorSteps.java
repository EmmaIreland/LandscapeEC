package sat;

import java.io.IOException;
import java.io.StringReader;

import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.IndividualComparator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import static junit.framework.Assert.*;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Named;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.steps.Steps;

public class ComparatorSteps extends Steps {
    private SatInstance satInstance;
    private Individual a;
    private Individual b;

    @Given("a SAT evaluator")
    public void constructSATEvaluator() { //
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        satInstance = satParser.parseInstance(stringReader);
        GlobalSatInstance.setInstance(satInstance);
    }
    
    @When("I have individuals: <individualA> and <individualB>")
    public void getIndividuals(@Named("individualA") String aString, @Named("individualB") String bString) {
        a = new Individual(aString);
        b = new Individual(bString);
    }
    
    @Then("the result of the comparison is <result>")
    public void getComparisonResult(@Named("result") String expectedResult) {
        IndividualComparator comparator = new IndividualComparator(satInstance);
        
        int actualResult = comparator.compare(a, b);
        
        switch (expectedResult.charAt(0)) {
            case '+': assertTrue(actualResult>0); break;
            case '-': assertTrue(actualResult<0); break;
            case '0': assertTrue(actualResult==0); break;
        } 
    }

}
