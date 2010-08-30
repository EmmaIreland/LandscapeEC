package sat;

public class Individual {
    private String bitString;

    public Individual(String bitString) {
        this.bitString = bitString;
    }

    public String getBitString() {
        return bitString;
    }

    public void setBitString(String bitString) {
        this.bitString = bitString;
    }

    public int getValueAt(int variable) {
        return Integer.parseInt(bitString.substring(variable, variable+1));
    }

    boolean valueIsFalse(Literal literal) {
        return getValueAt(literal.variable) == 0;
    }
    
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Individual)) return false;
        Individual individual = (Individual)other;
        return bitString.equals(individual.bitString);
    }
    
    @Override
    public int hashCode() {
        return bitString.hashCode();
    }
    
    @Override
    public String toString() {
        return bitString;
    }
}