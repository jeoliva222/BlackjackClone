package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CardPanel extends JPanel {

	// Prevents warnings
	private static final long serialVersionUID = 1L;
	
	// Width and Height of a card
	private static final int cWidth = 100;
	private static final int cHeight = 150;
	
	// Label to display the value of the card
	private JLabel numLabel;
	
	// Value of the card
	private int value;
	
	// Constructor
	public CardPanel(int value, boolean shouldShow) {
		super();
		
		// Set value
		this.value = value;
		
		// Set Layout to allow for exact content positioning
		this.setLayout(new GridBagLayout());
		
		// Set white background and black border
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		
		// Add the number label to the panel
		if(shouldShow) {
			this.numLabel = new JLabel(Integer.toString(this.value));
		} else {
			this.numLabel = new JLabel("?");
		}
		this.numLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 65));
		this.add(numLabel);
	}
	
	// Constructor Overload
	public CardPanel(int value) {
		this(value, true);
	}
	
	public int getValue() {
		return this.value;
	}
	
	public JLabel getLabel() {
		return this.numLabel;
	}
	
	public static int getCWidth() {
		return CardPanel.cWidth;
	}
	
	public static int getCHeight() {
		return CardPanel.cHeight;
	}

}
