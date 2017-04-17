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
        botHand = botHand.updateVariables(this);
        calculateOpponentPreflopStats();
        botHand.getNewBotAction();
        System.out.println();
    }

    private void calculateOpponentPreflopStats() {
        List<String> botActionHistory = botHand.getBotActionHistory();

        if(botActionHistory == null) {
//            //update VPIP
//            String opponentAction = botHand.getOpponentAction();
//            String playerName = botHand.getOpponentPlayerName();
//
//            if(opponentAction != null && (opponentAction.contains("call") || opponentAction.contains("raise"))) {
//                double handsOpponentVPIP = opponentPlayerNamesAndStats.get(playerName).get(4) + 1;
//                opponentPlayerNamesAndStats.get(playerName).set(4, handsOpponentVPIP);
//            }
        } else if(botActionHistory.size() < 2) {
            addPlayerToMapIfNecessary(botHand);

            if(opponentPlayerNamesAndStats.get(botHand.getOpponentPlayerName()) != null) {
                String opponentAction = botHand.getOpponentAction();
                String playerName = botHand.getOpponentPlayerName();
                boolean botIsButton = botHand.isBotIsButton();
                String currentStreet = botHand.getStreet();
                String streetAtPreviousAction = botHand.getStreetAtPreviousActionRequest();

//                //update VPIP
//                if(opponentAction != null && (opponentAction.contains("call") || opponentAction.contains("raise"))) {
//                    double handsOpponentVPIP = opponentPlayerNamesAndStats.get(playerName).get(4) + 1;
//                    opponentPlayerNamesAndStats.get(playerName).set(4, handsOpponentVPIP);
//                }

                if(botIsButton && botActionHistory.size() == 1 && botActionHistory.get(0).contains("raise")) {
                    double handsOpponentOopFacingPreflop2bet = opponentPlayerNamesAndStats.get(playerName).get(0) + 1;
                    opponentPlayerNamesAndStats.get(playerName).set(0, handsOpponentOopFacingPreflop2bet);
                }

                if(botIsButton) {
                    if(streetAtPreviousAction != null && streetAtPreviousAction.equals("preflop")) {
                        if(currentStreet != null && currentStreet.equals("preflop")) {
                            if(opponentAction != null && opponentAction.contains("raise")) {
                                double handsOpponentOop3bet = opponentPlayerNamesAndStats.get(playerName).get(2) + 1;
                                opponentPlayerNamesAndStats.get(playerName).set(2, handsOpponentOop3bet);
                            }
                        } else if(currentStreet != null && currentStreet.equals("flop")) {
                            if(botActionHistory.size() == 1 && botActionHistory.get(0).contains("raise")) {
                                double handsOpponentOopCall2bet = opponentPlayerNamesAndStats.get(playerName).get(1) + 1;
                                opponentPlayerNamesAndStats.get(playerName).set(1, handsOpponentOopCall2bet);
                            }
                        }
                    }
                }

                if(opponentPlayerNamesAndStats.get(playerName).get(3) >= 20) {
                    botHand.setOpponentPreCall2betStat(opponentPlayerNamesAndStats.get(playerName).get(1) /
                            opponentPlayerNamesAndStats.get(playerName).get(0));
                    botHand.setOpponentPre3betStat(opponentPlayerNamesAndStats.get(playerName).get(2) /
                            opponentPlayerNamesAndStats.get(playerName).get(0));

                    botHand.setOpponentVpipStat(opponentPlayerNamesAndStats.get(playerName).get(4) /
                            opponentPlayerNamesAndStats.get(playerName).get(3));
                }

                for (Map.Entry<String, List<Double>> entry : opponentPlayerNamesAndStats.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
                System.out.println("opponentVpipStat in calculateOpponentPreflopStats() in BotTable: " + opponentPlayerNamesAndStats.get(playerName).get(4) /
                        opponentPlayerNamesAndStats.get(playerName).get(3));
                System.out.println("opponentPre3betStat in calculateOpponentPreflopStats() in BotTable: " + opponentPlayerNamesAndStats.get(playerName).get(2) /
                        opponentPlayerNamesAndStats.get(playerName).get(0));
            }
        }
    }

    public void addPlayerToMapIfNecessary(BotHand botHand) {
        if(opponentPlayerNamesAndStats == null) {
            opponentPlayerNamesAndStats = new HashMap<>();
        }

        String playerName = botHand.getOpponentPlayerName();

        if(playerName != null && opponentPlayerNamesAndStats.get(playerName) == null) {
            opponentPlayerNamesAndStats.put(playerName, new ArrayList<>());
            for(int i = 0; i < 5; i++) {
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

    public Map<String, List<Double>> getOpponentPlayerNamesAndStats() {
        return opponentPlayerNamesAndStats;
    }

    public void setOpponentPlayerNamesAndStats(Map<String, List<Double>> opponentPlayerNamesAndStats) {
        this.opponentPlayerNamesAndStats = opponentPlayerNamesAndStats;
    }
}