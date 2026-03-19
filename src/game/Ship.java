package game;

public class Ship {

    public double x, y;
    public double vx, vy;

    private int payload;

    public Ship(double x, double y, int payload) {
        this.x = x;
        this.y = y;
        this.payload = payload;
    }

    public int getPayload() {
        return payload;
    }
}