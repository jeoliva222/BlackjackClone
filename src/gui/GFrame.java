package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import models.GRoster;
import models.Opponent;

// Class that represents the UI frame the game runs in
public class GFrame extends JFrame {

	// Prevents warnings
	private static final long serialVersionUID = 1L;
	
	// Singleton instance of class
	private static GFrame instance = null;
	
	// Width and Height of the frame
	private int fWidth = 1080;
	private int fHeight = 640;
	
	// Number to try to reach in the game
	public static final int numGoal = 21;
	
	// Number of round wins to for a contender to win the game
	public static final int winGoal = 4;
	
	// Player's cards
	private ArrayList<CardPanel> playerCards = new ArrayList<CardPanel>();
	
	// Opponent's cards
	private ArrayList<CardPanel> aiCards = new ArrayList<CardPanel>();
	
	// Your Score label
	private JLabel playerScore;
	
	// Opponent Score label
	private JLabel aiScore;
	
	// Deck count label
	private JLabel deckLabel;
	
	// Victory/Loss label
	private JLabel victoryLabel;
	
	// Labels for AI and Player win count
	private JLabel playerWinLabel;
	private JLabel aiWinLabel;
	
	// Opponent Panel to display AI's information
	private OpponentInfoPanel opponentInfo;
	
	// Button to deal card to player
	private JButton hitButton;
	
	// Button to stay
	private JButton stayButton;
	
	// Button to start new game
	private JButton newButton;
	
	// Lineup of opponents and index indicating current active opponent
	private GRoster lineup;
	private int opptIndex = 3;
	
	// Number of wins from either contender
	private int playerWins = 0;
	private int aiWins = 0;
	
	// Deck of card values and top index
	private int[] deck = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
	private int topIndex = 0;
	
	// Flags for whether player and AI stayed on current turn
	// Current game ends if both players stay on the same turn
	public boolean playerStayed = false;
	public boolean aiStayed = false;
	
