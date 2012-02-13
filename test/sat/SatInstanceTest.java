package sat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import landscapeEC.observers.vis.MapVisualizer;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.Problem;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.Literal;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;

import org.junit.Test;

public class SatInstanceTest {
    
    @Test
    public void clauseEvaluationEasyTest() throws Exception {
        String satInstanceString = "p cnf 4 4\n1 -3\n2 -3\n3 -4\n4 -1\n";
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
        String satInstanceString = "p cnf 3 4\n1 2 3\n-1 2 -3\n1 2 -3\n1 -2 3\n";
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

    @Test
    public void SubInstanceTest() throws Exception {
        //Create a list of clauses
        List<Clause> clauseList = new ArrayList<Clause>();
        String satInstanceString = "1 2 3\n-1 2 -3\n1 2 -3\n1 -2 3\n3 4 5\n-3 4 -5\n3 4 -5\n3 -4 5\n";
        String[] clausesArray = satInstanceString.split("\n");
        for(int i = 0; i < clausesArray.length; i++){
            Clause newClause = new Clause(i);
            String[] clause = clausesArray[i].split(" ");
            for(int j=0;j<clause.length;j++){
                int variable = Integer.parseInt(clause[j].trim());
                newClause.addLiteral(new Literal(Math.abs(variable)-1, Math.signum(variable)==-1));
            }
            clauseList.add(newClause);
        }
        
        //Make a satInstance and subInstance
        SatInstance instance = new SatInstance(clauseList, 1.0);
        SatInstance subInstance = (SatInstance) instance.getSubProblem(0.75);
        
        //Make ClauseSet with the first 75% of the clauses
        clauseList.remove(7);
        clauseList.remove(6);
        Set<Clause> clauseSet = new HashSet<Clause>(clauseList);

        //Assert that there are the same clauses and the same number of clauses
        assertEquals(subInstance.getNumClauses(), clauseSet.size());
        assertEquals(clauseSet, subInstance.getClauses());
    }
    
    @Test
    public void OffSetSubInstanceTest() throws Exception {
        //Create a list of clauses
        List<Clause> clauseList = new ArrayList<Clause>();
        String satInstanceString = "1 2 3\n-1 2 -3\n1 2 -3\n1 -2 3\n3 4 5\n-3 4 -5\n3 4 -5\n3 -4 5\n";
        String[] clausesArray = satInstanceString.split("\n");
        for(int i = 0; i < clausesArray.length; i++){
            Clause newClause = new Clause(i);
            String[] clause = clausesArray[i].split(" ");
            for(int j=0;j<clause.length;j++){
                int variable = Integer.parseInt(clause[j].trim());
                newClause.addLiteral(new Literal(Math.abs(variable)-1, Math.signum(variable)==-1));
            }
            clauseList.add(newClause);
        }
        
        //Make a satInstance and subInstance
        SatInstance instance = new SatInstance(clauseList, 1.0);
        SatInstance subInstance = (SatInstance) instance.getSubProblem(0.5, 0.25);
        
        //Make ClauseSet with 50% of clauses, offset by the first 25%
        clauseList.remove(7);
        clauseList.remove(6);
        clauseList.remove(1);
        clauseList.remove(0);
        Set<Clause> clauseSet = new HashSet<Clause>(clauseList);

        //Assert that there are the same clauses and the same number of clauses
        assertEquals(subInstance.getNumClauses(), clauseSet.size());
        assertEquals(clauseSet, subInstance.getClauses());
    }

    private void checkInstance(String satInstanceString, Clause[] expectedClauses, String individualBitString, boolean[] expectedAnswers) throws IOException {
        SatParser parser = new SatParser();
        StringReader stringReader = new StringReader(satInstanceString);
        SatInstance instance = parser.parseProblem(stringReader);
        GlobalProblem.setProblem(instance);
        
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

        SatEvaluator evaluator = new SatEvaluator();
        
        assertEquals("Evaluate and OnesPercent were not the same value", evaluator.evaluate(instance, individual), 
                    MapVisualizer.onesPercent(evaluator.getResultString(instance, individual)), 1e-5);
    }
}
