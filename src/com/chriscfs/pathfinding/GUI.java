package com.chriscfs.pathfinding;

import com.chriscfs.pathfinding.algorithms.AStar;
import com.chriscfs.pathfinding.algorithms.Dijkstra;
import com.chriscfs.pathfinding.algorithms.PathFinding;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GUI extends JPanel implements ActionListener, KeyListener {

    JFrame window;
    PathFinding pathfinding;
    char currentKey = (char) 0;
    int size = 25;
    boolean showSteps, searching;
    Node startNode, endNode;
    Controls controls;
    MouseMovement movingAdapt = new MouseMovement();
    private Rectangle2D.Float startRect = new Rectangle2D.Float(151, 401, size - 1, size - 1);
    private Rectangle2D.Float endRect = new Rectangle2D.Float(1200 - 174, 401, size - 1, size - 1);
    Map<String, PathFinding> algorithms = new HashMap<>();


    Timer timer = new Timer(100, this);


    public GUI(){
        searching = false;
        showSteps = true;
        controls = new Controls(this);
        addKeyListener(this);
        addMouseListener(movingAdapt);
        addMouseMotionListener(movingAdapt);

        // Set up pathfinding (basic case AStar)
        algorithms.put("A* Search", new AStar(this, size));
        algorithms.put("Dijstra's", new Dijkstra(this,size));
        pathfinding = algorithms.get("A* Search");
        pathfinding.setDiagonal(true);

        //Set up GUI
        window = new JFrame();
        window.setContentPane(this);
        window.setTitle("Pathfinding Visualization");
        window.getContentPane().setPreferredSize(new Dimension(1200, 800));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // add all controls
        controls.addAll();

        this.revalidate();
        this.repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if (pathfinding.isNoPath()) {
            // Set timer for animation
            timer.setDelay(50);
            timer.start();

            // Set text of "start" button to "Reset"
            controls.getButton("start").setText("Reset");

            searching = false;

            controls.getLabel("noPath").setVisible(true);
            this.add(controls.getLabel("noPath"));
            this.revalidate();
        }

        // If pathfinding is complete (found path)
        if (pathfinding.isComplete() && !pathfinding.isNoPath()) {
            controls.getButton("start").setText("Reset");
            controls.getLabel("completed").setVisible(true);
            this.add(controls.getLabel("completed"));
            this.revalidate();
            searching = false;
        }

        // Draws Grid
        for (int i = 0; i < this.getHeight(); i += size) {
            for (int j = 0; j < getWidth(); j += size) {
                g.drawRect(j, i + 75, size, size);
            }
        }

        //Draws all borders
        g.setColor(Color.black);
        for (int i = 0; i < pathfinding.getBorders().size(); i++) {
            g.fillRect(pathfinding.getBorders().get(i).getX() + 1, pathfinding.getBorders().get(i).getY() + 1,
                    size - 1, size - 1);
        }

        // Draws all open Nodes (path finding nodes)
        for (int i = 0; i < pathfinding.getOpen().size(); i++) {
            Node current = pathfinding.getOpen().get(i);
            g.setColor(new Color(20, 240, 100));
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);
        }

        // Draws all closed nodes
        for (int i = 0; i < pathfinding.getClosed().size(); i++) {
            Node current = pathfinding.getClosed().get(i);

            g.setColor(new Color(240, 70, 90));
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);
        }

        // Draw all final path nodes
        for (int i = 0; i < pathfinding.getPath().size(); i++) {
            Node current = pathfinding.getPath().get(i);

            g.setColor(new Color(30, 120, 230));
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);
        }

        // setup start and end node
        if(startNode == null)
            startNode = new Node(150, 400);
        if(endNode == null)
            endNode = new Node(1200-175, 400);

        showSteps = controls.getCheckBox("showSteps").isSelected();
        pathfinding.setDiagonal(controls.getCheckBox("diagonal").isSelected());

        // Draws start node (rectangle)
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setColor(new Color(30, 120, 230));
        graphics2d.fill(startRect);
        graphics2d.setColor(new Color(230, 20, 60));
        graphics2d.fill(endRect);

        // position the controls
        controls.position();

        showSteps = controls.getCheckBox("showSteps").isSelected();
        pathfinding.setDiagonal(controls.getCheckBox("diagonal").isSelected());
    }

    // class for mouse movement (drag and drop or painting borders)
    class MouseMovement extends MouseAdapter {
        private int x;
        private int y;

        //method for editing the walls with the mouse
        private void editWalls(MouseEvent e){
            // If right mouse button is clicked
            if(SwingUtilities.isLeftMouseButton(e)){
                // disable drawing on menu bar
                if(e.getY() >= 75){
                    // create wall
                    int xBorder = e.getX() - (e.getX() % size);
                    int yBorder = e.getY() - (e.getY() % size);

                    Node newBorder = new Node(xBorder, yBorder);
                    pathfinding.addBorder(newBorder);
                    repaint();
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {

                // delete wall
                int mouseBoxX = e.getX() - (e.getX() % size);
                int mouseBoxY = e.getY() - (e.getY() % size);

                // remove wall
                int Location = pathfinding.searchBorder(mouseBoxX, mouseBoxY);
                if (Location != -1)
                    pathfinding.removeBorder(Location);

                repaint();

            }
        }

        @Override
        public void mousePressed(MouseEvent event) {
            x = event.getX();
            y = event.getY();
        }
        @Override

        public void mouseDragged(MouseEvent e) {
            int dx = e.getX() - x;
            int dy = e.getY() - y;

            if (startRect.getBounds2D().contains(x, y)) {
                startRect.x += dx;
                startRect.y += dy;
            }else if(endRect.getBounds2D().contains(x, y)){
                endRect.x += dx;
                endRect.y += dy;
            }else{
                editWalls(e);
            }
            repaint();
            x += dx;
            y += dy;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // makes rectangles snap to grid layout
            int dx = e.getX() - (e.getX() % size);
            int dy = e.getY() - (e.getY() % size);

            int wallNodeLocation = pathfinding.searchBorder(dx, dy);

            // delete wall node if start/end node is placed on top of it.
            if(wallNodeLocation != -1) pathfinding.removeBorder(wallNodeLocation);

            if (startRect.getBounds2D().contains(x, y)) {
                startRect.x = dx + 1;
                startRect.y = dy + 1;
                if(startNode == null){
                    startNode = new Node(dx, dy);
                }else{
                    startNode.setXY(dx, dy);
                }
            }else if(endRect.getBounds2D().contains(x, y)){
                endRect.x = dx + 1;
                endRect.y = dy + 1;
                if(endNode == null){
                    endNode = new Node(dx, dy);
                }else{
                    endNode.setXY(dx, dy);
                }
            }else if(pathfinding.searchBorder(dx, dy) != -1){
                //doesn't allow to place the start or end node on top of a wall node.
                endRect.x = endNode.getX();
                endRect.y = endNode.getY();

                startRect.x = startNode.getX();
                startRect.y = startNode.getY();
            }
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            editWalls(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKey = (char) 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        currentKey = e.getKeyChar();

        // Start if space is pressed
        if (currentKey == KeyEvent.VK_SPACE) {
            startOrStop();
        }
    }

    public boolean showSteps() {
        return showSteps;
    }

    // clearing the whole canvas and stops search algorithm
    private void reset(){
        JButton start = controls.getButton("start");
        start.setBackground(new Color(20, 200, 200));
        start.setText("Start");
        timer.stop();
        pathfinding.getOpen().clear();
        pathfinding.getClosed().clear();
        searching = false;
        controls.getLabel("noPath").setVisible(false);
        controls.getLabel("completed").setVisible(false);
        pathfinding.reset();
        repaint();
    }

    // handles the action when pressing start or stop
    private void startOrStop() {
        JButton startButton = controls.getButton("start");
        if(!searching){
            searching = true;
            start();
            startButton.setBackground(new Color(210, 40, 70));
            startButton.setText("Stop");
        }else{
            searching = false;
            timer.stop();
            stop();
            startButton.setText("Start");
            startButton.setBackground(new Color(20, 200, 200));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(pathfinding.isRunning()){
            pathfinding.findPath(pathfinding.getParent());
        }

        // Actions of the start/stop, clear board buttons
        if(e.getActionCommand() != null){
            switch (e.getActionCommand()) {
                case "Start":
                case "Stop":
                    startOrStop();
                    break;
                case "Reset":
                    reset();
                    break;
                case "New Maze":
                    generateMaze();
                    break;
                case "comboBoxChanged":
                    JComboBox cb = (JComboBox) e.getSource();
                    String algorithm = (String) cb.getSelectedItem();
                    reset();
                    pathfinding = algorithms.get(algorithm);
                    repaint();
                    System.out.println(algorithm);
                    break;
                case "Clear Board":
                    pathfinding.getBorders().clear();
                    reset();
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    // generates a new random maze of boarders
    public void generateMaze(){
        // clears the board and stops the algorithm
        pathfinding.getBorders().clear();
        reset();

        Random randomNum = new Random();
        int count = (this.getWidth()/size * this.getHeight()/size);

        for (int i = 0; i < count/3; i++) {
            int randX = randomNum.nextInt(this.getWidth());
            int randY = randomNum.nextInt((this.getHeight() - 75) + 1) + 75;
            int xBorder = randX - (randX % size);
            int yBorder = randY - (randY % size);
            Node newBorder = new Node(xBorder, yBorder);
            if((newBorder.getX() == startNode.getX() && newBorder.getY() == startNode.getY()) ||
                    (newBorder.getX() == endNode.getX() && newBorder.getY() == endNode.getY()))
                continue;
            else
                pathfinding.addBorder(newBorder);

            repaint();
        }
    }

    // starts path finding algorithm
    public void start(){
        if(startNode != null && endNode != null){
            if(!showSteps){
                pathfinding.start(startNode, endNode);
            } else {
                pathfinding.setup(startNode, endNode);
                setSpeed();
                timer.start();
            }
        }
    }

    // stops path finding algorithm
    public void stop(){
        timer.stop();
    }

    public void setSpeed() {
        int slide = controls.getSpeedSlider().getValue();
        int delay = (int)(2500.0000*Math.pow(1/50.0000, slide/50.0000));
        timer.setDelay(delay);
    }

}
