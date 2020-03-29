package com.chriscfs.pathfinding;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// class for buttons, sliders, drop down menu
public class Controls {
    private GUI gui;
    private JButton startButton, clearButton, mazeButton;
    private JComboBox<String> algorithmBox;
    private JSlider speedSlider;
    private JLabel noPath, completed;
    private JCheckBox showSteps, diagonal;
    private ArrayList<JCheckBox> checkBoxes;
    private ArrayList<JLabel> labels;
    private ArrayList<JButton> buttons;
    private ArrayList<JComboBox<String>> algorithms;
    Dimension npD;

    public Controls(GUI gui){
        this.gui = gui;
        buttons = new ArrayList<>();
        labels = new ArrayList<>();
        algorithms = new ArrayList<>();
        checkBoxes = new ArrayList<>();

        // setup JLabels
        noPath = new JLabel("NO PATH ...");
        noPath.setName("noPath");
        noPath.setForeground(new Color(240, 200, 10));
        noPath.setVisible(false);       //transparent by default
        noPath.setFont(new Font("arial", Font.BOLD, 80));
        npD = noPath.getPreferredSize();

        completed = new JLabel("COMPLETED");
        completed.setName("completed");
        completed.setForeground(new Color(240, 200, 10));
        completed.setVisible(false);       //transparent by default
        completed.setFont(new Font("arial", Font.BOLD, 80));
        npD = completed.getPreferredSize();

        // add JLabels to list
        labels.add(noPath);
        labels.add(completed);

        // setup buttons
        startButton = new JButton();
        startButton.setText("Start");
        startButton.setName("start");
        startButton.setFocusable(false);
        startButton.addActionListener(gui);
        startButton.setMargin(new Insets(10,20,10,20));
        startButton.setBackground(new Color(20, 200, 200));
        startButton.setFont(new Font("arial", Font.PLAIN, 17));
        startButton.setVisible(true);

        clearButton = new JButton();
        clearButton.setText("Clear Board");
        clearButton.setName("clear");
        clearButton.setFocusable(false);
        clearButton.addActionListener(gui);
        clearButton.setMargin(new Insets(10,20,10,20));
        clearButton.setBackground(new Color(240, 130, 0));
        clearButton.setFont(new Font("arial", Font.PLAIN, 17));
        clearButton.setVisible(true);

        mazeButton = new JButton();
        mazeButton.setText("New Maze");
        mazeButton.setName("maze");
        mazeButton.setFocusable(false);
        mazeButton.addActionListener(gui);
        mazeButton.setMargin(new Insets(10,20,10,20));
        mazeButton.setBackground(new Color(240, 130, 0));
        mazeButton.setFont(new Font("arial", Font.PLAIN, 17));
        mazeButton.setVisible(true);

        // Add JButtons to list
        buttons.add(startButton);
        buttons.add(clearButton);
        buttons.add(mazeButton);

        // Set up JSliders
        speedSlider = new JSlider();
        speedSlider.setName("speed");
        speedSlider.setOpaque(false);
        speedSlider.setVisible(true);
        speedSlider.setFocusable(false);
        speedSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            speedSlider.setValue(source.getValue());
            gui.setSpeed();
            gui.repaint();
        });

        // Set up JCheckBox
        showSteps = new JCheckBox();
        showSteps.setText("Show Steps");
        showSteps.setName("showSteps");
        showSteps.setFont(new Font("arial", Font.PLAIN, 17));
        showSteps.setSelected(true);
        showSteps.setOpaque(false);
        showSteps.setFocusable(false);
        showSteps.setVisible(true);

        diagonal = new JCheckBox();
        diagonal.setText("Diagonal");
        diagonal.setName("diagonal");
        diagonal.setFont(new Font("arial", Font.PLAIN, 17));
        diagonal.setOpaque(false);
        diagonal.setSelected(true);
        diagonal.setFocusable(false);
        diagonal.setVisible(true);

        // Add JCheckBoxes to list
        checkBoxes.add(showSteps);
        checkBoxes.add(diagonal);

        // Set up JComboBox
        algorithmBox = new JComboBox<>();
        algorithmBox.setName("Algorithms");
        algorithmBox.addItem("A* Search");
        algorithmBox.addItem("Dijstra's");
        algorithmBox.addActionListener(gui);

        algorithms.add(algorithmBox);
    }

    // gets JSlider
    public JSlider getSpeedSlider() {
        return speedSlider;
    }

    // gets specific JButton
    public JButton getButton(String name){
        for (JButton button : buttons) {
            if (button.getName().equals(name))
                return button;
        }
        return null;
    }

    // gets specific JCheckBox
    public JCheckBox getCheckBox(String name){
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.getName().equals(name))
                return checkBox;
        }
        return null;
    }

    // gets specific JLabel
    public JLabel getLabel(String name){
        for (JLabel label : labels) {
            if (label.getName().equals(name))
                return label;
        }
        return null;
    }

    // gets JComboBox
    public JComboBox<String> getAlgorithmBox() {
        return algorithmBox;
    }

    public void position() {
        // Center Labels
        noPath.setBounds((int)((gui.getWidth()/2)-(npD.getWidth()/2)),
                (gui.getHeight()/2)-75,
                (int)npD.getWidth(), (int)npD.getHeight());

        completed.setBounds((int)((gui.getWidth()/2)-(npD.getWidth()/2)),
                (gui.getHeight()/2)-75,
                (int)npD.getWidth(), (int)npD.getHeight());

        // Set button bounds
        startButton.setBounds((int) (gui.getWidth()*0.02), 15, startButton.getWidth(), startButton.getHeight());
        clearButton.setBounds((int) (gui.getWidth()*0.12), 15, clearButton.getWidth(), clearButton.getHeight());
        mazeButton.setBounds((int) (gui.getWidth()*0.27), 15, mazeButton.getWidth(), mazeButton.getHeight());

        // Set check box bounds
        showSteps.setBounds((int) (gui.getWidth()*0.42), 30, showSteps.getWidth(), showSteps.getHeight());
        diagonal.setBounds((int) (gui.getWidth()*0.44 + 150) , 30, diagonal.getWidth(), diagonal.getHeight());

        // Set slider bounds
        speedSlider.setBounds((int) (gui.getWidth()*0.68), 33, speedSlider.getWidth(), speedSlider.getHeight());

        // Set JComboBox bounds
        algorithmBox.setBounds((int) (gui.getWidth()*0.88), 30, algorithmBox.getWidth(), algorithmBox.getHeight());
    }

    public void addAll(){
        gui.add(startButton);
        gui.add(clearButton);
        gui.add(mazeButton);
        gui.add(speedSlider);
        gui.add(noPath);
        gui.add(completed);
        gui.add(algorithmBox);
        gui.add(diagonal);
        gui.add(showSteps);
    }
}
