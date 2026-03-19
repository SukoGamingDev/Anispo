package engine;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import graphics.BackGroundAnimation;

public class Window {

    public int screenWidth;
    public int screenHeight;

    private JFrame frame;
    private JLayeredPane layeredPane;

    private ScreenManager screenManager;
    private BackGroundAnimation bg;

    public Window() {

        frame = new JFrame("Anispo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200,800);

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        frame.setContentPane(layeredPane);
        frame.setVisible(true);

        // 🔥 ADD THIS BLOCK RIGHT HERE
        SwingUtilities.invokeLater(() -> {
            frame.toFront();
            frame.requestFocus();
        });

        screenWidth = frame.getContentPane().getWidth();
        screenHeight = frame.getContentPane().getHeight();

        bg = new BackGroundAnimation(layeredPane, screenWidth, screenHeight);

        screenManager = new ScreenManager(this, layeredPane, screenWidth, screenHeight);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int newW = frame.getContentPane().getWidth();
                int newH = frame.getContentPane().getHeight();

                if(newW <= 0 || newH <= 0) return;

                screenWidth = newW;
                screenHeight = newH;

                layeredPane.setBounds(0,0,newW,newH);

                bg.resizeTo(newW,newH);
                screenManager.resizeTo(newW,newH);
            }
        });
    }

    public ScreenManager getScreenManager(){
        return screenManager;
    }
}