package landscapeEC.parameters;


public enum IntParameter
{
    NUM_EVALS_TO_DO, NUM_RUNS, TOURNAMENT_SIZE, CARRYING_CAPACITY, 
    VISUALIZER_X_SCALE, VISUALIZER_Y_SCALE, 
    MIGRATION_DISTANCE, VIRAL_CLAUSE_THRESHOLD, REMEMBER_GENERATIONS,
    NUM_OF_GRAPH_SNAPSHOTS;

    /**
     * Return the (integer) value of this parameter.
     * 
     * @return the integer value of this parameter
     */
    public int getValue()
    {
        String valueString = GlobalParameters.getStringValue(toString());
        return Integer.parseInt(valueString);
    }
    
/*    public int getValue(String postfix)
    {
        String valueString = GlobalParameters.getStringValue(toString()+postfix);
        return Integer.parseInt(valueString);
    }*/

  /*  public boolean isDefined() {
        return GlobalParameters.isSet(toString());
    }*/
}
