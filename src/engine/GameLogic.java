package engine;

import game.*;

import java.util.ArrayList;

public class GameLogic {

    public ArrayList<Planet> planets = new ArrayList<>();
    public ArrayList<Fleet> fleets = new ArrayList<>();

    public Player player = new Player(1, java.awt.Color.BLUE);
    public Player enemy  = new Player(2, java.awt.Color.RED);

    public void update(double dt) {

        for (Planet p : planets) {
            p.update(dt);
        }

        for (int i = fleets.size() - 1; i >= 0; i--) {
            Fleet f = fleets.get(i);

            // Fleet.update already handles ship landing + damage
            if (f.update(dt, planets)) {
                fleets.remove(i);
            }
        }
    }

    public void sendShips(Planet from, Planet to, int percent) {

        if (from == null || to == null) return;
        if (from == to) return;
        if (from.getOwner() == null) return;

        int sendAmount = (int) (from.getShips() * (percent / 100.0));

        if (sendAmount < 1 && from.getShips() > 0) {
            sendAmount = 1;
        }

        if (sendAmount <= 0) return;
        if (sendAmount > from.getShips()) {
            sendAmount = from.getShips();
        }

        from.setShips(from.getShips() - sendAmount);

        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return;

        double nx = dx / dist;
        double ny = dy / dist;

        int startX = (int) (from.getX() + nx * from.getRadius());
        int startY = (int) (from.getY() + ny * from.getRadius());

        fleets.add(new Fleet(startX, startY, from, to, sendAmount, from.getOwner()));
    }
}