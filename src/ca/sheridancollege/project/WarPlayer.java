/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.sheridancollege.project;

/**
 *
 * @author Diya Sharma
 * @modifier Razia Kauser
 * @modifier Mohammed Fahad Ahmed
 * @modifier Harsh Saini 
 */
public class WarPlayer extends Player {

    private GroupOfCards hand;

    public WarPlayer(String name) {
        super(name);
        this.hand = new GroupOfCards(0);
    }

    public GroupOfCards getHand() {
        return hand;
    }

    public void setHand(GroupOfCards hand) {
        this.hand = hand;
    }

    /**
     * Play top card (draw from top of hand)
     *
     * @return
     */
    public Card playCard() {
        return hand.drawTopCard();
    }

    public void addWonCards(GroupOfCards won) {
        if (won == null) {
            return;
        }
        this.hand.addAll(won);
    }

    public boolean hasEnoughCards(int count) {
        return hand.getCount() >= count;
    }

@Override
    public void play() {
        playCard();
    }
}
