package com.lennart.model.handtracker;

import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class ActionRequest {

    private double topTotalPotSize;
    private List<Card> board;
    private List<PlayerActionRound> actionsSinceLastRequest;



    public static void main(String[] args) {
        List<Card> board = new ArrayList<>();
//        board.add(new Card(6, 'd'));
//        board.add(new Card(7, 's'));
//        board.add(new Card(12, 'c'));

        List<ActionRequest> allRequests = new ArrayList<>();

        ActionRequest actionRequest1 = new ActionRequest(allRequests, 0.75, board, true, 0.5);

        double totalOpponentBetSize = actionRequest1.getMostRecentActionRoundOfPLayer(actionRequest1.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        PlayerActionRound botActionRound = new PlayerActionRound("bot", board, 1.25, totalOpponentBetSize, "preflop", "raise");
        actionRequest1.getActionsSinceLastRequest().add(botActionRound);

        allRequests.add(actionRequest1);

        ActionRequest actionRequest2 = new ActionRequest(allRequests, 5.25, board, true, 0.5);

        double totalOpponentBetSize2 = actionRequest2.getMostRecentActionRoundOfPLayer(actionRequest2.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        PlayerActionRound botActionRound2 = new PlayerActionRound("bot", board, 1.25, totalOpponentBetSize2, "preflop", "call");
        actionRequest2.getActionsSinceLastRequest().add(botActionRound2);

        allRequests.add(actionRequest2);

        List<Card> board2 = new ArrayList<>();
        board2.add(new Card(6, 'd'));
        board2.add(new Card(7, 's'));
        board2.add(new Card(12, 'c'));

        ActionRequest actionRequest3 = new ActionRequest(allRequests, 8, board2, true, 0.5);

        double totalOpponentBetSize3 = actionRequest3.getMostRecentActionRoundOfPLayer(actionRequest3.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        PlayerActionRound botActionRound3 = new PlayerActionRound("bot", board2, 5, totalOpponentBetSize3, "flop", "bet75pct");
        actionRequest3.getActionsSinceLastRequest().add(botActionRound3);

        allRequests.add(actionRequest3);

        List<Card> board3 = new ArrayList<>();
        board3.add(new Card(6, 'd'));
        board3.add(new Card(7, 's'));
        board3.add(new Card(12, 'c'));
        board3.add(new Card(13, 'd'));

        ActionRequest actionRequest4 = new ActionRequest(allRequests, 21, board3, true, 0.5);

        double totalOpponentBetSize4 = actionRequest4.getMostRecentActionRoundOfPLayer(actionRequest4.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        PlayerActionRound botActionRound4 = new PlayerActionRound("bot", board3, 16, totalOpponentBetSize4, "turn", "raise");
        actionRequest4.getActionsSinceLastRequest().add(botActionRound4);

        allRequests.add(actionRequest4);

        ActionRequest actionRequest5 = new ActionRequest(allRequests, 54, board3, true, 0.5);

        double totalOpponentBetSize5 = actionRequest5.getMostRecentActionRoundOfPLayer(actionRequest5.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        PlayerActionRound botActionRound5 = new PlayerActionRound("bot", board3, 16, totalOpponentBetSize5, "turn", "call");
        actionRequest5.getActionsSinceLastRequest().add(botActionRound5);

        allRequests.add(actionRequest5);

        List<Card> board4 = new ArrayList<>();
        board4.add(new Card(6, 'd'));
        board4.add(new Card(7, 's'));
        board4.add(new Card(12, 'c'));
        board4.add(new Card(13, 'd'));
        board4.add(new Card(2, 'd'));

        ActionRequest actionRequest6 = new ActionRequest(allRequests, 79.86, board4, true, 0.5);

        System.out.println("wacht");


    }



    public ActionRequest(List<ActionRequest> allActionRequestsOfHand, double topTotalPotSize, List<Card> board, boolean position, double bigBlind) {
        this.topTotalPotSize = topTotalPotSize;
        this.board = board;
        actionsSinceLastRequest = deriveActionsSinceLastRequest(allActionRequestsOfHand, position, bigBlind);
    }

    private List<PlayerActionRound> deriveActionsSinceLastRequest(List<ActionRequest> allActionRequestsOfHand, boolean position, double bigBlind) {
        List<PlayerActionRound> actionsSinceLastRequest = new ArrayList<>();

        if(allActionRequestsOfHand.isEmpty()) {
            if(position) {
                PlayerActionRound playerActionRound1 = new PlayerActionRound("bot", board, bigBlind / 2, bigBlind, "preflop", "postSB");
                PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, bigBlind / 2, bigBlind, "preflop", "bet");
                actionsSinceLastRequest.add(playerActionRound1);
                actionsSinceLastRequest.add(playerActionRound2);
            } else {
                PlayerActionRound playerActionRound1 = new PlayerActionRound("opponent", board, bigBlind, bigBlind / 2, "preflop", "postSB");
                PlayerActionRound playerActionRound2 = new PlayerActionRound("bot", board, bigBlind, bigBlind / 2, "preflop", "bet");
                actionsSinceLastRequest.add(playerActionRound1);
                actionsSinceLastRequest.add(playerActionRound2);

                List<Card> board = new ArrayList<>();

                if(topTotalPotSize == (2 * bigBlind)) {
                    PlayerActionRound playerActionRound3 = new PlayerActionRound("opponent", board, bigBlind, bigBlind, "preflop", "call");
                    actionsSinceLastRequest.add(playerActionRound3);
                } else {
                    double totalOpponentBetSize = topTotalPotSize - bigBlind;

                    PlayerActionRound playerActionRound3 = new PlayerActionRound("opponent", board, bigBlind, totalOpponentBetSize, "preflop", "raise");
                    actionsSinceLastRequest.add(playerActionRound3);
                }
            }
        } else {
            ActionRequest previousActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 1);
            List<PlayerActionRound> allActionsOfPreviousActionRequest = previousActionRequest.getActionsSinceLastRequest();
            PlayerActionRound botLastActionRound = getMostRecentActionRoundOfPLayer(allActionsOfPreviousActionRequest, "bot");

            List<Card> boardAtLastActionRequest = previousActionRequest.getBoard();
            String botLastAction = botLastActionRound.getAction();

            if(board.isEmpty() && allActionRequestsOfHand.size() == 1) {
                System.out.println("yoyo wachten!");
            }

            if(board.equals(boardAtLastActionRequest)) {
                if(botLastAction.equals("check")) {
                    double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                    double totalOpponentBetSize = topTotalPotSize - previousTotalPotSize;

                    PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0.0, totalOpponentBetSize, "thecorrectstreet", "bet75pct");
                    actionsSinceLastRequest.add(playerActionRound);
                } else if(botLastAction.equals("bet75pct")) {
                    double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                    double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                    double totalOpponentBetSize = topTotalPotSize - previousTotalPotSize;

                    PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, previousTotalBotBetSize, totalOpponentBetSize, "thecorrectstreet", "raise");
                    actionsSinceLastRequest.add(playerActionRound);
                } else if(botLastAction.equals("raise")) {
                    double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                    double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                    double previousTotalOpponentBetSize = botLastActionRound.getTotalOpponentBetSize();
                    double totalOpponentBetSize = topTotalPotSize - previousTotalPotSize - previousTotalOpponentBetSize;

                    PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, previousTotalBotBetSize, totalOpponentBetSize, "thecorrectstreet", "raise");
                    actionsSinceLastRequest.add(playerActionRound);
                }
            } else {
                //nieuwe straat...
                if(position) {
                    if(botLastAction.equals("check")) {
                        double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();

                        if(previousTotalPotSize == topTotalPotSize) {
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "check");
                            actionsSinceLastRequest.add(playerActionRound);
                        } else {
                            double totalOpponentBetSize = topTotalPotSize - previousTotalPotSize;
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, totalOpponentBetSize, "thecorrectstreet", "bet75pct");
                            actionsSinceLastRequest.add(playerActionRound);
                        }
                    } else if(botLastAction.equals("bet75pct")) {
                        //either call / check or call / bet...

                        double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                        double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                        double previousTotalOpponentBetSize = botLastActionRound.getTotalOpponentBetSize();

                        PlayerActionRound playerActionRound1 = new PlayerActionRound("opponent", boardAtLastActionRequest, previousTotalBotBetSize, previousTotalOpponentBetSize, "thecorrectstreet", "call");
                        actionsSinceLastRequest.add(playerActionRound1);

                        if(equalsRake(topTotalPotSize, (previousTotalPotSize + previousTotalBotBetSize + previousTotalBotBetSize))) {
                            PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "check");
                            actionsSinceLastRequest.add(playerActionRound2);
                        } else {
                            double totalOpponentBetSize = topTotalPotSize - (previousTotalPotSize + previousTotalBotBetSize + previousTotalBotBetSize);
                            PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, totalOpponentBetSize, "thecorrectstreet", "bet75pct");
                            actionsSinceLastRequest.add(playerActionRound2);
                        }
                    } else if(botLastAction.equals("call")) {
                        //either facing check or bet...

                        double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                        double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                        double previousTotalOpponentBetSize = botLastActionRound.getTotalOpponentBetSize();

                        if(equalsRake(topTotalPotSize, previousTotalPotSize + (previousTotalOpponentBetSize - previousTotalBotBetSize))) {
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "check");
                            actionsSinceLastRequest.add(playerActionRound);
                        } else {
                            double totalOpponentBetSize = topTotalPotSize - (previousTotalPotSize + (previousTotalOpponentBetSize - previousTotalBotBetSize));
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, totalOpponentBetSize, "thecorrectstreet", "bet75pct");
                            actionsSinceLastRequest.add(playerActionRound);
                        }
                    } else if(botLastAction.equals("raise")) {
                        //either call / check or call / bet...

                        PlayerActionRound botSecondLastActionRound = getSecondMostRecentActionRoundOfPLayer(allActionsOfPreviousActionRequest, "bot");
                        //PlayerActionRound opponentSecondLastActionRound = getSecondMostRecentActionRoundOfPLayer(allActionsOfPreviousActionRequest, "opponent");

                        if(botSecondLastActionRound == null) {
                            ActionRequest previousPreviousActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 2);
                            List<PlayerActionRound> allActionsOfPreviousPreviousActionRequest = previousPreviousActionRequest.getActionsSinceLastRequest();
                            botSecondLastActionRound = getMostRecentActionRoundOfPLayer(allActionsOfPreviousPreviousActionRequest, "bot");
                        }

                        double previousTotalPotSize = previousActionRequest.getTopTotalPotSize();
                        double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                        double previousTotalOpponentBetSize = botLastActionRound.getTotalOpponentBetSize();
                        double previousPreviousTotalBotBetSzie = botSecondLastActionRound.getTotalBotBetSize();
                        double previousPreviousTotalOpponentBetSize = botSecondLastActionRound.getTotalOpponentBetSize();

                        PlayerActionRound playerActionRound1 = new PlayerActionRound("opponent", boardAtLastActionRequest, previousTotalBotBetSize, previousTotalOpponentBetSize, "thecorrectstreet", "call");
                        actionsSinceLastRequest.add(playerActionRound1);

                        if(equalsRake(topTotalPotSize, previousTotalPotSize + (previousTotalBotBetSize - previousPreviousTotalBotBetSzie) + (previousTotalBotBetSize - previousPreviousTotalOpponentBetSize))) {
                            PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "check");
                            actionsSinceLastRequest.add(playerActionRound2);
                        } else {
                            double totalOpponentBetSize = topTotalPotSize - (previousTotalPotSize + (previousTotalBotBetSize - previousTotalOpponentBetSize));
                            PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, totalOpponentBetSize, "thecorrectstreet", "bet75pct");
                            actionsSinceLastRequest.add(playerActionRound2);
                        }
                    }
                } else {
                    if(botLastAction.equals("check")) {
                        if(board.size() == 3) {
                            //bot checked after opponent preflop limp
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "empty");
                            actionsSinceLastRequest.add(playerActionRound);
                        } else {
                            //opponent action has to also be 'check'
                            PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "check");
                            actionsSinceLastRequest.add(playerActionRound);

                            PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "empty");
                            actionsSinceLastRequest.add(playerActionRound2);
                        }
                    } else if(botLastAction.equals("bet75pct")) {
                        //opponent action has to be 'call'
                        double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                        PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, previousTotalBotBetSize, 0, "thecorrectstreet", "call");
                        actionsSinceLastRequest.add(playerActionRound);

                        PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "empty");
                        actionsSinceLastRequest.add(playerActionRound2);
                    } else if(botLastAction.equals("call")) {
                        //als jij oop gecallt hebt dan ben jij per definitie meteen weer aan de beurt nu
                        //no new PlayerActionRound since your last action
                        PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "empty");
                        actionsSinceLastRequest.add(playerActionRound);
                    } else if(botLastAction.equals("raise")) {
                        //opponent action has to be 'call'

                        double previousTotalBotBetSize = botLastActionRound.getTotalBotBetSize();
                        double previousTotalOpponentBetSize = botLastActionRound.getTotalOpponentBetSize();
                        PlayerActionRound playerActionRound = new PlayerActionRound("opponent", board, previousTotalBotBetSize, previousTotalOpponentBetSize, "thecorrectstreet", "call");
                        actionsSinceLastRequest.add(playerActionRound);

                        PlayerActionRound playerActionRound2 = new PlayerActionRound("opponent", board, 0, 0, "thecorrectstreet", "empty");
                        actionsSinceLastRequest.add(playerActionRound2);
                    }
                }
            }
        }

        return actionsSinceLastRequest;
    }

    public double getTopTotalPotSize() {
        return topTotalPotSize;
    }

    public List<Card> getBoard() {
        return board;
    }

    public List<PlayerActionRound> getActionsSinceLastRequest() {
        return actionsSinceLastRequest;
    }

    //je vult een ActionRequest cyclus telkens ZONDER daarbij ook jouw actie die hoort bij de huidige ActionRequest

    private boolean equalsRake(double actualValue, double expectedValue) {
        boolean equalsRake;

        //if(expectedValue - actualValue <= 2) {
            if(actualValue >= (expectedValue * 0.94) && actualValue <= expectedValue) {
                equalsRake = true;
            } else {
                equalsRake = false;
            }
//        } else {
//            equalsRake = false;
//        }

        return equalsRake;
    }

    public PlayerActionRound getMostRecentActionRoundOfPLayer(List<PlayerActionRound> actionsSinceLastRequest, String playerName) {
        for(int i = actionsSinceLastRequest.size() - 1; i >= 0; i--) {
            if(actionsSinceLastRequest.get(i).getPlayerName().equals(playerName)) {
                return actionsSinceLastRequest.get(i);
            }

        }

        return null;
    }

    public PlayerActionRound getSecondMostRecentActionRoundOfPLayer(List<PlayerActionRound> actionsSinceLastRequest, String playerName) {
        int counter = 0;

        for(int i = actionsSinceLastRequest.size() - 1; i >= 0; i--) {
            if(actionsSinceLastRequest.get(i).getPlayerName().equals(playerName)) {
                counter++;

                if(counter == 2) {
                    return actionsSinceLastRequest.get(i);
                }
            }

        }

        return null;
    }

}
