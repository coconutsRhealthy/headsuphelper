package com.lennart.model.action;

import com.lennart.model.action.actionbuilders.PostFlopActionBuilder;
import com.lennart.model.action.actionbuilders.PreflopActionBuilder;
import com.lennart.model.botgame.BotHand;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.List;

/**
 * Created by LPO10346 on 10/12/2016.
 */
public class Action {
    private double sizing;
    private String writtenAction;
    private RangeBuilder rangeBuilder;
    private PreflopActionBuilder preflopActionBuilder;
    private PostFlopActionBuilder postFlopActionBuilder;

    public Action() {
        //default constructor
    }

    public Action(Actionable actionable, RangeBuilder rangeBuilder) {
        this.rangeBuilder = rangeBuilder;

        if(actionable.getBoard() == null) {
            getAndProcessPreflopAction(actionable);
        } else {
            getAndProcessPostFlopAction(actionable);
        }
    }

    public double getSizing() {
        return sizing;
    }

    public String getWrittenAction() {
        return writtenAction;
    }

    //helper methods
    private void getAndProcessPreflopAction(Actionable actionable) {
        preflopActionBuilder = new PreflopActionBuilder(rangeBuilder);
        String action = preflopActionBuilder.getAction(actionable);
        setSizingIfNecessary(actionable, action);
        setNewWrittenAction(action, actionable);
    }

    private void getAndProcessPostFlopAction(Actionable actionable) {
        postFlopActionBuilder = new PostFlopActionBuilder(rangeBuilder.getBoardEvaluator(),
                rangeBuilder.getHandEvaluator(), actionable);
        String action = postFlopActionBuilder.getAction(actionable.getOpponentRange());
        setSizingIfNecessary(actionable, action);
        setNewWrittenAction(action, actionable);
    }

    private void setNewWrittenAction(String action, Actionable actionable) {
        if(actionable instanceof BotHand) {
            if(sizing == 0) {
                writtenAction = action;
            } else {
                writtenAction = action + " " + sizing;
            }
        } else {
            List<Card> board = actionable.getBoard();
            if(board == null) {
                getWrittenActionFromAction(action, "Preflop");
            } else if(board.size() == 3) {
                getWrittenActionFromAction(action, "Flop");
            } else if(board.size() == 4) {
                getWrittenActionFromAction(action, "Turn");
            } else if(board.size() == 5) {
                getWrittenActionFromAction(action, "River");
            }
        }
    }

    private void getWrittenActionFromAction(String action, String street) {
        if(action.equals("fold")) {
            writtenAction = street + ": Computer folds";
        } else if(action.equals("check")) {
            writtenAction = street + ": Computer checks";
        } else if(action.equals("call")) {
            writtenAction = street + ": Computer calls";
        } else if(action.equals("bet")) {
            writtenAction = street + ": Computer bets";
        } else if(action.equals("raise")) {
            writtenAction = street + ": Computer raises";
        }
    }

    private void setSizingIfNecessary(Actionable actionable, String myAction) {
        if(actionable.getFlopCards() == null) {
            if(myAction.equals("raise")) {
                sizing = preflopActionBuilder.getSize(actionable);
            }
        } else {
            if(myAction.equals("bet") || myAction.equals("raise")) {
                sizing = postFlopActionBuilder.getSizing();
            }
        }
    }

    public RangeBuilder getRangeBuilder() {
        return rangeBuilder;
    }
}
