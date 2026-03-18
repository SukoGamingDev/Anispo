package game;

import java.awt.*;
import java.util.ArrayList;

public class Fleet {

    double x, y;
    double targetX, targetY;

    double speed = 200; // pixels per second

    int ships;
    Player owner;

    Planet target;
    Planet source;
    private int visualShips;
    ArrayList<Point> offsets = new ArrayList<>();

    private int calculateVisualShips() {

        if (ships <= 18) {
            return ships; // 1:1
        }

        // compression after 18
        return 18 + (int)((ships - 18) * 0.4);
    }

    public Fleet(int x, int y, Planet source, Planet target, int ships, Player owner) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.targetX = target.getX();
        this.targetY = target.getY();
        this.ships = ships;
        this.owner = owner;
        this.visualShips = calculateVisualShips();
        this.source = source;
        this.target = target;

        for (int i = 0; i < visualShips; i++) {

            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 6;

            int ox = (int)(Math.cos(angle) * radius);
            int oy = (int)(Math.sin(angle) * radius);

            offsets.add(new Point(ox, oy));
        }

    }

    public boolean update(double dt, ArrayList<Planet> planets) {

        // 1. direction to target
        double dx = target.getX() - x;
        double dy = target.getY() - y;

        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 5) return true;

        double nx = dx / dist;
        double ny = dy / dist;

// 2. ALWAYS move toward target FIRST
        x += nx * speed * dt;
        y += ny * speed * dt;

// 3. THEN apply SMALL avoidance
        for (Planet p : planets) {

            if (p == target) continue;
            if (p == source) continue; // 🔥 IMPORTANT

            double pdx = p.getX() - x;
            double pdy = p.getY() - y;

            double pdist = Math.sqrt(pdx * pdx + pdy * pdy);

            double avoidRadius = p.getRadius() + 80; // 🔥 BIGGER radius

            if (pdist < avoidRadius) {

                double strength = (avoidRadius - pdist) / avoidRadius;

                // 🔥 PERPENDICULAR push (THIS FIXES ORBIT + CLIPPING)
                double px = -pdy / pdist;
                double py =  pdx / pdist;

                x += px * speed * dt * strength * 1.2;
                y += py * speed * dt * strength * 1.2;
            }
        }

        // =========================================
        // NORMAL MOVEMENT
        // =========================================
        x += nx * speed * dt;
        y += ny * speed * dt;

        return false;
    }

    public void draw(Graphics2D g) {

        g.setColor(owner.color);

        for (Point p : offsets) {
            g.fillOval((int)x + p.x - 2, (int)y + p.y - 2, 4, 4);
        }
    }

    public int getShips() {
        return ships;
    }

    public Player getOwner() {
        return owner;
    }

    public Planet getTarget() {
        return target;
    }
}