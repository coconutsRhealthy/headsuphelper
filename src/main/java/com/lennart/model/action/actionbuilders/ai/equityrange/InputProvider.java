package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OppIdentifierPreflopStats;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

import java.util.Map;

/**
 * Created by LennartMac on 13/05/2020.
 */
public class InputProvider {

    private static final String LOW = "low";
    private static final String MEDIUM = "medium";
    private static final String HIGH = "high";
    private static final String SMALL = "small";
    private static final String LARGE = "large";

    private Map<String, String> oppPreGroupMap = null;
    private OpponentIdentifier2_0 opponentIdentifier2_0 = null;


    public String getOppPreCall2betGroup(String oppName) {
        String oppPreCall2betGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPreCall2betGroup = oppPreGroupMap.get("preCall2betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall2betGroup = MEDIUM;
        }

        return oppPreCall2betGroup;
    }

    public String getOppPreCall3betGroup(String oppName) {
        String oppPreCall3betGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPreCall3betGroup = oppPreGroupMap.get("preCall3betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall3betGroup = MEDIUM;
        }

        return oppPreCall3betGroup;
    }

    public String getOppPreCall4betUpGroup(String oppName) {
        String oppPreCall4betUpGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPreCall4betUpGroup = oppPreGroupMap.get("preCall4bet_up_group");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall4betUpGroup = MEDIUM;
        }

        return oppPreCall4betUpGroup;
    }

    public String getOppPre2betGroup(String oppName) {
        String oppPre2betGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPre2betGroup = oppPreGroupMap.get("pre2betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre2betGroup = MEDIUM;
        }

        return oppPre2betGroup;
    }

    public String getOppPre3betGroup(String oppName) {
        String oppPre3betGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPre3betGroup = oppPreGroupMap.get("pre3betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre3betGroup = MEDIUM;
        }

        return oppPre3betGroup;
    }

    public String getOppPre4betUpGroup(String oppName) {
        String oppPre4betGroup;

        try {
            if(oppPreGroupMap == null) {
                oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName);
            }

            oppPre4betGroup = oppPreGroupMap.get("pre4bet_up_group");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre4betGroup = MEDIUM;
        }

        return oppPre4betGroup;
    }

    public String getOppPostAggroness(String oppName) {
        String postAggronessGroup;

        try {
            if(opponentIdentifier2_0 == null) {
                opponentIdentifier2_0 = new OpponentIdentifier2_0(oppName);
            }

            if(opponentIdentifier2_0.getNumberOfHands() >= 5) {
                double postAggroness = opponentIdentifier2_0.getOppPostAggroness();

                if(postAggroness < 0) {
                    postAggronessGroup = MEDIUM;
                } else if(postAggroness < 0.3055555555555556) {
                    postAggronessGroup = LOW;
                } else if(postAggroness < 0.43902439024390244) {
                    postAggronessGroup = MEDIUM;
                } else {
                    postAggronessGroup = HIGH;
                }
            } else {
                postAggronessGroup = MEDIUM;
            }
        } catch (Exception e) {
            e.printStackTrace();
            postAggronessGroup = MEDIUM;
        }

        return postAggronessGroup;
    }

    public String getOppPostLooseness(String oppName) {
        String postLoosenessGroup;

        try {
            if(opponentIdentifier2_0 == null) {
                opponentIdentifier2_0 = new OpponentIdentifier2_0(oppName);
            }

            if(opponentIdentifier2_0.getNumberOfHands() >= 5) {
                double postLooseness = opponentIdentifier2_0.getOppPostLooseness();

                if(postLooseness < 0) {
                    postLoosenessGroup = MEDIUM;
                } else if(postLooseness < 0.4) {
                    postLoosenessGroup = LOW;
                } else if(postLooseness < 0.5652173913043478) {
                    postLoosenessGroup = MEDIUM;
                } else {
                    postLoosenessGroup = HIGH;
                }
            } else {
                postLoosenessGroup = MEDIUM;
            }
        } catch (Exception e) {
            e.printStackTrace();
            postLoosenessGroup = MEDIUM;
        }

        return postLoosenessGroup;
    }

    public String getBotSizingGroup(double botSizing, double oppStack, double oppTotalBetsize) {
        String botSizingGroup;

        double oppAmountToCall = botSizing - oppTotalBetsize;

        if(oppAmountToCall > oppStack) {
            oppAmountToCall = oppStack;
        }

        if(oppAmountToCall <= 60) {
            botSizingGroup = SMALL;
        } else if(oppAmountToCall <= 160) {
            botSizingGroup = MEDIUM;
        } else {
            botSizingGroup = LARGE;
        }

        return botSizingGroup;
    }

    public String getOppSizingGroup(double oppTotalBetsize, double botStack, double botTotalBetsize) {
        String oppSizingGroup;

        double amountToCall = oppTotalBetsize - botTotalBetsize;

        if(amountToCall > botStack) {
            amountToCall = botStack;
        }

        if(amountToCall <= 60) {
            oppSizingGroup = SMALL;
        } else if(amountToCall <= 160) {
            oppSizingGroup = MEDIUM;
        } else {
            oppSizingGroup = LARGE;
        }

        return oppSizingGroup;
    }

    public String getPotSizeGroup(double potSize) {
        String potSizeGroup;

        if(potSize < 90) {
            potSizeGroup = SMALL;
        } else if(potSize < 180) {
            potSizeGroup = MEDIUM;
        } else {
            potSizeGroup = LARGE;
        }

        return potSizeGroup;
    }

    public String determinBotPreflopRaiseType(double botPfRaiseSizing, double bigBlind) {
        return determineOppPreflopRaiseType(botPfRaiseSizing, bigBlind);
    }

    public String determineOppPreflopRaiseType(double oppPfRaiseSize, double bigBlind) {
        String oppPfRaiseType;

        double oppPfRaiseSizeBb = oppPfRaiseSize / bigBlind;

        if(oppPfRaiseSizeBb <= 3) {
            oppPfRaiseType = "2bet";
        } else if(oppPfRaiseSizeBb <= 10) {
            oppPfRaiseType = "3bet";
        } else {
            oppPfRaiseType = "4bet_up";
        }

        return oppPfRaiseType;
    }
}
