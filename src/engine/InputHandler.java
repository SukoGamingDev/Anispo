package engine;

import engine.GameLogic;
import game.Fleet;
import game.Planet;
import game.Ship;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

public class InputHandler extends MouseAdapter implements java.awt.event.KeyListener {

    private final GameLogic logic;

    private Planet dragSource = null;
    private Planet hoverTarget = null;

    private boolean dragging = false;
    private boolean boxSelecting = false;

    private int boxStartX, boxStartY;
    private int boxEndX, boxEndY;

    private int sendPercent = 50;

    ArrayList<Fleet> selectedFleets = new ArrayList<>();

    public InputHandler(GameLogic logic) {
        this.logic = logic;
    }

    public int getSendPercent() {
        return sendPercent;
    }

    public boolean isBoxSelecting() {
        return boxSelecting;
    }

    public void setSendPercent(int percent) {
        this.sendPercent = percent;
    }

    public int getBoxStartX() { return boxStartX; }
    public int getBoxStartY() { return boxStartY; }
    public int getBoxEndX() { return boxEndX; }
    public int getBoxEndY() { return boxEndY; }

    public Planet getDragSource() { return dragSource; }
    public Planet getHoverTarget() { return hoverTarget; }

    public void clearSelection() {
        for (Planet p : logic.planets) {
            p.setSelected(false);
        }
    }



    @Override
    public void mousePressed(MouseEvent e) {

        ((JComponent) e.getSource()).requestFocusInWindow();

        int mx = e.getX();
        int my = e.getY();

        boolean clickedPlanet = false;

        // RIGHT CLICK SEND
        if (SwingUtilities.isRightMouseButton(e)) {

            Planet target = null;

            // find clicked planet
            for (Planet p : logic.planets) {
                if (p.contains(mx, my)) {
                    target = p;
                    break;
                }
            }

            for (Fleet f : selectedFleets) {
                f.setTarget(target);
                f.setSelected(false); // 🔥 ADD THIS
            }
            selectedFleets.clear();

            if (target != null) {

                // =========================
                // 🔥 PRIORITY: REDIRECT FLEETS
                // =========================
                if (!selectedFleets.isEmpty()) {

                    for (Fleet f : selectedFleets) {
                        f.setTarget(target);
                    }

                    selectedFleets.clear();
                    return;
                }

                // =========================
                // OTHERWISE: SEND FROM PLANETS
                // =========================
                for (Planet p : logic.planets) {

                    if (p.isSelected() && p.getOwner() == logic.player) {

                        if (p == target) continue;

                        int sendAmount = (int)(p.getShips() * (sendPercent / 100.0));

                        if (sendAmount < 1 && p.getShips() > 0) {
                            sendAmount = 1;
                        }

                        if (sendAmount > 0) {

                            p.setShips(p.getShips() - sendAmount);

                            logic.fleets.add(new Fleet(
                                    p.getX(),
                                    p.getY(),
                                    p,
                                    target,
                                    sendAmount,
                                    logic.player
                            ));
                        }
                    }
                }

                clearSelection(); // planets
            }

            return;
        }

        // LEFT CLICK
        for (Planet p : logic.planets) {
            if (p.contains(mx, my)) {

                if (p.getOwner() == logic.player) {

                    dragSource = p;
                    dragging = true;

                    boolean anySelected = false;

                    for (Planet other : logic.planets) {
                        if (other.isSelected()) {
                            anySelected = true;
                            break;
                        }
                    }

                    if (!anySelected) {
                        p.setSelected(true);
                    }
                }

                clickedPlanet = true;
                break;
            }
        }

        // BOX SELECT
        if (!clickedPlanet) {

            dragging = false;
            dragSource = null;

            boxSelecting = true;

            boxStartX = mx;
            boxStartY = my;
            boxEndX = mx;
            boxEndY = my;

            clearSelection();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (boxSelecting) {

            int minX = Math.min(boxStartX, boxEndX);
            int maxX = Math.max(boxStartX, boxEndX);
            int minY = Math.min(boxStartY, boxEndY);
            int maxY = Math.max(boxStartY, boxEndY);


            for (Fleet f : selectedFleets) {
                f.setSelected(false);
            }
            selectedFleets.clear();

            // clear previous selection
            for (Planet p : logic.planets) {
                p.setSelected(false);
            }
            selectedFleets.clear();

            boolean anyPlanetSelected = false;

            // =========================
            // 1. SELECT PLANETS FIRST
            // =========================
            for (Planet p : logic.planets) {

                if (p.getOwner() != logic.player) continue;

                int px = p.getX();
                int py = p.getY();

                if (px >= minX && px <= maxX && py >= minY && py <= maxY) {
                    p.setSelected(true);
                    anyPlanetSelected = true;
                }
            }

            // =========================
            // 2. IF NO PLANETS → SELECT FLEETS
            // =========================
            if (!anyPlanetSelected) {

                for (Fleet f : logic.fleets) {

                    if (f.getOwner() != logic.player) continue;

                    if (f.isInsideBox(minX, minY, maxX, maxY)) {
                        f.setSelected(true);
                        selectedFleets.add(f);
                    }
                }
            }

            boxSelecting = false;
        }

        else if (dragging && dragSource != null && hoverTarget != null && hoverTarget != dragSource) {

            for (Planet p : logic.planets) {
                if (p.isSelected() && p.getOwner() == logic.player) {
                    logic.sendShips(p, hoverTarget, sendPercent);
                }
            }

            clearSelection();
        }

        dragging = false;
        dragSource = null;
        hoverTarget = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        int mx = e.getX();
        int my = e.getY();

        if (dragging && !boxSelecting) {

            hoverTarget = null;

            for (Planet p : logic.planets) {
                if (p.contains(mx, my)) {
                    hoverTarget = p;
                    break;
                }
            }
        }

        if (boxSelecting) {
            boxEndX = mx;
            boxEndY = my;
        }
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {

        int key = e.getKeyCode();

        if (key >= java.awt.event.KeyEvent.VK_1 && key <= java.awt.event.KeyEvent.VK_9) {
            sendPercent = (key - java.awt.event.KeyEvent.VK_0) * 10;
        }

        if (key == java.awt.event.KeyEvent.VK_0) {
            sendPercent = 100;
        }
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {}

    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {}
}