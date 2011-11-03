package landscapeEC.problem.sat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import landscapeEC.problem.ProblemParser;

public class SatParser implements ProblemParser{
    private final static double MAXIMUM_DIFFICULTY = 1.0;
    
    public SatParser() {
        //
    }
    
    @Override
    public SatInstance parseProblem(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String line;
        String clauses="";
        int numVariables = -1;
        while((line = bufferedReader.readLine()) != null) {
            if(ignoreLine(line)) continue;
            if(line.startsWith("p")) {
                String[] tokens = line.split(" +");
                numVariables = Integer.parseInt(tokens[2]);
            }
            else{
                clauses = clauses.concat(line);
                clauses = clauses.concat("\n");
            }
        }
        if (numVariables == -1) {
            throw new IllegalStateException("The problem parsed did not set its number of variables.");
        }

        List<Clause> parsedClauses = parseClauses(clauses);
        
        SatInstance newSatInstance = new SatInstance(parsedClauses, MAXIMUM_DIFFICULTY);
        newSatInstance.setNumVariables(numVariables);
        
        //Shuffle Clauses to prevent similar geographies from forming
        newSatInstance.shuffleClauses();
        
        return newSatInstance;
    }

    private List<Clause> parseClauses(String clauses) {
        List<Clause> result = new ArrayList<Clause>();
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
            result.add(newClause);
        }
        
        return result;
    }

    private boolean ignoreLine(String line) {
        return line.startsWith("c")||line.startsWith("%")||line.startsWith("0");
    }

}
