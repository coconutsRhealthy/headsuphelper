package com.lennart.model.action.actionbuilders.ai;

/**
 * Created by LennartMac on 19/02/17.
 */
public interface GameVariable {

    boolean isPreviousBluffAction();

    void setPreviousBluffAction(boolean previousBluffAction);

    boolean isBotIsButton();

    boolean isDrawBettingActionDone();

    void setDrawBettingActionDone(boolean drawBettingActionDone);
}
