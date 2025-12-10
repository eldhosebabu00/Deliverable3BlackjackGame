package ca.sheridancollege.project;

import java.util.ArrayList;
import java.util.Scanner;

public class BlackjackGame extends Game {

    private GroupOfCards deck;
    private Dealer dealer;
    private final int STARTING_BALANCE = 100;

    public BlackjackGame(String name) {
        super(name);
        deck = new GroupOfCards(52);
        deck.setCards(new ArrayList<Card>());
        dealer = new Dealer();
    }

    @Override
    public void play() {
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to Blackjack!");
        setUpPlayers(input);

        boolean keepPlaying = true;

        while (keepPlaying && atLeastOneHasMoney()) {

            ensureDeckHasEnoughCards();
            takeBets(input);
            dealInitialCards();

            System.out.println();
            dealer.showInitialHand();
            for (Player p : getPlayers()) {
                BlackjackPlayer bp = (BlackjackPlayer) p;
                if (bp.getCurrentBet() > 0) {
                    bp.showHand();
                }
            }

            for (Player p : getPlayers()) {
                BlackjackPlayer bp = (BlackjackPlayer) p;
                if (bp.getCurrentBet() > 0) {
                    System.out.println();
                    System.out.println("---- " + bp.getName() + "'s turn ----");
                    bp.playTurn(deck, input);
                }
            }

            if (anyActivePlayerNotBusted()) {
                dealer.playTurn(deck, input);
            } else {
                System.out.println("All players busted. Dealer does not need to play.");
            }

            settleBets();

            System.out.println();
            System.out.println("---- Balances ----");
            for (Player p : getPlayers()) {
                BlackjackPlayer bp = (BlackjackPlayer) p;
                System.out.println(bp.getName() + ": $" + bp.getBalance());
            }

            clearHands();

            if (!atLeastOneHasMoney()) {
                System.out.println("All players are out of money. Game over.");
                break;
            }

            System.out.println();
            System.out.print("Play another round? (y/n): ");
            String answer = input.nextLine().trim().toLowerCase();
            if (!answer.startsWith("y")) {
                keepPlaying = false;
            }
        }

        declareFinalWinner(); 
        System.out.println("Thank you for playing!");
    }

    @Override
    public void declareWinner() {
    }

    private void declareFinalWinner() {
        System.out.println("\n==== FINAL GAME RESULT ====");

        boolean allZero = true;
        BlackjackPlayer winner = (BlackjackPlayer) getPlayers().get(0);
        boolean tie = false;

        for (Player p : getPlayers()) {
            BlackjackPlayer bp = (BlackjackPlayer) p;
            System.out.println(bp.getName() + " final balance: $" + bp.getBalance());
            if (bp.getBalance() > 0) {
                allZero = false;
            }
            if (bp.getBalance() > winner.getBalance()) {
                winner = bp;
                tie = false; 
            } else if (bp.getBalance() == winner.getBalance() && bp != winner) {
                tie = true;
            }
        }

        // CASE 1 — MULTIPLE PLAYERS & ALL ZERO
        if (getPlayers().size() > 1 && allZero) {
            System.out.println("\n BETTER LUCK NEXT TIME!");
        }
        // CASE 2 — ONE PLAYER & ZERO MONEY
        else if (getPlayers().size() == 1 && winner.getBalance() == 0) {
            System.out.println("\n BETTER LUCK NEXT TIME: " + winner.getName().toUpperCase());
        }
        // CASE 3 — TIE
        else if (tie) {
            System.out.println("\n The game ends in a DRAW!");
        }
        // CASE 4 — WINNER FOUND
        else {
            System.out.println("\n WINNER OF THE GAME: " + winner.getName().toUpperCase());
        }

        System.out.println("--------------------------------");
    }
    

    private void setUpPlayers(Scanner input) {
        int numPlayers = readIntInRange(input,
                "Enter number of players (1-4): ", 1, 4);

        ArrayList<Player> players = new ArrayList<>();

        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = input.nextLine().trim();
            if (name.isEmpty()) {
                name = "Player" + i;
            }
            BlackjackPlayer player = new BlackjackPlayer(name);
            players.add(player);
        }

