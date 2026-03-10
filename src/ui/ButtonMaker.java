package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonMaker {

    // Button assets
    private final String filepath;
    private final String hoverFilepath;
    private final String text;

    // --- Swing component ---
    private JButton button;

    // --- scaled icons ---
    private ImageIcon normalIcon;
    private ImageIcon hoverIcon;

    // --- original images (cached once) ---
    private Image originalNormal;
    private Image originalHover;

    public ButtonMaker(int sizeX, int sizeY, int locationX, int locationY,
                       String filepath, String hoverFilepath, String text) {

        this.filepath = filepath;
        this.hoverFilepath = hoverFilepath;
        this.text = text;

        // Create transparent Swing button
        button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        // Disable Swing default rollover (we handle manually)
        button.setRolloverEnabled(false);


        // Text styling
        button.setText(text);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setForeground(Color.WHITE);

        // Load original images once
        // Prevents reloading on every resize
        ImageIcon normalTmp = new ImageIcon(getClass().getResource(filepath));
        ImageIcon hoverTmp = new ImageIcon(getClass().getResource(hoverFilepath));

        originalNormal = normalTmp.getImage();
        originalHover = hoverTmp.getImage();

        // Hover behavior
        // Swap icons on mouse enter/exit
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
            public void mouseExited(MouseEvent e) {
                button.setIcon(normalIcon);
            }
        });

        // NOTE:
        // Bounds intentionally NOT set here
        // Menu class controls layout
    }

    // Resize + reposition button
    // Called during window resize
    public void setBounds(int w, int h, int x, int y){

        // Scale icons from cached originals
        normalIcon = new ImageIcon(originalNormal.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        hoverIcon = new ImageIcon(originalHover.getScaledInstance(w, h, Image.SCALE_SMOOTH));

        // Move button
        button.setBounds(x, y, w, h);

        // Force icon refresh (avoids ghosting)
        button.setIcon(null);
        button.setIcon(normalIcon);

        button.revalidate();
        button.repaint();

        // font scaling
        // Keeps text readable at all sizes
        int fontSize = Math.max(14, h / 3);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
    }

    public JButton getButton(){
        return button;
    }
}