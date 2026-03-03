import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Window {

    private CardLayout cardLayout;
    private JPanel screenContainer;

    public int screenWidth;
    public int screenHeight;

    private JLayeredPane layeredPane;
    private JFrame mainWindow;

    private BackGroundAnimation bg;
    private MenuMain menu;   // ⭐ keep reference
    private MenuAccountSettings accSettingsMenu;


    public Window() {

        mainWindow = new JFrame("Anispo");
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setSize(1200, 800);
        mainWindow.setContentPane(new JPanel(null));

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        mainWindow.getContentPane().add(layeredPane);

        mainWindow.setVisible(true);

        // ⭐ REAL usable content size
        screenWidth = mainWindow.getContentPane().getWidth();
        screenHeight = mainWindow.getContentPane().getHeight();

        layeredPane.setBounds(0, 0, screenWidth, screenHeight);

        // Background
        bg = new BackGroundAnimation(layeredPane, screenWidth, screenHeight);

        // Screen system
        cardLayout = new CardLayout();
        screenContainer = new JPanel(cardLayout);
        screenContainer.setOpaque(false);
        screenContainer.setBounds(0, 0, screenWidth, screenHeight);

        menu = new MenuMain(this);
        screenContainer.add(menu, "MAIN");

        layeredPane.add(screenContainer, Integer.valueOf(5));

        cardLayout.show(screenContainer, "MAIN");

        accSettingsMenu = new MenuAccountSettings(this);
        screenContainer.add(accSettingsMenu, "MenuAccountSettings");
        accSettingsMenu.resizeTo(screenWidth, screenHeight);

        // ⭐ Force correct initial layout
        menu.resizeTo(screenWidth, screenHeight);

        // Resize handling
        mainWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int newW = mainWindow.getContentPane().getWidth();
                int newH = mainWindow.getContentPane().getHeight();

                if (newW <= 0 || newH <= 0) return;

                screenWidth = newW;
                screenHeight = newH;

                layeredPane.setBounds(0, 0, newW, newH);
                screenContainer.setBounds(0, 0, newW, newH);

                bg.resizeTo(newW, newH);

                // ⭐ THIS was missing before
                menu.resizeTo(newW, newH);
                accSettingsMenu.resizeTo(newW, newH);
            }
        });
    }

    public void showScreen(String name) {
        cardLayout.show(screenContainer, name);
    }
}

