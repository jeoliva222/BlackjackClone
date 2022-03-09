package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

// Class that represents a UI Panel that displays information about your current opponent
public class OpponentInfoPanel extends JPanel {

	// Prevents warnings
	private static final long serialVersionUID = 1L;
	
	// Width and Height of panel
	protected static final int iWidth = 350;
	protected static final int iHeight = 160;
	
	// Title of the opponent
	protected JLabel titleLabel;
	
	// Description area for describing opponent
	protected JTextArea descArea;
	
	protected OpponentInfoPanel() {
		super();
		
		// Allow exact positioning
		this.setLayout(null);
		
		// Create black border
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		// Set white background
		this.setBackground(Color.WHITE);
		
		this.titleLabel = new JLabel(" 10: Alexander the Minister");
		this.titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.titleLabel.setBounds(0, 0, iWidth, 50);
		this.titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		this.add(this.titleLabel);
		
		// Set bounds/properties of nodeText and add to frame
		this.descArea = new JTextArea();
		this.descArea.setBounds(5, 50, iWidth - 5, iHeight - 50);
		this.descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		this.descArea.setLineWrap(true);
		this.descArea.setWrapStyleWord(true);
		this.descArea.setEditable(false);
		this.descArea.setOpaque(false);
		this.descArea.setVisible(true);
		this.descArea.setText("Test text balasasjdhakjsdhak ajshdkasd");
		this.add(this.descArea);
	}

}
