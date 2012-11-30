package landscapeEC.problem.ecc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;

public class EccParser implements ProblemParser {

	private int intBitsPerWord;
	private int intNumCodeWords;
    
    //TODO Make sure Ecc stuff works.
    @Override
    public Problem parseProblem(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String string1 = bufferedReader.readLine();
        String string2 = bufferedReader.readLine();
        bufferedReader.close();
        
        String[] stringArr1 = string1.split(":");
        String[] stringArr2 = string2.split(":");
        
        if(stringArr1[0].trim().equals("numOfBitsPerWord")) {
        	intBitsPerWord = Integer.parseInt(stringArr1[1]);
        	intNumCodeWords = Integer.parseInt(stringArr2[1]);
        	
        }else{
        	intBitsPerWord = Integer.parseInt(stringArr2[1]);
        	intNumCodeWords = Integer.parseInt(stringArr1[1]);
        }
        
        return new EccProblem(intBitsPerWord, intNumCodeWords);
    }
    
    public int getBitsPerWord() {
    	return intBitsPerWord;
    }
    
    public int getNumCodeWords() {
    	return intNumCodeWords;
    }
                               

}
