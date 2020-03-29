package com.chriscfs.pathfinding.algorithms;

import com.chriscfs.pathfinding.GUI;
import com.chriscfs.pathfinding.Node;

import java.util.Comparator;

public class Dijkstra extends PathFinding {

    public Dijkstra(GUI gui, int size){
        super(gui, size);
    }

    @Override
    public void setup(Node s, Node e){
        running = true;
        startNode = s;
        endNode = e;
        parentNode = s;
        s.setG(0);
        addClosed(s);
    }

    @Override
    public void start(Node s, Node e) {
        running = true;
        startNode = s;
        startNode.setG(0);
        endNode = e;

        // Adding the starting node to the closed list
        addClosed(startNode);

        findPath(startNode);
        completed = true;
    }

    @Override
    public void findPath(Node parent) {
        if (diagonal) {
            // Detects and adds one step of nodes to open list
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 && j == 1) {
                        continue;
                    }
                    int possibleX = (parent.getX() - size) + (size * i);
                    int possibleY = (parent.getY() - size) + (size * j);

                    // See if there are borders in the way
                    int crossBorderX = parent.getX() + (possibleX - parent.getX());
                    int crossBorderY = parent.getY() + (possibleY - parent.getY());

                    // Disables ability to cut corners around borders
                    if (searchBorder(crossBorderX, parent.getY()) != -1
                            || searchBorder(parent.getX(), crossBorderY) != -1 && ((j == 0 || j == 2) && i != 1)) {
                        continue;
                    }

                    calculateNodeValues(possibleX, possibleY, parent);
                }
            }
        }else {
            for (int i = 0; i < 4; i++) {
                int possibleX = (int) Math.round(parent.getX() + (-size * Math.cos(Math.PI/2 * i)));
                int possibleY = (int) Math.round(parent.getY() + (-size * Math.sin(Math.PI/2 * i)));

                calculateNodeValues(possibleX, possibleY, parent);
            }
        }

        gui.repaint();

        // set the new parent node
        parent = lowestGCost();

        if (parent == null) {
            noPath = true;
            running = false;
            gui.repaint();
            return;
        }

        if (Node.isEqual(parent, endNode)) {
            endNode.setParent(parent.getParent());
            connectPath();
            running = false;
            completed = true;
            gui.repaint();
            return;
        }

        // Remove parent node from open list and add it to closed list
        removeOpen(parent);
        addClosed(parent);


        if(!gui.showSteps()) {
            findPath(parent);
        }
        else {
            parentNode = parent;
        }

    }

    private void calculateNodeValues(int possibleX, int possibleY, Node parent) {
        // If the coordinates are outside of the borders
        if (possibleX < 0 || possibleY < 75 || possibleX >= gui.getWidth() || possibleY >= gui.getHeight()) {
            return;
        }

        // check if the node is traversable or a closed node or an open node
        if (searchBorder(possibleX, possibleY) != -1 || searchClosed(possibleX, possibleY) != -1
                || searchOpen(possibleX, possibleY) != -1) {
            return;
        }
        // Create an open node and set parent
        Node openNode = new Node(possibleX, possibleY);
        openNode.setParent(parent);

        // Calculating cost
        int GxMoveCost = openNode.getX() - parent.getX();
        int GyMoveCost = openNode.getY() - parent.getY();
        int gCost = parent.getG();

        if (GxMoveCost != 0 && GyMoveCost != 0) {
            gCost += diagonalMoveCost;
        } else {
            gCost += size;
        }
        openNode.setG(gCost);

        open.add(openNode);
    }

    private Node lowestGCost() {
        if (open.size() > 0) {
            open.sort(Comparator.comparing(Node::getG));
            return open.get(0);
        }
        return null;
    }
}

