package com.lennart.model.botgame;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotGame {

    private Hand hand;
    private double opponentStack;
    private double botStack;

    public BotGame() {
        if(this.hand == null) {
            this.hand = new Hand();
        }
    }
}
