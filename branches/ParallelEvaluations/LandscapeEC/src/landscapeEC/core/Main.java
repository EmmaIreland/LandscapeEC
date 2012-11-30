package landscapeEC.core;

import java.io.File;

import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.GlobalParameters;

public class Main {

    public static void main(String[] args) throws Exception{
        if(args.length <= 0) System.out.println("Wrong number of arguments");
        for (String arg : args)
        {                        
            File paramsFile = new File(arg);
            GlobalParameters.setParameters(paramsFile);
            GARun run = new GARun(arg);
            run.run();
        }
    }
        
}