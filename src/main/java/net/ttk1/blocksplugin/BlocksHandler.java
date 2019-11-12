package net.ttk1.blocksplugin;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class BlocksHandler extends AbstractHandler {
    private final BlocksPlugin plugin;

    BlocksHandler(BlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // log
        plugin.getLogger().info(target);

        // process
        response.setContentType(" application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();

        // chunkデータの送信
        World world = plugin.getServer().getWorld("world");
        if (world == null) {
            writer.println("{}");
            baseRequest.setHandled(true);
            return;
        }

        Chunk chunk = world.getChunkAt(0, 0);

        writer.print("[");
        for (int x = 0; x < 16; x++) {
            writer.print("[");
            for (int y = 0; y < 256; y++) {
                writer.print("[");
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    writer.print("\"" + block.getType() + "\"");
                    if (z < 15) {
                        writer.print(",");
                    }
                }
                writer.print("]");
                if (y < 255) {
                    writer.print(",");
                }
            }
            writer.print("]");
            if (x < 15) {
                writer.print(",");
            }
        }
        writer.print("]");
        baseRequest.setHandled(true);
    }
}
