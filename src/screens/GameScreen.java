package screens;

import engine.InputHandler;
import game.Fleet;
import game.Planet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import engine.GameLogic;
import engine.GameMode;

public class GameScreen extends JPanel {

    GameLogic logic;
    GameMode mode;
    InputHandler input;

    // 🔥 MUST be class-level (not inside constructor)
    int lastW = 0;
    int lastH = 0;

    public GameScreen(GameMode selectedMode) {

        setBackground(Color.BLACK);

        logic = new GameLogic();
        mode = selectedMode;

        input = new InputHandler(logic);

        addMouseListener(input);
        addMouseMotionListener(input);

        setFocusable(true);

        // =========================
        // 🔥 RESIZE = SCALE (NOT RESET)
        // =========================
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int newW = getWidth();
                int newH = getHeight();

                if (newW <= 0 || newH <= 0) return;

                // first resize (initialize baseline)
                if (lastW == 0 || lastH == 0) {
                    lastW = newW;
                    lastH = newH;
                    return;
                }

                double scaleX = newW / (double) lastW;
                double scaleY = newH / (double) lastH;

                // 🔥 SCALE PLANETS
                for (Planet p : logic.planets) {
                    int newX = (int)(p.getX() * scaleX);
                    int newY = (int)(p.getY() * scaleY);
                    p.setPosition(newX, newY);
                }

                // 🔥 SCALE FLEETS
                for (Fleet f : logic.fleets) {
                    f.scalePosition(scaleX, scaleY);
                }

                lastW = newW;
                lastH = newH;
            }
        });

        // =========================
        // 🔥 KEY BINDINGS (NO FOCUS ISSUES)
        // =========================
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        for (int i = 1; i <= 9; i++) {
            int percent = i * 10;
            String key = "setPercent" + percent;

            im.put(KeyStroke.getKeyStroke(String.valueOf(i)), key);

            am.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    input.setSendPercent(percent);
                }
            });
        }

        im.put(KeyStroke.getKeyStroke("0"), "setPercent100");

        am.put("setPercent100", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                input.setSendPercent(100);
            }
        });

        // =========================
        // GAME LOOP
        // =========================
        new Timer(16, e -> {
            double dt = 0.016;
            logic.update(dt);
            repaint();
        }).start();

        // =========================
        // 🔥 GENERATE MAP ONCE (AFTER SIZE EXISTS)
        // =========================
        SwingUtilities.invokeLater(() -> {
            mode.generate(logic, getWidth(), getHeight());

            // set baseline size AFTER generation
            lastW = getWidth();
            lastH = getHeight();

            // focus fix
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.toFront();
                window.requestFocus();
            }

            requestFocusInWindow();
        });
    }

    // =========================
    // RENDER
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // BOX SELECT
        if (input.isBoxSelecting()) {

            int x = Math.min(input.getBoxStartX(), input.getBoxEndX());
            int y = Math.min(input.getBoxStartY(), input.getBoxEndY());
            int w = Math.abs(input.getBoxStartX() - input.getBoxEndX());
            int h = Math.abs(input.getBoxStartY() - input.getBoxEndY());

            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRect(x, y, w, h);

            g2.setColor(Color.WHITE);
            g2.drawRect(x, y, w, h);
        }

        // PLANETS
        for (Planet p : logic.planets) {
            p.draw(g2);
        }

        // DRAG LINE
        if (input.getDragSource() != null && input.getHoverTarget() != null) {

            Planet dragSource = input.getDragSource();
            Planet hoverTarget = input.getHoverTarget();

            double dx = hoverTarget.getX() - dragSource.getX();
            double dy = hoverTarget.getY() - dragSource.getY();

            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist != 0) {

                double nx = dx / dist;
                double ny = dy / dist;

                int startX = (int)(dragSource.getX() + nx * dragSource.getRadius());
                int startY = (int)(dragSource.getY() + ny * dragSource.getRadius());

                int endX = (int)(hoverTarget.getX() - nx * hoverTarget.getRadius());
                int endY = (int)(hoverTarget.getY() - ny * hoverTarget.getRadius());

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(startX, startY, endX, endY);
                g2.setStroke(new BasicStroke(3));
            }
        }

        // FLEETS
        for (Fleet f : logic.fleets) {
            f.draw(g2);
        }

        // UI TEXT
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        String text = input.getSendPercent() + "%";

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int x = getWidth() - textWidth - 20;
        int y = getHeight() - 20;

        g2.drawString(text, x, y);
    }
}