package util;

import parameters.*;

public class MockParameters extends Parameters
{
	/**
     * Because we extend from Parameters, which is serializable, it's recommended that we have
     * a UID.  This is apparently the default value.
     */
    private static final long serialVersionUID = 1L;

    public MockParameters()
	{
		for(DoubleParameter param : DoubleParameter.values())
		{
			this.put(param.name(), "1.0");
		}
		
		for(IntParameter param : IntParameter.values())
		{
			this.put(param.name(), "1");
		}
		
		for(StringParameter param : StringParameter.values())
		{
			this.put(param.name(), "value");
		}
		
		for(StringArrayParameter param : StringArrayParameter.values())
		{
			this.put(param.name(), "value1,value2,value3");
		}
		
        for(DoubleArrayParameter param : DoubleArrayParameter.values())
        {
            this.put(param.name(), "1.0,2.0,3.0");
        }
        
        for (BooleanParameter param : BooleanParameter.values()) {
            this.put(param.name(), "false");
        }
	}
}
