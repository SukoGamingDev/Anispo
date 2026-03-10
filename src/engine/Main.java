package engine;

import ui.FPSCounter;

public class Main {
    public static void main(String[] args) {

      Window window = new Window();
        //Menu menu = new Menu(window);
        FPSCounter fpsCounter = new FPSCounter();
        fpsCounter.frameRendered();
    }
}