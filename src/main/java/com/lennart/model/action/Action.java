package com.lennart.model.action;

import com.lennart.model.action.actionbuilders.PostFlopActionBuilder;
import com.lennart.model.action.actionbuilders.PreflopActionBuilder;
import com.lennart.model.botgame.BotHand;
import com.lennart.model.card.Card;
import com.lennart.model.computergame.ComputerGame;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.List;
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

    public Action(ComputerGame computerGame, RangeBuilder rangeBuilder) {
        this.rangeBuilder = rangeBuilder;

        if(computerGame.getBoard() == null) {
            getAndProcessPreflopAction(computerGame);
        } else {
            getAndProcessPostFlopAction(computerGame);
        }
    }

    public Action(BotHand botHand, RangeBuilder rangeBuilder) {
        this.rangeBuilder = rangeBuilder;



    }

    public double getSizing() {
        return sizing;
    }

    public String getWrittenAction() {
        return writtenAction;
    }

    //helper methods
    private void getAndProcessPreflopAction(ComputerGame computerGame) {
        preflopActionBuilder = new PreflopActionBuilder(rangeBuilder);
        rangeBuilder.getOpponentRange(computerGame);
        String action;
        action = preflopActionBuilder.getAction(computerGame);
        setNewWrittenAction(action, computerGame.getBoard());
        setSizingIfNecessary(computerGame, action);
    }

    private void getAndProcessPostFlopAction(ComputerGame computerGame) {
        postFlopActionBuilder = new PostFlopActionBuilder(rangeBuilder.getBoardEvaluator(),
                rangeBuilder.getHandEvaluator(), computerGame);
        Set<Set<Card>> opponentRange;
        String action;
        opponentRange = rangeBuilder.getOpponentRange(computerGame);

        if(!computerGame.isOnlyCallRangeNeeded()) {
            action = postFlopActionBuilder.getAction(opponentRange);
            setNewWrittenAction(action, computerGame.getBoard());
            setSizingIfNecessary(computerGame, action);
        }
    }

    private void setNewWrittenAction(String action, List<Card> board) {
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
