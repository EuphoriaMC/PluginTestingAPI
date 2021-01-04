package net.euphoriamc.plugintesting;

import net.euphoriamc.plugintesting.wrappers.EventWrapper;
import net.euphoriamc.plugintesting.wrappers.MethodWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class TestingAPI extends JavaPlugin implements Listener {

    static TestingAPI instance;

    RegisteredListener registeredListenerWithIgnore;
    RegisteredListener registeredListenerWithoutIgnore;

    final List<EventWrapper> eventMethods = new ArrayList<>();
    final List<MethodWrapper> testMethods = new ArrayList<>();

    static boolean isListenerRegistered;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new ServerLoadListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ArrayList<HandlerList> list = HandlerList.getHandlerLists();
            if (ServerLoadListener.previousCount < list.size())
                ServerLoadListener.registerToHandlers();
        }, 0L, 60L);
    }

    @Override
    public void onDisable() {
        if (isListenerRegistered) {
            HandlerList.unregisterAll(registeredListenerWithIgnore.getListener());
            isListenerRegistered = false;
        }
    }

    public void registerFolder(JavaPlugin plugin, String pathToPackage) {
        try {
            String path = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            List<Class<?>> classes = getClasses(path, pathToPackage);
            getMethodsAnnotatedWith(classes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Class<?>> getClasses(String jarLocation, String pathToPackage) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        URL jarUrl = new URL("file://" + jarLocation);
        URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, TestingAPI.class.getClassLoader());
        JarFile jar = new JarFile(jarLocation);

        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.endsWith(".class") && file.startsWith(pathToPackage.replaceAll("\\.", "/"))) {
                String classname =
                        file.replace('/', '.').substring(0, file.length() - 6).split("\\$")[0];
                try {
                    Class<?> c = loader.loadClass(classname);
                    classes.add(c);
                } catch (Throwable e) {
                    getInstance().getLogger().log(Level.WARNING, "Failed to instantiate " + classname + " from " + file + ".");
                    e.printStackTrace();
                }
            }
        }
        jar.close();
        return classes;
    }

    public void getMethodsAnnotatedWith(final List<Class<?>> types) throws IllegalAccessException, InstantiationException {
        for (Class<?> type : types) {
            for (final Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SpigotTest.class)) {
                    MethodWrapper wrapper = new MethodWrapper(type.newInstance(), method);
                    testMethods.add(wrapper);
                }

                if (method.isAnnotationPresent(SpigotEventTest.class)) {
                    SpigotEventTest annotation = method.getAnnotation(SpigotEventTest.class);

                    EventWrapper wrapper = new EventWrapper(type.newInstance(), method, annotation.event(), annotation.ignoreCancel(), annotation.runOnce());
                    eventMethods.add(wrapper);
                }
            }
        }
    }

    @EventHandler
    public void onEventWithIgnore(Event e) {
        for (EventWrapper wrapper : eventMethods) {
            boolean ignoreCancel = wrapper.ignoreCancelled;
            if (ignoreCancel)
                return;
            invokeEventMethod(e, wrapper);
        }
    }

    @EventHandler
    public void onEventWithoutIgnore(Event e) {
        for (EventWrapper wrapper : eventMethods) {
            boolean ignoreCancel = wrapper.ignoreCancelled;
            if (!ignoreCancel)
                return;
            invokeEventMethod(e, wrapper);
        }
    }

    private void invokeEventMethod(Event e, EventWrapper wrapper) {
        Class<?> eventObject = wrapper.event;
        String eventName = eventObject.getName();
        boolean runOnce = wrapper.runOnce;
        if (!e.getClass().getName().equals(eventName))
            return;
        System.out.println("Test names:" + e.getClass().getName() + " : " + eventName);
        Method method = wrapper.method;
        Type[] types = method.getGenericParameterTypes();
        try {
            if (types.length == 0)
                method.invoke(wrapper.classObject);
            else
                method.invoke(wrapper.classObject, e);
        } catch (IllegalAccessException | InvocationTargetException error) {
            error.printStackTrace();
        }
        if (runOnce)
            eventMethods.remove(wrapper);
    }

    public static TestingAPI getInstance() {
        return instance;
    }
}
