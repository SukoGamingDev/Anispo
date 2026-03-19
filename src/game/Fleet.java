package game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Fleet {

    int ships;
    Player owner;

    Planet target;
    Planet source;

    public ArrayList<Ship> shipsList = new ArrayList<>();

    private Image shipImage;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Fleet(int x, int y, Planet source, Planet target, int ships, Player owner) {

        this.source = source;
        this.target = target;
        this.ships = ships;
        this.owner = owner;

        int visualShips = calculateVisualShips();

        // 🔥 spawn real ships
        for (int i = 0; i < visualShips; i++) {

            double spread = 8;

            double sx = x + (Math.random() - 0.5) * spread;
            double sy = y + (Math.random() - 0.5) * spread;

            shipsList.add(new Ship(sx, sy));
        }

        // 🔥 load image
        try {
            java.net.URL url = getClass().getResource("/GUI/ship.png");

            if (url != null) {
                shipImage = new ImageIcon(url).getImage();
            } else {
                shipImage = null;
                System.out.println("IMAGE PATH NOT FOUND");
            }

        } catch (Exception e) {
            e.printStackTrace();
            shipImage = null;
        }
    }

    // =========================
    // VISUAL SHIP COUNT
    // =========================
    private int calculateVisualShips() {
        if (ships <= 18) return ships;
        return 18 + (int)((ships - 18) * 0.4);
    }

    public boolean contains(int mx, int my) {

        double radius = 20; // selection radius

        for (Ship s : shipsList) {

            double dx = mx - s.x;
            double dy = my - s.y;

            if (dx * dx + dy * dy <= radius * radius) {
                return true;
            }
        }

        return false;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean update(double dt, ArrayList<Planet> planets) {
        if (target == null) return true; // kill the fleet safely

        Iterator<Ship> it = shipsList.iterator();

        while (it.hasNext()) {

            Ship s = it.next();

            double dx = target.getX() - s.x;
            double dy = target.getY() - s.y;

            double dist = Math.sqrt(dx * dx + dy * dy);

            // 🔥 ship lands individually
            double planetRadius = target.getRadius();

            if (dist < planetRadius) {

                // snap ship to surface (optional but nice)
                double nx = dx / dist;
                double ny = dy / dist;

                s.x = target.getX() - nx * planetRadius;
                s.y = target.getY() - ny * planetRadius;

                target.receiveShip(owner);
                it.remove();
                continue;
            }

            double nx = dx / dist;
            double ny = dy / dist;

            // smooth movement
            s.vx += nx * 400 * dt;
            s.vy += ny * 400 * dt;

            s.vx *= 0.9;
            s.vy *= 0.9;

            s.x += s.vx * dt;
            s.y += s.vy * dt;

            // =========================
            // PLANET AVOIDANCE
            // =========================
            for (Planet p : planets) {

                if (p == target) continue;
                if (p == source) continue;

                double pdx = p.getX() - s.x;
                double pdy = p.getY() - s.y;

                double pdist = Math.sqrt(pdx * pdx + pdy * pdy);

                double avoidRadius = p.getRadius() + 30;

                if (pdist < avoidRadius && pdist > 0) {

                    double strength = (avoidRadius - pdist) / avoidRadius;

                    double px = -pdy / pdist;
                    double py =  pdx / pdist;

                    s.x += px * 100 * dt * strength;
                    s.y += py * 100 * dt * strength;
                }
            }

            // =========================================
// 🔥 HARD COLLISION (ADD THIS AFTER)
// =========================================
            for (Planet p : planets) {

                if (p == target) continue;
                if (p == source) continue;

                double dxp = s.x - p.getX();
                double dyp = s.y - p.getY();

                double distp = Math.sqrt(dxp * dxp + dyp * dyp);

                double minDist = p.getRadius() + 4;

                if (distp < minDist && distp > 0) {

                    double nxp = dxp / distp;
                    double nyp = dyp / distp;

                    // push ship OUTSIDE planet
                    s.x = p.getX() + nxp * minDist;
                    s.y = p.getY() + nyp * minDist;

                    // remove inward velocity
                    double dot = s.vx * nxp + s.vy * nyp;

                    if (dot < 0) {
                        s.vx -= dot * nxp;
                        s.vy -= dot * nyp;
                    }
                }
            }
        }

        // =========================
        // 🔥 SHIP SEPARATION
        // =========================
        for (int i = 0; i < shipsList.size(); i++) {
            Ship a = shipsList.get(i);

            for (int j = i + 1; j < shipsList.size(); j++) {
                Ship b = shipsList.get(j);

                double dx = a.x - b.x;
                double dy = a.y - b.y;

                double dist = Math.sqrt(dx * dx + dy * dy);

                double minDist = 10;

                if (dist > 0 && dist < minDist) {

                    double push = (minDist - dist) / 2;

                    double nx = dx / dist;
                    double ny = dy / dist;

                    a.x += nx * push;
                    a.y += ny * push;

                    b.x -= nx * push;
                    b.y -= ny * push;
                }
            }
        }

        // 🔥 fleet is done when all ships are gone
        return shipsList.isEmpty();
    }

    // =========================
    // DRAW
    // =========================
    public void draw(Graphics2D g) {
        if (target == null) return;

        int size = 12;

        // =========================
        // DRAW SHIPS
        // =========================
        for (Ship s : shipsList) {

            double angle = Math.atan2(
                    target.getY() - s.y,
                    target.getX() - s.x
            );

            if (shipImage != null) {

                Graphics2D gCopy = (Graphics2D) g.create();

                gCopy.translate(s.x, s.y);
                gCopy.rotate(angle);

                gCopy.drawImage(shipImage, -size / 2, -size / 2, size, size, null);

                gCopy.dispose();

            } else {
                g.fillRect((int)s.x - size/2, (int)s.y - size/2, size, size);
            }
        }

        // =========================
        // 🔥 DRAW SELECTION RING (ONCE)
        // =========================
        if (selected && !shipsList.isEmpty()) {

            // center of fleet
            double avgX = 0;
            double avgY = 0;

            for (Ship s : shipsList) {
                avgX += s.x;
                avgY += s.y;
            }

            avgX /= shipsList.size();
            avgY /= shipsList.size();

            int radius = 12; // 🔥 fixed small size

            g.setColor(owner.color); // nicer than white
            g.setStroke(new BasicStroke(2));

            g.drawOval((int)avgX - radius, (int)avgY - radius, radius * 2, radius * 2);
        }
    }

    // =========================
    // GETTERS
    // =========================
    public int getShips() {
        return shipsList.size();
    }

    public Player getOwner() {
        return owner;
    }

    public Planet getTarget() {
        return target;
    }

    public void setTarget(Planet newTarget) {
        if (newTarget == null) return;
        this.target = newTarget;
    }

    private boolean selected = false;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void scalePosition(double scaleX, double scaleY) {

        for (Ship s : shipsList) {
            s.x *= scaleX;
            s.y *= scaleY;
        }
    }

    public boolean isInsideBox(int minX, int minY, int maxX, int maxY) {

        for (Ship s : shipsList) {

            if (s.x >= minX && s.x <= maxX &&
                    s.y >= minY && s.y <= maxY) {
                return true;
            }
        }

        return false;
    }
}