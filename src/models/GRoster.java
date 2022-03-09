package models;

import java.util.ArrayList;

import gui.CardPanel;
import gui.GFrame;

// Class that contains an array of Opponents that the player can face
public class GRoster {

	public Opponent[] roster;
	
	public GRoster() {
		roster = new Opponent[] {
				
				new Opponent("Bunny Bruce", "Nibbles his cards.") {
					@Override
					public void doTurn() {
						GFrame.getInstance().aiHold();
					}
				},
				
				new Opponent("Simple Jack", "Can only count to 16.") {
					@Override
					public void doTurn() {
						GFrame frame = GFrame.getInstance();
						
						// Fetch AI's total score
						int score = 0;
						for (CardPanel card: frame.getAiCards()) {
							score += card.getValue();
						}
						
						if (score >= 16) {
							frame.aiHold();
						} else {
							frame.dealAI();
						}
					}
				},
				
				new Opponent("Mr. Thick", "Only has eyes for his own cards.") {
					@Override
					public void doTurn() {
						GFrame frame = GFrame.getInstance();
						
						// List of known values
						ArrayList<Integer> knownValues = new ArrayList<Integer>();
						
						// Fetch AI's total score
						int score = 0;
						for (CardPanel card: frame.getAiCards()) {
							score += card.getValue();
							knownValues.add(card.getValue());
						}
						
						// If we have numGoal (usually 21) or more, do nothing else and return
						if ((score >= GFrame.numGoal)) {
							frame.aiHold();
							return;
						} else {
							// Check for good and bad draw possibilities
							int hitCount = 0;
							int stayCount = 0;
							for (int x = 1; x <= 11; x++) {
								// If we know the value on the board, don't consider it as a draw possibility
								if (knownValues.contains(x)) {
									// Do nothing
								} else {
									// Check whether draw would be good or bad for AI
									if((score + x) > GFrame.numGoal) {
										stayCount += 1;
									} else {
										hitCount += 1;
									}
								}
							} // END 'FOR' LOOP
							
							// If we have more incentive to hit, then draw a card
							// Otherwise, stay for the round
							if (hitCount > stayCount) {
								frame.dealAI();
							} else {
								frame.aiHold();
							}
							
						}
					}
				},
				
				new Opponent("Worthy Foe", "A fair mirror.") {
					@Override
					public void doTurn() {
						GFrame frame = GFrame.getInstance();
						
						// List of known values
						ArrayList<Integer> knownValues = new ArrayList<Integer>();
						
						// Fetch AI's total score
						int score = 0;
						for (CardPanel card: frame.getAiCards()) {
							score += card.getValue();
							knownValues.add(card.getValue());
						}
						
						// Count known player values and player score
						int plrScore = 0;
						for (int i = 1; i < frame.getPlayerCards().size(); i++) {
							plrScore += frame.getPlayerCards().get(i).getValue();
							knownValues.add(frame.getPlayerCards().get(i).getValue());
						}
						
						// If we have numGoal (usually 21) or more, do nothing else and return
						if ((score >= GFrame.numGoal) || (plrScore >= GFrame.numGoal)) {
							frame.aiHold();
							return;
						} else {
							// Check for good and bad draw possibilities
							int hitCount = 0;
							int stayCount = 0;
							for (int x = 1; x <= 11; x++) {
								// If we know the value on the board, don't consider it as a draw possibility
								if(knownValues.contains(x)) {
									// Do nothing
								} else {
									// Check whether draw would be good or bad for AI
									if ((score + x) > GFrame.numGoal) {
										stayCount += 1;
									} else {
										hitCount += 1;
									}
									
									// Check possibility of player's score beating AI's
									if (((plrScore + x) > score) && ((plrScore + x) <= GFrame.numGoal)) {
										hitCount += 1;
									} else if ((plrScore + x) == score) {
										// Don't count as anything (Neutral)
									} else {
										stayCount += 1;
									}
								}
							} // END 'FOR' LOOP
							
							// If we have more incentive to hit, then draw a card
							// Otherwise, stay for the round
							if (hitCount > stayCount) {
								frame.dealAI();
							} else {
								frame.aiHold();
							}
							
						}
					}
				},
				
				new Opponent("Oswald the Great", "He sees your whole hand.") {
					@Override
					public void doTurn() {
						GFrame frame = GFrame.getInstance();
						
						// List of known values
						ArrayList<Integer> knownValues = new ArrayList<Integer>();
						ArrayList<Integer> plrKnownValues = new ArrayList<Integer>();
						
						// Fetch AI's total score
						int score = 0;
						int shownScore = 0;
						for (int i = 0; i < frame.getAiCards().size(); i++) {
							score += frame.getAiCards().get(i).getValue();
							knownValues.add(frame.getAiCards().get(i).getValue());
							if (i != 0) {
								shownScore += frame.getAiCards().get(i).getValue();
								plrKnownValues.add(frame.getAiCards().get(i).getValue());
							}
						}
						
						// Count player values and player score
						int plrScore = 0;
						for (int i = 0; i < frame.getPlayerCards().size(); i++) {
							plrScore += frame.getPlayerCards().get(i).getValue();
							knownValues.add(frame.getPlayerCards().get(i).getValue());
						}
						
						boolean playerWinning = ((plrScore > score) && (plrScore <= GFrame.numGoal));
						
						// If player stayed, went first, and is winning, we must draw no matter what
						if (playerWinning && frame.isPlayerFirst && frame.playerStayed) {
							frame.dealAI();
							return;
						}
						
						// If we went over or if we're beating player and they went first and stayed, hold for the turn
						if ((score >= GFrame.numGoal) || (plrScore > GFrame.numGoal) ||
								(frame.isPlayerFirst && frame.playerStayed && (score > plrScore))) {
							frame.aiHold();
							return;
						} else {
							// Check for good and bad draw possibilities
							int hitCount = 0;
							int stayCount = 0;
							for (int x = 1; x <= 11; x++) {
								// If we know the value on the board, don't consider it as a draw possibility
								if (knownValues.contains(x)) {
									// Do nothing
								} else {
									// Check whether draw would be good or bad for AI
									if ((score + x) > GFrame.numGoal) {
										stayCount += 1;
									} else {
										hitCount += 1;
									}			
								}
								
								// If player is winning and didn't stay, check the probability that they THINK
								// we are beating them with our unknown card
								if (playerWinning) {
									if (plrKnownValues.contains(x)) {
										// Do nothing
									} else {
										if ((shownScore + x) > plrScore && ((shownScore + x) <= GFrame.numGoal)) {
											stayCount += 1;
										} else {
											hitCount += 1;
										}	
									}
								}
							} // END 'FOR' LOOP	
							
							// If we have more incentive to hit, then draw a card
							// Otherwise, stay for the round
							if (hitCount > stayCount) {
								frame.dealAI();
							} else {
								frame.aiHold();
							}
							
						}
					}
				},
				
				new Opponent("Alexander the Minister", "Knows absolutely all there is to know.") {
					@Override
					public void doTurn() {
						GFrame frame = GFrame.getInstance();
						
						// Fetch AI's total score
						int score = 0;
						for (int i = 0; i < frame.getAiCards().size(); i++) {
							score += frame.getAiCards().get(i).getValue();
						}
						
						// Count player values and player score
						int plrScore = 0;
						for (int i = 0; i < frame.getPlayerCards().size(); i++) {
							plrScore += frame.getPlayerCards().get(i).getValue();
						}
						
						// Whether player is beating 
						boolean playerWinning = ((plrScore > score) && (plrScore <= GFrame.numGoal));
						
						// The next draw that occurs in the deck
						int nextDraw = frame.peekDeck();
						
						// If we went over or if we're beating player and they went first and stayed, hold for the turn
						if ((score >= GFrame.numGoal) || (plrScore > GFrame.numGoal) ||
								(frame.isPlayerFirst && frame.playerStayed && (score > plrScore))) {
							frame.aiHold();
							return;
						}
						
						// If player is losing and next draw puts them over, then stay no matter what
						if (!playerWinning && (plrScore + nextDraw) > GFrame.numGoal) {
							frame.aiHold();
							return;
						}
						
						// Otherwise, check if draw helps or hurts us
						if ((score + nextDraw) <= GFrame.numGoal) {
							// If the draw is good, then draw
							frame.dealAI();
							return;
						} else {
							// If the draw puts us over, then stay
							frame.aiHold();
							return;
						}
						
					}
				},
				
		};
	}
	
}
