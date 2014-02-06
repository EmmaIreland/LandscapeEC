package landscapeEC.problem.knapsack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import landscapeEC.problem.Problem;
import landscapeEC.problem.ProblemParser;

public class KnapsackParser implements ProblemParser {


    
    @Override
    public Problem parseProblem(Reader reader) throws IOException {
        
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        int numOfItems = Integer.parseInt(bufferedReader.readLine());
        int[] values = new int[numOfItems];
        int[] weights = new int[numOfItems];
        
        for(int i=0; i<numOfItems; i++){
            String item = bufferedReader.readLine().trim();
            String[] parts = item.split(" +");
            values[i] = Integer.parseInt(parts[1]);
            weights[i] = Integer.parseInt(parts[2]);
        }
        
        int weightLimit = Integer.parseInt(bufferedReader.readLine());
        bufferedReader.close();
        
        return new KnapsackProblem(weightLimit, values, weights);

    }

}
