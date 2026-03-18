package screens;

import game.Fleet;
import game.Planet;
import game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameScreen extends JPanel {

    ArrayList<Planet> planets = new ArrayList<>();
    ArrayList<Fleet> fleets = new ArrayList<>();
    Player player = new Player(1, Color.BLUE);
    Player enemy  = new Player(2, Color.RED);

    Planet dragSource = null;
    int mouseX = 0;
    int mouseY = 0;
    boolean dragging = false;
    Planet hoverTarget = null;

    int sendPercent = 50; // default = 50%

    boolean boxSelecting = false;

    int boxStartX, boxStartY;
    int boxEndX, boxEndY;

    private void handleArrival(Fleet f) {

        Planet target = f.getTarget();

        for (int i = 0; i < f.getShips(); i++) {
            target.receiveShip(f.getOwner());
        }
    }

    private void clearSelection() {
        for (Planet p : planets) {
            p.setSelected(false);
        }
    }




    public GameScreen() {
        setBackground(Color.BLACK);
        System.out.println("GameScreen created"); // 👈 add this

        planets.add(new Planet(300, 300, 80, player, 100, 0));
        planets.add(new Planet(600, 300, 50, null, 0, 25));
        planets.add(new Planet(900, 300, 80, enemy, 100, 0));

        setFocusable(true);
        requestFocusInWindow();



        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {

                int key = e.getKeyCode();

                if (key >= java.awt.event.KeyEvent.VK_1 && key <= java.awt.event.KeyEvent.VK_9) {
                    sendPercent = (key - java.awt.event.KeyEvent.VK_0) * 10;
                }

                if (key == java.awt.event.KeyEvent.VK_0) {
                    sendPercent = 100;
                }

                System.out.println("Percent: " + sendPercent);
            }
        });




        new Timer(16, e -> {

            double dt = 0.016;

            // update planets
            for (Planet p : planets) {
                p.update(dt);
            }

            // update fleets
            for (int i = fleets.size() - 1; i >= 0; i--) {

                Fleet f = fleets.get(i);

                if (f.update(dt, planets)) {
                    handleArrival(f);
                    fleets.remove(i);
                }
            }

            repaint();

        }).start();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                requestFocusInWindow();

                int mx = e.getX();
                int my = e.getY();

                boolean clickedPlanet = false;

                if (SwingUtilities.isRightMouseButton(e)) {



                    Planet target = null;

                    for (Planet p : planets) {
                        if (p.contains(mx, my)) {
                            target = p;
                            break;
                        }
                    }

                    if (target != null) {



                        for (Planet p : planets) {
                            if (p.isSelected() && p.getOwner() == player) {
                                if (p == target) continue;


                                int sendAmount = (int)(p.getShips() * (sendPercent / 100.0));

                                if (sendAmount < 1 && p.getShips() > 0) {
                                    sendAmount = 1;
                                }

                                if (sendAmount > 0) {

                                    p.setShips(p.getShips() - sendAmount);

                                    double dx = target.getX() - p.getX();
                                    double dy = target.getY() - p.getY();

                                    double dist = Math.sqrt(dx * dx + dy * dy);

                                    double nx = dx / dist;
                                    double ny = dy / dist;

                                    int startX = (int)(p.getX() + nx * p.getRadius());
                                    int startY = (int)(p.getY() + ny * p.getRadius());



                                    fleets.add(new Fleet(
                                            startX,
                                            startY,
                                            p,
                                            target,   // ✅ NOT hoverTarget
                                            sendAmount,
                                            player
                                    ));

                                }
                            }
                        }
                        clearSelection();
                    }


                    return;
                }


                for (Planet p : planets) {
                    if (p.contains(mx, my)) {



                        if (p.getOwner() == player) {


                            dragSource = p;
                            dragging = true;

                            // 🔥 ONLY select it if NOTHING is selected yet
                            boolean anySelected = false;

                            for (Planet other : planets) {
                                if (other.isSelected()) {
                                    anySelected = true;
                                    break;
                                }
                            }

                            if (!anySelected) {
                                p.setSelected(true);
                            }
                        }

                        clickedPlanet = true;
                        break;
                    }
                }

                // 🔥 If NOT clicking a planet → start box select
                if (!clickedPlanet) {

                    dragging = false; // 🔥 stop drag mode
                    dragSource = null;

                    boxSelecting = true;

                    boxStartX = mx;
                    boxStartY = my;
                    boxEndX = mx;
                    boxEndY = my;

                    // clear previous selection
                    for (Planet p : planets) {
                        p.setSelected(false);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                // =========================
                // BOX SELECT FINISH
                // =========================
                if (boxSelecting) {

                    int minX = Math.min(boxStartX, boxEndX);
                    int maxX = Math.max(boxStartX, boxEndX);
                    int minY = Math.min(boxStartY, boxEndY);
                    int maxY = Math.max(boxStartY, boxEndY);

                    for (Planet p : planets) {
                        if (p.getOwner() == player) {

                            int px = p.getX();
                            int py = p.getY();

                            if (px >= minX && px <= maxX && py >= minY && py <= maxY) {
                                p.setSelected(true);
                            }
                        }
                    }

                    boxSelecting = false; // 🔥 MUST BE HERE
                }

                // =========================
                // DRAG SEND
                // =========================
                else if (dragging && dragSource != null && hoverTarget != null && hoverTarget != dragSource) {

                    for (Planet p : planets) {

                        if (p.isSelected() && p.getOwner() == player) {

                            if (p == hoverTarget) continue; // 🔥 PREVENT SELF-SEND

                            int sendAmount = (int)(p.getShips() * (sendPercent / 100.0));

                            if (sendAmount < 1 && p.getShips() > 0) {
                                sendAmount = 1;
                            }

                            if (sendAmount > 0) {

                                p.setShips(p.getShips() - sendAmount);

                                double dx = hoverTarget.getX() - p.getX();
                                double dy = hoverTarget.getY() - p.getY();

                                double dist = Math.sqrt(dx * dx + dy * dy);

                                double nx = dx / dist;
                                double ny = dy / dist;

                                int startX = (int)(p.getX() + nx * p.getRadius());
                                int startY = (int)(p.getY() + ny * p.getRadius());

                                fleets.add(new Fleet(
                                        startX,
                                        startY,
                                        p,
                                        hoverTarget,
                                        sendAmount,
                                        player
                                ));
                            }
                        }
                    }

                    clearSelection();
                }


                // =========================
                // RESET STATES
                // =========================
                dragging = false;
                dragSource = null;
                hoverTarget = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                int mx = e.getX();
                int my = e.getY();

                if (dragging && !boxSelecting) {
                    hoverTarget = null;

                    for (Planet p : planets) {
                        if (p.contains(mx, my)) {
                            hoverTarget = p;
                            break;
                        }
                    }
                }

                if (boxSelecting) {
                    boxEndX = mx;
                    boxEndY = my;
                }
            }
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (boxSelecting) {

            int x = Math.min(boxStartX, boxEndX);
            int y = Math.min(boxStartY, boxEndY);
            int w = Math.abs(boxStartX - boxEndX);
            int h = Math.abs(boxStartY - boxEndY);

            g2.setColor(new Color(255, 255, 255, 80)); // transparent fill
            g2.fillRect(x, y, w, h);

            g2.setColor(Color.WHITE);
            g2.drawRect(x, y, w, h);
        }

        for (Planet p : planets) {
            p.draw(g2); // ✅ clean and works
        }
        if (dragging && dragSource != null && hoverTarget != null) {

            double dx = hoverTarget.getX() - dragSource.getX();
            double dy = hoverTarget.getY() - dragSource.getY();

            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist != 0) {

                double nx = dx / dist;
                double ny = dy / dist;

                int startX = (int)(dragSource.getX() + nx * dragSource.getRadius());
                int startY = (int)(dragSource.getY() + ny * dragSource.getRadius());

                int endX = (int)(hoverTarget.getX() - nx * hoverTarget.getRadius());
                int endY = (int)(hoverTarget.getY() - ny * hoverTarget.getRadius());

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));

                g2.drawLine(startX, startY, endX, endY);
                g2.setStroke(new BasicStroke(3));
            }
        }
        for (Fleet f : fleets) {
            f.draw(g2);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        String text = sendPercent + "%";

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int x = getWidth() - textWidth - 20;
        int y = getHeight() - 20;

        g2.drawString(text, x, y);


    }




}