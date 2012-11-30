package landscapeEC.locality;

public class MigrationInWorldOfSizeOneException extends RuntimeException {
    // Thrown if we try to migrate in a world of size one.
    private static final long serialVersionUID = -8978604210420715703L;

    public MigrationInWorldOfSizeOneException(IndexOutOfBoundsException e) {
        super(e);
    }
}
