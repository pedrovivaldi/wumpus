package com.facamp.com747.wumpus.agent;

import java.util.ArrayList;
import java.util.List;

public class TravelPlan {

    List<Dungeon> path;

    public TravelPlan() {
        this.path = new ArrayList<>();
    }

    public TravelPlan(List<Dungeon> path) {
        this.path = path;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public Dungeon push() {
        return path.remove(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Dungeon d : path) {
            sb.append("(" + d.getX() + "," + d.getY() + ")");
        }
        return sb.toString();
    }

}
