package ca.sheridancollege.project;

import java.util.ArrayList;
import java.util.Scanner;

public class BlackjackPlayer extends Player {

    private ArrayList<BlackjackCard> hand = new ArrayList<>();
    private int balance = 100;
    private int currentBet = 0;

    public BlackjackPlayer(String name) {
        super(name);
    }

    @Override
    public void play() {
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setBet(int amount) {
        currentBet = amount;
        balance -= amount;
    }

    public void winBet() {
        balance += currentBet * 2;
        currentBet = 0;
    }

    public void pushBet() {
        balance += currentBet;
        currentBet = 0;
    }

    public void loseBet() {
        currentBet = 0;
    }

    public void clearHand() {
        hand.clear();
    }

    public void receiveCard(BlackjackCard card) {
        hand.add(card);
    }

    public ArrayList<BlackjackCard> getHand() {
        return hand;
    }

    public int getHandValue() {
        int total = 0;
        int aceCount = 0;

        for (BlackjackCard c : hand) {
            total += c.getValue();
            if (c.getRank().equals("A")) {
                aceCount++;
            }
        }

        // Ace logic
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    public boolean isBusted() {
        return getHandValue() > 21;
    }

    public void showHand() {
        System.out.println(getName() + "'s hand: " + getHandAsString()
                + " (value = " + getHandValue() + ")");
    }

    private String getHandAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i).toString());
            if (i < hand.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void playTurn(GroupOfCards deck, Scanner input) {
        while (true) {
            showHand();
            if (isBusted()) {
                System.out.println(getName() + " busts!");
                break;
            }

            System.out.print(getName() + ", Hit or Stand? (h/s): ");
            String choice = input.nextLine().trim().toLowerCase();

            if (choice.startsWith("h")) {
                BlackjackCard card = (BlackjackCard) deck.getCards().remove(0);
                System.out.println(getName() + " draws " + card);
                receiveCard(card);
            } else if (choice.startsWith("s")) {
                System.out.println(getName() + " stands with value " + getHandValue());
                break;
            } else {
                System.out.println("Please enter 'h' for Hit or 's' for Stand.");
            }
        }
    }
}
