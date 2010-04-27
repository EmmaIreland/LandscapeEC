package sat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class SatParser {

    public SatInstance parseInstance(Reader reader) throws IOException {
        SatInstance newSatInstance = new SatInstance();
        
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String line;
        while((line = bufferedReader.readLine()) != null) {
            if(line.startsWith("c")) continue;
            if(line.startsWith("p")) {
                String[] tokens = line.split(" +");
                int numVariables = Integer.parseInt(tokens[2]);
                newSatInstance.setNumVariables(numVariables);
                int numClauses = Integer.parseInt(tokens[3]);
                newSatInstance.setNumClauses(numClauses);
            }
        }
        
        
        
        return newSatInstance;
    }

}
