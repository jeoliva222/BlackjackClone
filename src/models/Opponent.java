package models;

import gui.GFrame;

// Represents an opponent you face in-game
public class Opponent {

	// Name of the opponent
	public String name;
	
	// Description of the opponent
	public String desc;
	
	public Opponent(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	// Does the AI's turn (By default, just hold)
	public void doTurn() {
		GFrame.getInstance().aiHold();
	}
	
}
