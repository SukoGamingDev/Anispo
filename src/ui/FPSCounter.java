package ui;

public class FPSCounter {

    // ======================================================
    // ⭐ Time tracking
    // Stores when the last 1-second measurement started
    // ======================================================
    private long lastTime = System.nanoTime();

    // --- frame counter within current second ---
    private int frames = 0;

    // --- last computed FPS value ---
    private int fps = 0;

    // ======================================================
    // ⭐ Call this once per frame/update
    // Counts frames and updates FPS every 1 second
    // ======================================================
    public void frameRendered() {
        frames++;

        long now = System.nanoTime();

        // If one second passed → finalize FPS
        if (now - lastTime >= 1_000_000_000) {
            fps = frames;
            frames = 0;
            lastTime = now;
        }
    }

    // ======================================================
    // ⭐ Retrieve last measured FPS value
    // ======================================================
    public int getFPS() {
        return fps;
    }
}