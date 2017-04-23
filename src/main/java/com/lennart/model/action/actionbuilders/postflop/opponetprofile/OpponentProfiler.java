package com.lennart.model.action.actionbuilders.postflop.opponetprofile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LennartMac on 11/04/17.
 */
public class OpponentProfiler {

    private Map<Integer, Double> tightPassiveBet;
    private Map<Integer, Double> tightMediumBet;
    private Map<Integer, Double> tightAggressiveBet;
    private Map<Integer, Double> mediumPassiveBet;
    private Map<Integer, Double> mediumMediumBet;
    private Map<Integer, Double> mediumAggressiveBet;
    private Map<Integer, Double> loosePassiveBet;
    private Map<Integer, Double> looseMediumBet;
    private Map<Integer, Double> looseAggressiveBet;

    private Map<Integer, Double> tightPassiveRaise;
    private Map<Integer, Double> tightMediumRaise;
    private Map<Integer, Double> tightAggressiveRaise;
    private Map<Integer, Double> mediumPassiveRaise;
    private Map<Integer, Double> mediumMediumRaise;
    private Map<Integer, Double> mediumAggressiveRaise;
    private Map<Integer, Double> loosePassiveRaise;
    private Map<Integer, Double> looseMediumRaise;
    private Map<Integer, Double> looseAggressiveRaise;

    private Map<Integer, Double> tightPassiveCall;
    private Map<Integer, Double> tightMediumCall;
    private Map<Integer, Double> tightAggressiveCall;
    private Map<Integer, Double> mediumPassiveCall;
    private Map<Integer, Double> mediumMediumCall;
    private Map<Integer, Double> mediumAggressiveCall;
    private Map<Integer, Double> loosePassiveCall;
    private Map<Integer, Double> looseMediumCall;
    private Map<Integer, Double> looseAggressiveCall;

    private Map<Integer, Double> passiveDidBettingActionBet;
    private Map<Integer, Double> passiveDidBettingActionRaise;

    public OpponentProfiler() {
        fillMaps();
    }

