package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.postflop.FlopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/12/2016.
 */
public class Action {
    //Future class to determine your suggestedAction

    private double sizing;
    private String writtenAction;
    private BoardEvaluator boardEvaluator;
    private RangeBuilder rangeBuilder;
    private PreflopRangeBuilder preflopRangeBuilder;
    private FlopRangeBuilder flopRangeBuilder;
    private HandEvaluator handEvaluator;
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
        preflopRangeBuilder = new PreflopRangeBuilder(computerGame.getKnownGameCards());
        preflopActionBuilder = new PreflopActionBuilder(preflopRangeBuilder);

        if(computerGame.getBoard() != null) {
            boardEvaluator = new BoardEvaluator(computerGame);
            rangeBuilder = new RangeBuilder(boardEvaluator, computerGame.getComputerHoleCards(), computerGame.getKnownGameCards());
            flopRangeBuilder = new FlopRangeBuilder(rangeBuilder, preflopRangeBuilder);
            handEvaluator = new HandEvaluator(boardEvaluator, rangeBuilder);
            postFlopActionBuilder = new PostFlopActionBuilder(boardEvaluator, handEvaluator, computerGame);
        }

        switch(computerGame.getHandPath()) {
            case "05betF1bet":
                handPathAfterAction = preflopActionBuilder.get05betF1bet(computerGame);
                newPartOfHandPath = getNewPartOfHandPath();
                computerGame.setHandPath(handPathAfterAction);
                setSizingIfNecessary(computerGame);
                setNewWrittenAction();
                break;
            case "2betFcheck":
                //nu zet je de range
                Set<Set<Card>> sortedOpponentRange = flopRangeBuilder.get2betCheck();

                //en nu ga je de actie doen, met postFlopActionBuilder...
                String action = postFlopActionBuilder.getAction(sortedOpponentRange);

                System.out.println("wacht");

            default:
                System.out.println("no action available for handpath: " + computerGame.getHandPath());
                sizing = 0;
        }
    }

    private String getNewPartOfHandPath() {
        String newPartOfHandPath = "";

        if(handPathBeforeAction.charAt(0) != handPathAfterAction.charAt(0)) {
            newPartOfHandPath = handPathAfterAction;
        } else {
            char[] oldHandPathChars = handPathBeforeAction.toCharArray();
            char[] newHandPathChars = handPathAfterAction.toCharArray();

            int indexWhereStringsStartToDiffer;

            for(int i = 0; i < oldHandPathChars.length; i++) {
                if(oldHandPathChars[i] == newHandPathChars [i]) {
                    continue;
                }
                indexWhereStringsStartToDiffer = i;
                newPartOfHandPath = handPathAfterAction.substring(indexWhereStringsStartToDiffer);
            }
        }
        return newPartOfHandPath;
    }

    private void setNewWrittenAction() {
        if(newPartOfHandPath.contains("Fold")) {
            writtenAction = "Computer folds";
        } else if(newPartOfHandPath.contains("Check")) {
            writtenAction = "Computer checks";
        } else if(newPartOfHandPath.contains("Call")) {
            writtenAction = "Computer calls";
        } else if(newPartOfHandPath.contains("1Bet")) {
            writtenAction = "Computer bets";
        } else {
            writtenAction = "Computer raises";
        }
    }

    private void setSizingIfNecessary(ComputerGame computerGame) {
        if(newPartOfHandPath.contains("bet")) {
            sizing = preflopActionBuilder.getSize(computerGame);
        }
    }

    public double getSizing() {
        return sizing;
    }

    public void setSizing(double sizing) {
        this.sizing = sizing;
    }

    public String getWrittenAction() {
        return writtenAction;
    }

    public void setWrittenAction(String writtenAction) {
        this.writtenAction = writtenAction;
    }

    public PreflopActionBuilder getPreflopActionBuilder() {
        return preflopActionBuilder;
    }

    public void setPreflopActionBuilder(PreflopActionBuilder preflopActionBuilder) {
        this.preflopActionBuilder = preflopActionBuilder;
    }

    public PostFlopActionBuilder getPostFlopActionBuilder() {
        return postFlopActionBuilder;
    }

    public void setPostFlopActionBuilder(PostFlopActionBuilder postFlopActionBuilder) {
        this.postFlopActionBuilder = postFlopActionBuilder;
    }

    public BoardEvaluator getBoardEvaluator() {
        return boardEvaluator;
    }

    public void setBoardEvaluator(BoardEvaluator boardEvaluator) {
        this.boardEvaluator = boardEvaluator;
    }

    public FlopRangeBuilder getFlopRangeBuilder() {
        return flopRangeBuilder;
    }

    public void setFlopRangeBuilder(FlopRangeBuilder flopRangeBuilder) {
        this.flopRangeBuilder = flopRangeBuilder;
    }

    public String yourAction(int handStrenght, RangeBuilder opponentRange, boolean ip) {

        //evaluate your hand against opponentRange
        //if 80%> valueBet 80% van de keren op flop en turn, en 100% op river
        //if 60% - 80% valueBet X% van de keren op flop, turn en river

        //draws
        //bet X% van de keren met je draws op de flop en turn

        //air
        //bet X% van de keren met je air, afhankelijk van board texture
        //flop
        //*!*hoe vaak komen de board kaarten voor in de range die jouw opponent jou geeft. En zijn eigen range?
        //hoe wet is de flop?
        //turn
        //*!*hoe vaak komt de turn kaart voor in de range die jouw opponent jou geeft. En in zijn eigen range?
        //zijn er meer draws gearriveerd?
        //zijn er veel draws gecomplete?
        //is er overcard bijgekomen?
        //is boat mogelijk geworden?
        //river
        //*!*hoe vaak komt de river kaart voor in de range die jouw opponent jou geeft. En in zijn eigen range?
        //hoeveel draws zijn er gearriveerd?
        //is er boat of beter mogelijk geworden?
        //is er overcard bijgekomen?
        //bepaal je showdown value



        return null;
    }
}
