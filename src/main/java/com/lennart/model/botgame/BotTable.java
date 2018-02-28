package com.lennart.model.botgame;

import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotTable {

    private BotHand botHand;
    private Map<String, Integer> numberOfHandsPerOpponentMap = new HashMap<>();

    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand("initialize", this);
        botHand.getNewBotAction(this);
    }

    public BotTable(boolean continuously) {
        boolean initializationNeeded = true;

        while(initializationNeeded) {
            if(NetBetTableReader.botIsToAct()) {
                botHand = new BotHand("initialize", this);
                botHand.getNewBotAction(this);
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
                getNewBotActionInBotTable();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getNewBotActionInBotTable() {
        botHand.updateVariables(this);
        botHand.getNewBotAction(this);
        System.out.println();
    }

    public void setBotHand(BotHand botHand) {
        this.botHand = botHand;
    }

    public void updateNumberOfHandsPerOpponentMap(String opponentPlayerName) {
        if(numberOfHandsPerOpponentMap.get(opponentPlayerName) == null) {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, 0);
        } else {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, numberOfHandsPerOpponentMap.get(opponentPlayerName) + 1);
        }
    }

    public Map<String, Integer> getNumberOfHandsPerOpponentMap() {
        return numberOfHandsPerOpponentMap;
    }

    public BotHand getBotHand() {
        return botHand;
    }

    public void setNumberOfHandsPerOpponentMap(Map<String, Integer> numberOfHandsPerOpponentMap) {
        this.numberOfHandsPerOpponentMap = numberOfHandsPerOpponentMap;
    }
}