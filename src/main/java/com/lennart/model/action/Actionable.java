package com.lennart.model.action;

import com.lennart.model.card.Card;

import java.util.List;
import java.util.Set;

/**
 * Created by LennartMac on 19/02/17.
 */
public interface Actionable {

    List<Card> getBotHoleCards();

    List<Card> getFlopCards();

    List<Card> getBoard();

    Set<Card> getKnownGameCards();

    void setKnownGameCards(Set<Card> knownGameCards);

    double getPotSize();

    double getBigBlind();

    double getBotTotalBetSize();

    double getOpponentTotalBetSize();

    boolean isBotIsButton();

    String getOpponentAction();

    void removeHoleCardsFromKnownGameCards();

    void addHoleCardsToKnownGameCards();

    double getBotStack();

    double getOpponentStack();

    boolean isPreviousBluffAction();

    void setPreviousBluffAction(boolean previousBluffAction);

    boolean isDrawBettingActionDone();

    void setDrawBettingActionDone(boolean drawBettingActionDone);

    String getOpponentType();

    boolean isPre3betOrPostRaisedPot();

    boolean isBettingActionDoneByPassivePlayer();

    double getHandsPlayedAgainstOpponent();

    boolean isOpponentIsDecentThinking();

    String getFloatAction();

    void setFloatAction(String floatAction);

    boolean isBotIsPre3bettor();

    void setBotIsPre3bettor(boolean botIsPre3bettor);

    boolean isOpponentBetsOrRaisesPostFlop();
}
