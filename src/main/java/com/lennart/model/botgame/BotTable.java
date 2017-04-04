package com.lennart.model.botgame;

import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotTable {

    private BotHand botHand;

    public BotTable() {
        //default constructor
    }

    public BotTable(String initialize) {
        botHand = new BotHand("initialize");
        botHand.getNewBotAction();
    }

    public BotTable(boolean continuously) {
        boolean initializationNeeded = true;
        int counter = 0;

        while(initializationNeeded) {
            counter++;
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
                getNewBotAction();
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

    public void getNewBotAction() {
        botHand = botHand.updateVariables();
        botHand.getNewBotAction();
        System.out.println();
    }

    public BotHand getBotHand() {
        return botHand;
    }

    public void setBotHand(BotHand botHand) {
        this.botHand = botHand;
    }
}
