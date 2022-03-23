package net.ttk1.blocksplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;

public class BlocksPlugin extends JavaPlugin {
    private Server server;

    @Override
    public void onEnable() {
        getLogger().info("Hi!");

        // config
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        int port = config.getInt("port", 9000);

        // Start JettyServer!
        getLogger().info("starting jetty server...");
        server = new Server(port);
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
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("Bye!");
    }
}
