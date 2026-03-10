package graphics;

import ui.FPSCounter;

import javax.swing.*;
import java.awt.*;

public class MovingSpritePanel extends JPanel {

    // ======================================================
    // ⭐ Sprite position + motion bounds
    // ======================================================
    float x, y;
    float startX, endX;
    float width, height;

    // --- motion ---
    float speed;          // signed pixels/sec
    float baseSpeed;
    float minX, maxX;

    // --- pause at edges ---
    boolean paused = false;
    long pauseStart = 0;
    long pauseDuration = 3000;

    // --- image data ---
    Image originalImg;
    Image img;

    // --- fps debug ---
    FPSCounter fpsCounter = new FPSCounter();

    // --- delta timing ---
    long lastUpdate = System.nanoTime();

    public MovingSpritePanel(String path, float startX, float endX, float startY,
                             float speed, float width, float height) {

        this.startX = startX;
        this.endX = endX;
        this.x = startX;
        this.y = startY;
        this.width = width;
        this.height = height;

        this.baseSpeed = speed;

        // Compute motion bounds + direction
        recalcMotion();

        // Load original image once
        originalImg = new ImageIcon(getClass().getResource(path)).getImage();
        scaleImage();

        setOpaque(false);
        setDoubleBuffered(true);

        // =========================================
        // ⭐ Animation timer (~165 FPS target)
        // =========================================
        Timer timer = new Timer(6, e -> update());
        timer.setCoalesce(false);
        timer.start();
    }

    // ======================================================
    // ⭐ Called when window resizes
    // Updates bounds without resetting animation
    // ======================================================
    public void configure(float startX, float endX, float startY,
                          float width, float height){

        this.startX = startX;
        this.endX = endX;
        this.y = startY;
        this.width = width;
        this.height = height;

        recalcMotion();

        // Clamp position so sprite doesn't jump outside bounds
        x = Math.max(minX, Math.min(x, maxX));

        scaleImage();
    }

    // ======================================================
    // ⭐ Recompute motion direction + bounds
    // ======================================================
    private void recalcMotion(){
        float dir = Math.signum(endX - startX);
        this.speed = baseSpeed * (dir == 0 ? 1 : dir);

        this.minX = Math.min(startX, endX);
        this.maxX = Math.max(startX, endX);
    }

    // ======================================================
    // ⭐ Scale sprite image safely
    // ======================================================
    private void scaleImage(){
        if(originalImg == null) return;

        int w = Math.max(1, (int)width);
        int h = Math.max(1, (int)height);

        img = originalImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    // ======================================================
    // ⭐ Animation update
    // Uses delta time for smooth motion
    // ======================================================
    private void update() {

        long now = System.nanoTime();
        float delta = (now - lastUpdate) / 1_000_000_000f;
        lastUpdate = now;

        if (paused) {
            if (System.currentTimeMillis() - pauseStart >= pauseDuration) {
                paused = false;
                speed = -speed;
            }
        } else {
            x += speed * delta;

            // Hit edge → stop + pause
            if (x > maxX) {
                x = maxX;
                paused = true;
                pauseStart = System.currentTimeMillis();
            } else if (x < minX) {
                x = minX;
                paused = true;
                pauseStart = System.currentTimeMillis();
            }
        }

        fpsCounter.frameRendered();
        repaint();
    }

    // ======================================================
    // ⭐ Rendering
    // Panel fills screen but sprite is translated internally
    // ======================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.translate(x, y);

        if(img != null){
            g2.drawImage(img, 0, 0, this);
        }

        g2.dispose();

        // Debug FPS display
        g.drawString("FPS: " + fpsCounter.getFPS(), 10, 20);
    }
}