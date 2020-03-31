package com.chriscfs.pathfinding.algorithms;

import com.chriscfs.pathfinding.GUI;
import com.chriscfs.pathfinding.Node;
import java.util.Comparator;

public class AStar extends PathFinding {

    public AStar(GUI gui, int size){
        super(gui, size);
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
        parent = lowestFCost();

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

        // correction for shortest path during runtime
        if (diagonal) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 1 && j == 1) {
                        continue;
                    }
                    int possibleX = (parent.getX() - size) + (size * i);
                    int possibleY = (parent.getY() - size) + (size * j);
                    Node openCheck = getOpenNode(possibleX, possibleY);

                    if (openCheck != null) {
                        int distanceX = parent.getX() - openCheck.getX();
                        int distanceY = parent.getY() - openCheck.getY();
                        int newG = parent.getG();

                        if (distanceX != 0 && distanceY != 0) {
                            newG += diagonalMoveCost;
                        } else {
                            newG += size;
                        }

                        if (newG < openCheck.getG()) {
                            int s = searchOpen(possibleX, possibleY);
                            open.get(s).setParent(parent);
                            open.get(s).setG(newG);
                            open.get(s).setF(open.get(s).getG() + open.get(s).getH());
                        }
                    }
                }
            }
        }
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

        // Calculating G cost
        int GxMoveCost = openNode.getX() - parent.getX();
        int GyMoveCost = openNode.getY() - parent.getY();
        int gCost = parent.getG();

        if (GxMoveCost != 0 && GyMoveCost != 0) {
            gCost += diagonalMoveCost;
        } else {
            gCost += size;
        }
        openNode.setG(gCost);

        // Calculating H Cost
        int HxMoveCost = Math.abs(endNode.getX() - openNode.getX());
        int HyMoveCost = Math.abs(endNode.getY() - openNode.getY());
        int hCost = HxMoveCost + HyMoveCost;
        openNode.setH(hCost);

        // Calculating F Cost
        int fCost = gCost + hCost;
        openNode.setF(fCost);

        addOpen(openNode);
    }

    private Node lowestFCost() {
        if (open.size() > 0) {
            open.sort(Comparator.comparing(Node::getF));
            return open.get(0);
        }
        return null;
    }

}
