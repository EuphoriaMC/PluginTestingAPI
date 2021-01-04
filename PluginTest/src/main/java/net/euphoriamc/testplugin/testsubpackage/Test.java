package net.euphoriamc.testplugin.testsubpackage;

import net.euphoriamc.plugintesting.SpigotEventTest;
import net.euphoriamc.plugintesting.SpigotTest;
import org.bukkit.event.player.PlayerJoinEvent;

public class Test {
    @SpigotEventTest(event = PlayerJoinEvent.class, runOnce = false)
    public void SpigotEventTest(PlayerJoinEvent e) {
        System.out.println("[Event] Test Plugin");
        System.out.println("[Event] " + e.getPlayer());
    }

    @SpigotTest
    public void SpigotTest() {
        System.out.println("[Startup] Test Plugin");
    }
}
