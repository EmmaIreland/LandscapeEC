package sat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClauseList implements Iterable<Clause> {
    private List<Clause> clauses = new ArrayList<Clause>();
    
    public void addClause(Clause clause) {
        clauses.add(clause);
    }
    
    public Clause getClause(int index){
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
		int numClauses = (int) Math.ceil(clauses.size()*percentage);
		
		ClauseList subList = new ClauseList();
		for(int i = 0; i < numClauses; i++) {
			subList.addClause(clauses.get(i));
		}
		
		return subList;
	}
}