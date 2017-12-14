package com.facamp.com747.wumpus.agent;

public class Percept {

    String state;
    boolean stench;
    boolean breeze;
    boolean scream;
    boolean glitter;
    boolean bump;
    int t;
    double points;
    boolean soundOfRockFallingOnTheGround;
    boolean rockOnTheGround;

    public boolean isRockOnTheGround() {
        return rockOnTheGround;
    }

    public void setRockOnTheGround(boolean rockOnTheGround) {
        this.rockOnTheGround = rockOnTheGround;
    }

    public boolean isSoundOfRockFallingOnTheGround() {
        return soundOfRockFallingOnTheGround;
    }

    public void setSoundOfRockFallingOnTheGround(boolean soundOfRockFallingOnTheGround) {
        this.soundOfRockFallingOnTheGround = soundOfRockFallingOnTheGround;
    }

    public Percept(boolean stench, boolean breeze, boolean scream, boolean bump, boolean glitter, int t, double point, boolean rockOnTheGround) {
        super();
        this.stench = stench;
        this.breeze = breeze;
        this.scream = scream;
        this.bump = bump;
        this.glitter = glitter;
        this.t = t;
        this.points = point;
        this.rockOnTheGround = rockOnTheGround;
    }

    public Percept() {
    }

    public boolean isStench() {
        return stench;
    }

    public void setStench(boolean stench) {
        this.stench = stench;
    }

    public boolean isBreeze() {
        return breeze;
    }

    public void setBreeze(boolean breeze) {
        this.breeze = breeze;
    }

    public boolean isScream() {
        return scream;
    }

    public void setScream(boolean scream) {
        this.scream = scream;
    }

    public boolean isBump() {
        return bump;
    }

    public void setBump(boolean bump) {
        this.bump = bump;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double point) {
        this.points = point;
    }

    @Override
    public String toString() {
        return "Percept{" + "state=" + state + ", stench=" + stench + ", breeze=" + breeze + ", scream=" + scream + ", glitter=" + glitter + ", bump=" + bump + ", t=" + t + ", points=" + points + ", soundOfRockFallingOnTheGround=" + soundOfRockFallingOnTheGround + ", rockOnTheGround=" + rockOnTheGround + '}';
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isGlitter() {
        return glitter;
    }

    public void setGlitter(boolean glitter) {
        this.glitter = glitter;
    }

}
