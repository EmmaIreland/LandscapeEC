package landscapeEC.observers.vis;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import landscapeEC.sat.Clause;
import landscapeEC.sat.GlobalSatInstance;
import landscapeEC.sat.Individual;
import landscapeEC.sat.SatEvaluator;
import landscapeEC.sat.SatInstance;
import landscapeEC.sat.SatParser;

import org.junit.Test;

public class MapVisualizerTest {
    
    @Test
    public void clauseEvaluationEasyTest() throws Exception {
        SatParser parser = new SatParser(false);
        StringReader stringReader = new StringReader("1 -3\n2 -3\n3 -4\n4 -1\n");
        SatInstance instance = parser.parseInstance(stringReader);
        List<Clause> clauses = new ArrayList<Clause>();
        for (Clause clause : instance.getClauseList()) {
            clauses.add(clause);
        }
        
        assertEquals("Clauses in clause list are out of order or incorrect", clauses.toString(),"[( 3 -4 ), ( 1 -3 ), ( 4 -1 ), ( 2 -3 )]");
        
        String individualBitString = "0101";
        Individual individual = new Individual(individualBitString);
        
        //Assert that each clause is correctly satisfied manually
        assertFalse(clauses.get(0).satisfiedBy(individual));
        assertTrue(clauses.get(1).satisfiedBy(individual));
        assertTrue(clauses.get(2).satisfiedBy(individual));
        assertTrue(clauses.get(3).satisfiedBy(individual));
        
        assertEquals("Evaluate and OnesPercent were not the same value", SatEvaluator.evaluate(instance, individual), MapVisualizer.onesPercent(SatEvaluator.getSolvedClausesBitstring(instance, individual)), 1e-5);
    }
    
    @Test
    public void clauseEvaluationMediumTest() throws Exception {
        SatParser parser = new SatParser(false);
        StringReader stringReader = new StringReader("1 2 3\n-1 2 -3\n1 2 -3\n1 -2 3\n");
        SatInstance instance = parser.parseInstance(stringReader);
        GlobalSatInstance.setInstance(instance);
        
        List<Clause> clauses = new ArrayList<Clause>();
        for (Clause clause : instance.getClauseList()) {
            clauses.add(clause);
        }
        
        assertEquals("Clauses in clause list are out of order or incorrect", "[( 1 2 3 ), ( 1 2 -3 ), ( -1 2 -3 ), ( 1 -2 3 )]", clauses.toString());
        
        String individualBitString = "101";
        Individual individual = new Individual(individualBitString);
        
        //Assert that each clause is correctly satisfied manually
        assertTrue(clauses.get(0).satisfiedBy(individual));
        assertTrue(clauses.get(1).satisfiedBy(individual));
        assertFalse(clauses.get(2).satisfiedBy(individual));
        assertTrue(clauses.get(3).satisfiedBy(individual));
        
        assertEquals("Evaluate and OnesPercent were not the same value", SatEvaluator.evaluate(instance, individual), MapVisualizer.onesPercent(SatEvaluator.getSolvedClausesBitstring(instance, individual)), 1e-5);
    }
}
