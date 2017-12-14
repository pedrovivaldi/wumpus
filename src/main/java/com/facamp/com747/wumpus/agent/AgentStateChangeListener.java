package com.facamp.com747.wumpus.agent;

public interface AgentStateChangeListener {
	public void locationHasChanged();
	public void timeHasChanged();
	public void stateHasChanged();
	public void goalHasChanged();
}
