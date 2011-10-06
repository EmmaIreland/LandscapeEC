package landscapeEC.problem;

import java.io.Reader;

import landscapeEC.problem.sat.SatInstance;

public interface ProblemParser {
    //we need a method that can take in a string and parse the problem from the file
    //

    public Problem parceProblem(Reader reader);
    
    
    
}
