package sat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IndividualTest {
    @Test
    public void testEquals() {
        Individual testIndividual = new Individual("1101001");
        
        String[] comparisonString = {"1101011", "000000", "1", "1101001", "10010", "110100100000"};
        boolean[] comparisonExpectation = {false, false, false, true, false, false};
        
        int numTests = comparisonString.length;
        
        for(int i=0; i<numTests; i++) {
            Individual comparisonIndividual = new Individual(comparisonString[i]);
            assertEquals("Test " + i + ": ", comparisonExpectation[i], testIndividual.equals(comparisonIndividual));
        }
    }
}
