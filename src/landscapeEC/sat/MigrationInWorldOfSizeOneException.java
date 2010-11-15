package landscapeEC.sat;

public class MigrationInWorldOfSizeOneException extends RuntimeException {
    // Thrown if we try to migrate in a world of size one.

    public MigrationInWorldOfSizeOneException(IndexOutOfBoundsException e) {
        super(e);
    }
}
