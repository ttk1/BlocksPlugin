package net.ttk1.blocksplugin;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.eclipse.jetty.http.HttpStatus;
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

        // worldの選択
        String worldName = request.getParameter("world_name");
        if (worldName == null) {
            writer.println("[]");
            response.setStatus(HttpStatus.NOT_FOUND_404);
            baseRequest.setHandled(true);
            return;
        }
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            writer.println("[]");
            response.setStatus(HttpStatus.NOT_FOUND_404);
            baseRequest.setHandled(true);
            return;
        }

        // chunkの選択
        Chunk chunk;
        try {
            int x = Integer.parseInt(request.getParameter("x"));
            int z = Integer.parseInt(request.getParameter("z"));
            chunk = world.getChunkAt(x, z);
        } catch (NumberFormatException e) {
            writer.println("[]");
            response.setStatus(HttpStatus.NOT_FOUND_404);
            baseRequest.setHandled(true);
            return;
        }

        // ブロックデータ抽出
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
        response.setStatus(HttpStatus.OK_200);
        baseRequest.setHandled(true);
    }
}
