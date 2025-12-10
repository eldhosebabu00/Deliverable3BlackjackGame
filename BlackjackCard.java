package ca.sheridancollege.project;

public class BlackjackCard extends Card {

    private String suit;
    private String rank;
    private int value;

    public BlackjackCard(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

 
    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public int getValue() {
        return value;
    }

    public String getRank() {
        return rank;
    }
}
