package net.ttk1.blocksplugin;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
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
    private final ChunkCache cache;

    BlocksHandler(BlocksPlugin plugin) {
        this.plugin = plugin;
        this.cache = new ChunkCache(20, 20 * 60 * 5);
    }

    private ChunkSnapshot getChunk(World world, int chunkX, int chunkZ) {
        ChunkSnapshot chunk = cache.getChunk(world.getName(), chunkX, chunkZ, world.getFullTime());
        if (chunk == null && world.isChunkGenerated(chunkX, chunkZ)) {
            chunk = world.getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            cache.setChunk(chunk, world.getFullTime());
        }
        return  chunk;
    }

    // 後で何とかする
    private boolean getRelative(World world, int chunkX, int chunkZ, int x, int y, int z, int modX, int modY, int modZ) {
        if (y + modY < -64 || y + modY >= 320) {
            return true;
        }
        ChunkSnapshot chunk = getChunk(world, chunkX + (x + modX + 16) / 16 - 1, chunkZ + (z + modZ + 16) / 16 - 1);
        if (chunk == null) {
            return true;
        } else {
            // return chunk.getBlockType(((x + modX) % 16 + 16) % 16, y + modY, ((z + modZ) % 16 + 16) % 16).isAir();
            return chunk.getBlockType((x + modX + 16) % 16, y + modY, (z + modZ + 16) % 16) == Material.AIR;
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // log
        plugin.getLogger().info(target);

        // process
        baseRequest.setHandled(true);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(" application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();

        // worldの選択
        String worldName = request.getParameter("world_name");
        if (worldName == null) {
            writer.println("[]");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            writer.println("[]");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // chunk の取得
        int chunkX;
        int chunkZ;
        try {
            chunkX = Integer.parseInt(request.getParameter("x"));
            chunkZ = Integer.parseInt(request.getParameter("z"));
        } catch (NumberFormatException e) {
            writer.println("[]");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        ChunkSnapshot chunk = getChunk(world, chunkX, chunkZ);
        if (chunk == null) {
            writer.println("[]");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // ブロックデータ抽出
        writer.print("[");
        for (int x = 0; x < 16; x++) {
            writer.print("[");
            for (int y = -64; y < 320; y++) {
                writer.print("[");
                for (int z = 0; z < 16; z++) {
                    Material blockType = chunk.getBlockType(x, y, z);
                    if (!blockType.isAir() && (getRelative(world, chunkX, chunkZ, x, y, z, -1, 0, 0) ||
                            getRelative(world, chunkX, chunkZ, x, y, z, 1, 0, 0) ||
                            getRelative(world, chunkX, chunkZ, x, y, z, 0, -1, 0) ||
                            getRelative(world, chunkX, chunkZ, x, y, z, 0, 1, 0) ||
                            getRelative(world, chunkX, chunkZ, x, y, z, 0, 0, -1) ||
                            getRelative(world, chunkX, chunkZ, x, y, z, 0, 0, 1))) {
                        writer.print("\"" + blockType + "\"");
                    } else {
                        writer.print("null");
                    }
                    if (z < 15) {
                        writer.print(",");
                    }
                }
                writer.print("]");
                if (y < 319) {
                    writer.print(",");
                }
            }
            writer.print("]");
            if (x < 15) {
                writer.print(",");
            }
        }
        writer.print("]");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }
}
