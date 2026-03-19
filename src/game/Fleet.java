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
    private boolean selected = false;

    public Fleet(int x, int y, Planet source, Planet target, int ships, Player owner) {

        this.source = source;
        this.target = target;
        this.ships = ships;
        this.owner = owner;

        int visualShips = calculateVisualShips();

        // 🔥 distribute exact payload
        int base = ships / visualShips;
        int extra = ships % visualShips;

        for (int i = 0; i < visualShips; i++) {

            double spread = 8;

            double sx = x + (Math.random() - 0.5) * spread;
            double sy = y + (Math.random() - 0.5) * spread;

            int payload = base + (i < extra ? 1 : 0);

            shipsList.add(new Ship(sx, sy, payload));
        }

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

    private int calculateVisualShips() {
        if (ships <= 18) return ships;
        return 18 + (int)((ships - 18) * 0.4);
    }

    public boolean update(double dt, ArrayList<Planet> planets) {
        if (target == null) return true;

        Iterator<Ship> it = shipsList.iterator();

        while (it.hasNext()) {

            Ship s = it.next();

            double dx = target.getX() - s.x;
            double dy = target.getY() - s.y;

            double dist = Math.sqrt(dx * dx + dy * dy);
            double planetRadius = target.getRadius();

            // 🔥 LANDING (FIXED)
            if (dist < planetRadius) {

                for (int i = 0; i < s.getPayload(); i++) {
                    target.receiveShip(owner);
                }

                it.remove(); // 🔥 CRITICAL FIX

                continue;
            }

            double nx = dx / dist;
            double ny = dy / dist;

            s.vx += nx * 400 * dt;
            s.vy += ny * 400 * dt;

            s.vx *= 0.9;
            s.vy *= 0.9;

            s.x += s.vx * dt;
            s.y += s.vy * dt;

            // avoidance
            for (Planet p : planets) {

                if (p == target || p == source) continue;

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
// 🔥 HARD COLLISION (FIX PLANET CLIPPING)
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

                    // 🔥 PUSH ship OUTSIDE planet
                    s.x = p.getX() + nxp * minDist;
                    s.y = p.getY() + nyp * minDist;

                    // 🔥 REMOVE inward velocity (prevents suction)
                    double dot = s.vx * nxp + s.vy * nyp;

                    if (dot < 0) {
                        s.vx -= dot * nxp;
                        s.vy -= dot * nyp;
                    }
                }
            }
        }

        // =========================
// 🔥 SHIP SEPARATION (FIX)
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

        return shipsList.isEmpty();
    }

    public void draw(Graphics2D g) {
        if (target == null) return;

        int size = 12;

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

        if (selected && !shipsList.isEmpty()) {

            double avgX = 0;
            double avgY = 0;

            for (Ship s : shipsList) {
                avgX += s.x;
                avgY += s.y;
            }

            avgX /= shipsList.size();
            avgY /= shipsList.size();

            int radius = 12;

            g.setColor(owner.color);
            g.setStroke(new BasicStroke(2));

            g.drawOval((int)avgX - radius, (int)avgY - radius, radius * 2, radius * 2);
        }
    }

    public Player getOwner() { return owner; }
    public Planet getTarget() { return target; }

    public void setTarget(Planet newTarget) {
        if (newTarget != null) this.target = newTarget;
    }

    // =========================
// SELECTION
// =========================
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    // =========================
// BOX SELECTION
// =========================
    public boolean isInsideBox(int minX, int minY, int maxX, int maxY) {

        for (Ship s : shipsList) {

            if (s.x >= minX && s.x <= maxX &&
                    s.y >= minY && s.y <= maxY) {
                return true;
            }
        }

        return false;
    }

    // =========================
// SCALE POSITION (for resizing)
// =========================
    public void scalePosition(double scaleX, double scaleY) {

        for (Ship s : shipsList) {
            s.x *= scaleX;
            s.y *= scaleY;
        }
    }
}