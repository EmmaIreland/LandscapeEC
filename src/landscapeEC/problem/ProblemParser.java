package landscapeEC.problem;

import java.io.IOException;
import java.io.Reader;

public interface ProblemParser {
    //we need a method that can take in a string and parse the problem from the file
    //

    public Problem parseProblem(Reader reader) throws IOException;
    
    
    
}
