package sat;

public class Literal {
    public int variable;
    public boolean negated;
    
    public Literal(int variable, boolean negated){
        this.variable = variable;
        this.negated = negated;
    }
    public boolean equals(Literal literal){
        if(this.negated==literal.negated&&this.variable==literal.variable){
            return true;
        }
        else{
            return false;
        }
    }

}
