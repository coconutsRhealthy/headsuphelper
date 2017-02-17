package com.lennart.model.botgame;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotGame {

    private Hand hand;
    private double opponentStack;
    private double botStack;

    public BotGame() {
        hand = new Hand();
        hand.getNewBotAction();
    }

    public void getNewBotAction() {
        if(hand == null) {
            hand = new Hand();
        } else {
            hand.updateVariables();
        }
        hand.getNewBotAction();
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public double getOpponentStack() {
        return opponentStack;
    }

    public void setOpponentStack(double opponentStack) {
        this.opponentStack = opponentStack;
    }

    public double getBotStack() {
        return botStack;
    }

    public void setBotStack(double botStack) {
        this.botStack = botStack;
    }
}
