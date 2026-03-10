package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LabelMaker {

    // ======================================================
    // ⭐ Wrapped JLabel that displays the scaled image
    // ======================================================
    private final JLabel label;

    // --- Original image stored as BufferedImage for fast scaling ---
    private final BufferedImage original;

    // --- Used to repaint previous area to avoid "ghost trails" ---
    private Rectangle lastBounds = new Rectangle(0, 0, 0, 0);

    public LabelMaker(String path) {

        // =========================================
        // ⭐ Load image once
        // Convert to BufferedImage for reliable scaling
        // =========================================
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage();
        original = toBufferedImage(img);

        label = new JLabel();
        label.setOpaque(false);
    }

    // ======================================================
    // ⭐ Resize + move label
    // w/h first because your project uses that convention
    // ======================================================
    public void setBounds(int w, int h, int x, int y) {

        // Save previous bounds so we can repaint dirty region
        Rectangle old = lastBounds;

        // Scale image synchronously (avoids async ghosting)
        BufferedImage scaled = scaleBuffered(original, w, h);
        label.setIcon(new ImageIcon(scaled));

        // Move label
        label.setBounds(x, y, w, h);

        // =========================================
        // ⭐ Dirty region repaint
        // Prevents leftover pixels when label moves
        // =========================================
        Container parent = label.getParent();
        if (parent != null) {
            Rectangle now = new Rectangle(x, y, w, h);
            Rectangle dirty = old.union(now);
            parent.repaint(dirty.x, dirty.y, dirty.width, dirty.height);
            lastBounds = now;
        } else {
            lastBounds = new Rectangle(x, y, w, h);
            label.repaint();
        }
    }

    // --- expose wrapped label ---
    public JLabel getLabel() {
        return label;
    }

    // ======================================================
    // ⭐ Convert any Image → BufferedImage
    // Needed for reliable synchronous scaling
    // ======================================================
    private static BufferedImage toBufferedImage(Image img) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        // Safety fallback
        if (w <= 0) w = 1;
        if (h <= 0) h = 1;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bi;
    }

    // ======================================================
    // ⭐ High-quality synchronous scaling
    // Avoids getScaledInstance ghosting + async behavior
    // ======================================================
    private static BufferedImage scaleBuffered(BufferedImage src, int w, int h) {

        if (w <= 0) w = 1;
        if (h <= 0) h = 1;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();

        return out;
    }
}