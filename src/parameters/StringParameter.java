package parameters;


public enum StringParameter
{    
      
    SEED, PROBLEM_FILE, MUTATION_OPERATOR, SELECTION_OPERATOR, CROSSOVER_OPERATOR, OBSERVERS, GEOGRAPHY_TYPE;

    /**
     * Return the (String) value of this parameter. It will trim all whitespace
     * before returning the value.
     * 
     * @return the String value of this parameter
     */
    public String getValue()
    {
        String valueString = GlobalParameters.getStringValue(toString()).trim();
        return valueString;
    }
    
/*    public String getValue(String postfix)
    {
        String valueString = GlobalParameters.getStringValue(toString()+postfix).trim();
        return valueString;
    }*/

}
