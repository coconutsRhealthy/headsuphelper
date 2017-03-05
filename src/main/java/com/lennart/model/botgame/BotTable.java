package com.lennart.model.botgame;

import java.util.List;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotTable {

    private List<String> opponentPlayerNames;
    private String stake;
    private BotHand botHand;

    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand("initialize");
        botHand.getNewBotAction();
    }

    public void getNewBotAction() {
        botHand = botHand.updateVariables();
        botHand.getNewBotAction();
    }

    public BotHand getBotHand() {
        return botHand;
    }

    public void setBotHand(BotHand botHand) {
        this.botHand = botHand;
    }

    public List<String> getOpponentPlayerNames() {
        return opponentPlayerNames;
    }

    public void setOpponentPlayerNames(List<String> opponentPlayerNames) {
        this.opponentPlayerNames = opponentPlayerNames;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }
}
