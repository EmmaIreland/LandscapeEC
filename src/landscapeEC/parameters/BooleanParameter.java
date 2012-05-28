package landscapeEC.parameters;


public enum BooleanParameter
{
    TOROIDAL, PROMOTE_SMALL_POPULATIONS, QUIT_ON_SUCCESS,VIRAL_CLAUSES, USE_THREADS;

    /**
     * Return the (boolean) value of this parameter.
     * 
     * @return the boolean value of this parameter
     */
    public boolean getValue()
    {
        String valueString = GlobalParameters.getStringValue(toString());
        return Boolean.parseBoolean(valueString);
    }
    
    /**
     * Return the (boolean) value of this parameter, with the given suffix appended
     * to the end of the parameter name.
     * 
     * @param suffix the suffix to append to the parameter
     * @return the boolean value of this parameter
     */
    public boolean getValue(String suffix)
    {
        String valueString = GlobalParameters.getStringValue(toString()+suffix);
        return Boolean.parseBoolean(valueString);
    }
}
