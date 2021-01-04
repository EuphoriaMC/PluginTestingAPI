package net.euphoriamc.plugintesting.wrappers;

import java.lang.reflect.Method;

public class EventWrapper extends MethodWrapper {

    public Class<?> event;
    public boolean ignoreCancelled;
    public boolean runOnce;

    public EventWrapper(Object classObject, Method method, Class<?> event, boolean ignoreCanceled, boolean runOnce) {
        super(classObject, method);
        this.event = event;
        this.ignoreCancelled = ignoreCanceled;
        this.runOnce = runOnce;
    }
}
