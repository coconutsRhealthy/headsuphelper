package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.Set;

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

    public Action(ComputerGame computerGame) {
        rangeBuilder = new RangeBuilder(computerGame);
        preflopActionBuilder = new PreflopActionBuilder(rangeBuilder);

        if(computerGame.getBoard() == null) {
            getAndProcessPreflopAction(computerGame);
        } else {
            getAndProcessPostFlopAction(computerGame);
        }
    }

    public double getSizing() {
        return sizing;
    }

    public String getWrittenAction() {
        return writtenAction;
    }

    //helper methods
    private void getAndProcessPreflopAction(ComputerGame computerGame) {
        String action;
        action = preflopActionBuilder.getAction(computerGame);
        setNewWrittenAction(action);
        setSizingIfNecessary(computerGame, action);
    }

    private void getAndProcessPostFlopAction(ComputerGame computerGame) {
        postFlopActionBuilder = new PostFlopActionBuilder(rangeBuilder.getBoardEvaluator(),
                rangeBuilder.getHandEvaluator(), computerGame);
        Set<Set<Card>> opponentRange;
        String action;
        opponentRange = rangeBuilder.getOpponentRange(computerGame);
        action = postFlopActionBuilder.getAction(opponentRange);
        setNewWrittenAction(action);
        setSizingIfNecessary(computerGame, action);

    }

    private void setNewWrittenAction(String action) {
        if(action.equals("fold")) {
            writtenAction = "Computer folds";
        } else if(action.equals("check")) {
            writtenAction = "Computer checks";
        } else if(action.equals("call")) {
            writtenAction = "Computer calls";
        } else if(action.equals("bet")) {
            writtenAction = "Computer bets";
        } else if(action.equals("raise")) {
            writtenAction = "Computer raises";
        }
    }

    private void setSizingIfNecessary(ComputerGame computerGame, String myAction) {
        if(computerGame.getFlopCards() == null) {
            if(myAction.equals("raise")) {
                sizing = preflopActionBuilder.getSize(computerGame);
            }
        } else {
            if(myAction.equals("bet") || myAction.equals("raise")) {
                sizing = postFlopActionBuilder.getSize();
            }
        }
    }

    public RangeBuilder getRangeBuilder() {
        return rangeBuilder;
    }
}
