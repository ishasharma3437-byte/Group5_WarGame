/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.sheridancollege.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Diya Sharma
 * @modifier Razia Kauser
 * @modifier Mohammed Fahad Ahmed
 * @modifier Harsh Saini 
 */
public class WarGame extends Game {

    private static final int MAX_ROUNDS = 200;
    private static final int WAR_FACE_DOWN = 3;

    private GroupOfCards deck;
    private GroupOfCards centralPile;
    private WarPlayer player;
    private WarPlayer computer;
    private int roundCount = 0;

    public WarGame(String name, String humanName, String computerName) {
        super(name);
        this.deck = new GroupOfCards(52);
        this.centralPile = new GroupOfCards(52);
        this.player = new WarPlayer(humanName);
        this.computer = new WarPlayer(computerName);

        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(computer);
        super.setPlayers((ArrayList<Player>) players);
    }

    @Override
    public void play() {
        startGame();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            roundCount++;
            if (roundCount > MAX_ROUNDS) {
                System.out.println("\nMaximum rounds (" + MAX_ROUNDS + "). Determining winner by card counts...");
                declareWinner();
                break;
            }

            if (checkForGameEnd()) {
                break;
            }

            System.out.println("\n--- Round " + roundCount + " ---");
            System.out.println("Press Enter to play next round (or 'q' to quit).");
            String input = scanner.nextLine().trim();
            if ("q".equalsIgnoreCase(input)) {
                System.out.println("Player quit the game.");
                break;
            }

            playRound();

            if (checkForGameEnd()) {
                break;
            }
        }

        scanner.close();
    }

    @Override
    public void declareWinner() {
        int h = player.getHand().getCount();
        int c = computer.getHand().getCount();
        System.out.println("\nGame Over!");
        System.out.println(player.getName() + " cards: " + h);
        System.out.println(computer.getName() + " cards: " + c);
        if (h > c) {
            System.out.println("Winner: " + player.getName());
        } else if (c > h) {
            System.out.println("Winner: " + computer.getName());
        } else {
            System.out.println("Result: Draw");
        }
    }

    

    public void startGame() {
        initializeDeck();
        deck.shuffle();
        deal();
        System.out.println("Game started! Deck shuffled and dealt evenly.");
        System.out.println(player.getName() + " cards: " + player.getHand().getCount());
        System.out.println(computer.getName() + " cards: " + computer.getHand().getCount());
    }

    /**
     * creates a 52-card deck
     */
    private void initializeDeck() {
        for (WarCard.Suit s : WarCard.Suit.values()) {
            for (WarCard.Rank r : WarCard.Rank.values()) {
                deck.addCardToBottom(new WarCard(s, r));
            }
        }
    }

    
    private void deal() {
        boolean toHuman = true;
        while (!deck.isEmpty()) {
            Card c = deck.drawTopCard();
            if (toHuman) {
                player.getHand().addCardToBottom(c);
            } else {
                computer.getHand().addCardToBottom(c);
            }
            toHuman = !toHuman;
        }
    }

    public void playRound() {
        centralPile = new GroupOfCards(52);

        Card hCard = player.playCard();
        Card cCard = computer.playCard();

        if (hCard == null) {
            System.out.println(player.getName() + " has no cards left.");
            declareWinner();
            return;
        }
        if (cCard == null) {
            System.out.println(computer.getName() + " has no cards left.");
            declareWinner();
            return;
        }

        // place played cards into central pile
        centralPile.addCardToBottom(hCard);
        centralPile.addCardToBottom(cCard);

        System.out.println(player.getName() + " plays: " + hCard);
        System.out.println(computer.getName() + " plays: " + cCard);

        int result = compareCards(hCard, cCard);
        if (result > 0) {
            // player wins
            System.out.println(player.getName() + " wins the round and takes " + centralPile.getCount() + " card(s).");
            player.addWonCards(centralPile);
        } else if (result < 0) {
            System.out.println(computer.getName() + " wins the round and takes " + centralPile.getCount() + " card(s).");
            computer.addWonCards(centralPile);
        } else {
            // war!
            System.out.println("War! Both cards are equal rank.");
            boolean ended = resolveWar(centralPile);
            if (ended) {
                return; // this ends game cause player doesn't have enough cards
            }
        }

        System.out.println("Counts -> " + player.getName() + ": " + player.getHand().getCount()
                + " | " + computer.getName() + ": " + computer.getHand().getCount());
    }

    /**
     * Compare two cards by rank.
     */
    private int compareCards(Card c1, Card c2) {
        int v1 = ((WarCard) c1).getRankValue();
        int v2 = ((WarCard) c2).getRankValue();

        if (v1 == 1) {
            v1 = 14;
        }
        if (v2 == 1) {
            v2 = 14;
        }

        return v1 - v2;
    }

//    this is the war code
    private boolean resolveWar(GroupOfCards central) {
        // For war we need ceetain face-down cards and  1 face-up each (total WAR_FACE_DOWN+1)
        int needed = WAR_FACE_DOWN + 1;

        // check
        if (!player.hasEnoughCards(needed)) {
            System.out.println(player.getName() + " doesn't have enough cards to continue the war.");
            System.out.println(computer.getName() + " wins!");
            computer.addWonCards(central); // with this the computer will win central pile
            declareWinner();
            return true;
        }
        if (!computer.hasEnoughCards(needed)) {
            System.out.println(computer.getName() + " does not have enough cards to continue the war.");
            System.out.println(player.getName() + " wins the game!");
            player.addWonCards(central);// with this the palyer will win central pile
            declareWinner();
            return true;
        }

        System.out.println("Each player places " + WAR_FACE_DOWN + " cards face-down...");

        for (int i = 0; i < WAR_FACE_DOWN; i++) {
            central.addCardToBottom(player.playCard());
            central.addCardToBottom(computer.playCard());
        }

        Card hUp = player.playCard();
        Card cUp = computer.playCard();
        central.addCardToBottom(hUp);
        central.addCardToBottom(cUp);

        System.out.println(player.getName() + " war card: " + hUp);
        System.out.println(computer.getName() + " war card: " + cUp);

        int result = compareCards(hUp, cUp);
        if (result > 0) {
            System.out.println(player.getName() + " wins the war and takes " + central.getCount() + " card(s).");
            player.addWonCards(central);
        } else if (result < 0) {
            System.out.println(computer.getName() + " wins the war and takes " + central.getCount() + " card(s).");
            computer.addWonCards(central);
        } else {
            System.out.println("War again! Another tie during war resolves into a new war.");
            return resolveWar(central); // recursive war
        }
        return false;
    }

    /**
     * Check if players have no cards.
     * @return 
     */
    public boolean checkForGameEnd() {
        if (player.getHand().isEmpty()) {
            System.out.println(player.getName() + " has no cards left. " + computer.getName() + " wins!");
            declareWinner();
            return true;
        }
        if (computer.getHand().isEmpty()) {
            System.out.println(computer.getName() + " has no cards left. " + player.getName() + " wins!");
            declareWinner();
            return true;
        }
        return false;
    }

    
     public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== War Game ===");
        System.out.print("Enter your name: ");
        String playerName = sc.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Player";

        WarGame game = new WarGame("War", playerName, "Computer");
        game.play();

        sc.close();
    }
}
