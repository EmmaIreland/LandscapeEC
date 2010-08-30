package sat;

import java.util.List;

public interface CrossoverOperator {
    public Individual crossover(List<Individual> parents);
}
