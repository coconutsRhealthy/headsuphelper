package com.lennart.model.rangebuilder;

import com.lennart.model.card.Card;

import java.util.List;
import java.util.Set;

/**
 * Created by LennartMac on 19/02/17.
 */
public interface RangeBuildable {

    List<Card> getBotHoleCards();

    List<Card> getFlopCards();

    void setFlopCards(List<Card> flopCards);

    Card getTurnCard();

    void setTurnCard(Card turnCard);

    Card getRiverCard();

    void setRiverCard(Card riverCard);

    List<Card> getBoard();

    void setBoard(List<Card> board);

    Set<Card> getKnownGameCards();

    void setKnownGameCards(Set<Card> knownGameCards);

    double getOpponentPreCall2betStat();

    double getOpponentPre3betStat();

    Set<Set<Card>> getOpponentRange();

    void setOpponentRange(Set<Set<Card>> opponentRange);

    double getPotSize();

    double getBigBlind();

    double getBotTotalBetSize();

    double getOpponentTotalBetSize();

    boolean isBotIsButton();

    double getHandsOpponentOopFacingPreflop2bet();

    List<String> getBotActionHistory();

    void setRangeBuilder(RangeBuilder rangeBuilder);

    double getPotSizeAfterLastBotAction();

    String getOpponentAction();
}
