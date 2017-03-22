package com.lennart.model.botgame;

import com.lennart.model.card.Card;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotTable {

    private List<String> opponentPlayerNames;
    private String stake;
    private BotHand botHand;
    private double handsOpponentOopCall2bet;
    private double handsOpponentOop3bet;
    private double handsOpponentOopFacingPreflop2bet;


    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand("initialize");
        botHand.getNewBotAction();
    }

    public BotTable(boolean continuously) {
        botHand = new BotHand("initialize");
        botHand.getNewBotAction();

        while(true) {
            if(botHand.botIsToAct()) {
                getNewBotAction();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(700);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getNewBotAction() {
        botHand = botHand.updateVariables();
        calculateOpponentPreflopStats();
        botHand.getNewBotAction();
    }

    private void calculateOpponentPreflopStats() {
        if(!botHand.isOpponentPreflopStatsDoneForHand()) {
            List<Card> board = botHand.getBoard();
            String botWrittenAction = botHand.getBotWrittenAction();
            String opponentAction = botHand.getOpponentAction();
            boolean botIsButton = botHand.isBotIsButton();
            double opponentTotalBetSize = botHand.getOpponentTotalBetSize();
            double bigBlind = botHand.getBigBlind();

            if(board == null && botIsButton && botWrittenAction.contains("raise") && opponentTotalBetSize == bigBlind) {
                handsOpponentOopFacingPreflop2bet++;
                if(opponentAction.equals("call")) {
                    handsOpponentOopCall2bet++;
                }
                if(opponentAction.equals("raise")) {
                    handsOpponentOop3bet++;
                }
            }
            botHand.setHandsOpponentOopFacingPreflop2bet(handsOpponentOopFacingPreflop2bet);
            botHand.setOpponentPreCall2betStat(handsOpponentOopCall2bet / handsOpponentOopFacingPreflop2bet);
            botHand.setOpponentPre3betStat(handsOpponentOop3bet / handsOpponentOopFacingPreflop2bet);
            botHand.setOpponentPreflopStatsDoneForHand(true);
        }
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

    public double getHandsOpponentOopCall2bet() {
        return handsOpponentOopCall2bet;
    }

    public void setHandsOpponentOopCall2bet(double handsOpponentOopCall2bet) {
        this.handsOpponentOopCall2bet = handsOpponentOopCall2bet;
    }

    public double getHandsOpponentOop3bet() {
        return handsOpponentOop3bet;
    }

    public void setHandsOpponentOop3bet(double handsOpponentOop3bet) {
        this.handsOpponentOop3bet = handsOpponentOop3bet;
    }

    public double getHandsOpponentOopFacingPreflop2bet() {
        return handsOpponentOopFacingPreflop2bet;
    }

    public void setHandsOpponentOopFacingPreflop2bet(double handsOpponentOopFacingPreflop2bet) {
        this.handsOpponentOopFacingPreflop2bet = handsOpponentOopFacingPreflop2bet;
    }
}
