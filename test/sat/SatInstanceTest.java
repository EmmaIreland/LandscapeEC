package sat;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import landscapeEC.observers.vis.MapVisualizer;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.Literal;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import org.junit.Test;

public class SatInstanceTest {
    
    @Test
    public void clauseEvaluationEasyTest() throws Exception {
        String satInstanceString = "1 -3\n2 -3\n3 -4\n4 -1\n";
        Clause[] expectedClauses = new Clause[]{
                new Clause(0, new Literal[] {new Literal(0, false), new Literal(2, true)}),
                new Clause(1, new Literal[] {new Literal(1, false), new Literal(2, true)}),
                new Clause(2, new Literal[] {new Literal(2, false), new Literal(3, true)}),
                new Clause(3, new Literal[] {new Literal(3, false), new Literal(0, true)})
        };
        String individualBitString = "0101";
        boolean expectedAnswers[] = new boolean[] { true, true, false, true };

        checkInstance(satInstanceString, expectedClauses, individualBitString, expectedAnswers);
    }
    
    @Test
    public void clauseEvaluationMediumTest() throws Exception {
        String satInstanceString = "1 2 3\n-1 2 -3\n1 2 -3\n1 -2 3\n";
        Clause[] expectedClauses = new Clause[]{
                new Clause(0, new Literal[] {new Literal(0, false), new Literal(1, false), new Literal(2, false)}),
                new Clause(1, new Literal[] {new Literal(0, true), new Literal(1, false), new Literal(2, true)}),
                new Clause(2, new Literal[] {new Literal(0, false), new Literal(1, false), new Literal(2, true)}),
                new Clause(3, new Literal[] {new Literal(0, false), new Literal(1, true), new Literal(2, false)})
        };
        String individualBitString = "101";
        boolean expectedAnswers[] = new boolean[] {true, false, true, true};

        checkInstance(satInstanceString, expectedClauses, individualBitString, expectedAnswers);
    }

    private void checkInstance(String satInstanceString, Clause[] expectedClauses, String individualBitString, boolean[] expectedAnswers) throws IOException {
        SatParser parser = new SatParser();
        StringReader stringReader = new StringReader(satInstanceString);
        SatInstance instance = parser.parseInstance(stringReader);
        GlobalSatInstance.setInstance(instance);
        
        List<Clause> clauses = new ArrayList<Clause>();
        for (Clause clause : instance) {
            clauses.add(clause);
        }
        
        for(Clause c : expectedClauses){
            assertTrue(instance.contains(c));
        }
        
        Individual individual = new Individual(individualBitString);
        
        for (int i=0; i<expectedAnswers.length; ++i) {
            assertEquals(expectedAnswers[i], expectedClauses[i].satisfiedBy(individual));
        }

        assertEquals("Evaluate and OnesPercent were not the same value", SatEvaluator.evaluate(instance, individual), 
                    MapVisualizer.onesPercent(SatEvaluator.getSolvedClausesBitstring(instance, individual)), 1e-5);
    }
}