    private void fillMaps() {
        tightPassiveBet = new HashMap<>();
        tightMediumBet = new HashMap<>();
        tightAggressiveBet = new HashMap<>();
        mediumPassiveBet = new HashMap<>();
        mediumMediumBet = new HashMap<>();
        mediumAggressiveBet = new HashMap<>();
        loosePassiveBet = new HashMap<>();
        looseMediumBet = new HashMap<>();
        looseAggressiveBet = new HashMap<>();

        tightPassiveRaise = new HashMap<>();
        tightMediumRaise = new HashMap<>();
        tightAggressiveRaise = new HashMap<>();
        mediumPassiveRaise = new HashMap<>();
        mediumMediumRaise = new HashMap<>();
        mediumAggressiveRaise = new HashMap<>();
        loosePassiveRaise = new HashMap<>();
        looseMediumRaise = new HashMap<>();
        looseAggressiveRaise = new HashMap<>();

        tightPassiveCall = new HashMap<>();
        tightMediumCall = new HashMap<>();
        tightAggressiveCall = new HashMap<>();
        mediumPassiveCall = new HashMap<>();
        mediumMediumCall = new HashMap<>();
        mediumAggressiveCall = new HashMap<>();
        loosePassiveCall = new HashMap<>();
        looseMediumCall = new HashMap<>();
        looseAggressiveCall = new HashMap<>();

        tightPassiveBet.put(5, 0.50);
        tightPassiveBet.put(20, 0.70);
        tightPassiveBet.put(40, 0.80);
        tightPassiveBet.put(70, 0.85);
        tightPassiveBet.put(71, 0.87);

        tightMediumBet.put(5, 0.50);
        tightMediumBet.put(20, 0.70);
        tightMediumBet.put(40, 0.80);
        tightMediumBet.put(70, 0.85);
        tightMediumBet.put(71, 0.87);

        tightAggressiveBet.put(5, 0.50);
        tightAggressiveBet.put(20, 0.70);
        tightAggressiveBet.put(40, 0.80);
        tightAggressiveBet.put(70, 0.85);
        tightAggressiveBet.put(71, 0.87);

        mediumPassiveBet.put(5, 0.50);
        mediumPassiveBet.put(20, 0.60);
        mediumPassiveBet.put(40, 0.75);
        mediumPassiveBet.put(70, 0.80);
        mediumPassiveBet.put(71, 0.85);

        mediumMediumBet.put(5, 0.50);
        mediumMediumBet.put(20, 0.60);
        mediumMediumBet.put(40, 0.75);
        mediumMediumBet.put(70, 0.80);
        mediumMediumBet.put(71, 0.85);

        mediumAggressiveBet.put(5, 0.50);
        mediumAggressiveBet.put(20, 0.60);
        mediumAggressiveBet.put(40, 0.75);
        mediumAggressiveBet.put(70, 0.80);
        mediumAggressiveBet.put(71, 0.85);

        loosePassiveBet.put(5, 0.50);
        loosePassiveBet.put(20, 0.60);
        loosePassiveBet.put(40, 0.67);
        loosePassiveBet.put(70, 0.75);
        loosePassiveBet.put(71, 0.80);

        looseMediumBet.put(5, 0.50);
        looseMediumBet.put(20, 0.60);
        looseMediumBet.put(40, 0.67);
        looseMediumBet.put(70, 0.75);
        looseMediumBet.put(71, 0.80);

        looseAggressiveBet.put(5, 0.50);
        looseAggressiveBet.put(20, 0.60);
        looseAggressiveBet.put(40, 0.65);
        looseAggressiveBet.put(70, 0.75);
        looseAggressiveBet.put(71, 0.80);

        tightPassiveRaise.put(5, 0.85);
        tightPassiveRaise.put(20, 0.87);
        tightPassiveRaise.put(40, 0.90);
        tightPassiveRaise.put(70, 0.90);
        tightPassiveRaise.put(71, 0.90);

        tightMediumRaise.put(5, 0.85);
        tightMediumRaise.put(20, 0.85);
        tightMediumRaise.put(40, 0.90);
        tightMediumRaise.put(70, 0.90);
        tightMediumRaise.put(71, 0.90);

        tightAggressiveRaise.put(5, 0.85);
        tightAggressiveRaise.put(20, 0.85);
        tightAggressiveRaise.put(40, 0.90);
        tightAggressiveRaise.put(70, 0.90);
        tightAggressiveRaise.put(71, 0.90);

        mediumPassiveRaise.put(5, 0.85);
        mediumPassiveRaise.put(20, 0.87);
        mediumPassiveRaise.put(40, 0.90);
        mediumPassiveRaise.put(70, 0.90);
        mediumPassiveRaise.put(71, 0.90);

        mediumMediumRaise.put(5, 0.80);
        mediumMediumRaise.put(20, 0.84);
        mediumMediumRaise.put(40, 0.85);
        mediumMediumRaise.put(70, 0.85);
        mediumMediumRaise.put(71, 0.90);

        mediumAggressiveRaise.put(5, 0.80);
        mediumAggressiveRaise.put(20, 0.84);
        mediumAggressiveRaise.put(40, 0.85);
        mediumAggressiveRaise.put(70, 0.85);
        mediumAggressiveRaise.put(71, 0.90);

        loosePassiveRaise.put(5, 0.85);
        loosePassiveRaise.put(20, 0.87);
        loosePassiveRaise.put(40, 0.90);
        loosePassiveRaise.put(70, 0.90);
        loosePassiveRaise.put(71, 0.90);

        looseMediumRaise.put(5, 0.80);
        looseMediumRaise.put(20, 0.84);
        looseMediumRaise.put(40, 0.85);
        looseMediumRaise.put(70, 0.85);
        looseMediumRaise.put(71, 0.90);

        looseAggressiveRaise.put(5, 0.80);
        looseAggressiveRaise.put(20, 0.84);
        looseAggressiveRaise.put(40, 0.84);
        looseAggressiveRaise.put(70, 0.84);
        looseAggressiveRaise.put(71, 0.90);

        tightPassiveCall.put(5, 0.50);
        tightPassiveCall.put(20, 0.84);
        tightPassiveCall.put(40, 0.84);
        tightPassiveCall.put(70, 0.85);
        tightPassiveCall.put(71, 0.90);

        tightMediumCall.put(5, 0.50);
        tightMediumCall.put(20, 0.80);
        tightMediumCall.put(40, 0.80);
        tightMediumCall.put(70, 0.84);
        tightMediumCall.put(71, 0.85);

        tightAggressiveCall.put(5, 0.50);
        tightAggressiveCall.put(20, 0.80);
        tightAggressiveCall.put(40, 0.80);
        tightAggressiveCall.put(70, 0.84);
        tightAggressiveCall.put(71, 0.85);

        mediumPassiveCall.put(5, 0.50);
        mediumPassiveCall.put(20, 0.84);
        mediumPassiveCall.put(40, 0.84);
        mediumPassiveCall.put(70, 0.85);
        mediumPassiveCall.put(71, 0.90);

        mediumMediumCall.put(5, 0.50);
        mediumMediumCall.put(20, 0.70);
        mediumMediumCall.put(40, 0.75);
        mediumMediumCall.put(70, 0.80);
        mediumMediumCall.put(71, 0.82);

        mediumAggressiveCall.put(5, 0.50);
        mediumAggressiveCall.put(20, 0.65);
        mediumAggressiveCall.put(40, 0.70);
        mediumAggressiveCall.put(70, 0.80);
        mediumAggressiveCall.put(71, 0.82);

        loosePassiveCall.put(5, 0.50);
        loosePassiveCall.put(20, 0.84);
        loosePassiveCall.put(40, 0.84);
        loosePassiveCall.put(70, 0.85);
        loosePassiveCall.put(71, 0.90);

        looseMediumCall.put(5, 0.50);
        looseMediumCall.put(20, 0.60);
        looseMediumCall.put(40, 0.70);
        looseMediumCall.put(70, 0.80);
        looseMediumCall.put(71, 0.84);

        looseAggressiveCall.put(5, 0.50);
        looseAggressiveCall.put(20, 0.60);
        looseAggressiveCall.put(40, 0.65);
        looseAggressiveCall.put(70, 0.70);
        looseAggressiveCall.put(71, 0.80);

        passiveDidBettingActionBet.put(5, 0.85);
        passiveDidBettingActionBet.put(20, 0.87);
        passiveDidBettingActionBet.put(40, 0.90);
        passiveDidBettingActionBet.put(70, 0.90);
        passiveDidBettingActionBet.put(71, 0.91);

        passiveDidBettingActionRaise.put(5, 0.85);
        passiveDidBettingActionRaise.put(20, 0.87);
        passiveDidBettingActionRaise.put(40, 0.90);
        passiveDidBettingActionRaise.put(70, 0.90);
        passiveDidBettingActionRaise.put(71, 0.91);
    }

