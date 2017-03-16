package com.lennart.model.action;

import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuildable;

import java.util.List;

/**
 * Created by LennartMac on 19/02/17.
 */
public interface Actionable extends RangeBuildable {

    void removeHoleCardsFromKnownGameCards();

    void addHoleCardsToKnownGameCards();

    double getBotStack();

    double getOpponentStack();

    String getOpponentAction();

    boolean isPreviousBluffAction();

    void setPreviousBluffAction(boolean previousBluffAction);

    boolean isDrawBettingActionDone();

    void setDrawBettingActionDone(boolean drawBettingActionDone);
}
