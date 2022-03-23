package net.ttk1.blocksplugin;

import org.bukkit.ChunkSnapshot;

public class ChunkCache {
    private final int size;
    private final int ttl;
    private final ChunkSnapshot[][] cache;

    public ChunkCache(int size, int ttl) {
        this.size = size;
        this.ttl = ttl;
        this.cache = new ChunkSnapshot[size][size];
    }

    public ChunkSnapshot getChunk(String worldName, int chunkX, int chunkZ, long currentFullTime) {
        ChunkSnapshot chunk = cache[(chunkX % size + size) % size][(chunkZ % size + size) % size];
        if (chunk == null) {
            return null;
        } else if (!chunk.getWorldName().equals(worldName)) {
            return null;
        } else if (chunk.getX() != chunkX || chunk.getZ() != chunkZ) {
            return null;
        } else if (currentFullTime - chunk.getCaptureFullTime() > ttl) {
            return null;
        } else {
            return chunk;
        }
    }

    public void setChunk(ChunkSnapshot chunk, long currentFullTime) {
        if (chunk != null && currentFullTime - chunk.getCaptureFullTime() <= ttl) {
            cache[(chunk.getX() % size + size) % size][(chunk.getZ() % size + size) % size] = chunk;
        }
    }
}