	private GFrame() {
		super();
		
		// Set default size for frame
		Dimension size = new Dimension(this.fWidth, this.fHeight);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setResizable(false);
		
		// Set Layout to allow for exact content positioning
		this.setLayout(null);
		
		// Initialize the score labels
		this.playerScore = new JLabel("Your score: ##");
		this.playerScore.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.playerScore.setBounds(30, 350, 200, 40);
		this.add(this.playerScore);
		//--
		this.aiScore = new JLabel("AI score: ##");
		this.aiScore.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.aiScore.setBounds(30, 210, 200, 40);
		this.add(this.aiScore);
		
		// Initialize deck label
		this.deckLabel = new JLabel(" Deck Count: ##");
		this.deckLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.deckLabel.setBounds(30, 280, 200, 40);
		this.deckLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		this.add(this.deckLabel);
		
		// Initialize victory label
		this.victoryLabel = new JLabel("Victory Status");
		this.victoryLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		this.victoryLabel.setBounds(230, 350, 200, 40);
		this.victoryLabel.setVisible(false);
		this.add(this.victoryLabel);
		
		// Initialize win count labels
		this.playerWinLabel = new JLabel("Player Wins: 0 / "+Integer.toString(winGoal));
		this.playerWinLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.playerWinLabel.setBounds(30, 555, 200, 40);
		this.add(this.playerWinLabel);
		//--
		this.aiWinLabel = new JLabel("AI Wins: 0 / "+Integer.toString(winGoal));
		this.aiWinLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.aiWinLabel.setBounds(30, 5, 200, 40);
		this.add(this.aiWinLabel);
		
		// Add in the hit button
		this.hitButton = new JButton("Hit Me");
		this.hitButton.setBounds(250, 280, 100, 40);
		this.hitButton.setFocusPainted(false);
		this.hitButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				GFrame.this.hitButton.setEnabled(false);
				GFrame.this.stayButton.setEnabled(false);
				GFrame.this.dealPlayer();
				GFrame.this.updateScores();
				GFrame.this.updateDeckCount();
				GFrame.this.repaint();
				//--
			    ActionListener listener = new ActionListener(){
			        public void actionPerformed(ActionEvent event){
						GFrame.this.aiTakeTurn();
						GFrame.this.updateScores();
						GFrame.this.updateDeckCount();
						GFrame.this.checkEnd();
						GFrame.this.repaint();
			        }
			    };
			    Timer timer = new Timer(400, listener);
			    timer.setRepeats(false);
			    timer.start();
			  } 
		});
		this.add(hitButton);
		
		// Add in the stay button
		this.stayButton = new JButton("I'll Stay");
		this.stayButton.setBounds(370, 280, 100, 40);
		this.stayButton.setFocusPainted(false);
		this.stayButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				GFrame.this.hitButton.setEnabled(false);
				GFrame.this.stayButton.setEnabled(false);
				GFrame.this.playerStayed = true;
			    ActionListener listener = new ActionListener(){
			        public void actionPerformed(ActionEvent event){
						GFrame.this.aiTakeTurn();
						GFrame.this.updateScores();
						GFrame.this.updateDeckCount();
						GFrame.this.checkEnd();
						GFrame.this.repaint();
			        }
			    };
			    Timer timer = new Timer(400, listener);
			    timer.setRepeats(false);
			    timer.start();
			  } 
		});
		this.add(stayButton);
		
		// Add in the new game button
		this.newButton = new JButton("New Round");
		this.newButton.setBounds(580, 280, 100, 40);
		this.newButton.setBackground(new Color(255, 159, 128));
		this.newButton.setFocusPainted(false);
		this.newButton.setEnabled(false);
		this.newButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				GFrame.this.startNewGame();
				GFrame.this.newButton.setEnabled(false);
			  } 
		});
		this.add(newButton);
		
		// Initialize the roster of opponents
		this.lineup = new GRoster();
		
		// Initialize Opponent info panel
		this.opponentInfo = new OpponentInfoPanel();
		this.opponentInfo.setBounds(720, 220, OpponentInfoPanel.iWidth, OpponentInfoPanel.iHeight);
		this.add(this.opponentInfo);
		
		// Set up initial game
		this.startNewGame();
		
		// Prepare frame for viewing
		this.setTitle("Modified Blackjack");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
	}
	
	public void dealPlayer() {
		// If we can't deal any more cards, end the round
		if(this.topIndex >= 11) {
			this.playerStayed = true;
			this.aiStayed = true;
			return;
		}
		
		// Get number of currently held cards
		int currentlyHeld = this.playerCards.size();
		
		// Create card with value from top of deck
		CardPanel card;
		if(currentlyHeld == 0) {
			card = new CardPanel(this.popDeck());
			card.getLabel().setForeground(Color.RED);
		} else {
			card = new CardPanel(this.popDeck());
		}
		
		// Set the bounds of the new card
		card.setBounds(50 + ((CardPanel.getCWidth() + 10)*currentlyHeld), 400,
				CardPanel.getCWidth(), CardPanel.getCHeight());
		
		// Add references to card
		this.playerCards.add(card);
		this.add(card);
		
		// Indicate that player did not stay this turn
		this.playerStayed = false;
	}
	
	public void dealAI() {
		// If we can't deal any more cards, end the round
		if(this.topIndex >= 11) {
			this.playerStayed = true;
			this.aiStayed = true;
			return;
		}
		
		// Get number of currently held cards
		int currentlyHeld = this.aiCards.size();
		
		// Create card with value from top of deck
		CardPanel card;
		if(currentlyHeld == 0) {
			card = new CardPanel(this.popDeck(), false);
		} else {
			card = new CardPanel(this.popDeck());
		}
		
		// Set the bounds for the card
		card.setBounds(50 + ((CardPanel.getCWidth() + 10)*currentlyHeld), 50,
				CardPanel.getCWidth(), CardPanel.getCHeight());
		
		// Add references to card
		this.aiCards.add(card);
		this.add(card);
		
		// Indicate that AI did not stay this turn
		this.aiStayed = false;
	}
	
	// Pops the top value from the deck
	public int popDeck() {
		if(this.topIndex >= this.deck.length) {
			return -1;
		} else {
			int top = this.deck[this.topIndex];
			this.topIndex += 1;
			return top;
		}
	}
	
	// Peeks the top value from the deck
	public int peekDeck() {
		if(this.topIndex >= this.deck.length) {
			return -1;
		} else {
			return this.deck[this.topIndex];
		}
	}
	
	// Shuffles the deck randomly
	public void shuffleDeck() {
		// Reset deck parameters
		this.topIndex = 0;
		
		// Shuffle the deck
		Random r = new Random();
		for(int i = this.deck.length - 1; i > 0; i--) {
			// Generate index to swap with
			int shufIndex = r.nextInt(i + 1);
			
			// Swap the indices
			int temp = this.deck[i];
			this.deck[i] = this.deck[shufIndex];
			this.deck[shufIndex] = temp;
		}
	}
	
	// Update scores for the player and AI
	public void updateScores() {
		int plrScore = 0;
		for(CardPanel card: this.playerCards) {
			plrScore += card.getValue();
		}
		
		int aiScore = 0;
		boolean firstPassed = false;
		for(CardPanel card: this.aiCards) {
			if(firstPassed) {
				aiScore += card.getValue();
			} else {
				firstPassed = true;
			}
		}
		
		this.playerScore.setText("Your score: "+Integer.toString(plrScore));
		this.aiScore.setText("AI score: "+Integer.toString(aiScore)+" + ??");
	}
	
	// Updates the win count
	public void updateWins() {
		this.playerWinLabel
			.setText("Player Wins: "+Integer.toString(this.playerWins)+" / "+Integer.toString(winGoal));
		this.aiWinLabel
			.setText("AI Wins: "+Integer.toString(this.aiWins)+" / "+Integer.toString(winGoal));
	}
	
	// Takes the AI's turn
	public void aiTakeTurn() {
		this.lineup.roster[this.opptIndex].doTurn();
	}
	
	public void checkEnd() {
		if(this.playerStayed && this.aiStayed) {
			// Reveal AI Score and cards
			CardPanel aiFirstCard = this.aiCards.get(0);
			aiFirstCard.getLabel().setText(Integer.toString(aiFirstCard.getValue()));
			//--
			int aiScore = 0;
			for(CardPanel card: this.aiCards) {
				aiScore += card.getValue();
			}
			this.aiScore.setText("AI score: "+Integer.toString(aiScore));
			
			// Un-red player's first card
			this.playerCards.get(0).getLabel().setForeground(Color.BLACK);
			
			// Disable Hit and Stay buttons
			this.hitButton.setEnabled(false);
			this.stayButton.setEnabled(false);
			
			// Get player score
			int plrScore = 0;
			for(CardPanel card: this.playerCards) {
				plrScore += card.getValue();
			}
			
			boolean aiOver = (aiScore > numGoal);
			boolean plrOver = (plrScore > numGoal);
			
			// Display victory/loss message
			if((aiOver && plrOver) || plrScore == aiScore) {
				// Tie Game
				this.victoryLabel.setText("Tie Game");
				this.victoryLabel.setForeground(Color.BLACK);
				this.victoryLabel.setVisible(true);
			} else if(aiOver || (plrScore > aiScore && (!plrOver))) {
				// Victory for player
				this.victoryLabel.setText("Player Wins!!!");
				this.victoryLabel.setForeground(new Color(0, 102, 0));
				this.victoryLabel.setVisible(true);
				this.playerWins += 1;
			} else {
				// Loss for player
				this.victoryLabel.setText("AI Wins");
				this.victoryLabel.setForeground(new Color(255, 102, 102));
				this.victoryLabel.setVisible(true);
				this.aiWins += 1;
			}
			
			// Update the win count
			this.updateWins();
			
			// Enable 'New Round' Button
			this.newButton.setEnabled(true);
		} else {
			// Disable Hit and Stay buttons
			this.hitButton.setEnabled(true);
			this.stayButton.setEnabled(true);
		}
	}
	
	// Updates the deck count label
	public void updateDeckCount() {
		this.deckLabel.setText(" Deck Count: "+Integer.toString(this.deck.length - this.topIndex));
	}
	
	public void updateOpponentInfo() {
		Opponent currentOppt = this.lineup.roster[this.opptIndex];
		this.opponentInfo.titleLabel.setText(" "+Integer.toString(this.opptIndex + 1)+": "+currentOppt.name);
		this.opponentInfo.descArea.setText(currentOppt.desc);
	}
	
	// Starts a new game of Blackjack
	public void startNewGame() {
		// Hide victory label
		this.victoryLabel.setVisible(false);
		
		// If certain number of rounds won by contender, start a new game
		if(playerWins >= winGoal) {
			this.playerWins = 0;
			this.aiWins = 0;
			if(opptIndex < (lineup.roster.length - 1))
				this.opptIndex += 1;
		} else if(aiWins >= winGoal) {
			this.playerWins = 0;
			this.aiWins = 0;
		}
		
		// Remove player cards
		for(CardPanel card: this.playerCards) {
			this.remove(card);
		}
		
		// Remove AI cards
		for(CardPanel card: this.aiCards) {
			this.remove(card);
		}
		
		// Clear card references
		this.playerCards.clear();
		this.aiCards.clear();
		
		// Re-shuffle deck and deal two cards to each player
		this.shuffleDeck();
		this.dealPlayer();
		this.dealAI();
		this.dealPlayer();
		this.dealAI();
		
		// Update UI
		this.updateScores();
		this.updateDeckCount();
		this.updateWins();
		this.updateOpponentInfo();
		
		// Enable Hit and Stay buttons
		this.hitButton.setEnabled(true);
		this.stayButton.setEnabled(true);
		
		// Repaint the screen
		this.repaint();
	}
	
	//---------------------------------------------------------------------------------

	// Retrieves instance of the GFrame, constructing one if it doesn't exist already
	public static GFrame getInstance() {
		if(instance == null) {
			instance = new GFrame();
		}
		return instance;
	}
	
	public ArrayList<CardPanel> getAiCards() {
		return this.aiCards;
	}
	
	public ArrayList<CardPanel> getPlayerCards() {
		return this.playerCards;
	}
	
	public void aiHold() {
		this.aiStayed = true;
	}
	
}
