package modes;

import engine.GameLogic;
import engine.GameMode;
import game.Planet;

public class HexMode implements GameMode {

    private long startTime;
    private boolean finished = false;

    @Override
    public void generate(GameLogic logic, int width, int height) {

        int[] rowSizes = {3, 4, 5, 4, 3};

        int maxCols = 5;
        int rows = rowSizes.length;

// 🔥 dynamic spacing based on screen size
        int spacingX = width / (maxCols + 2);
        int spacingY = height / (rows + 2);

// keep aspect ratio (important)
        int spacing = Math.min(spacingX, spacingY);

        spacingX = spacing;
        spacingY = spacing;

// center of screen
        int centerX = width / 2;
        int centerY = height / 2;

// total height of hex
        int totalHeight = rows * spacingY;

        int startY = centerY - totalHeight / 2;

        Planet centerPlanet = null;

        for (int row = 0; row < rowSizes.length; row++) {

            int cols = rowSizes[row];

            int rowWidth = cols * spacingX;
            int startX = centerX - (rowWidth / 2);

            for (int col = 0; col < cols; col++) {

                int x = startX + col * spacingX;
                int y = startY + row * spacingY;

                Planet p = new Planet(x, y, 80, null, 0, 5);

                logic.planets.add(p);

                if (row == 2 && col == 2) {
                    centerPlanet = p;
                }
            }
        }

        // =========================
        // PLAYER START (CENTER)
        // =========================
        if (centerPlanet != null) {
            centerPlanet.setOwner(logic.player);
            centerPlanet.setShips(100);
        }
    }

    // =========================
    // CALLED EVERY FRAME
    // =========================
    @Override
    public void update(GameLogic logic) {

        if (finished) return;

        // check if all planets are owned
        for (Planet p : logic.planets) {
            if (p.getOwner() != logic.player) {
                return;
            }
        }

        // =========================
        // WIN CONDITION
        // =========================
        finished = true;

        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;

        System.out.println("Finished in: " + seconds + " seconds");
    }
}