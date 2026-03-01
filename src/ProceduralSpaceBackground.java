import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ProceduralSpaceBackground extends JPanel {

    private float time = 0f;
    private final Timer timer;

    private BufferedImage buffer;
    private int lastW = -1;
    private int lastH = -1;

    private final Random rand = new Random();

    private final int STAR_COUNT = 300;
    private float[] starX = new float[STAR_COUNT];
    private float[] starY = new float[STAR_COUNT];
    private float[] starDepth = new float[STAR_COUNT];

    public ProceduralSpaceBackground() {
        setOpaque(false);

        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = rand.nextFloat();
            starY[i] = rand.nextFloat();
            starDepth[i] = 0.2f + rand.nextFloat() * 0.8f;
        }

        timer = new Timer(16, e -> {
            time += 0.0025f;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) return;

        if (buffer == null || w != lastW || h != lastH) {
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            lastW = w;
            lastH = h;
        }

        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        drawNebula(g2, w, h);
        drawStars(g2, w, h);

        g2.dispose();

        g.drawImage(buffer, 0, 0, null);
    }

    private void drawNebula(Graphics2D g2, int w, int h) {

        for (int y = 0; y < h; y += 2) {
            for (int x = 0; x < w; x += 2) {

                float nx = (float)x / w;
                float ny = (float)y / h;

                // Multi-layer animated fractal noise
                float n1 = fbm(nx * 2f + time * 0.15f, ny * 2f, 4);
                float n2 = fbm(nx * 4f - time * 0.1f, ny * 4f, 3);
                float n3 = fbm(nx * 1.5f + time * 0.05f, ny * 1.5f, 2);

                float nebula = clamp(n1 * 0.6f + n2 * 0.3f + n3 * 0.4f);

                // Dynamic hue shift across screen
                float hueShift = (nx * 0.5f + ny * 0.5f + time * 0.05f) % 1f;

                Color base = Color.getHSBColor(
                        0.65f + hueShift * 0.3f,  // purple → blue → teal
                        0.8f,
                        nebula
                );

                // Add bright core glow
                float glow = (float)Math.pow(nebula, 3);
                int r = clamp255(base.getRed() + (int)(glow * 120));
                int g = clamp255(base.getGreen() + (int)(glow * 60));
                int b = clamp255(base.getBlue() + (int)(glow * 160));

                g2.setColor(new Color(r, g, b));
                g2.fillRect(x, y, 2, 2);
            }
        }
    }

    private void drawStars(Graphics2D g2, int w, int h) {

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

        for (int i = 0; i < STAR_COUNT; i++) {

            float depth = starDepth[i];
            float drift = time * depth * 20f;

            int x = (int)((starX[i] * w + drift) % w);
            int y = (int)(starY[i] * h);

            int size = (int)(1 + depth * 2);

            int brightness = (int)(150 + depth * 100);

            g2.setColor(new Color(brightness, brightness, 255));
            g2.fillOval(x, y, size, size);
        }
    }

    // ===============================
    // Fractal Brownian Motion
    // ===============================

    private float fbm(float x, float y, int octaves) {
        float total = 0;
        float amplitude = 1;
        float frequency = 1;
        float maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, y * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5f;
            frequency *= 2f;
        }

        return total / maxValue;
    }

    // Basic smooth noise
    private float noise(float x, float y) {
        int xi = (int)x;
        int yi = (int)y;

        float xf = x - xi;
        float yf = y - yi;

        float topRight = random2(xi + 1, yi + 1);
        float topLeft = random2(xi, yi + 1);
        float bottomRight = random2(xi + 1, yi);
        float bottomLeft = random2(xi, yi);

        float u = fade(xf);
        float v = fade(yf);

        float lerp1 = lerp(bottomLeft, bottomRight, u);
        float lerp2 = lerp(topLeft, topRight, u);

        return lerp(lerp1, lerp2, v);
    }

    private float random2(int x, int y) {
        int n = x * 49632 + y * 325176 + 1234567;
        n = (n << 13) ^ n;
        return 1.0f - ((n * (n * n * 15731 + 789221) + 1376312589)
                & 0x7fffffff) / 1073741824f;
    }

    private float fade(float t) {
        return t * t * (3 - 2 * t);
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private int clamp255(int v) {
        return Math.max(0, Math.min(255, v));
    }
}