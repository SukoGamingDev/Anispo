import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class StarsPanel extends JPanel {

    // ======================================================
    // ⭐ Star data container
    // Each star tracks position, size, speed, and brightness
    // ======================================================
    private static class Star {
        float x, y;
        float speed;
        int size;
        int alpha;
    }

    private final Star[] stars;
    private final Random rng = new Random();

    // --- timing for delta motion ---
    private long lastUpdate = System.nanoTime();

    // --- previous panel size (used for resize scaling) ---
    private int prevW = -1;
    private int prevH = -1;

    public StarsPanel(int count) {

        setOpaque(false);
        setDoubleBuffered(true);

        // =========================================
        // ⭐ Create star objects (no positions yet)
        // =========================================
        stars = new Star[count];
        for (int i = 0; i < count; i++) {
            stars[i] = new Star();
        }

        // =========================================
        // ⭐ Resize listener
        // Keeps stars visually stable when window resizes
        // =========================================
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleResize();
            }
        });

        // =========================================
        // ⭐ Animation timer (~60 FPS)
        // =========================================
        Timer timer = new Timer(16, e -> update());
        timer.start();
    }

    // ======================================================
    // ⭐ Randomize star properties
    // anywhere = true → spawn inside screen
    // anywhere = false → spawn just off right side
    // ======================================================
    private void randomizeStar(Star s, boolean anywhere) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        s.x = anywhere
                ? rng.nextFloat() * w
                : w + rng.nextFloat() * (w * 0.25f);

        s.y = rng.nextFloat() * h;

        s.size = 2 + rng.nextInt(3);
        s.speed = 10f + rng.nextFloat() * 15f;
        s.alpha = 80 + rng.nextInt(176);
    }

    // ======================================================
    // ⭐ Handle resize
    // Scales star positions so they don’t jump visually
    // ======================================================
    private void handleResize() {
        int w = getWidth();
        int h = getHeight();

        if (w == 0 || h == 0) return;

        if (prevW <= 0 || prevH <= 0) {
            // first valid size → fully randomize
            for (Star s : stars) randomizeStar(s, true);
        } else {
            float scaleX = w / (float) prevW;
            float scaleY = h / (float) prevH;

            for (Star s : stars) {
                s.x *= scaleX;
                s.y *= scaleY;
            }
        }

        prevW = w;
        prevH = h;
    }

    // ======================================================
    // ⭐ Animation update
    // Moves stars left using delta time
    // Respawns stars when they exit screen
    // ======================================================
    private void update() {
        long now = System.nanoTime();
        float delta = (now - lastUpdate) / 1_000_000_000f;
        lastUpdate = now;

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        for (Star s : stars) {
            s.x -= s.speed * delta;

            if (s.x + s.size < 0) {
                randomizeStar(s, false);
            }
        }

        repaint();
    }

    // ======================================================
    // ⭐ Rendering
    // Draws each star with individual alpha + size
    // ======================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Star s : stars) {
            g2.setColor(new Color(255, 255, 255, s.alpha));
            g2.fillOval(Math.round(s.x), Math.round(s.y), s.size, s.size);
        }

        g2.dispose();
    }
}