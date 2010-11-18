package landscapeEC.parameters;

public enum IntArrayParameter
{
    WORLD_DIMENSIONS;

    private static final String SEPARATOR = ", *";
    /**
     * Return the (String) value of this parameter. It will trim all whitespace
     * before returning the value.
     * 
     * @return the String value of this parameter
     */
    public Integer[] getValue()
    {
		String[] valueStrings = GlobalParameters.getStringValue(toString()).trim().split(SEPARATOR);
        Integer[] values = new Integer[valueStrings.length];
        for(int i = 0; i < valueStrings.length; i++)
        {
            values[i] = Integer.parseInt(valueStrings[i]);
        }
         
        return values;
    }
    
}
