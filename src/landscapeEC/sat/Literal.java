package landscapeEC.sat;

import java.io.Serializable;

public class Literal implements Serializable {
    private static final long serialVersionUID = -6905437698532067448L;
    public int variable;
    public boolean negated;

    public Literal(int variable, boolean negated) {
        this.variable = variable;
        this.negated = negated;
    }

    public boolean equals(Literal literal) {
        if (this.negated == literal.negated
                && this.variable == literal.variable) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (negated) {
            return -variable;
        }
        return variable;
    }

    @Override
    public String toString() {
        String string = "";
        if (this.negated) {
            string = string + "-";
        }
        string = string + (variable + 1);
        return string;
    }

}
