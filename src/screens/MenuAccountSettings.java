package screens;

import engine.Window;
import ui.ButtonMaker;
import ui.LabelMaker;

import javax.swing.*;


public class MenuAccountSettings extends JPanel{
    final Window window;

    ButtonMaker playButton;
    ButtonMaker backButton;

    ButtonMaker accountButton;
    ButtonMaker controlButton;
    ButtonMaker graphicsButton;
    ButtonMaker soundButton;


    LabelMaker logo;
    LabelMaker bgPanel;

    public MenuAccountSettings(Window window) {
        this.window = window;
        setLayout(null);
        setOpaque(false);
    }

    private void buildUI(int screenWidth, int screenHeight) {

        int spacing = 15;
        int buttonCount = 5;

        // ===============================
        // SMART BUTTON SCALING
        // ===============================

        // Width-based scaling
        int buttonW_fromWidth = screenWidth / 8;
        int buttonH_fromWidth = buttonW_fromWidth / 3;

        // Height-based scaling (prevent overflow)
        int maxStackHeight = (int)(screenHeight * 0.65);
        int buttonH_fromHeight =
                (maxStackHeight - (buttonCount - 1) * spacing) / buttonCount;

        int buttonW_fromHeight = buttonH_fromHeight * 3;

        // Use whichever fits vertically
        int buttonW = Math.min(buttonW_fromWidth, buttonW_fromHeight);
        int buttonH = buttonW / 3;

        // ===============================
        // STACK POSITIONING
        // ===============================

        int totalHeight = buttonCount * buttonH + (buttonCount - 1) * spacing;
        int startY = screenHeight / 2 - totalHeight / 2 - screenHeight / 40;
        int centerX = screenWidth / 2 - buttonW / 2;

        // ===============================
        // LOGO (clamped so it never goes off top)
        // ===============================

        int logoW = buttonW * 6;
        int logoH = logoW / 2;
        int logoX = screenWidth / 2 - logoW / 2;
        int logoY = (int)(screenHeight * 0.03);



        removeAll();

        bgPanel = new LabelMaker("/GUI/Trialbg2bb.png");
        logo = new LabelMaker("/GUI/Anispocool2.png");

        playButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 5*(buttonH+spacing),
                "/GUI/panel1aa.png",
                "/GUI/panel1ab.png",
                "Play");

        accountButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 3*(buttonH+spacing),
                "/GUI/panel1aa.png",
                "/GUI/panel1ab.png",
                "Account");

        controlButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 3*(buttonH+spacing),
                "/GUI/panel1aa.png",
                "/GUI/panel1ab.png",
                "Controls");

        graphicsButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 3*(buttonH+spacing),
                "/GUI/panel1aa.png",
                "/GUI/panel1ab.png",
                "Graphics");

        soundButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 3*(buttonH+spacing),
                "/GUI/panel1aa.png",
                "/GUI/panel1ab.png",
                "SFX");


        backButton = new ButtonMaker(buttonW, buttonH, 0,
                startY + 5*(buttonH+spacing),
                "/GUI/panel1ac.png",
                "/GUI/panel1ac.png",
                "Back");


        backButton.getButton().addActionListener(e -> {
            System.out.println("Clicked");
            MenuAccountSettings.this.setVisible(false);
            System.out.println("Visible? " + MenuAccountSettings.this.isVisible());
            window.getScreenManager().show("MAIN");
        });

        // ===============================
        // PANEL WRAP (perfectly around stack)
        // ===============================

        int panelPadding = 25;

        int firstButtonY = startY + (buttonH + spacing);
        int lastButtonY  = startY + 5*(buttonH + spacing);

        int panelTop = firstButtonY - panelPadding / 2;
        int panelBottom = lastButtonY + buttonH + panelPadding / 2;

        int panelH = panelBottom - panelTop;
        int panelW = buttonW*4 + panelPadding;

        int panelX = screenWidth / 2 - panelW / 2;

        bgPanel.setBounds(panelW, panelH, panelX, panelTop);

        // ===============================
        // APPLY BOUNDS
        // ===============================



        int columns = 4;
        int columnWidth = panelW / columns;
        int buttonRowOffsetY = -10;

        int accX = panelX + columnWidth * 0 + columnWidth/2 - buttonW/2;
        int accY = panelTop + panelPadding + buttonRowOffsetY;

        int controlX = panelX + columnWidth * 1 + columnWidth/2 - buttonW/2;
        int controlY = accY;

        int graphicsX = panelX + columnWidth * 2 + columnWidth/2 - buttonW/2;
        int graphicsY = accY;

        int sfxX = panelX + columnWidth * 3 + columnWidth/2 - buttonW/2;
        int sfxY = accY;

        int bottomColumns = 2;
        int bottomColumnWidth = panelW / bottomColumns;

        int backX = panelX + bottomColumnWidth * 0 + bottomColumnWidth/2 - buttonW/2;
        int playX = panelX + bottomColumnWidth * 1 + bottomColumnWidth/2 - buttonW/2;

        int backY = panelBottom - buttonH - panelPadding;
        int playY = backY;


        logo.setBounds(logoW, logoH, logoX, logoY);

        backButton.setBounds(buttonW, buttonH, backX, backY);
        playButton.setBounds(buttonW, buttonH, playX, playY);

        accountButton.setBounds(buttonW, buttonH, accX, accY);
        controlButton.setBounds(buttonW, buttonH, controlX, controlY);
        graphicsButton.setBounds(buttonW, buttonH, graphicsX, graphicsY);
        soundButton.setBounds(buttonW, buttonH, sfxX, sfxY);


        // ===============================
        // ADD COMPONENTS (correct Z-order)
        // ===============================
        add(logo.getLabel());
        add(bgPanel.getLabel());

        add(playButton.getButton());
        add(backButton.getButton());

        add(accountButton.getButton());
        add(controlButton.getButton());
        add(graphicsButton.getButton());
        add(soundButton.getButton());

        setComponentZOrder(logo.getLabel(), getComponentCount() - 1);
        setComponentZOrder(bgPanel.getLabel(), getComponentCount() - 2);

        revalidate();
        repaint();
    }

    public void resizeTo(int screenWidth, int screenHeight) {
        buildUI(screenWidth, screenHeight);
    }

}
