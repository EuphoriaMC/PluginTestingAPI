package net.euphoriamc.plugintesting;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class TestingAPI extends JavaPlugin implements Listener {

    private static TestingAPI instance;
    private int counter = 0;
    private int rollover = 0;

    private RegisteredListener registeredListener;

    private Set<Method> eventMethods = new HashSet<>();
    private Set<Method> testMethods = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event), EventPriority.MONITOR, this, false);
            for (HandlerList handler : HandlerList.getHandlerLists())
                handler.register(registeredListener);
        }, 1L);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(registeredListener.getListener());
    }

    public void registerFolder(JavaPlugin plugin, String pathToPackage) {
        try {
            Set<Class<?>> classes = getClasses2(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), pathToPackage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<Class<?>> getClasses2(String jarLocation, String pathToPackage) throws Exception {
        Set<Class<?>> classes = new HashSet<>();

        URL jarUrl = new URL("file://" + jarLocation);
        URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl});
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

    @EventHandler
    public void onEvent(Event e) {
        if (counter == 2147483647) {
            rollover++;
            counter = 0;
        }
        counter++;
        System.out.println("Rollover:" + rollover + " Counter:" + counter + " " + e.getEventName());
    }

    public static TestingAPI getInstance() {
        return instance;
    }
}
