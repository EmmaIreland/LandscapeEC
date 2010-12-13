package landscapeEC.sat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Clause implements Serializable{
    private List<Literal> literals = new ArrayList<Literal>();
    private int cachedHash = Integer.MIN_VALUE;

    public void addLiteral(Literal literal) {
        literals.add(literal);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Clause)) {
            return false;
        }
        
        if(hashCode() != object.hashCode()) return false;

        Clause clause = (Clause) object;
        for (int i = 0; i < literals.size(); i++) {
            if (!clause.literals.get(i).equals(this.literals.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
    	if(cachedHash != Integer.MIN_VALUE) {
    		return cachedHash;
    	}
    	
        int result = 17;
        for (Literal literal : literals) {
            result = 37 * result + literal.hashCode();
        }
        
        cachedHash = result;
        
        return result;
    }

    @Override
    public String toString() {
        String string = "( ";
        for (int i = 0; i < literals.size(); i++) {
            string = string + literals.get(i) + " ";
        }
        return string + ")";
    }

    public boolean satisfiedBy(Individual individual) {
        for (Literal literal : literals) {
            if (satisfiesLiteral(individual, literal)) {
                return true;
            }
        }
        return false;
    }

    private boolean satisfiesLiteral(Individual individual, Literal literal) {
        return individual.valueIsFalse(literal) == literal.negated;
    }

}
