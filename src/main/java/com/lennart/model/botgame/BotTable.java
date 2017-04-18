package com.lennart.model.botgame;

import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotTable {

    private List<String> opponentPlayerNames;
    private String stake;
    private BotHand botHand;

    private Map<String, List<Double>> opponentPlayerNamesAndStats;

    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand(this);
        botHand.getNewBotAction();
    }

    public BotTable(boolean continuously) {
        boolean initializationNeeded = true;
        int counter = 0;

        while(initializationNeeded) {
            counter++;
            if(NetBetTableReader.botIsToAct()) {
                botHand = new BotHand(this);
                botHand.getNewBotAction();
                initializationNeeded = false;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(counter > 60) {
                MouseKeyboard.moveMouseToLocation(1565, 909);
                MouseKeyboard.click(1565, 909);
                MouseKeyboard.moveMouseToLocation(20, 20);
                counter = 0;
            }
        }

        while(true) {
            counter++;
            if(NetBetTableReader.botIsToAct()) {
                getNewBotActionInBotTable();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(counter > 60) {
                MouseKeyboard.moveMouseToLocation(1565, 909);
                MouseKeyboard.click(1565, 909);
                MouseKeyboard.moveMouseToLocation(20, 20);
                counter = 0;
            }
        }
    }

    public void getNewBotActionInBotTable() {
        botHand = botHand.updateVariables(this);
        botHand.getNewBotAction();
        System.out.println();
    }

    public void addHandToHandsEligibleForVpip(String opponentPlayerName) {
        addPlayerToMapIfNecessary(opponentPlayerName);
        double handsEligibleForVpip = opponentPlayerNamesAndStats.get(opponentPlayerName).get(0) + 1;
        opponentPlayerNamesAndStats.get(opponentPlayerName).set(0, handsEligibleForVpip);
    }

    public void addHandToHandsVpip(String opponentPlayerName) {
        addPlayerToMapIfNecessary(opponentPlayerName);
        double handsVpip = opponentPlayerNamesAndStats.get(opponentPlayerName).get(1) + 1;
        opponentPlayerNamesAndStats.get(opponentPlayerName).set(1, handsVpip);
    }

    public void addHandToHandsEligibleFor3bet(String opponentPlayerName) {
        addPlayerToMapIfNecessary(opponentPlayerName);
        double handsEligibleFor3bet = opponentPlayerNamesAndStats.get(opponentPlayerName).get(2) + 1;
        opponentPlayerNamesAndStats.get(opponentPlayerName).set(2, handsEligibleFor3bet);
    }

    public void addHandToHands3bet(String opponentPlayerName) {
        addPlayerToMapIfNecessary(opponentPlayerName);
        double hands3bet = opponentPlayerNamesAndStats.get(opponentPlayerName).get(3) + 1;
        opponentPlayerNamesAndStats.get(opponentPlayerName).set(3, hands3bet);
    }

    public void addPlayerToMapIfNecessary(String opponentPlayerName) {
        if(opponentPlayerNamesAndStats == null) {
            opponentPlayerNamesAndStats = new HashMap<>();
        }

        if(opponentPlayerName != null && opponentPlayerNamesAndStats.get(opponentPlayerName) == null) {
            opponentPlayerNamesAndStats.put(opponentPlayerName, new ArrayList<>());
            for(int i = 0; i < 4; i++) {
                opponentPlayerNamesAndStats.get(opponentPlayerName).add(0d);
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

    public Map<String, List<Double>> getOpponentPlayerNamesAndStats() {
        return opponentPlayerNamesAndStats;
    }

    public void setOpponentPlayerNamesAndStats(Map<String, List<Double>> opponentPlayerNamesAndStats) {
        this.opponentPlayerNamesAndStats = opponentPlayerNamesAndStats;
    }
}