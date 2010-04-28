package sat;

import java.util.ArrayList;
import java.util.List;

public class ClauseList {
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
}