package net.ttk1.blocksplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;

public class BlocksPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hi!");

        // Start JettyServer!
        getLogger().info("starting jetty server...");
        Server server = new Server(3000);
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
