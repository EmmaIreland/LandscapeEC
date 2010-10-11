package sat;

public class Individual {
    private int[] bits;

    public Individual(String bitString) {
        int[] newBits = new int[bitString.length()];
        char[] chars = bitString.toCharArray();
        for(int i=0; i<chars.length; i++) {
            if(chars[i] == '0') newBits[i] = 0;
            else newBits[i] = 1;
        }
        this.bits = newBits;
    }
    
    public Individual(int[] bits) {
        this.bits = bits;
    }

    public int[] getBits() {
        return bits;
    }

    boolean valueIsFalse(Literal literal) {
        return bits[literal.variable] == 0;
    }
    
    @Override
    public String toString() {
        String str = "";
        for(int i:bits) {
            str += i;
        }
        return str;
    }
}