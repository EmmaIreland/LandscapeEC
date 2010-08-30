package sat;

import java.util.List;

public interface SelectionOperator {
    public List<Individual> selectParents(List<Individual> population);
}
