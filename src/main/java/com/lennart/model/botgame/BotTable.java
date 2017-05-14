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

    public void addHandToHandsEligibleForVpip(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(0) != null) {
            double handsEligibleForVpip = opponentPlayerNamesAndStats.get(opponentPlayerName).get(0) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(0, handsEligibleForVpip);
        }
    }

    public void addHandToHandsVpip(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(1) != null) {
            double handsVpip = opponentPlayerNamesAndStats.get(opponentPlayerName).get(1) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(1, handsVpip);
        }
    }

    public void addHandToHandsEligibleFor3bet(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(2) != null) {
            double handsEligibleFor3bet = opponentPlayerNamesAndStats.get(opponentPlayerName).get(2) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(2, handsEligibleFor3bet);
        }
    }

    public void addHandToHands3bet(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(3) != null) {
            double hands3bet = opponentPlayerNamesAndStats.get(opponentPlayerName).get(3) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(3, hands3bet);
        }
    }

    public void addHandToHandsEligibleForIpPfr(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(4) != null) {
            double handsEligibleForIpPfr = opponentPlayerNamesAndStats.get(opponentPlayerName).get(4) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(4, handsEligibleForIpPfr);
        }
    }

    public void addHandToHandsIpPfr(String opponentPlayerName) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(5) != null) {
            double handsIpPfr = opponentPlayerNamesAndStats.get(opponentPlayerName).get(5) + 1;
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(5, handsIpPfr);
        }
    }

    public void setInitialStackSizeOfOpponent(String opponentPlayerName, double stackSizeBb) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(6) != null) {
            if(opponentPlayerNamesAndStats.get(opponentPlayerName).get(6) == 0) {
                opponentPlayerNamesAndStats.get(opponentPlayerName).set(6, stackSizeBb);
            }
        }
    }

    public void setLastPfrSizingOfOpponent(String opponentPlayerName, double pfrSizingBb) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(7) != null) {
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(7, pfrSizingBb);
        }
    }

    public void setOpponentWonMoreThan100bb(String opponentPlayerName, double trueOrFalse) {
        addPlayerToStatsMapIfNecessary(opponentPlayerName);

        if(opponentPlayerNamesAndStats != null && opponentPlayerNamesAndStats.get(opponentPlayerName) != null &&
                opponentPlayerNamesAndStats.get(opponentPlayerName).get(8) != null) {
            opponentPlayerNamesAndStats.get(opponentPlayerName).set(8, trueOrFalse);
        }
    }

    private void addPlayerToStatsMapIfNecessary(String opponentPlayerName) {
        if(opponentPlayerNamesAndStats == null) {
            opponentPlayerNamesAndStats = new HashMap<>();
        }

        //element (8) is gonna be if opponent won more than 100bb this session. 0 if false, 1 if true
        if(opponentPlayerName != null && opponentPlayerNamesAndStats.get(opponentPlayerName) == null) {
            opponentPlayerNamesAndStats.put(opponentPlayerName, new ArrayList<>());
            for(int i = 0; i < 9; i++) {
                opponentPlayerNamesAndStats.get(opponentPlayerName).add(0d);
            }
        }
    }

    public void addBooleanToBotIsButtonHistoryPerOpponentMap(String opponentPlayerName, Boolean botIsButton) {
        addPlayerToBotIsButtonHistoryPerOpponentMapIfNecessary(opponentPlayerName);

        if(opponentPlayerName != null && botIsButtonHistoryPerOpponentMap.get(opponentPlayerName) == null) {
            botIsButtonHistoryPerOpponentMap.get(opponentPlayerName).add(botIsButton);
        }
    }

    private void addPlayerToBotIsButtonHistoryPerOpponentMapIfNecessary(String opponentPlayerName) {
        if(botIsButtonHistoryPerOpponentMap == null) {
            botIsButtonHistoryPerOpponentMap = new HashMap<>();
        }

        if(opponentPlayerName != null && botIsButtonHistoryPerOpponentMap.get(opponentPlayerName) == null) {
            botIsButtonHistoryPerOpponentMap.put(opponentPlayerName, new ArrayList<>());
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

    public Map<String, List<Boolean>> getBotIsButtonHistoryPerOpponentMap() {
        return botIsButtonHistoryPerOpponentMap;
    }

    public void setBotIsButtonHistoryPerOpponentMap(Map<String, List<Boolean>> botIsButtonHistoryPerOpponentMap) {
        this.botIsButtonHistoryPerOpponentMap = botIsButtonHistoryPerOpponentMap;
    }
}