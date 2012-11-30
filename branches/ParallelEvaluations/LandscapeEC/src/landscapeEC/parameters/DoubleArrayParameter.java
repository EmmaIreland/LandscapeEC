package landscapeEC.parameters;


public enum DoubleArrayParameter
{
    REPORTING_INTERVALS;

    private static final String SEPARATOR = ",";
    
    /**
     * Return the (String) value of this parameter. It will trim all whitespace
     * before returning the value.
     * 
     * @return the String value of this parameter
     */
    public double[] getValue()
    {
		String[] valueStrings = GlobalParameters.getStringValue(toString()).trim().split(SEPARATOR);
        double[] values = new double[valueStrings.length];
        for(int i = 0; i < valueStrings.length; i++)
        {
            values[i] = Double.parseDouble(valueStrings[i]);
        }
         
        return values;
    }
    
    public double[] getValue(String postfix)
    {
        String[] valueStrings = GlobalParameters.getStringValue(toString()+postfix).trim().split(SEPARATOR);
        double[] values = new double[valueStrings.length];
        for(int i = 0; i < valueStrings.length; i++)
        {
            values[i] = Double.parseDouble(valueStrings[i]);
        }         
        return values;
    }
}
