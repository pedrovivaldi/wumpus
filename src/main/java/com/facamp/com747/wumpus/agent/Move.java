package com.facamp.com747.wumpus.agent;

public class Move {
	public Direction direction;
	public int fromX;
	public int fromY;
	public int toX;
	public int toY;
	public int fromT;
	public int toT;
	
	public Move(Direction d, int fromX, int fromY, int fromT) {
		this.direction = d;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = fromX;
		this.toY = fromY;
		this.fromT = fromT;
		this.toT = fromT+1;
		switch(d) {
		case NORTH:
			toY = fromY + 1;
			break;
		case SOUTH:
			toY = fromY - 1;
			break;
		case EAST:
			toX = fromX -1;
			break;
		case WEST:
			toX = fromX + 1;
		}
	}
	

	public Move(int fromX, int fromY, int toX, int toY, int fromT, int toT) throws Exception {
		super();
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.fromT = fromT;
		this.toT = toT;
		
		if (toX - fromX == 1 && toY - fromY == 0)
			direction = Direction.WEST;
		else if (toX - fromX == -1 && toY - fromY == 0)
			direction = Direction.EAST;
		else if (toX - fromX == 0 && toY - fromY == 1)
			direction = Direction.NORTH;
		else if (toX - fromX == 0 && toY - fromY == -1)
			direction = Direction.SOUTH;
		else
			throw(new Exception("Impossible movement!"));
	}


	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getFromX() {
		return fromX;
	}

	public void setFromX(int fromX) {
		this.fromX = fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public void setFromY(int fromY) {
		this.fromY = fromY;
	}

	public int getToX() {
		return toX;
	}

	public void setToX(int toX) {
		this.toX = toX;
	}

	public int getToY() {
		return toY;
	}

	public void setToY(int toY) {
		this.toY = toY;
	}


	public int getFromT() {
		return fromT;
	}


	public void setFromT(int fromT) {
		this.fromT = fromT;
	}


	public int getToT() {
		return toT;
	}


	public void setToT(int toT) {
		this.toT = toT;
	}


	@Override
	public String toString() {
		return "Move [direction=" + direction + ", fromX=" + fromX + ", fromY=" + fromY + ", toX=" + toX + ", toY=" + toY + ", fromT=" + fromT + ", toT=" + toT + "]";
	}
	

}
