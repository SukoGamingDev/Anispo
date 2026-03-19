package screens;

import engine.Window;
import modes.EliminatorMode;
import modes.HexMode;
import ui.ButtonMaker;
import ui.LabelMaker;

import javax.swing.*;
import java.awt.*;

public class MenuMain extends JPanel {

    final engine.Window window;

    ButtonMaker playButton;
    ButtonMaker tutorialButton;
    ButtonMaker settingsButton;
    ButtonMaker discordButton;
    ButtonMaker quitButton;

    LabelMaker logo;
    LabelMaker bgPanel;

    public MenuMain(Window window2) {
        this.window = window2;
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
        int buttonW_fromWidth = screenWidth / 4;
        int buttonH_fromWidth = buttonW_fromWidth / 4;

        // Height-based scaling (prevent overflow)
        int maxStackHeight = (int)(screenHeight * 0.65);
        int buttonH_fromHeight =
                (maxStackHeight - (buttonCount - 1) * spacing) / buttonCount;

        int buttonW_fromHeight = buttonH_fromHeight * 3;

        // Use whichever fits vertically
        int buttonW = Math.min(buttonW_fromWidth, buttonW_fromHeight);
        int buttonH = buttonW / 4;

        // ===============================
        // STACK POSITIONING
        // ===============================



        // ===============================
        // LOGO (clamped so it never goes off top)
        // ===============================

        int logoW = buttonW * 3;
        int logoH = logoW / 3;


        if(screenHeight>screenWidth){
            //logoW = logoW*2;
            //logoH = logoH*2;

            buttonW = buttonW*2;
            buttonH = buttonH*2;

        }
        int logoX = screenWidth / 2 - logoW / 2;
        int logoY = (int)(screenHeight * 0.03);

        int totalHeight = buttonCount * buttonH + (buttonCount - 1) * spacing;
        int spacingBelowLogo = (int)(logoH * 0.25);

// Top boundary (just under logo)
        int topBound = logoY + logoH + spacingBelowLogo;

// Bottom boundary (give a little margin)
        int bottomBound = (int)(screenHeight * 0.95);

// Available space
        int availableHeight = bottomBound - topBound;

// Total menu height


// ✅ Center menu in that region
        int centerY = topBound + (availableHeight - totalHeight) / 2;

// consistent upward offset
        int offset = (int)(screenHeight * 0.05);

        int startY = centerY - offset;
        int centerX = screenWidth / 2 - buttonW / 2;

        String buttonIcon = "/GUI/BlueButtonHex.png";
        String buttonHoverIcon = "/GUI/DarkBlueButton.png";

        removeAll();

        bgPanel = new LabelMaker("/GUI/BluePanelTall.png");
        logo = new LabelMaker("/GUI/logoGQ.png");

        playButton = new ButtonMaker(buttonW, buttonH,
                buttonIcon,
                "/GUI/GreenButton.png",
                "Play");

        tutorialButton = new ButtonMaker(buttonW, buttonH,
                buttonIcon,
                buttonHoverIcon,
                "Tutorial");

        settingsButton = new ButtonMaker(buttonW, buttonH,
                buttonIcon,
                buttonHoverIcon,
                "Settings");

        discordButton = new ButtonMaker(buttonW, buttonH,
                buttonIcon,
                buttonHoverIcon,
                "Discord");

        quitButton = new ButtonMaker(buttonW, buttonH,
                buttonIcon,
                "/GUI/RedButton.png",
                "Quit");


        playButton.getButton().addActionListener(e -> {
            System.out.println("Play clicked");
            window.getScreenManager().startGame(new EliminatorMode());
            //window.getScreenManager().startGame(new HexMode());
        });

        settingsButton.getButton().addActionListener(e -> {
            System.out.println("Clicked");
            MenuMain.this.setVisible(false);
            System.out.println("Visible? " + MenuMain.this.isVisible());
            window.getScreenManager().show("settings");
        });

        discordButton.getButton().addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://discord.gg/vmZuDGz5R"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        quitButton.getButton().addActionListener(e -> {
            System.exit(0);
        });




        // ===============================
        // PANEL WRAP (perfectly around stack)
        // ===============================

        int panelPaddingX = 25;
        int panelPaddingY = 20;

        int firstButtonY = startY;
        int lastButtonY  = startY + (buttonCount - 1) * (buttonH + spacing);

        int panelTop = firstButtonY - panelPaddingY;
        int panelBottom = lastButtonY + buttonH + panelPaddingY;

        int panelH = panelBottom - panelTop;
        int panelW = buttonW + panelPaddingX * 2;

        int panelX = screenWidth / 2 - panelW / 2;

        bgPanel.setBounds(panelW, panelH, panelX, panelTop);

        bgPanel.setBounds(panelW, panelH, panelX, panelTop);

        // ===============================
        // APPLY BOUNDS
        // ===============================

        logo.setBounds(logoW, logoH, logoX, logoY);

        playButton.setBounds(buttonW, buttonH, centerX, startY);
        tutorialButton.setBounds(buttonW, buttonH, centerX, startY + 1*(buttonH+spacing));
        settingsButton.setBounds(buttonW, buttonH, centerX, startY + 2*(buttonH+spacing));
        discordButton.setBounds(buttonW, buttonH, centerX, startY + 3*(buttonH+spacing));
        quitButton.setBounds(buttonW, buttonH, centerX, startY + 4*(buttonH+spacing));



        // ===============================
        // ADD COMPONENTS (correct Z-order)
        // ===============================
        add(logo.getLabel());
        add(bgPanel.getLabel());
        add(playButton.getButton());
        add(tutorialButton.getButton());
        add(settingsButton.getButton());
        add(discordButton.getButton());
        add(quitButton.getButton());

        setComponentZOrder(logo.getLabel(), getComponentCount() - 1);
        setComponentZOrder(bgPanel.getLabel(), getComponentCount() - 2);

        revalidate();
        repaint();
    }


    public void resizeTo(int screenWidth, int screenHeight) {
        buildUI(screenWidth, screenHeight);
    }
}