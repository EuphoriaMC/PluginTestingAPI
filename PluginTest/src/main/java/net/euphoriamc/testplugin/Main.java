package net.euphoriamc.testplugin;

import net.euphoriamc.plugintesting.TestingAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static final String MAIN_PACKAGE_PATH = "net.euphoriamc.testplugin";

    @Override
    public void onEnable() {
        TestingAPI testingAPI = (TestingAPI) getServer().getPluginManager().getPlugin("TestingAPI");
        if (testingAPI == null)
            return;
        testingAPI.registerFolder(this, MAIN_PACKAGE_PATH + ".testsubpackage");
    }
}
