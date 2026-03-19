package modes;

import engine.GameLogic;
import engine.GameMode;
import game.Planet;

import java.util.ArrayList;
import java.util.Random;

public class EliminatorMode implements GameMode {

    @Override
    public void generate(GameLogic logic, int width, int height) {



        logic.planets.clear();

        int totalPlanets = 26;
        int half = totalPlanets / 2;
        int padding = 100;

        Random rand = new Random();
        ArrayList<Planet> leftSide = new ArrayList<>();

        // PLAYER HOME
        Planet playerHome = new Planet(padding, height / 2, 80, logic.player, 100, 0);
        leftSide.add(playerHome);

        // GENERATE LEFT SIDE
        while (leftSide.size() < half) {

            int x = rand.nextInt(width / 2 - padding * 2) + padding;
            int y = rand.nextInt(height - padding * 2) + padding;
            int value = rand.nextInt(81) + 20;

            boolean tooClose = false;

            for (Planet p : leftSide) {
                double dx = p.getX() - x;
                double dy = p.getY() - y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < p.getRadius() + (20 + value / 4) + 40) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                int cost = rand.nextInt(51);
                leftSide.add(new Planet(x, y, value, null, 0, cost));
            }
        }

        // MIRROR RIGHT SIDE
        for (Planet p : leftSide) {

            int mx = width - p.getX();
            int my = p.getY();

            Planet mirrored;

            if (p.getOwner() == logic.player) {
                mirrored = new Planet(mx, my, 80, logic.enemy, 100, 0);
            } else {
                mirrored = new Planet(mx, my, p.getRadius(), null, 0, p.getCost());
            }

            logic.planets.add(p);
            logic.planets.add(mirrored);
        }
    }

    @Override
    public void update(GameLogic logic) {
        // nothing yet (Eliminator logic later)
    }
}