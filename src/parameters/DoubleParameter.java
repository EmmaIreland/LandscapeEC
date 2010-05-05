package parameters;


public enum DoubleParameter
{

    MUTATION_RATE, ELITE_PROPORTION;
    
    /**
     * Returns the (double) value of this parameter.
     * @return the double value of this parameter
     */
    public double getValue()
    {
        String valueString = GlobalParameters.getStringValue(toString());
        return Double.parseDouble(valueString);
    }
    
/*    *//**
     * Returns the (double) value of this parameter, after adding the given
     * suffix.
     * 
     * @param suffix the suffix to add to the parameter name before look-up
     * @return the value of the parameter
     *//*
    public double getValue(String suffix)
    {
        String valueString = GlobalParameters.getStringValue(toString()+suffix);
        return Double.parseDouble(valueString);
    }*/
}
