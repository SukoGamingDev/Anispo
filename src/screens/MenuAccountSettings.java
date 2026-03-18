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

        removeAll();

        // ===============================
        // PANEL SIZE (stable base)
        // ===============================
        int panelW = (int)(screenWidth * 0.6);
        int panelH = (int)(screenHeight * 0.5);

        int panelX = screenWidth / 2 - panelW / 2;
        int panelY = screenHeight / 2 - panelH / 2;

        // ===============================
        // PANEL PADDING
        // ===============================
        int panelPaddingX = (int)(panelW * 0.04);
        int panelPaddingY = (int)(panelH * 0.12);

        int usableW = panelW - panelPaddingX * 2;
        int usableH = panelH - panelPaddingY * 2;

        // ===============================
        // GRID SETUP
        // ===============================
        int columns = 4;
        int columnWidth = usableW / columns;

        // Button sizing based on grid
        int gap = (int)(columnWidth * 0.05); // small gap
        int buttonW = columnWidth - gap;
        int buttonH = (int)(buttonW / 2.5);

        // ===============================
        // CREATE UI
        // ===============================
        bgPanel = new LabelMaker("/GUI/BluePanel2.png");
        String ButtonIcons = "/GUI/BlueButtonHex.png";


        playButton = new ButtonMaker(buttonW, buttonH,
                "/GUI/BlueButton.png",
                "/GUI/GreenButton.png",
                "Play");

        accountButton = new ButtonMaker(buttonW, buttonH,
                ButtonIcons,
                "/GUI/DarkBlueButton.png",
                "Account");

        controlButton = new ButtonMaker(buttonW, buttonH,
                ButtonIcons,
                "/GUI/DarkBlueButton.png",
                "Controls");

        graphicsButton = new ButtonMaker(buttonW, buttonH,
                ButtonIcons,
                "/GUI/DarkBlueButton.png",
                "Graphics");

        soundButton = new ButtonMaker(buttonW, buttonH,
                ButtonIcons,
                "/GUI/DarkBlueButton.png",
                "SFX");

        backButton = new ButtonMaker(buttonW, buttonH,
                ButtonIcons,
                "/GUI/RedButton.png",
                "Back");

        // ===============================
        // ACTIONS
        // ===============================
        backButton.getButton().addActionListener(e -> {
            MenuAccountSettings.this.setVisible(false);
            window.getScreenManager().show("screens.MenuMain");
        });

        // ===============================
        // POSITIONING
        // ===============================

        // Top row
        int topOffset = (int)(panelH * 0.06); // tweak this
        int topY = panelY + topOffset;

        int accX = panelX + panelPaddingX + columnWidth * 0 + columnWidth/2 - buttonW/2;
        int controlX = panelX + panelPaddingX + columnWidth * 1 + columnWidth/2 - buttonW/2;
        int graphicsX = panelX + panelPaddingX + columnWidth * 2 + columnWidth/2 - buttonW/2;
        int sfxX = panelX + panelPaddingX + columnWidth * 3 + columnWidth/2 - buttonW/2;

        // Bottom row
        int bottomOffset = (int)(panelH * 0.06); // tweak this
        int bottomY = panelY + panelH - buttonH - bottomOffset;

        int backX = panelX + panelPaddingX + (int)(usableW * 0.25) - buttonW/2;
        int playX = panelX + panelPaddingX + (int)(usableW * 0.75) - buttonW/2;

        // ===============================
        // APPLY BOUNDS
        // ===============================
        bgPanel.setBounds(panelW, panelH, panelX, panelY);

        accountButton.setBounds(buttonW, buttonH, accX, topY);
        controlButton.setBounds(buttonW, buttonH, controlX, topY);
        graphicsButton.setBounds(buttonW, buttonH, graphicsX, topY);
        soundButton.setBounds(buttonW, buttonH, sfxX, topY);

        backButton.setBounds(buttonW, buttonH, backX, bottomY);
        playButton.setBounds(buttonW, buttonH, playX, bottomY);

        // ===============================
        // ADD COMPONENTS
        // ===============================
        add(bgPanel.getLabel());

        add(accountButton.getButton());
        add(controlButton.getButton());
        add(graphicsButton.getButton());
        add(soundButton.getButton());

        add(backButton.getButton());
        add(playButton.getButton());

        setComponentZOrder(bgPanel.getLabel(), getComponentCount() - 1);

        revalidate();
        repaint();
    }

    public void resizeTo(int screenWidth, int screenHeight) {
        buildUI(screenWidth, screenHeight);
    }

}
