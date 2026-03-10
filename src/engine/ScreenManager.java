package engine;

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

    public ScreenManager(Window window, JLayeredPane pane, int w, int h){

        layout = new CardLayout();
        container = new JPanel(layout);

        container.setOpaque(false);
        container.setBounds(0,0,w,h);

        menu = new MenuMain(window);
        settings = new MenuAccountSettings(window);
        play = new MenuPlay(window);

        container.add(menu,"screens.MenuMain");
        container.add(settings,"screens.MenuAccountSettings");
        container.add(play,"screens.MenuPlay");

        pane.add(container, Integer.valueOf(5));

        layout.show(container,"screens.MenuMain");

        resizeTo(w,h);
    }

    public void show(String screen){
        layout.show(container,screen);
    }

    public void resizeTo(int w, int h){

        container.setBounds(0,0,w,h);

        menu.resizeTo(w,h);
        settings.resizeTo(w,h);
        play.resizeTo(w,h);
    }
}