package landscapeEC.sat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import landscapeEC.locality.World;

public class SatInstance implements Serializable{
    private int numVariables;
    private ClauseList cnf = new ClauseList();

    public SatInstance() {
    	//Do nothing!
    }
    
    public SatInstance(ClauseList clauseList, int numVars) {
    	cnf = clauseList;
    	numVariables = numVars;
    }
    
    public int getNumVariables() {
        return numVariables;
    }

    public int getNumClauses() {
        return cnf.getNumClauses();
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }

    public ClauseList getClauseList(){
        return cnf;
    }
    
    public SatInstance getSubInstance(double percentage) {
    	return new SatInstance(cnf.getSubClauseList(percentage), numVariables);
    }
    
    public void addClause(Clause newClause){
        cnf.addClause(newClause);
    }
    
    public void serialize(String fileName) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(fileName + ".satins.sav");
        ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);
        outputStream.writeObject(this);
    }
    
    public static SatInstance deserialize(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(file);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);
        SatInstance result = (SatInstance) objectStream.readObject();
        return result;
    }
    
    @Override
    public String toString() {
        return
            "Num variables: " + numVariables + "\n" +
            "Clauses: " + cnf;
    }
}
