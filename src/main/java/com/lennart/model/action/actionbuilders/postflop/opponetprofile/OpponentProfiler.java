package com.lennart.model.action.actionbuilders.postflop.opponetprofile;

import com.lennart.model.card.Card;

import java.util.HashMap;
import java.util.List;
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

    private List<Card> board;

    public OpponentProfiler(List<Card> board) {
        this.board = board;
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

        passiveDidBettingActionBet = new HashMap<>();
        passiveDidBettingActionRaise = new HashMap<>();

        tightPassiveBet.put(5, get50PercentScore());
        tightPassiveBet.put(20, getMpScore());
        tightPassiveBet.put(40, getTpScore());
        tightPassiveBet.put(70, getTpTkScore());
        tightPassiveBet.put(71, getOpScore());

        tightMediumBet.put(5, get50PercentScore());
        tightMediumBet.put(20, getMpScore());
        tightMediumBet.put(40, getTpScore());
        tightMediumBet.put(70, getTpTkScore());
        tightMediumBet.put(71, getOpScore());

        tightAggressiveBet.put(5, get50PercentScore());
        tightAggressiveBet.put(20, getMpScore());
        tightAggressiveBet.put(40, getTpScore());
        tightAggressiveBet.put(70, getTpTkScore());
        tightAggressiveBet.put(71, getOpScore());

        mediumPassiveBet.put(5, get50PercentScore());
        mediumPassiveBet.put(20, getBpScore());
        mediumPassiveBet.put(40, getMpGkScore());
        mediumPassiveBet.put(70, getTpScore());
        mediumPassiveBet.put(71, getTpTkScore());

        mediumMediumBet.put(5, get50PercentScore());
        mediumMediumBet.put(20, getBpScore());
        mediumMediumBet.put(40, getMpGkScore());
        mediumMediumBet.put(70, getTpScore());
        mediumMediumBet.put(71, getTpTkScore());

        mediumAggressiveBet.put(5, get50PercentScore());
        mediumAggressiveBet.put(20, getBpScore());
        mediumAggressiveBet.put(40, getMpGkScore());
        mediumAggressiveBet.put(70, getTpScore());
        mediumAggressiveBet.put(71, getTpTkScore());

        loosePassiveBet.put(5, get50PercentScore());
        loosePassiveBet.put(20, getBpScore());
        loosePassiveBet.put(40, getBpTkScore());
        loosePassiveBet.put(70, getMpGkScore());
        loosePassiveBet.put(71, getTpScore());

        looseMediumBet.put(5, get50PercentScore());
        looseMediumBet.put(20, getBpScore());
        looseMediumBet.put(40, getBpTkScore());
        looseMediumBet.put(70, getMpGkScore());
        looseMediumBet.put(71, getTpScore());

        looseAggressiveBet.put(5, get50PercentScore());
        looseAggressiveBet.put(20, getBpScore());
        looseAggressiveBet.put(40, getBpGkScore());
        looseAggressiveBet.put(70, getMpGkScore());
        looseAggressiveBet.put(71, getTpScore());

        tightPassiveRaise.put(5, getTpTkScore());
        tightPassiveRaise.put(20, getOpScore());
        tightPassiveRaise.put(40, get2pairScore());
        tightPassiveRaise.put(70, get2pairScore());
        tightPassiveRaise.put(71, get2pairScore());

        tightMediumRaise.put(5, getTpTkScore());
        tightMediumRaise.put(20, getTpTkScore());
        tightMediumRaise.put(40, get2pairScore());
        tightMediumRaise.put(70, get2pairScore());
        tightMediumRaise.put(71, get2pairScore());

        tightAggressiveRaise.put(5, getTpTkScore());
        tightAggressiveRaise.put(20, getTpTkScore());
        tightAggressiveRaise.put(40, get2pairScore());
        tightAggressiveRaise.put(70, get2pairScore());
        tightAggressiveRaise.put(71, get2pairScore());

        mediumPassiveRaise.put(5, getTpTkScore());
        mediumPassiveRaise.put(20, getOpScore());
        mediumPassiveRaise.put(40, get2pairScore());
        mediumPassiveRaise.put(70, get2pairScore());
        mediumPassiveRaise.put(71, get2pairScore());

        mediumMediumRaise.put(5, getTpScore());
        mediumMediumRaise.put(20, getTpGkScore());
        mediumMediumRaise.put(40, getTpTkScore());
        mediumMediumRaise.put(70, getTpTkScore());
        mediumMediumRaise.put(71, get2pairScore());

        mediumAggressiveRaise.put(5, getTpScore());
        mediumAggressiveRaise.put(20, getTpGkScore());
        mediumAggressiveRaise.put(40, getTpTkScore());
        mediumAggressiveRaise.put(70, getTpTkScore());
        mediumAggressiveRaise.put(71, get2pairScore());

        loosePassiveRaise.put(5, getTpTkScore());
        loosePassiveRaise.put(20, getOpScore());
        loosePassiveRaise.put(40, get2pairScore());
        loosePassiveRaise.put(70, get2pairScore());
        loosePassiveRaise.put(71, get2pairScore());

        looseMediumRaise.put(5, getTpScore());
        looseMediumRaise.put(20, getTpGkScore());
        looseMediumRaise.put(40, getTpTkScore());
        looseMediumRaise.put(70, getTpTkScore());
        looseMediumRaise.put(71, get2pairScore());

        looseAggressiveRaise.put(5, getTpScore());
        looseAggressiveRaise.put(20, getTpGkScore());
        looseAggressiveRaise.put(40, getTpGkScore());
        looseAggressiveRaise.put(70, getTpGkScore());
        looseAggressiveRaise.put(71, get2pairScore());

        tightPassiveCall.put(5, get50PercentScore());
        tightPassiveCall.put(20, getTpGkScore());
        tightPassiveCall.put(40, getTpGkScore());
        tightPassiveCall.put(70, getTpTkScore());
        tightPassiveCall.put(71, get2pairScore());

        tightMediumCall.put(5, get50PercentScore());
        tightMediumCall.put(20, getTpScore());
        tightMediumCall.put(40, getTpScore());
        tightMediumCall.put(70, getTpGkScore());
        tightMediumCall.put(71, getTpTkScore());

        tightAggressiveCall.put(5, get50PercentScore());
        tightAggressiveCall.put(20, getTpScore());
        tightAggressiveCall.put(40, getTpScore());
        tightAggressiveCall.put(70, getTpGkScore());
        tightAggressiveCall.put(71, getTpTkScore());

        mediumPassiveCall.put(5, get50PercentScore());
        mediumPassiveCall.put(20, getTpGkScore());
        mediumPassiveCall.put(40, getTpGkScore());
        mediumPassiveCall.put(70, getTpTkScore());
        mediumPassiveCall.put(71, get2pairScore());

        mediumMediumCall.put(5, get50PercentScore());
        mediumMediumCall.put(20, getMpScore());
        mediumMediumCall.put(40, getMpGkScore());
        mediumMediumCall.put(70, getTpScore());
        mediumMediumCall.put(71, getTpMkScore());

        mediumAggressiveCall.put(5, get50PercentScore());
        mediumAggressiveCall.put(20, getBpGkScore());
        mediumAggressiveCall.put(40, getMpScore());
        mediumAggressiveCall.put(70, getTpScore());
        mediumAggressiveCall.put(71, getTpMkScore());

        loosePassiveCall.put(5, get50PercentScore());
        loosePassiveCall.put(20, getTpGkScore());
        loosePassiveCall.put(40, getTpGkScore());
        loosePassiveCall.put(70, getTpTkScore());
        loosePassiveCall.put(71, get2pairScore());

        looseMediumCall.put(5, get50PercentScore());
        looseMediumCall.put(20, getBpScore());
        looseMediumCall.put(40, getMpScore());
        looseMediumCall.put(70, getTpScore());
        looseMediumCall.put(71, getTpGkScore());

        looseAggressiveCall.put(5, get50PercentScore());
        looseAggressiveCall.put(20, getBpScore());
        looseAggressiveCall.put(40, getBpGkScore());
        looseAggressiveCall.put(70, getMpScore());
        looseAggressiveCall.put(71, getTpScore());

        passiveDidBettingActionBet.put(5, getTpTkScore());
        passiveDidBettingActionBet.put(20, getOpScore());
        passiveDidBettingActionBet.put(40, get2pairScore());
        passiveDidBettingActionBet.put(70, get2pairScore());
        passiveDidBettingActionBet.put(71, get2pairScore());

        passiveDidBettingActionRaise.put(5, getTpTkScore());
        passiveDidBettingActionRaise.put(20, getOpScore());
        passiveDidBettingActionRaise.put(40, get2pairScore());
        passiveDidBettingActionRaise.put(70, get2pairScore());
        passiveDidBettingActionRaise.put(71, get2pairScore());
    }

    private double get50PercentScore() {
        return 0.50;
    }

    private double getTpTkScore() {
        double tpTkScore = 1;
        if(board.size() == 3) {
            tpTkScore = 0.93;
        } else if(board.size() == 4) {
            tpTkScore = 0.90;
        } else if(board.size() == 5) {
            tpTkScore = 0.85;
        }
        return tpTkScore;
    }

    private double getTpMkScore() {
        double tpMkScore = 1;
        if(board.size() == 3) {
            tpMkScore = 0.89;
        } else if(board.size() == 4) {
            tpMkScore = 0.86;
        } else if(board.size() == 5) {
            tpMkScore = 0.82;
        }
        return tpMkScore;
    }

    private double getTpScore() {
        double tpScore = 1;
        if(board.size() == 3) {
            tpScore = 0.84;
        } else if(board.size() == 4) {
            tpScore = 0.82;
        } else if(board.size() == 5) {
            tpScore = 0.80;
        }
        return tpScore;
    }

    private double getMpScore() {
        double mpScore = 1;
        if(board.size() == 3) {
            mpScore = 0.73;
        } else if(board.size() == 4) {
            mpScore = 0.72;
        } else if(board.size() == 5) {
            mpScore = 0.70;
        }
        return mpScore;
    }

    private double getBpScore() {
        double bpScore = 1;
        if(board.size() == 3) {
            bpScore = 0.63;
        } else if(board.size() == 4) {
            bpScore = 0.62;
        } else if(board.size() == 5) {
            bpScore = 0.60;
        }
        return bpScore;
    }

    private double getOpScore() {
        double opScore = 1;
        if(board.size() == 3) {
            opScore = 0.94;
        } else if(board.size() == 4) {
            opScore = 0.91;
        } else if(board.size() == 5) {
            opScore = 0.87;
        }
        return opScore;
    }

    private double getTpGkScore() {
        double tpGkScore = 1;
        if(board.size() == 3) {
            tpGkScore = 0.91;
        } else if(board.size() == 4) {
            tpGkScore = 0.88;
        } else if(board.size() == 5) {
            tpGkScore = 0.84;
        }
        return tpGkScore;
    }

    private double getMpGkScore() {
        double mpGkScore = 1;
        if(board.size() == 3) {
            mpGkScore = 0.80;
        } else if(board.size() == 4) {
            mpGkScore = 0.78;
        } else if(board.size() == 5) {
            mpGkScore = 0.75;
        }
        return mpGkScore;
    }

    private double getBpTkScore() {
        double bpTkScore = 1;
        if(board.size() == 3) {
            bpTkScore = 0.72;
        } else if(board.size() == 4) {
            bpTkScore = 0.70;
        } else if(board.size() == 5) {
            bpTkScore = 0.67;
        }
        return bpTkScore;
    }

    private double getBpGkScore() {
        double bpGkScore = 1;
        if(board.size() == 3) {
            bpGkScore = 0.70;
        } else if(board.size() == 4) {
            bpGkScore = 0.68;
        } else if(board.size() == 5) {
            bpGkScore = 0.65;
        }
        return bpGkScore;
    }

    private double getBpMkScore() {
        double bpMkScore = 1;
        if(board.size() == 3) {
            bpMkScore = 0.68;
        } else if(board.size() == 4) {
            bpMkScore = 0.66;
        } else if(board.size() == 5) {
            bpMkScore = 0.62;
        }
        return bpMkScore;
    }

    private double get2pairScore() {
        double _2pairScore = 1;
        if(board.size() == 3) {
            _2pairScore = 0.95;
        } else if(board.size() == 4) {
            _2pairScore = 0.92;
        } else if(board.size() == 5) {
            _2pairScore = 0.90;
        }
        return _2pairScore;
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
