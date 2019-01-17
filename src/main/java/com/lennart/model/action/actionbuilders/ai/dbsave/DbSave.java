package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.RangeTracker;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class DbSave {

    public String getStreetViaLogic(List<Card> board) {
        String street;

        if(board != null) {
            if(board.size() >= 3) {
                if(board.size() == 3) {
                    street = "Flop";
                } else if(board.size() == 4) {
                    street = "Turn";
                } else {
                    street = "River";
                }
            } else {
                street = "Preflop";
            }
        } else {
            street = "Preflop";
        }

        return street;
    }

    public String getPositionLogic(boolean position) {
        String positionString;

        if(position) {
            positionString = "Ip";
        } else {
            positionString = "Oop";
        }

        return positionString;
    }

    public String getSizingGroupViaLogic(double sizingBb) {
        String sizingGroup;

        if(sizingBb <= 5) {
            sizingGroup = "Sizing_0-5bb";
        } else if(sizingBb <= 10) {
            sizingGroup = "Sizing_5-10bb";
        } else if(sizingBb <= 15) {
            sizingGroup = "Sizing_10-15bb";
        } else if(sizingBb <= 20) {
            sizingGroup = "Sizing_15-20bb";
        } else if(sizingBb <= 30) {
            sizingGroup = "Sizing_20-30bb";
        } else {
            sizingGroup = "Sizing_30bb_up";
        }

        return sizingGroup;
    }

    public String getStrongDrawLogic(boolean strongFlushDraw, boolean strongOosd) {
        String strongDraw;

        if(strongFlushDraw || strongOosd) {
            strongDraw = "StrongDrawTrue";
        } else {
            strongDraw = "StrongDrawFalse";
        }

        return strongDraw;
    }

    public String getEffectiveStackLogic(double botStackBb, double opponentStackBb) {
        String effectiveStackBbString;
        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 35) {
            effectiveStackBbString = "EffStack_0_35_";
        } else if(effectiveStackBb <= 70) {
            effectiveStackBbString = "EffStack_35_70_";
        } else if(effectiveStackBb <= 120) {
            effectiveStackBbString = "EffStack_70_120_";
        } else {
            effectiveStackBbString = "EffStack_120_up_";
        }

        return effectiveStackBbString;
    }

    public String getDrawWetnessLogic(List<Card> board, int drawWetnes) {
        String street = getStreet(board);

        if(street.equals("unknown")) {
            return street;
        } else {
            return new RangeTracker().getDrawWetnessString(street, drawWetnes);
        }
    }

    public String getBoatWetnessLogic(List<Card> board, int boatWetness) {
        String street = getStreet(board);

        if(street.equals("unknown")) {
            return street;
        } else {
            return new RangeTracker().getBoatWetnessString(street, boatWetness);
        }
    }

    private String getStreet(List<Card> board) {
        String street = "unknown";

        if(board != null) {
            if(board.size() == 3) {
                street = "Flop";
            } else if(board.size() == 4) {
                street = "Turn";
            } else if(board.size() == 5) {
                street = "River";
            }
        }

        return street;
    }

    public String getOppAggroGroupViaLogic(String opponentName) throws Exception {
        String oppAggroGroup;

        OpponentIdentifier opponentIdentifier = new OpponentIdentifier();
        int numberOfHands = opponentIdentifier.getOpponentNumberOfHandsFromDb(opponentName);

        if(numberOfHands < 20) {
            oppAggroGroup = "Aggro_unknown";
        } else {
            double oppAggressiveness = opponentIdentifier.getOppAggressiveness(opponentName);

            if(oppAggressiveness <= 0.1875) {
                oppAggroGroup = "Aggro_0_33_";
            } else if(oppAggressiveness <= 0.32) {
                oppAggroGroup = "Aggro_33_66_";
            } else {
                oppAggroGroup = "Aggro_66_100_";
            }
        }

        return oppAggroGroup;
    }

    public String getComboLogic(List<Card> holeCards) {
        String comboString = "";

        int rankCard1 = holeCards.get(0).getRank();
        int rankCard2 = holeCards.get(1).getRank();

        List<Card> holeCardsCopy = new ArrayList<>();

        if(rankCard1 < rankCard2) {
            holeCardsCopy.add(holeCards.get(1));
            holeCardsCopy.add(holeCards.get(0));
        } else {
            holeCardsCopy.addAll(holeCards);
        }

        for(Card card : holeCardsCopy) {
            int rank = card.getRank();

            switch(rank) {
                case 14:
                    comboString = comboString + "A";
                break;
                case 13:
                    comboString = comboString + "K";
                break;
                case 12:
                    comboString = comboString + "Q";
                break;
                case 11:
                    comboString = comboString + "J";
                break;
                case 10:
                    comboString = comboString + "T";
                break;
                default:
                    comboString = comboString + String.valueOf(rank);
            }
        }

        if(comboString.charAt(0) != comboString.charAt(1)) {
            if(holeCardsCopy.get(0).getSuit() == holeCardsCopy.get(1).getSuit()) {
                comboString = comboString + "s";
            } else {
                comboString = comboString + "o";
            }
        }

        return comboString;
    }
}
