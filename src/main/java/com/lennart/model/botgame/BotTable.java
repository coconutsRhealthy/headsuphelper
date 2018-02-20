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
    private Map<String, List<Boolean>> botIsButtonHistoryPerOpponentMap;

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
                try {
                    MouseKeyboard.moveMouseToLocation(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.click(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(500);
                    MouseKeyboard.moveMouseToLocation(20, 20);
                    counter = 0;
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.print(counter);
        }

        while(true) {
            counter++;
            if(NetBetTableReader.botIsToAct()) {
                if(!getNewBotActionInBotTable()) {
                    new BotTable(true);
                    return;
                }
            }

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(counter > 60) {
                try {
                    MouseKeyboard.moveMouseToLocation(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.click(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.moveMouseToLocation(20, 20);
                    counter = 0;
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.print(counter);
        }
    }

    public boolean getNewBotActionInBotTable() {
        if(botHand.updateVariables(this)) {
            botHand.getNewBotAction();
            System.out.println();
            return true;
        }
        return false;
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