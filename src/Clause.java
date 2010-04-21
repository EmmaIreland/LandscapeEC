import java.util.List;


public class Clause {
    List<Literal> literals;

    public void addLiteral(Literal literal) {
        literals.add(literal);
    }
}
