package landscapeEC.problem;

public interface TestCases<T> {
    
    public abstract TestCases<T> crossover(TestCases<T> firstParent, TestCases<T> secondParent, int noiseStrength);
}
