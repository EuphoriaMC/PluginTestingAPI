package net.euphoriamc.plugintesting.wrappers;

import java.lang.reflect.Method;

public class MethodWrapper {

    public Object classObject;
    public Method method;

    public MethodWrapper(Object classObject, Method method) {
        this.classObject = classObject;
        this.method = method;
    }
}
