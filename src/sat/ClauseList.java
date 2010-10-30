package sat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ClauseList implements Iterable<Clause> {
    private List<Clause> clauses = new ArrayList<Clause>();

    public void addClause(Clause clause) {
        clauses.add(clause);
    }

    public Clause getClause(int index) {
        return clauses.get(index);
    }

    public int size() {
        return clauses.size();
    }

    @Override
    public Iterator<Clause> iterator() {
        return clauses.iterator();
    }

    public int getNumClauses() {
        return clauses.size();
    }

    public ClauseList getSubClauseList(double percentage) {
        List<Clause> shuffledClauses = new ArrayList<Clause>(clauses);
        Collections.shuffle(shuffledClauses);
        
        int numClauses = (int) Math.ceil(shuffledClauses.size() * percentage);

        ClauseList subList = new ClauseList();
        for (int i = 0; i < numClauses; i++) {
            subList.addClause(shuffledClauses.get(i));
        }

        return subList;
    }
    
    public Boolean contains(Clause clause) {
    	return clauses.contains(clause);
    }
    
    @Override
    public String toString() {
        return clauses.toString();
    }
}