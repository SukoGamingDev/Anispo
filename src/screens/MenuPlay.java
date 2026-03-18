package screens;

import engine.Window;
import ui.ButtonMaker;

import javax.swing.*;
import java.awt.*;


public class MenuPlay extends JPanel {
    final engine.Window window;



    public MenuPlay(Window window) {
        this.window = window;
        setLayout(null);
        setOpaque(false);
        setOpaque(true);
        setBackground(Color.BLACK); // or any visible color
    }

    ButtonMaker backButton;
    ButtonMaker settingsButton;

    private void buildUI(int screenWidth, int screenHeight) {

        int spacing = 15;
        int buttonCount = 2;
        // Width-based scaling
        int buttonW_fromWidth = screenWidth / 6;
        int buttonH_fromWidth = buttonW_fromWidth / 3;
        // Height-based scaling (prevent overflow)
        int maxStackHeight = (int)(screenHeight * 0.65);
        int buttonH_fromHeight =
                (maxStackHeight - (buttonCount - 1) * spacing) / buttonCount;
        int buttonW_fromHeight = buttonH_fromHeight * 3;
        // Use whichever fits vertically
        int buttonW = Math.min(buttonW_fromWidth, buttonW_fromHeight);
        int buttonH = buttonW / 3;
        // STACK POSITIONING
        int totalHeight = buttonCount * buttonH + (buttonCount - 1) * spacing;
        int startY = screenHeight / 2 - totalHeight / 2 - screenHeight / 40;
        int centerX = screenWidth / 2 - buttonW / 2;

        removeAll();
        //button creation
        settingsButton = new ButtonMaker(buttonW, buttonH,
                "/GUI/BlueButtonHex.png",
                "/GUI/DarkBlueButton.png",
                "Settings");
        backButton = new ButtonMaker(buttonW, buttonH,
                "/GUI/BlueButtonHex.png",
                "/GUI/RedButton.png",
                "Back");

        // Action listeners
        settingsButton.getButton().addActionListener(e -> {
            System.out.println("Clicked");
            MenuPlay.this.setVisible(false);
            System.out.println("Visible? " + MenuPlay.this.isVisible());
            window.getScreenManager().show("screens.MenuAccountSettings");
        });
        backButton.getButton().addActionListener(e -> {
            System.out.println("Clicked");
            MenuPlay.this.setVisible(false);
            System.out.println("Visible? " + MenuPlay.this.isVisible());
            window.getScreenManager().show("MAIN");
        });

        // PANEL WRAP (perfectly around stack)
        int panelPadding = 25;

        int firstButtonY = startY + (buttonH + spacing);
        int lastButtonY  = startY + 5*(buttonH + spacing);

        int panelTop = firstButtonY - panelPadding / 2;
        int panelBottom = lastButtonY + buttonH + panelPadding / 2;

        int panelH = panelBottom - panelTop;
        int panelW = buttonW + panelPadding;
        int panelX = screenWidth / 2 - panelW / 2;

        // APPLY BOUNDS
        backButton.setBounds(buttonW, buttonH,
                0,
                screenHeight - buttonH);
        settingsButton.setBounds(buttonW, buttonH,
                screenWidth - buttonW,
                screenHeight - buttonH);

        // ADD COMPONENTS (correct Z-order)
        add(backButton.getButton());
        add(settingsButton.getButton());

        revalidate();
        repaint();
    }
    public void resizeTo(int screenWidth, int screenHeight) {
        buildUI(screenWidth, screenHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
