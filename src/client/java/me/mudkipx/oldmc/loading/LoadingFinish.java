package me.mudkipx.oldmc.loading;

/** Tracks presentation on one loading screen. Times come from a monotonic clock. */
public final class LoadingFinish {
    private static final long FULL_MS = 100;
    private static final long TERRAIN_MS = 150;
    private boolean sawGeneration;
    private long fullSince = -1;
    private long terrainSince = -1;
    private long readySince = -1;

    public boolean renderTerrain(boolean generated, boolean ready, long now) {
        if (!generated && !sawGeneration) {
            if (terrainSince < 0) terrainSince = now;
            return true;
        }
        if (generated && !sawGeneration) {
            sawGeneration = true;
            terrainSince = -1;
        }
        if (ready || !generated || fullSince >= 0) {
            if (fullSince < 0) fullSince = now;
            if (now - fullSince >= FULL_MS && terrainSince < 0) terrainSince = now;
        }
        return terrainSince >= 0;
    }

    public boolean mayClose(long now) {
        if (readySince < 0) readySince = now;
        // Do not wait indefinitely for frames when the window is minimized.
        return (terrainSince >= 0 && now - terrainSince >= TERRAIN_MS) || now - readySince >= 1000;
    }

    public boolean full() {
        return fullSince >= 0;
    }
}
