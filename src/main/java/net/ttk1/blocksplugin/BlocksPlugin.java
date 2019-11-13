package net.ttk1.blocksplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;

public class BlocksPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hi!");

        // config
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        int port = config.getInt("port", 9000);

        // Start JettyServer!
        getLogger().info("starting jetty server...");
        Server server = new Server(port);
        server.setHandler(new BlocksHandler(this));
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("jetty server started!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Bye!");
    }
}
