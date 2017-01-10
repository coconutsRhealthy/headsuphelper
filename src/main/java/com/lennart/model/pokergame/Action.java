package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.RangeBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Created by LPO10346 on 10/12/2016.
 */
public class Action {
    //Future class to determine your suggestedAction

    private double sizing;
    private String writtenAction;
    private RangeBuilder rangeBuilder;
    private PreflopActionBuilder preflopActionBuilder;
    private PostFlopActionBuilder postFlopActionBuilder;

    private String handPathBeforeAction;
    private String handPathAfterAction;
    private String newPartOfHandPath;


    public Action() {
        //default constructor
    }

    public Action(ComputerGame computerGame) {
        handPathBeforeAction = computerGame.getHandPath();
        rangeBuilder = new RangeBuilder(computerGame.getComputerHoleCards(), computerGame.getBoard(),
                computerGame.getKnownGameCards());
        preflopActionBuilder = new PreflopActionBuilder(rangeBuilder);

        if(computerGame.getBoard() != null) {
            postFlopActionBuilder = new PostFlopActionBuilder(rangeBuilder.getBoardEvaluator(),
                    rangeBuilder.getHandEvaluator(), computerGame);
        }

        Set<Set<Card>> opponentRange;
        String action;
        switch(computerGame.getHandPath()) {
            case "05betF1bet":
                action = preflopActionBuilder.get05betF1bet(computerGame);
                handPathAfterAction = action;
                processHandPath(computerGame);
                break;
            case "1betF2bet":
                action = preflopActionBuilder.get1betF2bet(computerGame);
                handPathAfterAction = action;
                processHandPath(computerGame);
                break;
            case "2betF3bet":
                action = preflopActionBuilder.get2betF3bet(computerGame);
                handPathAfterAction = action;
                processHandPath(computerGame);
                break;
            case "2betFcheck":
                opponentRange = rangeBuilder.getOpponentRange(computerGame.getAllHandPathsOfHand());
                action = postFlopActionBuilder.getAction(opponentRange);
                handPathAfterAction = includePostFlopActionInHandPath(computerGame.getHandPath(), action);
                processHandPath(computerGame);
                break;
            case "Call2bet":
                opponentRange = rangeBuilder.getOpponentRange(computerGame.getAllHandPathsOfHand());
                action = postFlopActionBuilder.getAction(opponentRange);
                handPathAfterAction = includePostFlopActionInHandPath(computerGame.getHandPath(), action);
                processHandPath(computerGame);
                break;

            default:
                System.out.println("no action available for handpath: " + computerGame.getHandPath());
                sizing = 0;
        }
    }

    public double getSizing() {
        return sizing;
    }

    public String getWrittenAction() {
        return writtenAction;
    }

    //helper methods
    private void processHandPath(ComputerGame computerGame) {
        newPartOfHandPath = getNewPartOfHandPath();
        computerGame.setHandPath(handPathAfterAction);
        setSizingIfNecessary(computerGame);
        setNewWrittenAction();
    }

    private String getNewPartOfHandPath() {
        String newPartOfHandPath = "";

        if(handPathBeforeAction.charAt(0) != handPathAfterAction.charAt(0)) {
            newPartOfHandPath = handPathAfterAction;
        } else {
            char[] oldHandPathChars = handPathBeforeAction.toCharArray();
            char[] newHandPathChars = handPathAfterAction.toCharArray();

            int indexWhereStringsStartToDiffer;

            for(int i = 0; i < newHandPathChars.length; i++) {
                if(i < oldHandPathChars.length) {
                    if(oldHandPathChars[i] == newHandPathChars [i]) {
                        continue;
                    }
                }
                indexWhereStringsStartToDiffer = i;
                newPartOfHandPath = handPathAfterAction.substring(indexWhereStringsStartToDiffer);
                break;
            }
        }
        return newPartOfHandPath;
    }

    private void setNewWrittenAction() {
        if(StringUtils.containsIgnoreCase(newPartOfHandPath, "fold")) {
            writtenAction = "Computer folds";
        } else if(StringUtils.containsIgnoreCase(newPartOfHandPath, "check")) {
            writtenAction = "Computer checks";
        } else if(StringUtils.containsIgnoreCase(newPartOfHandPath, "call")) {
            writtenAction = "Computer calls";
        } else if(StringUtils.containsIgnoreCase(newPartOfHandPath, "1bet")) {
            writtenAction = "Computer bets";
        } else if(StringUtils.containsIgnoreCase(newPartOfHandPath, "bet")) {
            writtenAction = "Computer raises";
        }
    }

    private void setSizingIfNecessary(ComputerGame computerGame) {
        if(computerGame.getFlopCards() == null) {
            if(newPartOfHandPath.contains("bet")) {
                sizing = preflopActionBuilder.getSize(computerGame);
            }
        } else {
            sizing = postFlopActionBuilder.getSize(computerGame.getPotSize());
        }
    }

    private String includePostFlopActionInHandPath(String handPath, String postFlopAction) {
        handPath = removeFacingPartFromHandPathIfPresent(handPath);

        if(postFlopAction.equals("check")) {
            handPath = handPath + "Check";
        } else if(postFlopAction.equals("1bet")) {
            handPath = handPath + "1bet";
        } else if(postFlopAction.equals("2bet")) {
            handPath = handPath + "2bet";
        } else if(postFlopAction.equals("call1bet")) {
            handPath = handPath + "Call1bet";
        } else if(postFlopAction.equals("call2bet")) {
            handPath = handPath + "Call2bet";
        } else if(postFlopAction.equals("call3bet")) {
            handPath = handPath + "Call3bet";
        }
        return handPath;
    }

    private String removeFacingPartFromHandPathIfPresent(String handPath) {
        if(handPath.contains("F")) {
            return handPath.substring(0, handPath.lastIndexOf("F"));
        }
        return handPath;
    }
}
