import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuMain extends JPanel {

    final Window window;

    ButtonMaker playButton;
    ButtonMaker tutorialButton;
    ButtonMaker settingsButton;
    ButtonMaker discordButton;
    ButtonMaker quitButton;

    LabelMaker logo;
    LabelMaker bgPanel;

    public MenuMain(Window window) {
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

        playButton = new ButtonMaker(buttonW, buttonH, centerX, startY + (buttonH+spacing),
                "/GUI/panel1aa.png","/GUI/panel1ab.png","Play");

        tutorialButton = new ButtonMaker(buttonW, buttonH, centerX, startY + 2*(buttonH+spacing),
                "/GUI/panel1aa.png","/GUI/panel1ab.png","Tutorial");

        settingsButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 3*(buttonH+spacing),
                "/GUI/panel1aa.png","/GUI/panel1ab.png","Settings");

        discordButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 4*(buttonH+spacing),
                "/GUI/panel1aa.png","/GUI/panel1ab.png","Discord");

        quitButton = new ButtonMaker(buttonW, buttonH, centerX,
                startY + 5*(buttonH+spacing),
                "/GUI/panel1ac.png","/GUI/panel1ac.png","Quit");


        settingsButton.getButton().addActionListener(e -> {
            System.out.println("Clicked");
            MenuMain.this.setVisible(false);
            System.out.println("Visible? " + MenuMain.this.isVisible());
            window.showScreen("MenuAccountSettings");
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

        int panelPadding = 25;

        int firstButtonY = startY + (buttonH + spacing);
        int lastButtonY  = startY + 5*(buttonH + spacing);

        int panelTop = firstButtonY - panelPadding / 2;
        int panelBottom = lastButtonY + buttonH + panelPadding / 2;

        int panelH = panelBottom - panelTop;
        int panelW = buttonW + panelPadding;

        int panelX = screenWidth / 2 - panelW / 2;

        bgPanel.setBounds(panelW, panelH, panelX, panelTop);

        // ===============================
        // APPLY BOUNDS
        // ===============================

        logo.setBounds(logoW, logoH, logoX, logoY);

        playButton.setBounds(buttonW, buttonH, centerX, startY + (buttonH+spacing));
        tutorialButton.setBounds(buttonW, buttonH, centerX, startY + 2*(buttonH+spacing));
        settingsButton.setBounds(buttonW, buttonH, centerX, startY + 3*(buttonH+spacing));
        discordButton.setBounds(buttonW, buttonH, centerX, startY + 4*(buttonH+spacing));
        quitButton.setBounds(buttonW, buttonH, centerX, startY + 5*(buttonH+spacing));



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