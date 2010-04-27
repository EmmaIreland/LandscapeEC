package sat;

import java.util.List;

public class CNF {
    private List<Clause> clauses;
    
    public void addClause(Clause clause) {
        clauses.add(clause);
    }
}