package landscapeEC.sat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import landscapeEC.util.SharedPRNG;

public class ClauseList implements Iterable<Clause>, Serializable {
    private Set<Clause> clauses = new HashSet<Clause>();
    private List<Clause> shuffledClauses = new ArrayList<Clause>();
    
    public void addClause(Clause clause) {
        clauses.add(clause);
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
        int numClauses = (int) Math.ceil(shuffledClauses.size() * percentage);

        ClauseList subList = new ClauseList();
        for (int i = 0; i < numClauses; i++) {
            subList.addClause(shuffledClauses.get(i));
        }

        return subList;
    }
    
    public Boolean contains(Clause c) {
        return clauses.contains(c);
    }
    
    @Override
    public String toString() {
        return clauses.toString();
    }

    public Set<Clause> getClauses() {
        return Collections.unmodifiableSet(clauses);
    }

    public void shuffleClauses() {
        shuffledClauses = new ArrayList<Clause>(clauses);
        Collections.shuffle(shuffledClauses, SharedPRNG.instance());
    }
}