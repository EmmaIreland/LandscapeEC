package sat;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.ClauseList;
import landscapeEC.problem.sat.Literal;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import static junit.framework.Assert.*;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;


public class Parse3SatSteps extends Steps {
    private SatParser satParser;
    private SatInstance satInstance;

    @Given("a 3-SAT parser")
    public void constructParser() {
        satParser = new SatParser();
    }
    
    @When("I parse this 3-SAT instance $instance")
    public void parseInstance(String instance) throws IOException {
        StringReader reader = new StringReader(instance);
        satInstance = satParser.parseInstance(reader);
    }
    
    @Then("the number of variables is $numVariables")
    public void confirmNumberOfVariables(int numVariables) {
        assertEquals(numVariables, satInstance.getNumVariables());
    }
    
    @Then("the number of clauses is $numClauses")
    public void confirmNumberOfClauses(int numClauses) {
        assertEquals(numClauses, satInstance.getNumClauses());
    }

    @Then("the instance contains these clauses: $clauses")
    public void confirmCNFInstance(String clauses) {
        Set<Clause> clauseSet = new HashSet<Clause>();
        
        String[] clausesArray = clauses.split("\n");
        for(int i =0; i<clausesArray.length; i++){
            Clause newClause = new Clause(i);
            String[] clause = clausesArray[i].split(" ");
            for(int j=0;j<clause.length;j++){
                int variable = Integer.parseInt(clause[j].trim());
                newClause.addLiteral(new Literal(Math.abs(variable)-1, Math.signum(variable)==-1));
            }
            clauseSet.add(newClause);
        }

        //There is the correct number of clauses in the clauseSet
        assertEquals(satInstance.getNumClauses(), clauseSet.size());
        
        //AND each clause in the satInstance are in the clauseSet
        assertEquals(clauseSet, satInstance.getClauses());
    }
}