        setPlayers(players);
    }

    private void ensureDeckHasEnoughCards() {
        int needed = (getPlayers().size() + 1) * 5;
        if (deck.getCards().size() < needed) {
            deck.setCards(new ArrayList<Card>());
            buildDeck();
            deck.shuffle();
        }
    }

    private void buildDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

        for (String s : suits) {
            for (String r : ranks) {
                int value;
                if (r.equals("J") || r.equals("Q") || r.equals("K")) {
                    value = 10;
                } else if (r.equals("A")) {
                    value = 11;
                } else {
                    value = Integer.parseInt(r);
                }
                deck.getCards().add(new BlackjackCard(s, r, value));
            }
        }
    }

    private void takeBets(Scanner input) {
        System.out.println();
        System.out.println("---- Place Bets ----");

        for (Player p : getPlayers()) {
            BlackjackPlayer bp = (BlackjackPlayer) p;
            if (bp.getBalance() <= 0) {
                System.out.println(bp.getName() + " has no money left and cannot bet.");
                continue;
            }
            System.out.println(bp.getName() + " balance: $" + bp.getBalance());
            int bet = readIntInRange(input,
                    "Enter bet for " + bp.getName() + ": ",
                    1, bp.getBalance());
            bp.setBet(bet);
        }
    }

    private void dealInitialCards() {
        dealer.clearHand();
        for (Player p : getPlayers()) {
            ((BlackjackPlayer)p).clearHand();
        }

        for (int i = 0; i < 2; i++) {
            for (Player p : getPlayers()) {
                BlackjackPlayer bp = (BlackjackPlayer)p;
                if (bp.getCurrentBet() > 0) {
                    BlackjackCard card = (BlackjackCard) deck.getCards().remove(0);
                    bp.receiveCard(card);
                }
            }
            BlackjackCard dealerCard = (BlackjackCard) deck.getCards().remove(0);
            dealer.receiveCard(dealerCard);
        }
    }

    private boolean anyActivePlayerNotBusted() {
        for (Player p : getPlayers()) {
            BlackjackPlayer bp = (BlackjackPlayer)p;
            if (bp.getCurrentBet() > 0 && !bp.isBusted()) {
                return true;
            }
        }
        return false;
    }

    private void settleBets() {
        int dealerValue = dealer.getHandValue();
        boolean dealerBusted = dealer.isBusted();

        System.out.println();
        System.out.println("---- Round Results ----");
        dealer.showFullHand();

        for (Player p : getPlayers()) {
            BlackjackPlayer bp = (BlackjackPlayer)p;
            if (bp.getCurrentBet() == 0) {
                continue;
            }
            int playerValue = bp.getHandValue();
            System.out.print(bp.getName() + " (" + playerValue + "): ");

            if (bp.isBusted()) {
                System.out.println("Busted. Lose bet.");
                bp.loseBet();
            } else if (dealerBusted) {
                System.out.println("Dealer busted. " + bp.getName() + " wins!");
                bp.winBet();
            } else if (playerValue > dealerValue) {
                System.out.println("Beats dealer. " + bp.getName() + " wins!");
                bp.winBet();
            } else if (playerValue < dealerValue) {
                System.out.println("Loses to dealer.");
                bp.loseBet();
            } else {
                System.out.println("Push (tie). Bet returned.");
                bp.pushBet();
            }
        }
    }

    private void clearHands() {
        dealer.clearHand();
        for (Player p : getPlayers()) {
            ((BlackjackPlayer)p).clearHand();
        }
    }

    private boolean atLeastOneHasMoney() {
        for (Player p : getPlayers()) {
            BlackjackPlayer bp = (BlackjackPlayer)p;
            if (bp.getBalance() > 0) {
                return true;
            }
        }
        return false;
    }

    private int readIntInRange(Scanner input, String prompt, int min, int max) {
        int value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String line = input.nextLine();
            try {
                value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a value between " + min + " and " + max + ".");
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter"
                + " a valid whole number.");
            }
        }

        return value;
    }
}
