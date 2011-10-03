package landscapeEC.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import landscapeEC.parameters.StringParameter;

public class ParameterClassLoader {

    public static <T> T loadClass(StringParameter parameter) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
    IllegalAccessException, InvocationTargetException {

        @SuppressWarnings("unchecked")
        Class<T> eval = (Class<T>) Class.forName(parameter.getValue());
        Constructor<T> factory = eval.getConstructor();
        T instance = factory.newInstance();
        return instance;
     }
}