    public Map<Integer, Double> getTightPassiveBet() {
        return tightPassiveBet;
    }

    public Map<Integer, Double> getTightMediumBet() {
        return tightMediumBet;
    }

    public Map<Integer, Double> getTightAggressiveBet() {
        return tightAggressiveBet;
    }

    public Map<Integer, Double> getMediumPassiveBet() {
        return mediumPassiveBet;
    }

    public Map<Integer, Double> getMediumMediumBet() {
        return mediumMediumBet;
    }

    public Map<Integer, Double> getMediumAggressiveBet() {
        return mediumAggressiveBet;
    }

    public Map<Integer, Double> getLoosePassiveBet() {
        return loosePassiveBet;
    }

    public Map<Integer, Double> getLooseMediumBet() {
        return looseMediumBet;
    }

    public Map<Integer, Double> getLooseAggressiveBet() {
        return looseAggressiveBet;
    }

    public Map<Integer, Double> getTightPassiveRaise() {
        return tightPassiveRaise;
    }

    public Map<Integer, Double> getTightMediumRaise() {
        return tightMediumRaise;
    }

    public Map<Integer, Double> getTightAggressiveRaise() {
        return tightAggressiveRaise;
    }

    public Map<Integer, Double> getMediumPassiveRaise() {
        return mediumPassiveRaise;
    }

    public Map<Integer, Double> getMediumMediumRaise() {
        return mediumMediumRaise;
    }

    public Map<Integer, Double> getMediumAggressiveRaise() {
        return mediumAggressiveRaise;
    }

    public Map<Integer, Double> getLoosePassiveRaise() {
        return loosePassiveRaise;
    }

    public Map<Integer, Double> getLooseMediumRaise() {
        return looseMediumRaise;
    }

    public Map<Integer, Double> getLooseAggressiveRaise() {
        return looseAggressiveRaise;
    }

    public Map<Integer, Double> getTightPassiveCall() {
        return tightPassiveCall;
    }

    public Map<Integer, Double> getTightMediumCall() {
        return tightMediumCall;
    }

    public Map<Integer, Double> getTightAggressiveCall() {
        return tightAggressiveCall;
    }

    public Map<Integer, Double> getMediumPassiveCall() {
        return mediumPassiveCall;
    }

    public Map<Integer, Double> getMediumMediumCall() {
        return mediumMediumCall;
    }

    public Map<Integer, Double> getMediumAggressiveCall() {
        return mediumAggressiveCall;
    }

    public Map<Integer, Double> getLoosePassiveCall() {
        return loosePassiveCall;
    }

    public Map<Integer, Double> getLooseMediumCall() {
        return looseMediumCall;
    }

    public Map<Integer, Double> getLooseAggressiveCall() {
        return looseAggressiveCall;
    }

    public Map<Integer, Double> getPassiveDidBettingActionBet() {
        return passiveDidBettingActionBet;
    }

    public Map<Integer, Double> getPassiveDidBettingActionRaise() {
        return passiveDidBettingActionRaise;
    }
}
