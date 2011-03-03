package sat;

import static org.junit.Assert.assertEquals;
import landscapeEC.sat.Clause;
import landscapeEC.sat.Literal;

import org.junit.Test;

public class ClauseTest {
    private final boolean F = false;
    private final boolean T = true;

    @Test
    public void compareClausesTest(){
        Clause testClause = new Clause(1);
        testClause.addLiteral(new Literal(3, false));
        testClause.addLiteral(new Literal(6, false));
        testClause.addLiteral(new Literal(13, true));
        
        int[][] variable = {{3, 6, 13}, {8, 6, 13}, {3, 6, 13}, {1, 2, 3}};
        boolean[][] negation = {{F, F, T}, {F, F, T}, {F, T, F}, {T, T, F}};
        boolean[] expectedEqual = {T, F, F, F};

        int numClauses = variable.length;
        int numLiterals = variable[0].length;
        
        Clause comparisonClause[] = new Clause[numClauses];
            
        for(int clause=0; clause<numClauses; clause++) {
            comparisonClause[clause] = new Clause(clause);
            for(int literal=0; literal<numLiterals; literal++) {
                comparisonClause[clause].addLiteral(new Literal(variable[clause][literal], negation[clause][literal]));
            }
        }
        
        for(int clause=0; clause<numClauses; clause++) {
            assertEquals(expectedEqual[clause], testClause.equals(comparisonClause[clause]));

        }
    }
}
