package landscapeEC.problem.sat.operators.localizedMutation;

import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Vector;

public interface ConcentrationRanker {
    
    void initialize(GridWorld newWorld, int generationNumber);
    int getAmp(Vector vector);
    
}
