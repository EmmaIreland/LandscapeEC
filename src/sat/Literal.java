package sat;

public class Literal {
    public int variable;
    public boolean negated;
    
    public Literal(int variable, boolean negated) {
        this.variable = variable;
        this.negated = negated;
    }
}
