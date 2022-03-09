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
	
	// Opponent index the game starts on
	private static final int START_INDEX = 0;
	
	// Singleton instance of class
	private static GFrame instance = null;
	
	// Animation delay (in milliseconds)
	private static final int ANIM_DELAY = 500;
	
	// Width and Height of the frame
	private static final int WIDTH = 1080;
	private static final int HEIGHT = 640;
	
	// Number to try to reach in the game
	public static final int numGoal = 21;
	
	// Number of round wins to for a contender to win the game
	public static final int winGoal = 4;
	
	// Player's cards
	private ArrayList<CardPanel> playerCards = new ArrayList<CardPanel>();
	
	// Opponent's cards
	private ArrayList<CardPanel> aiCards = new ArrayList<CardPanel>();
	
	// Your Hold Status label
	private JLabel playerHoldLabel;
	
	// Your Score label
	private JLabel playerScore;
	
	// Your First label
	private JLabel playerFirstLabel;
	
	// Opponent Hold Status label
	private JLabel aiHoldLabel;
	
	// Opponent Score label
	private JLabel aiScore;
	
	// Opponent First label
	private JLabel aiFirstLabel;
	
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
	private int opptIndex = START_INDEX;
	
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
	
	// Flag determining who goes first
	public boolean isPlayerFirst = false;
	
	private GFrame() {
		super();
		
		// Set default size for frame
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setResizable(false);
		
		// Set Layout to allow for exact content positioning
		this.setLayout(null);
		
		// Initialize the hold status labels
		this.playerHoldLabel = new JLabel();
		this.playerHoldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.playerHoldLabel.setBounds(30, 320, 200, 40);
		this.add(this.playerHoldLabel);
		//--
		this.aiHoldLabel = new JLabel();
		this.aiHoldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.aiHoldLabel.setBounds(30, 240, 200, 40);
		this.add(this.aiHoldLabel);
		
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
		this.victoryLabel.setBounds(230, 350, 300, 40);
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
		
		// Initialize the first labels
		this.playerFirstLabel = new JLabel();
		this.playerFirstLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.playerFirstLabel.setBounds(250, 555, 200, 40);
		this.add(this.playerFirstLabel);
		//--
		this.aiFirstLabel = new JLabel();
		this.aiFirstLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		this.aiFirstLabel.setBounds(250, 5, 200, 40);
		this.add(this.aiFirstLabel);
		
		// Add in the hit button
		this.hitButton = new JButton("Hit Me");
		this.hitButton.setBounds(250, 280, 100, 40);
		this.hitButton.setFocusPainted(false);
		this.hitButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				hitButton.setEnabled(false);
				stayButton.setEnabled(false);
				
				// Player's turn
				playerDrawTurn();
				
				// AI Turn
			    ActionListener listener = new ActionListener() {
			        public void actionPerformed(ActionEvent event) {
			        	aiFullTurn();
						hitButton.setEnabled(true);
						stayButton.setEnabled(true);
			        }
			    };
			    Timer timer = new Timer(ANIM_DELAY, listener);
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
				hitButton.setEnabled(false);
				stayButton.setEnabled(false);
				
				// Player turn
				playerHold();
				
				// If player is second, check end-round condition
				boolean roundEnd1 = false;
				if (!isPlayerFirst) {
					roundEnd1 = checkEnd();
				}
				
				// If round hasn't ended, take AI's turn
				if (!roundEnd1) {
				    ActionListener listener = new ActionListener() {
				        public void actionPerformed(ActionEvent event) {
				        	aiFullTurn();
				        	
				        	// If player was first, check end-round condition now
				        	boolean roundEnd2 = false;
				        	if (isPlayerFirst) {
				        		roundEnd2 = checkEnd();
				        	}
				        	
				        	// If round still going, re-enable buttons
			        		if (!roundEnd2) {
								hitButton.setEnabled(true);
								stayButton.setEnabled(true);
			        		}
			        		repaint();
				        }
				    };
				    Timer timer = new Timer(ANIM_DELAY, listener);
				    timer.setRepeats(false);
				    timer.start();
				}

			} 
		});
		this.add(stayButton);
		
		// Add in the new game button
		this.newButton = new JButton("New Round");
		this.newButton.setBounds(580, 280, 120, 40);
		this.newButton.setBackground(new Color(255, 159, 128));
		this.newButton.setFocusPainted(false);
		this.newButton.setEnabled(false);
		this.newButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				startNewGame();
				newButton.setEnabled(false);
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
		if (this.topIndex >= deck.length) {
			playerHold();
			aiHold();
			return;
		}
		
		// Get number of currently held cards
		int currentlyHeld = this.playerCards.size();
		
		// Create card with value from top of deck
		CardPanel card;
		if (currentlyHeld == 0) {
			card = new CardPanel(this.popDeck());
			card.getLabel().setForeground(Color.RED);
		} else {
			card = new CardPanel(this.popDeck());
		}
		
		// Set the bounds of the new card
		card.setBounds(50 + ((CardPanel.getCWidth() + 10) * currentlyHeld), 400,
				CardPanel.getCWidth(), CardPanel.getCHeight());
		
		// Add references to card
		this.playerCards.add(card);
		this.add(card);
		
		// Indicate that player did not stay this turn
		playerDrew();
	}
	
	public void dealAI() {
		// If we can't deal any more cards, end the round
		if (this.topIndex >= deck.length) {
			playerHold();
			aiHold();
			return;
		}
		
		// Get number of currently held cards
		int currentlyHeld = this.aiCards.size();
		
		// Create card with value from top of deck
		CardPanel card;
		if (currentlyHeld == 0) {
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
		aiDrew();
	}
	
	// Pops the top value from the deck
	public int popDeck() {
		if (this.topIndex >= this.deck.length) {
			return -1;
		} else {
			int top = this.deck[this.topIndex];
			this.topIndex += 1;
			return top;
		}
	}
	
	// Peeks the top value from the deck
	public int peekDeck() {
		if (this.topIndex >= this.deck.length) {
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
		for (int i = this.deck.length - 1; i > 0; i--) {
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
		for (CardPanel card: this.playerCards) {
			plrScore += card.getValue();
		}
		
		int aiScore = 0;
		boolean firstPassed = false;
		for (CardPanel card: this.aiCards) {
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
	
	public boolean checkEnd() {
		if (this.playerStayed && this.aiStayed) {
			// Reveal AI Score and cards
			CardPanel aiFirstCard = this.aiCards.get(0);
			aiFirstCard.getLabel().setText(Integer.toString(aiFirstCard.getValue()));
			//--
			int aiScore = 0;
			for (CardPanel card: this.aiCards) {
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
			for (CardPanel card: this.playerCards) {
				plrScore += card.getValue();
			}
			
			boolean aiOver = (aiScore > numGoal);
			boolean plrOver = (plrScore > numGoal);
			
			// Display victory/loss message
			if ((aiOver && plrOver) || plrScore == aiScore) {
				// Tie Game
				this.victoryLabel.setText("Tie Game");
				this.victoryLabel.setForeground(Color.BLACK);
				this.victoryLabel.setVisible(true);
			} else if (aiOver || (plrScore > aiScore && (!plrOver))) {
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
			return true;
		} else {
			return false;
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
		if (playerWins >= winGoal) {
			this.playerWins = 0;
			this.aiWins = 0;
			if(opptIndex < (lineup.roster.length - 1))
				this.opptIndex += 1;
		} else if(aiWins >= winGoal) {
			this.playerWins = 0;
			this.aiWins = 0;
		}
		
		// Remove player cards
		for (CardPanel card: this.playerCards) {
			this.remove(card);
		}
		
		// Remove AI cards
		for (CardPanel card: this.aiCards) {
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
		//--
		this.playerHoldLabel.setText("");
		this.aiHoldLabel.setText("");
		
		// Toggle who goes first
		toggleFirstCharacter();
		
		// If player goes second, take AI's turn now
		if (!isPlayerFirst) {
			// AI Turn
		    ActionListener listener = new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		        	aiFullTurn();
					hitButton.setEnabled(true);
					stayButton.setEnabled(true);
					repaint();
		        }
		    };
		    Timer timer = new Timer(ANIM_DELAY, listener);
		    timer.setRepeats(false);
		    timer.start();
		} else {
			// Enable Hit and Stay buttons
			this.hitButton.setEnabled(true);
			this.stayButton.setEnabled(true);
			
			// Repaint the screen
			this.repaint();
		}
	}
	
	private void toggleFirstCharacter() {
		this.isPlayerFirst = !(this.isPlayerFirst);
		
		if (isPlayerFirst) {
			this.playerFirstLabel.setText("FIRST");
			this.aiFirstLabel.setText("");
		} else {
			this.playerFirstLabel.setText("");
			this.aiFirstLabel.setText("FIRST");
		}
	}
	
	private void playerDrawTurn() {
		dealPlayer();
		updateScores();
		updateDeckCount();
		repaint();
	}
	
	private void aiFullTurn() {
		aiTakeTurn();
		updateScores();
		updateDeckCount();
		repaint();
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
		this.aiHoldLabel.setText("HOLD");
	}
	
	public void aiDrew() {
		this.aiStayed = false;
		this.aiHoldLabel.setText("DREW");
	}
	
	public void playerHold() {
		this.playerStayed = true;
		this.playerHoldLabel.setText("HOLD");
	}
	
	public void playerDrew() {
		this.playerStayed = false;
		this.playerHoldLabel.setText("DREW");
	}
	
}
