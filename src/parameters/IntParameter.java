package parameters;


public enum IntParameter
{
    NUM_RUNS, NUM_GENERATIONS, TOURNAMENT_SIZE, CARRYING_CAPACITY;

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
