package com.chriscfs.pathfinding.algorithms;

import com.chriscfs.pathfinding.GUI;
import com.chriscfs.pathfinding.Node;
import java.util.ArrayList;
import static java.util.Collections.reverse;

// General pathFinding class
public class PathFinding {
    protected static GUI gui;
    protected static int size, diagonalMoveCost;
    protected static Node startNode, endNode, parentNode;
    protected static ArrayList<Node> open, closed, path, borders;
    protected static boolean diagonal,running, noPath, completed;

    public PathFinding(GUI gui, int size){
        PathFinding.gui = gui;
        PathFinding.size = size;

        diagonalMoveCost = (int) (Math.sqrt(2 * (Math.pow(size, 2))));
        diagonal = true;
        running = false;
        completed = false;

        borders = new ArrayList<>();
        open = new ArrayList<>();
        closed = new ArrayList<>();
        path = new ArrayList<>();
    }

    public void setup(Node s, Node e){ 
        running = true;
        startNode = s;
        endNode = e;
        parentNode = s;
        s.setG(0);
        addClosed(s);
    }

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

    public void findPath(Node parent) { }

    public void connectPath() {
        if (getPath().size() == 0) {
            Node parentNode = endNode.getParent();

            while (!Node.isEqual(parentNode, startNode)) {
                addPath(parentNode);

                for (int i = 0; i < getClosed().size(); i++) {
                    Node current = getClosed().get(i);

                    if (Node.isEqual(current, parentNode)) {
                        parentNode = current.getParent();
                        break;
                    }
                }
            }
            reverse(getPath());
        }
    }

    public void reset() {
        open.clear();
        closed.clear();
        path.clear();

        noPath = false;
        running = false;
        completed = false;
    }


    public int searchBorder(int mouseBoxX, int mouseBoxY) {
        int Location = -1;

        for (int i = 0; i < borders.size(); i++) {
            if (borders.get(i).getX() == mouseBoxX && borders.get(i).getY() == mouseBoxY) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public int searchOpen(int possibleX, int possibleY) {
        int Location = -1;

        for (int i = 0; i < open.size(); i++) {
            if (open.get(i).getX() == possibleX && open.get(i).getY() == possibleY) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public int searchClosed(int possibleX, int possibleY) {
        int Location = -1;

        for (int i = 0; i < closed.size(); i++) {
            if (closed.get(i).getX() == possibleX && closed.get(i).getY() == possibleY) {
                Location = i;
                break;
            }
        }
        return Location;
    }

    public void removeBorder(int location) {
        borders.remove(location);
    }

    public void removeOpen(Node node) { open.remove(node); }

    public void removeClosed(int location) {
        closed.remove(location);
    }

    public void addBorder(Node newBorder) {
        if (borders.size() == 0) {
            borders.add(newBorder);
        } else if (!checkBorderDuplicate(newBorder)) {
            borders.add(newBorder);
        }
    }

    public void addOpen(Node node) {
        if (open.size() == 0) {
            open.add(node);
        } else if (!checkOpenDuplicate(node)) {
            open.add(node);
        }
    }

    public void addClosed(Node node) {
        if (closed.size() == 0) {
            closed.add(node);
        } else if (!checkClosedDuplicate(node)) {
            closed.add(node);
        }
    }

    public void addPath(Node node) {
        path.add(node);
    }

    public Node getParent() {
        return parentNode;
    }

    public boolean checkBorderDuplicate(Node newBorder) {
        for (Node border : borders) {
            if (newBorder.getX() == border.getX() && newBorder.getY() == border.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkOpenDuplicate(Node node) {
        for (Node value : open) {
            if (node.getX() == value.getX() && node.getY() == value.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkClosedDuplicate(Node node) {
        for (Node value : closed) {
            if (node.getX() == value.getX() && node.getY() == value.getY()) {
                return true;
            }
        }
        return false;
    }

    public Node getOpenNode(int x, int y) {
        for (Node node : open) {
            if (node.getX() == x && node.getY() == y) {
                return node;
            }
        }
        return null;
    }

    public boolean isRunning() {
        return running;
    }

    public ArrayList<Node> getBorders() {
        return borders;
    }

    public ArrayList<Node> getClosed() {
        return closed;
    }

    public ArrayList<Node> getOpen() {
        return open;
    }

    public ArrayList<Node> getPath() {
        return path;
    }

    public boolean isNoPath() {
        return noPath;
    }

    public boolean isComplete() {
        return completed;
    }

    public void setDiagonal(boolean diagonal) {
        PathFinding.diagonal = diagonal;
    }
}
