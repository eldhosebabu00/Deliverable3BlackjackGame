package ca.sheridancollege.project;

import java.util.ArrayList;
import java.util.Scanner;

public class Dealer extends Player {

    private ArrayList<BlackjackCard> hand = new ArrayList<>();

    public Dealer() {
        super("Dealer");
    }

    @Override
    public void play() {
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

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    public boolean isBusted() {
        return getHandValue() > 21;
    }

    public void showInitialHand() {
        if (hand.size() == 2) {
            System.out.println("Dealer's hand: [Hidden], " + hand.get(1));
        } else {
            System.out.println("Dealer's hand: " + getHandAsString());
        }
    }

    public void showFullHand() {
        System.out.println("Dealer's hand: " + getHandAsString()
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
        System.out.println();
        System.out.println("Dealer's turn...");
        showFullHand();

        while (getHandValue() < 17) {
            BlackjackCard card = (BlackjackCard) deck.getCards().remove(0);
            System.out.println("Dealer draws " + card);
            receiveCard(card);
            showFullHand();
        }

        if (isBusted()) {
            System.out.println("Dealer busts!");
        } else {
            System.out.println("Dealer stands with value " + getHandValue());
        }
    }
}
