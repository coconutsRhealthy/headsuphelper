package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, List<Double>> opponentPlayerNamesAndStats;


    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand("initialize");
        botHand.getNewBotAction();
    }

    public BotTable(boolean continuously) {
        boolean initializationNeeded = true;

        while(initializationNeeded) {
            if(NetBetTableReader.botIsToAct()) {
                botHand = new BotHand("initialize");
                botHand.getNewBotAction();
                initializationNeeded = false;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(true) {
            if(NetBetTableReader.botIsToAct()) {
                getNewBotAction();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getNewBotAction() {
        botHand = botHand.updateVariables();
        calculateOpponentPreflopStats();
        botHand.getNewBotAction();
        System.out.println();
    }

    private void calculateOpponentPreflopStats() {
        if(!botHand.isOpponentPreflopStatsDoneForHand()) {
            List<Card> board = botHand.getBoard();
            String botWrittenAction = botHand.getBotWrittenAction();
            String opponentAction = botHand.getOpponentAction();
            boolean botIsButton = botHand.isBotIsButton();
            double opponentTotalBetSize = botHand.getOpponentTotalBetSize();
            double bigBlind = botHand.getBigBlind();

            if(board == null && botIsButton && botWrittenAction != null && botWrittenAction.contains("raise") && opponentTotalBetSize == bigBlind) {
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

    private void calculateOpponentPreflopStatsNew() {
        if(!botHand.isOpponentPreflopStatsDoneForHand()) {
            addPlayerToMapIfNecessary();

            if(opponentPlayerNamesAndStats.get(botHand.getOpponentPlayerName()) != null) {
                List<Card> board = botHand.getBoard();
                String botWrittenAction = botHand.getBotWrittenAction();
                String opponentAction = botHand.getOpponentAction();
                String playerName = botHand.getOpponentPlayerName();
                boolean botIsButton = botHand.isBotIsButton();
                double opponentTotalBetSize = botHand.getOpponentTotalBetSize();
                double bigBlind = botHand.getBigBlind();

                if(board == null && botIsButton && botWrittenAction != null && botWrittenAction.contains("raise") && opponentTotalBetSize == bigBlind) {
                    //add 1 to handsOpponentOopFacingPreflop2bet:
                    opponentPlayerNamesAndStats.get(playerName).set(0, opponentPlayerNamesAndStats.get(playerName).get(0) + 1);
                    if(opponentAction.equals("call")) {
                        //add 1 to handsOpponentOopCall2bet:
                        opponentPlayerNamesAndStats.get(playerName).set(1, opponentPlayerNamesAndStats.get(playerName).get(1) + 1);
                    }
                    if(opponentAction.equals("raise")) {
                        //add 1 to handsOpponentOop3bet:
                        opponentPlayerNamesAndStats.get(playerName).set(2, opponentPlayerNamesAndStats.get(playerName).get(2) + 1);
                    }
                }
                botHand.setHandsOpponentOopFacingPreflop2bet(opponentPlayerNamesAndStats.get(playerName).get(0));
                botHand.setOpponentPreCall2betStat(opponentPlayerNamesAndStats.get(playerName).get(1) /
                        opponentPlayerNamesAndStats.get(playerName).get(0));
                botHand.setOpponentPre3betStat(opponentPlayerNamesAndStats.get(playerName).get(2) /
                        opponentPlayerNamesAndStats.get(playerName).get(0));
                botHand.setOpponentPreflopStatsDoneForHand(true);
            }
        }
    }

    private void addPlayerToMapIfNecessary() {
        if(opponentPlayerNamesAndStats == null) {
            opponentPlayerNamesAndStats = new HashMap<>();
        }

        String playerName = botHand.getOpponentPlayerName();

        if(playerName != null && opponentPlayerNamesAndStats.get(playerName) == null) {
            opponentPlayerNamesAndStats.put(playerName, new ArrayList<>());
            for(int i = 0; i < 3; i++) {
                opponentPlayerNamesAndStats.get(playerName).add(0d);
            }
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

    public Map<String, List<Double>> getOpponentPlayerNamesAndStats() {
        return opponentPlayerNamesAndStats;
    }

    public void setOpponentPlayerNamesAndStats(Map<String, List<Double>> opponentPlayerNamesAndStats) {
        this.opponentPlayerNamesAndStats = opponentPlayerNamesAndStats;
    }
}
