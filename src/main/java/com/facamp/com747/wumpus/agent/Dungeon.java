package com.facamp.com747.wumpus.agent;

public class Dungeon {

    private static int counter = 0;
    public int x;
    public int y;
    public State pit = State.UNKNOWN;
    public State wumpus = State.UNKNOWN;
    public State gold = State.UNKNOWN;
    public State rock = State.UNKNOWN;
    public boolean visited = false;
    public int id;
    public boolean valid = true;

    public State perceptBreeze = State.UNKNOWN;
    public State perceptGlitter = State.UNKNOWN;
    public State perceptStench = State.UNKNOWN;

    TwoDimensionalMentalModelStateChangeListener listener;

    public Dungeon(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        this.id = counter;
        counter++;
    }

    String[] toAscii(boolean agentIsThere) {
        String[] ret = new String[3];

        ret[0] = stateToAscii(pit, "P") + stateToAscii(perceptBreeze, "B") + stateToAscii(gold, "G") + stateToAscii(perceptGlitter, "G");
        ret[1] = stateToAscii(wumpus, "W") + stateToAscii(perceptStench, "S") + (visited ? "+" : "-") + (agentIsThere ? "A" : " ");
        ret[2] = String.format("%+d%+d", x, y);

        return ret;
    }

    String stateToAscii(State state, String yesStr) {
        switch (state) {
            case MAYBE:
                return "!";
            case UNKNOWN:
                return "?";
            case YES:
                return yesStr;
            case NO:
                return ".";
            case DEAD:
                return "*";
        }
        return "-";
    }

    boolean adjacent(Dungeon l2) {
        Dungeon l1 = this;
        if (l1.x == l2.x) {
            return Math.abs(l1.y - l2.y) == 1;
        }
        if (l1.y == l2.y) {
            return Math.abs(l1.x - l2.x) == 1;
        }
        return false;
    }

    public boolean safe(boolean considerWumpusAsThreat) {
        return (!considerWumpusAsThreat || wumpus == State.NO) && pit == State.NO;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public State getPit() {
        return pit;
    }

    public void setPit(State pit) {
        this.pit = pit;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public State getWumpus() {
        return wumpus;
    }

    public void setWumpus(State wumpus) {
        this.wumpus = wumpus;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public State getGold() {
        return gold;
    }

    public void setGold(State gold) {
        this.gold = gold;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public State getPerceptBreeze() {
        return perceptBreeze;
    }

    public void setPerceptBreeze(State perceptBreeze) {
        this.perceptBreeze = perceptBreeze;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public State getPerceptGlitter() {
        return perceptGlitter;
    }

    public void setPerceptGlitter(State perceptGlitter) {
        this.perceptGlitter = perceptGlitter;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    public State getPerceptStench() {
        return perceptStench;
    }

    public void setPerceptStench(State perceptStench) {
        this.perceptStench = perceptStench;
        if (listener != null) {
            listener.modelHasChanged();
        }
    }

    @Override
    public String toString() {
        return "Dungeon [x=" + x + ", y=" + y + ", pit=" + pit + ", wumpus=" + wumpus + ", gold=" + gold + ", visited=" + visited + ", perceptBreeze=" + perceptBreeze + ", perceptGlitter=" + perceptGlitter + ", perceptStench=" + perceptStench + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TwoDimensionalMentalModelStateChangeListener getListener() {
        return listener;
    }

    public void setListener(TwoDimensionalMentalModelStateChangeListener listener) {
        this.listener = listener;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public State getRock() {
        return rock;
    }

    public void setRock(State rock) {
        this.rock = rock;
    }

}
