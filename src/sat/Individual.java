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
}