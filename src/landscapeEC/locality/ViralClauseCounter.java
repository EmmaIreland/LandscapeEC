package landscapeEC.locality;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.parameters.IntParameter;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.util.SharedPRNG;

public class ViralClauseCounter implements Serializable {
    private static final long serialVersionUID = -5156231581336681271L;
    private Map<Clause, Double> clauseCounts = new HashMap<Clause, Double>();
    private final Location location;
    
    public ViralClauseCounter(Location location) {
        this.location = location;
    }
    
    public void updateClauseCounts(List<Clause> unsolvedClauses, World world) {
        double increment = 1/(double)unsolvedClauses.size();
        
        for (Clause clause : unsolvedClauses) {
            double count = 0;
            if (clauseCounts.get(clause) == null) {
                clauseCounts.put(clause, increment);
                count = increment;
            } else {
                count = clauseCounts.get(clause) + increment;
                clauseCounts.put(clause, count);
            }

            if(count > IntParameter.VIRAL_CLAUSE_THRESHOLD.getValue()) {
                spreadViralClause(world, clause);
                count -= IntParameter.VIRAL_CLAUSE_THRESHOLD.getValue();
                clauseCounts.put(clause, count);
            }
        }
    }

    private void spreadViralClause(World world, Clause clause) {
        List<Vector> neighborhood = world.getNeighborhood(location.getPosition(), 1);
        neighborhood.remove(location.getPosition()); //We don't want to spread to the same location
        Collections.shuffle(neighborhood, SharedPRNG.instance());
        
        for (Vector pos : neighborhood) {
            SatInstance locationProblem = (SatInstance) world.getLocation(pos).getProblem();

            //Only add clause to a location that does not contain it
            if (!locationProblem.contains(clause)) {
                locationProblem.addViralClause(clause);
                break;
            }
        }
    }
}
