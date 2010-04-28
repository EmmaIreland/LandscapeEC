package sat;

import java.util.ArrayList;
import java.util.List;


public class Clause{
    private List<Literal> literals = new ArrayList<Literal>();


    public void addLiteral(Literal literal) {
        literals.add(literal);    
    }
    
    public boolean equals(Object object){
        if(!(object instanceof Clause)){
            return false;
        }
        
        Clause clause = (Clause)object;
        boolean equalClauses=true;
        for(int i =0;i<literals.size();i++){
            if(!clause.literals.get(i).equals(this.literals.get(i))){
                equalClauses=false;
            }
        }       
        return equalClauses;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (Literal literal : literals) {
            result = 37 * result + literal.hashCode();
        }
        return result;
    }
    
    public String toString(){
        String string="";
        for(int i =0;i<literals.size();i++){
           string= string +literals.get(i)+ " ";           
        } 
        return string;
    }

    public boolean satisfiedBy(Individual individual) {
        for(Literal literal:literals) {
            if(satisfiesLiteral(individual, literal)) {
                return true;
            }
        }
        return false;
    }

    private boolean satisfiesLiteral(Individual individual, Literal literal) {
        return individual.valueIsFalse(literal) == literal.negated;
    }

}
