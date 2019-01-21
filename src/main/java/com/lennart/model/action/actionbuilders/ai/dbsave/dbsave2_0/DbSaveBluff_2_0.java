package com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 16/01/2019.
 */
public class DbSaveBluff_2_0 {

    private String boardWetnessGroup;

    public String getBoardWetnessGroupLogic(List<Card> board, int boardWetness) {
        String boardWetnessGroup = "";

        if(board.size() == 3) {
            //flushStraightWetness number
            if(boardWetness <= 112) {
                boardWetnessGroup = "dry";
            } else if(boardWetness <= 167) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "wet";
            }
        } else if(board.size() == 4) {
            if(boardWetness <= 67) {
                boardWetnessGroup = "wet";
            } else if(boardWetness <= 99) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "dry";
            }
        } else if(board.size() == 5) {
            if(boardWetness <= 66) {
                boardWetnessGroup = "wet";
            } else if(boardWetness <= 94) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "dry";
            }
        }

        return boardWetnessGroup;
    }

    public String getOpponentPreflopTypeLogic(String opponentName, boolean includingMedium) throws Exception {
        String opponentType;

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0();

        List<Double> preflopData = opponentIdentifier2_0.getOpponentLoosenessAndAggroness(opponentName, true);

        if(preflopData.isEmpty()) {
            opponentType = "uu";
        } else {
            double preflopLooseness = preflopData.get(0);
            double preflopAggroness = preflopData.get(1);

            if(includingMedium) {
                if(preflopLooseness < 0.67) {
                    opponentType = "t";
                } else if(preflopLooseness < 0.79) {
                    opponentType = "m";
                } else {
                    opponentType = "l";
                }

                if(preflopAggroness < 0.32) {
                    opponentType = opponentType + "p";
                } else if(preflopAggroness < 0.45) {
                    opponentType = opponentType + "m";
                } else {
                    opponentType = opponentType + "a";
                }
            } else {
                if(preflopLooseness < 0.725) {
                    opponentType = "t";
                } else {
                    opponentType = "l";
                }

                if(preflopAggroness < 0.375) {
                    opponentType = opponentType + "p";
                } else {
                    opponentType = opponentType + "a";
                }
            }
        }

        return opponentType;
    }

    public String getOpponentPostflopTypeLogic(String opponentName, boolean includingMedium) throws Exception {
        String opponentType;

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0();

        List<Double> postflopData = opponentIdentifier2_0.getOpponentLoosenessAndAggroness(opponentName, false);

        if(postflopData.isEmpty()) {
            opponentType = "uu";
        } else {
            double postflopLoosenes = postflopData.get(0);
            double postflopAggroness = postflopData.get(1);

            if(includingMedium) {
                if(postflopLoosenes < 0.46) {
                    opponentType = "t";
                } else if(postflopLoosenes < 0.54) {
                    opponentType = "m";
                } else {
                    opponentType = "l";
                }

                if(postflopAggroness < 0.30) {
                    opponentType = opponentType + "p";
                } else if(postflopAggroness < 0.40) {
                    opponentType = opponentType + "m";
                } else {
                    opponentType = opponentType + "a";
                }
            } else {
                if(postflopLoosenes < 0.5) {
                    opponentType = "t";
                } else {
                    opponentType = "l";
                }

                if(postflopAggroness < 0.343) {
                    opponentType = opponentType + "p";
                } else {
                    opponentType = opponentType + "a";
                }
            }
        }

        return opponentType;
    }
}
