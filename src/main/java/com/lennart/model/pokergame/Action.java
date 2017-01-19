package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.RangeBuilder;
import org.apache.commons.lang3.StringUtils;

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
        //action = preflopActionBuilder.getAction(computerGame);
        action = preflopActionBuilder.getAction("temporary", computerGame);
    }

    private void getAndProcessPostFlopAction(ComputerGame computerGame) {
        postFlopActionBuilder = new PostFlopActionBuilder(rangeBuilder.getBoardEvaluator(),
                rangeBuilder.getHandEvaluator(), computerGame);
        Set<Set<Card>> opponentRange;
        String action;
        opponentRange = rangeBuilder.getOpponentRange(computerGame);
        action = postFlopActionBuilder.getAction(opponentRange);
    }

    private void setNewWrittenAction() {
        //Todo: fix this method

        if(StringUtils.containsIgnoreCase("...", "fold")) {
            writtenAction = "Computer folds";
        } else if(StringUtils.containsIgnoreCase("...", "check")) {
            writtenAction = "Computer checks";
        } else if(StringUtils.containsIgnoreCase("...", "call")) {
            writtenAction = "Computer calls";
        } else if(StringUtils.containsIgnoreCase("...", "1bet")) {
            writtenAction = "Computer bets";
        } else if(StringUtils.containsIgnoreCase("...", "bet")) {
            writtenAction = "Computer raises";
        }
    }

    private void setSizingIfNecessary(ComputerGame computerGame) {
        //Todo: fix this method

        if(computerGame.getFlopCards() == null) {
            if("...".contains("bet")) {
                sizing = preflopActionBuilder.getSize(computerGame);
            }
        } else {
            sizing = postFlopActionBuilder.getSize(computerGame.getPotSize());
        }
    }
}
