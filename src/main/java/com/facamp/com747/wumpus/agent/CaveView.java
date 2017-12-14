package com.facamp.com747.wumpus.agent;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

public class CaveView extends JFrame implements AgentStateChangeListener, TwoDimensionalMentalModelStateChangeListener {

	JTextPane jtp = new JTextPane();
	JLabel iteractionJL;
	JLabel winsJL;
	JLabel pointsJL;
	
	Cave cave;
	Agent agent;
	
	public CaveView() {		
		configureLayout();
		startVisualization();
	}
	
	private void configureLayout() {
		iteractionJL = new JLabel();
		winsJL = new JLabel();
		pointsJL = new JLabel();
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(0, 2));
		jp.add(new JLabel("Iteraction"));
		jp.add(iteractionJL);
		jp.add(new JLabel("Wins"));
		jp.add(winsJL);
		jp.add(new JLabel("Points"));
		jp.add(pointsJL);

		JPanel jp2 = new JPanel(new BorderLayout());
		jp2.add(jp,BorderLayout.EAST);
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Font f = new Font(Font.MONOSPACED, 6, 8);
		jtp.setFont(f);
		this.setLayout(new BorderLayout());
		
		this.add(jtp,BorderLayout.CENTER);
		this.add(jp2, BorderLayout.NORTH);
	}
	
	private void startVisualization() {
		this.setSize(800, 2000);
		this.setVisible(true);
	}
	
	public void setIteraction(int i) {
		iteractionJL.setText(Integer.toString(i));
	}
	public void setWins(double wins) {
		winsJL.setText(String.format("%4.2f%%", wins));
	}
	public void setPoints(double p) {
		pointsJL.setText(String.format("%4.2f", p));
	}

	public String toAscii(int agentX, int agentY) {
		StringBuilder sb = new StringBuilder();
		for (int y = 10; y>=-10; y--) {
			StringBuilder[] line = new StringBuilder[4];
			for (int i=0; i<4; i++) 
				line[i] = new StringBuilder();
			
			if (y>cave.maxKnownHorizonVertical || y<cave.minKnownHorizonVertical) {
				line[0].append("     ");
				line[1].append("     ");
				line[2].append("     ");
				line[3].append("     ");
			} else {
				int my = y-cave.minKnownHorizonVertical;
				
				//1st line
				boolean last = false;
				for (int x = -10; x<=10; x++) {
					if (x > cave.maxKnownHorizonHorizontal || x < cave.minKnownHorizonHorizontal) {
						line[0].append("     ");
						line[1].append("     ");
						line[2].append("     ");
						line[3].append("     ");
					} else {
						//1st line
						int mx = x-cave.minKnownHorizonHorizontal;
						if (cave.map[my][mx]==null) {
							line[0].append("     ");
							last = false;
						} else {
							line[0].append("+----");
							last = true;
						}
						
						//2nd-4th line
						if (cave.map[my][mx]==null) {
							line[1].append("     ");
							line[2].append("     ");
							line[3].append("     ");
							last = false;
						} else {
							for (int j=0; j<3; j++) {
								line[j+1].append("|");
								line[j+1].append(cave.map[my][mx].toAscii(x==agentX && y==agentY)[j]);
							}
							last = true;
						}
						/*
						if (last) {
							line[0].append("+");
							for (int j=0; j<3; j++) 
								line[j+1].append("|");
						}
						*/
					}
				}

			}
			for (int i=0; i<4; i++) {
				sb.append(line[i]);
				sb.append("\n");
			}
			if (y == cave.minKnownHorizonVertical) {
				sb.append(line[0]);
				sb.append("\n");
			}				

		}
		
		return sb.toString();
	}

	@Override
	public void locationHasChanged() {
		jtp.setText(toAscii(agent.x, agent.y));
	}

	@Override
	public void timeHasChanged() {
	}

	@Override
	public void stateHasChanged() {
		jtp.setText(toAscii(agent.x, agent.y));
	}

	@Override
	public void goalHasChanged() {
	}

	@Override
	public void modelHasChanged() {
	}

	public Cave getCave() {
		return cave;
	}

	public void setCave(Cave cave) {
		this.cave = cave;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}	
	
}
