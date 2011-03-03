package landscapeEC.sat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class SatParser {

    public SatInstance parseInstance(Reader reader) throws IOException {
        SatInstance newSatInstance = new SatInstance();
        
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String line;
        String clauses="";
        while((line = bufferedReader.readLine()) != null) {
            if(ignoreLine(line)) continue;
            if(line.startsWith("p")) {
                String[] tokens = line.split(" +");
                int numVariables = Integer.parseInt(tokens[2]);
                newSatInstance.setNumVariables(numVariables);
            }
            else{
                clauses = clauses.concat(line);
                clauses = clauses.concat("\n");
            }
        }
        
        parseClauses(newSatInstance, clauses);        
        
        return newSatInstance;
    }

    private void parseClauses(SatInstance newSatInstance, String clauses) {
        String[] clausesArray = clauses.trim().split("\n");
        for(int i =0; i<clausesArray.length; i++){
            Clause newClause = new Clause(i);
            String[] clause = clausesArray[i].split(" ");
            for(int j=0;j<clause.length;j++){
                if(!clause[j].equals("0")) {
                    int variable = Integer.parseInt(clause[j]);
                    newClause.addLiteral(new Literal(Math.abs(variable)-1, Math.signum(variable)==-1));    
                }
            }
            newSatInstance.addClause(newClause);
        }
        
        //Shuffle Clauses to prevent similar geographies from forming
        newSatInstance.getClauseList().shuffleClauses();
    }

    private boolean ignoreLine(String line) {
        return line.startsWith("c")||line.startsWith("%")||line.startsWith("0");
    }

}
