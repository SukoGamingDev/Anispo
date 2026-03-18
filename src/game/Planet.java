package game;

import java.awt.*;

public class Planet {

    int x;
    int y;
    int value; // production + size
    int radius;

    int ships;
    int cost;

    private double productionTimer = 0;

    private Player owner;

    private boolean selected;

    public Player getOwner() {
        return owner;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getRadius() {
        return radius;
    }

    public int getShips() {
        return ships;
    }

    public void setShips(int ships) {
        this.ships = ships;
    }

    public Planet(int x, int y, int value, Player owner, int ships, int cost) {
        this.x = x;
        this.y = y;

        this.value = value;
        this.owner = owner;

        this.ships = ships;
        this.cost = cost;

        this.radius = 20 + (value / 4); // size based on value
    }
    public void receiveShip(Player attacker) {

        if (owner == null) {
            cost--;

            if (cost <= -1) {
                owner = attacker;
                ships = 1;
                cost = 0;
            }
            return;
        }

        if (owner == attacker) {
            ships++;
        } else {
            ships--;

            if (ships <= -1) {
                owner = attacker;
                ships = 1;
            }
        }
    }

    public void draw(Graphics2D g) {

        if (owner == null) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(owner.color);
        }

        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        g.setColor(Color.WHITE);

        String text = (owner == null)
                ? "" + cost
                : "" + ships;

        g.drawString(text, x - 10, y + 5);

        if (selected) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x - radius - 3, y - radius - 3, radius * 2 + 6, radius * 2 + 6);
        }
    }
    public boolean contains(int mx, int my) {
        int dx = mx - x;
        int dy = my - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    public void update(double deltaTime) {

        // only owned planets produce
        if (owner == null) return;

        productionTimer += deltaTime;

        double interval = 50.0 / value; // 🔥 key line

        while (productionTimer >= interval) {
            ships += 1;
            productionTimer -= interval;
        }
    }
}
