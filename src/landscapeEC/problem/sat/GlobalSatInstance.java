package landscapeEC.problem.sat;

public class GlobalSatInstance {
    private static SatInstance globalInstance;
    private static IndividualComparator globalComparator;
    
    public static void setInstance(SatInstance satInstance) {
        globalInstance = satInstance;
        globalComparator = new IndividualComparator(satInstance);
    }

    public static SatInstance getInstance() {
        if (globalInstance == null) {
            throw new RuntimeException("There was no global SatInstance set.");
        }
        return globalInstance;
    }
    
    public static IndividualComparator getComparator() {
        if (globalComparator == null) {
            throw new RuntimeException("There was no global SatInstance set.");
        }
        return globalComparator;
    }
}
