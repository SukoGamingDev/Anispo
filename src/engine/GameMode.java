package engine;

public interface GameMode {
    void generate(GameLogic logic, int width, int height);

    void update(GameLogic logic); // 🔥 ADD THIS
}