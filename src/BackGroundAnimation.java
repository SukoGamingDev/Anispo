import javax.swing.*;
import java.awt.*;

public class BackGroundAnimation {

    private final JLayeredPane pane;

    private JLabel backgroundLabel;
    private StarsPanel stars;

    private MovingSpritePanel planetPanel;
    private MovingSpritePanel planetPanel2;
    private MovingSpritePanel planetPanel3;

    // ⭐ Load background ONCE (important)
    private final Image backgroundImage;

    public BackGroundAnimation(JLayeredPane pane, int width, int height) {
        this.pane = pane;

        // Load background image safely once
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/GUI/backgroundcool.png"));
        backgroundImage = bgIcon.getImage();

        // Background label
        backgroundLabel = new JLabel();
        pane.add(backgroundLabel, Integer.valueOf(0));

        // Starfield
        stars = new StarsPanel(140);
        pane.add(stars, Integer.valueOf(1));

        // Planets
        planetPanel  = new MovingSpritePanel("/GUI/planetPNG.png", 0,0,0,2.2f,0,0);
        planetPanel2 = new MovingSpritePanel("/GUI/sandPlanet.png",0,0,0,2.5f,0,0);
        planetPanel3 = new MovingSpritePanel("/GUI/moon.png",      0,0,0,2.5f,0,0);

        pane.add(planetPanel,  Integer.valueOf(2));
        pane.add(planetPanel2, Integer.valueOf(2));
        pane.add(planetPanel3, Integer.valueOf(2));

        resizeTo(width, height);
    }

    public void resizeTo(int width, int height) {

        // ⭐ SAFETY GUARD (prevents crash)
        if (width <= 0 || height <= 0) return;

        // ----------------------------
        // Background scaling
        // ----------------------------
        backgroundLabel.setBounds(0, 0, width, height);

        Image scaled = backgroundImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaled));

        // ----------------------------
        // Starfield
        // ----------------------------
        stars.setBounds(0, 0, width, height);

        // ----------------------------
        // Planet layout math
        // ----------------------------
        float planetWidth  = width / 2.5f;
        float planetHeight = width / 2.5f;

        float startY = height - planetHeight;

        planetPanel.configure(
                0 - planetWidth / 2.6f,
                width / 3f - planetWidth,
                startY - 100,
                planetWidth * 1.5f,
                planetHeight * 1.5f
        );

        planetPanel2.configure(
                width - planetWidth / 1.3f,
                width - planetWidth / 1.3f - 50,
                height / 18f,
                planetWidth / 2f,
                planetHeight / 2f
        );

        planetPanel3.configure(
                width - planetWidth / 1.1f,
                width - planetWidth / 1.3f - 130,
                height / 1.5f,
                planetWidth / 4f,
                planetHeight / 4f
        );

        planetPanel.setBounds(0,0,width,height);
        planetPanel2.setBounds(0,0,width,height);
        planetPanel3.setBounds(0,0,width,height);

        pane.revalidate();
        pane.repaint();
    }
}