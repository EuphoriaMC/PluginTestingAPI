package net.euphoriamc.plugintesting;

import net.euphoriamc.plugintesting.wrappers.MethodWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ServerLoadListener implements Listener {

    private static final TestingAPI instance = TestingAPI.getInstance();

    static int previousCount;
    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        if (!instance.eventMethods.isEmpty()) {
            TestingAPI.isListenerRegistered = true;
            instance.registeredListenerWithIgnore = new RegisteredListener(this, (listener, event) -> instance.onEventWithIgnore(event),
                    EventPriority.MONITOR, instance, false);
            instance.registeredListenerWithoutIgnore = new RegisteredListener(this, (listener, event) -> instance.onEventWithoutIgnore(event),
                    EventPriority.MONITOR, instance, true);
            registerToHandlers();
        }

        for (MethodWrapper wrapper : instance.testMethods) {
            try {
                wrapper.method.invoke(wrapper.classObject);
            } catch (IllegalAccessException | InvocationTargetException error) {
                error.printStackTrace();
            }
        }
    }

    public static void registerToHandlers() {
        HandlerList.unregisterAll(instance.registeredListenerWithIgnore.getListener());
        HandlerList.unregisterAll(instance.registeredListenerWithoutIgnore.getListener());
        ArrayList<HandlerList> list = HandlerList.getHandlerLists();
        for (HandlerList handler : list) {
            handler.register(instance.registeredListenerWithIgnore);
            handler.register(instance.registeredListenerWithoutIgnore);
        }
        previousCount = list.size();
    }
}
