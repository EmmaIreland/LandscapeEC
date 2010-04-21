import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SATParser {
    private static CNF cnf;
    private static Scanner scanner;
    
    public static CNF parseMethod(File inputFile) throws FileNotFoundException {
        scanner = new Scanner(inputFile);
        
        cnf = new CNF();
        
        while(scanner.hasNext()) {
            String next = scanner.next();
            
            if(next.equals("c")) scanner.nextLine();
            if(next.equals("p")) {
                scanner.nextLine();
            }
            
            if(next.equals("%")) break;
            
            parseClause(next);
        }
        
        return cnf;
    }

    public static Clause parseClause(String next) {
        Clause clause = new Clause();
        String term = next;
        while(!term.equals("0")) {
            int value = Integer.parseInt(next);
            Literal literal = new Literal(Math.abs(value), Math.signum(value)==-1);
            clause.addLiteral(literal);
            term = scanner.next();
        }
        return clause;
    }
}