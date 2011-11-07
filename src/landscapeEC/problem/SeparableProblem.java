package landscapeEC.problem;

public interface SeparableProblem<T> {
    
    public abstract SeparableProblem<T> crossover(SeparableProblem<T> firstParent, SeparableProblem<T> secondParent, int noiseStrength);
}
