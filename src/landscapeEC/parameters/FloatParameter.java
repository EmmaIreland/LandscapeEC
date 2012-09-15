package landscapeEC.parameters;


public enum FloatParameter
{

    GEOGRAPHY_NOISE_STRENGTH;
    
    /**
     * Returns the (float) value of this parameter.
     * @return the float value of this parameter
     */
    public float getValue()
    {
        String valueString = GlobalParameters.getStringValue(toString());
        return Float.parseFloat(valueString);
    }
    
/*    *//**
     * Returns the (float) value of this parameter, after adding the given
     * suffix.
     * 
     * @param suffix the suffix to add to the parameter name before look-up
     * @return the value of the parameter
     *//*
    public float getValue(String suffix)
    {
        String valueString = GlobalParameters.getStringValue(toString()+suffix);
        return Float.parseFloat(valueString);
    }*/
}
