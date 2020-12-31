package net.euphoriamc.testplugin;

import net.euphoriamc.plugintesting.SpigotEventTest;
import net.euphoriamc.plugintesting.SpigotTest;
import net.euphoriamc.plugintesting.TestingAPI;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static final String MAIN_PACKAGE_PATH = "net.euphoriamc.testplugin";

    @Override
    public void onEnable() {
        TestingAPI testingAPI = (TestingAPI) getServer().getPluginManager().getPlugin("TestingAPI");
        if (testingAPI == null)
            return;
        System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        testingAPI.registerFolder(this,MAIN_PACKAGE_PATH);
        testingAPI.registerFolder(this, MAIN_PACKAGE_PATH + ".testsubpackage");
    }

    @SpigotEventTest(event = PlayerJoinEvent.class, runOnce = false)
    public void SpigotEventTest() {
        System.out.println("[Event] Yeet From Test Plugin");
    }

    @SpigotTest
    public void SpigotTest() {
        System.out.println("[Startup] Yeet From Test Plugin");
    }
}
