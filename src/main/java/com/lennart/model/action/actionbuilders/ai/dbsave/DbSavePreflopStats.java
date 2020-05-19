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

    private double oppPreCall2betCount;
    private double oppPreCall3betCount;
    private double oppPreCall4bet_up_count;

    private double oppPreTotalCount;
    private double oppPreCallTotalCount;

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

    public double getPreXbetCallCountLogic(double previousOppTotalBetsize, double bigBlind, String xBetToCheck) {
        double inMethodPreCallXbetCount = 0;
        double botRaiseSizeBb = previousOppTotalBetsize / bigBlind;

        if(xBetToCheck.equals("preCall2bet")) {
            if(botRaiseSizeBb > 1 && botRaiseSizeBb <= 3) {
                inMethodPreCallXbetCount = 1;
            }
        } else if(xBetToCheck.equals("preCall3bet")) {
            if(botRaiseSizeBb > 3 && botRaiseSizeBb <= 10) {
                inMethodPreCallXbetCount = 1;
            }
        } else if(xBetToCheck.equals("preCall4bet_up")) {
            if(botRaiseSizeBb > 10) {
                inMethodPreCallXbetCount = 1;
            }
        }

        return inMethodPreCallXbetCount;
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

    public double getOppPreCall2betCount() {
        return oppPreCall2betCount;
    }

    public void setOppPreCall2betCount(double oppPreCall2betCount) {
        this.oppPreCall2betCount = oppPreCall2betCount;
    }

    public double getOppPreCall3betCount() {
        return oppPreCall3betCount;
    }

    public void setOppPreCall3betCount(double oppPreCall3betCount) {
        this.oppPreCall3betCount = oppPreCall3betCount;
    }

    public double getOppPreCall4bet_up_count() {
        return oppPreCall4bet_up_count;
    }

    public void setOppPreCall4bet_up_count(double oppPreCall4bet_up_count) {
        this.oppPreCall4bet_up_count = oppPreCall4bet_up_count;
    }

    public double getOppPreCallTotalCount() {
        return oppPreCallTotalCount;
    }

    public void setOppPreCallTotalCount(double oppPreCallTotalCount) {
        this.oppPreCallTotalCount = oppPreCallTotalCount;
    }
}
