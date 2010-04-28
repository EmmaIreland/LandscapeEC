package sat;

import java.util.ArrayList;
import java.util.List;


public class Clause{
    private List<Literal> literals = new ArrayList<Literal>();


    public void addLiteral(Literal literal) {
        literals.add(literal);    
    }
    
    public boolean equals(Clause clause){
        boolean equalClauses=true;
        for(int i =0;i<literals.size();i++){
            if(!clause.literals.get(i).equals(this.literals.get(i))){
                equalClauses=false;
            }
        }       
        return equalClauses;
    }


}
