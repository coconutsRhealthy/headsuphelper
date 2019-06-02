package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 26/05/2019.
 */
public class DbSavePreflopStats extends DbSave {

    private double oppPre2betCount;
    private double oppPre3betCount;
    private double oppPre4bet_up_count;
    private double oppPreTotalCount;
    private String opponentName;

    public double getPreXbetCountLogic(String opponentAction, List<Card> board, double opponentBetSize, double bigBlind,
                                       String xBetToCheck) {
        double inMethodPreXbetCount = 0;

        if(board == null || board.isEmpty()) {
            if(opponentAction.equals("raise")) {
                double oppBetSizeBb = opponentBetSize / bigBlind;

                if(xBetToCheck.equals("pre2bet")) {
                    if(oppBetSizeBb > 1 && oppBetSizeBb <= 3) {
                        inMethodPreXbetCount = 1;
                    }
                } else if(xBetToCheck.equals("pre3bet")) {
                    if(oppBetSizeBb > 3 && oppBetSizeBb <= 10) {
                        inMethodPreXbetCount = 1;
                    }
                } else if(xBetToCheck.equals("pre4bet_up")) {
                    if(oppBetSizeBb > 10) {
                        inMethodPreXbetCount = 1;
                    }
                }
            }
        }

        return inMethodPreXbetCount;
    }

    public double getPreTotalCountLogic(String opponentAction, List<Card> board) {
        double inMethodPreTotalCount = 0;

        if((board == null || board.isEmpty()) && !opponentAction.equals("bet")) {
            inMethodPreTotalCount = 1;
        }

        return inMethodPreTotalCount;
    }

    public double getOppPre2betCount() {
        return oppPre2betCount;
    }

    public void setOppPre2betCount(double oppPre2betCount) {
        this.oppPre2betCount = oppPre2betCount;
    }

    public double getOppPre3betCount() {
        return oppPre3betCount;
    }

    public void setOppPre3betCount(double oppPre3betCount) {
        this.oppPre3betCount = oppPre3betCount;
    }

    public double getOppPre4bet_up_count() {
        return oppPre4bet_up_count;
    }

    public void setOppPre4bet_up_count(double oppPre4bet_up_count) {
        this.oppPre4bet_up_count = oppPre4bet_up_count;
    }

    public double getOppPreTotalCount() {
        return oppPreTotalCount;
    }

    public void setOppPreTotalCount(double oppPreTotalCount) {
        this.oppPreTotalCount = oppPreTotalCount;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }
}
