package com.lennart.model.action.actionbuilders.ai;

/**
 * Created by LennartMac on 18/04/18.
 */
public interface ContinuousTableable {

    boolean isOpponentHasInitiative();

    void setOpponentHasInitiative(boolean opponentHasInitiative);

    boolean isOpponentDidPreflop4betPot();

    void setOpponentDidPreflop4betPot(boolean opponentDidPreflop4betPot);

    boolean isPre3betOrPostRaisedPot();

    void setPre3betOrPostRaisedPot(boolean pre3betOrPostRaisedPot);
}
