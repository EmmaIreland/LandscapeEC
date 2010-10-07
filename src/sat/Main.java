package sat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import observers.vis.MapVisualizer;

import parameters.GlobalParameters;


public class Main {

    public static void main(String[] args) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException,
        NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException{
        if(args.length <= 0) System.out.println("Wrong number of arguments");
        for (String arg : args)
        {                        
            File paramsFile = new File(arg);
            GlobalParameters.setParameters(paramsFile);
            
            GARun run = new GARun();

            run.run();
            
            /*GlobalParameters.setupLogging();
            doRuns();*/
        }
    }
}
