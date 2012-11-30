package landscapeEC.problem.onesmax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;

public class OnesMaxParser implements ProblemParser {

    @Override
    public Problem parseProblem(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String inputString = bufferedReader.readLine();
        String[] tokens = inputString.split(":");
        
        //Strip whitespace and parse number of bits
        int numBits = Integer.parseInt(tokens[1]);
        
        bufferedReader.close();
        
        return new OnesMaxProblem(numBits);
    }

}
