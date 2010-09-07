package parameters;


public enum BooleanParameter
{
    MAX_LENGTH, POOL_SIZE, NUM_RUNS, DEGREE, FITNESS_EVALUATIONS, NUM_TEST_INPUTS,
    NUM_REGISTERS, NUM_CONSTANTS, BLOCK_SIZE, MAX_INSTRUCTIONS_MULTIPLIER, USE_GENE_DUPLICATION,
    GENERATE_GRAPHS, DRAW_RUNTIME_GRAPH;

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
