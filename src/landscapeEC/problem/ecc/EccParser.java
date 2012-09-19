package landscapeEC.problem.ecc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;

public class EccParser implements ProblemParser {

    
    //TODO Fix exampleProblems file hierarchy and where the parsers look for them.
    //TODO Make sure Ecc stuff works.
    @Override
    public Problem parseProblem(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String numOfBitsPerWord = bufferedReader.readLine();
        String numOfCodeWords = bufferedReader.readLine();
        bufferedReader.close();
        numOfBitsPerWord = numOfBitsPerWord.substring(16).trim();
        numOfCodeWords = numOfCodeWords.substring(14).trim();

        int intBitsPerWord = Integer.parseInt(numOfBitsPerWord);
        int intNumCodeWords = Integer.parseInt(numOfCodeWords);
        
        return new EccProblem(intBitsPerWord, intNumCodeWords);
    }

}
