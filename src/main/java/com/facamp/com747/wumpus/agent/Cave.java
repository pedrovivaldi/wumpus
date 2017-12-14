package com.facamp.com747.wumpus.agent;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cave {

    public static final Logger logger = LoggerFactory.getLogger(Cave.class);

    public int maxCoordinateVertical = Integer.MAX_VALUE;
    public int minCoordinateVertical = -Integer.MAX_VALUE;
    public int maxCoordinateHorizontal = Integer.MAX_VALUE;
    public int minCoordinateHorizontal = -Integer.MAX_VALUE;

    public int maxKnownHorizonVertical = 0;
    public int minKnownHorizonVertical = 0;
    public int maxKnownHorizonHorizontal = 0;
    public int minKnownHorizonHorizontal = 0;

    public int getMaxKnownHorizonVertical() {
        return maxKnownHorizonVertical;
    }

    public void setMaxKnownHorizonVertical(int maxKnownHorizonVertical) {
        this.maxKnownHorizonVertical = maxKnownHorizonVertical;
    }

    public int getMinKnownHorizonVertical() {
        return minKnownHorizonVertical;
    }

    public void setMinKnownHorizonVertical(int minKnownHorizonVertical) {
        this.minKnownHorizonVertical = minKnownHorizonVertical;
    }

    public int getMaxKnownHorizonHorizontal() {
        return maxKnownHorizonHorizontal;
    }

    public void setMaxKnownHorizonHorizontal(int maxKnownHorizonHorizontal) {
        this.maxKnownHorizonHorizontal = maxKnownHorizonHorizontal;
    }

    public int getMinKnownHorizonHorizontal() {
        return minKnownHorizonHorizontal;
    }

    public void setMinKnownHorizonHorizontal(int minKnownHorizonHorizontal) {
        this.minKnownHorizonHorizontal = minKnownHorizonHorizontal;
    }

    List<Dungeon> listOfDungeon = new ArrayList<>();
    Dungeon[][] map;
    TwoDimensionalMentalModelStateChangeListener listener;

    public void addDungeon(Dungeon d) {
        listOfDungeon.add(d);
        maxKnownHorizonHorizontal = Math.max(maxKnownHorizonHorizontal, d.getX());
        minKnownHorizonHorizontal = Math.min(minKnownHorizonHorizontal, d.getX());
        maxKnownHorizonVertical = Math.max(maxKnownHorizonVertical, d.getY());
        minKnownHorizonVertical = Math.min(minKnownHorizonVertical, d.getY());

        d.setListener(listener);
        updateMap();
    }

    public void removeDungeon(Dungeon d) {
        listOfDungeon.remove(d);

        updateMap();
    }

    private void updateMap() {
        map = new Dungeon[maxKnownHorizonVertical - minKnownHorizonVertical + 1][maxKnownHorizonHorizontal - minKnownHorizonHorizontal + 1];
        for (Dungeon d2 : listOfDungeon) {
            int x = d2.getX() - minKnownHorizonHorizontal;
            int y = d2.getY() - minKnownHorizonVertical;
            map[y][x] = d2;
        }
        listener.modelHasChanged();
    }

    class Node {

        Dungeon d;
        Node father;

        Node(Dungeon d, Node father) {
            this.d = d;
            this.father = father;
        }

        @Override
        public String toString() {
            return "(" + d.getX() + "," + d.getY() + ")";
        }
    }

    public TravelPlan findNearestUnvisitedDungeon(int x, int y, State desiredWumpusState, boolean considerWumpusAsThreat) {
        List<Node> fringe;
        List<Node> closed;
        fringe = new ArrayList<>();
        closed = new ArrayList<>();
        int mx = x - minKnownHorizonHorizontal;
        int my = y - minKnownHorizonVertical;
        if (map[my][mx] == null) {
            return null;
        }
        fringe.add(new Node(map[my][mx], null));
        while (!fringe.isEmpty()) {
            Node n = fringe.remove(0);
            mx = n.d.x - minKnownHorizonHorizontal;
            my = n.d.y - minKnownHorizonVertical;
            closed.add(n);
            if (!n.d.isVisited() && n.d.getWumpus() == desiredWumpusState && n.d.getPit() == State.NO && !(n.d.getX() == x && n.d.getY() == y)) {
                ArrayList<Dungeon> ld = new ArrayList<>();
                while (n != null) {
                    if (n.father != null) {
                        ld.add(0, n.d);
                    }
                    n = n.father;
                }
                return new TravelPlan(ld);
            }
            int[] cx = {-1, 1, 0, 0};
            int[] cy = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++) {
                int nmx = mx + cx[i];
                int nmy = my + cy[i];
                if (nmx >= 0 && nmy >= 0 && nmx < map[0].length && nmy < map.length) {
                    Dungeon aux = map[nmy][nmx];
                    if (aux != null && aux.safe(considerWumpusAsThreat) && !inClosedList(aux, closed)) {
                        fringe.add(new Node(aux, n));
                    }
                }
            }
        }
        return null;
    }

    public TravelPlan findNearestMaybePitDungeon(int x, int y, boolean rock) {
        List<Node> fringe;
        List<Node> closed;
        fringe = new ArrayList<>();
        closed = new ArrayList<>();
        int mx = x - minKnownHorizonHorizontal;
        int my = y - minKnownHorizonVertical;
        if (map[my][mx] == null) {
            return null;
        }
        fringe.add(new Node(map[my][mx], null));
        while (!fringe.isEmpty()) {
            Node n = fringe.remove(0);
            mx = n.d.x - minKnownHorizonHorizontal;
            my = n.d.y - minKnownHorizonVertical;
            closed.add(n);
            if (!n.d.isVisited() && n.d.getPit() == State.MAYBE && !(n.d.getX() == x && n.d.getY() == y)) {
                ArrayList<Dungeon> ld = new ArrayList<>();
                if (rock) {
                    if (n.father == null) {
                        return null;
                    }
                    n = n.father; //removendo o maybe-pit do travel plan
                }
                while (n != null) {
                    if (n.father != null) {
                        ld.add(0, n.d);
                    }
                    n = n.father;
                }
                return new TravelPlan(ld);
            }
            int[] cx = {-1, 1, 0, 0};
            int[] cy = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++) {
                int nmx = mx + cx[i];
                int nmy = my + cy[i];
                if (nmx >= 0 && nmy >= 0 && nmx < map[0].length && nmy < map.length) {
                    Dungeon aux = map[nmy][nmx];
                    if (aux != null && !inClosedList(aux, closed)) {
                        fringe.add(new Node(aux, n));
                    }
                }
            }
        }
        return null;
    }

    private boolean inClosedList(Dungeon d, List<Node> closed) {
        for (Node n : closed) {
            if (n.d == d) {
                return true;
            }
        }
        return false;
    }

    public Cave() {
    }

    public int getMaxCoordinateVertical() {
        return maxCoordinateVertical;
    }

    public void setMaxCoordinateVertical(int maxCoordinateVertical) {
        this.maxCoordinateVertical = maxCoordinateVertical;
    }

    public int getMinCoordinateVertical() {
        return minCoordinateVertical;
    }

    public void setMinCoordinateVertical(int minCoordenateVertical) {
        this.minCoordinateVertical = minCoordenateVertical;
    }

    public int getMaxCoordinateHorizontal() {
        return maxCoordinateHorizontal;
    }

    public void setMaxCoordinateHorizontal(int maxCoordinateHorizontal) {
        this.maxCoordinateHorizontal = maxCoordinateHorizontal;
    }

    public int getMinCoordinateHorizontal() {
        return minCoordinateHorizontal;
    }

    public void setMinCoordinateHorizontal(int minCoordinateHorizontal) {
        this.minCoordinateHorizontal = minCoordinateHorizontal;
    }

    @Override
    public String toString() {
        return "Cave [maxCoordinateVertical=" + maxCoordinateVertical + ", minCoordenateVertical=" + minCoordinateVertical + ", maxCoordinateHorizontal=" + maxCoordinateHorizontal + ", minCoordinateHorizontal=" + minCoordinateHorizontal + "]";
    }

    public TwoDimensionalMentalModelStateChangeListener getListener() {
        return listener;
    }

    public void setListener(TwoDimensionalMentalModelStateChangeListener listener) {
        this.listener = listener;
    }

}
