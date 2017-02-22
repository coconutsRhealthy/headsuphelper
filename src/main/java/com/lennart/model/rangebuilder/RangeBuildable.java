package com.lennart.model.rangebuilder;

import com.lennart.model.card.Card;

import java.util.List;
import java.util.Set;

/**
 * Created by LennartMac on 19/02/17.
 */
public interface RangeBuildable {

    List<Card> getBotHoleCards();

    List<Card> getBoard();

    Set<Card> getKnownGameCards();

    double getOpponentPreCall2betStat();

    double getOpponentPre3betStat();

    boolean isOpponentLastActionWasPreflop();

    Set<Set<Card>> getOpponentRange();

    double getPotSize();

    double getBigBlind();

    double getBotTotalBetSize();

    double getOpponentTotalBetSize();

    boolean isBotIsButton();

    double getHandsOpponentOopFacingPreflop2bet();

    double getOpponentFormerTotalCallAmount();
}
