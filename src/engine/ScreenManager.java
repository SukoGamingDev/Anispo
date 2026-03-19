package engine;

import screens.GameScreen;
import screens.MenuAccountSettings;
import screens.MenuMain;
import screens.MenuPlay;

import javax.swing.*;
import java.awt.*;

public class ScreenManager {

    private CardLayout layout;
    private JPanel container;

    private MenuMain menu;
    private MenuAccountSettings settings;
    private MenuPlay play;

    private GameScreen game; // 🔥 created dynamically

    public ScreenManager(Window window, JLayeredPane pane, int w, int h){

        layout = new CardLayout();
        container = new JPanel(layout);

        container.setOpaque(false);
        container.setBounds(0,0,w,h);

        // =========================
        // MENUS
        // =========================
        menu = new MenuMain(window);
        settings = new MenuAccountSettings(window);
        play = new MenuPlay(window);

        container.add(menu,"menu");
        container.add(settings,"settings");
        container.add(play,"play");

        pane.add(container, Integer.valueOf(5));

        layout.show(container,"menu");

        resizeTo(w,h);
    }

    // =========================
    // 🔥 START GAME (CORE METHOD)
    // =========================
    public void startGame(GameMode mode) {

        // remove old game if it exists
        if (game != null) {
            container.remove(game);
        }

        // create new game with selected mode
        game = new GameScreen(mode);

        container.add(game, "game");

        layout.show(container, "game");

        container.revalidate();
        container.repaint();
    }

    // =========================
    // SCREEN SWITCHING
    // =========================
    public void show(String screen){
        layout.show(container,screen);
    }

    // =========================
    // RESIZING
    // =========================
    public void resizeTo(int w, int h){

        container.setBounds(0,0,w,h);

        menu.resizeTo(w,h);
        settings.resizeTo(w,h);
        play.resizeTo(w,h);

        // 🔥 also resize game if active
        if (game != null) {
            game.setBounds(0,0,w,h);
        }
    }
}