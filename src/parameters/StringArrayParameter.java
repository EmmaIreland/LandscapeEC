package parameters;


public enum StringArrayParameter
{
    INSTRUCTION_GROUPS;

    private static final String SEPARATOR = ",";
    /**
     * Return the (String) value of this parameter. It will trim all whitespace
     * before returning the value.
     * 
     * @return the String value of this parameter
     */
    public String[] getValue()
    {
		String[] value = GlobalParameters.getStringValue(toString()).trim().split(SEPARATOR);
        return value;
    }
    
    public String[] getValue(String postfix)
    {
        String[] value = GlobalParameters.getStringValue(toString()+postfix).trim().split(SEPARATOR);
        return value;
    }
}
