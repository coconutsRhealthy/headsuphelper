package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 28/01/2018.
 */
public class AbstractOpponent {

    //To be overridden by extending classes
    public String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                           double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position,
                           boolean preflop, List<Card> board, double facingOdds) {
        return null;
    }
}
