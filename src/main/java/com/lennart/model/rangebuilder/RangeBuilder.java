package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRange;

import java.util.*;

/**
 * Created by LPO10346 on 9/2/2016.
 */
public class RangeBuilder {
    //Future class which will create estimated opponent ranges based on type of game (single raised, 3bet, 4bet,
    //ch-raised, etc. Then, to estimate the strength of your hand, you will evaluate your hand against this range.
    //For example if your hand beats 60% of the estimated range of your opponent, the hand is 'medium strong'

    //to evaluate your hand against a range, make a map of all possible starthands, sorted from strongest to weakest.
    //see how high your hand ranks in this map. To make this map, first add all combos that getCombosThatMakeRoyalFlush(),
    //then getCombosThatMakeStraightFlush, then getCombosThatMakeQuads(), etc. To correct this map for ranges, remove all
    //combos from this map that do not fall in the range. Of course, the combos that getCombosThatMakeRoyalFlush() and
    //the other methods return, should first be sorted from strongest to weakest, before added to the map.

    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();
    PreflopRange preflopRange = new PreflopRange();

    public Map<Integer, Set<Set<Card>>> getRange(String handPath, List<Card> board, List<Card> hand) {
//        if(handPath.equals("2bet2bet")) {
        //get alle combos, sorted
        Map<Integer, Set<Set<Card>>> allSortedCombos = boardEvaluator.getSortedCombos(board);

        Map<Integer, Set<Set<Card>>> allSortedCombosClearedForRange = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            allSortedCombosClearedForRange.put(allSortedCombosClearedForRange.size(), new HashSet<>());
            allSortedCombosClearedForRange.get(allSortedCombosClearedForRange.size()-1).addAll(entry.getValue());
        }

        //preflop range van opponent
        //all pocket pair
        //x
        Map<Integer, Set<Card>> allPocketPairs = preflopRange.getPocketPairs(2);

        //all suited
        //x
        Map<Integer, Set<Card>> allSuitedHoleCards = preflopRange.getSuitedHoleCards(2, 2);

        //all offsuit connectors lowest 4
        //x
        Map<Integer, Set<Card>> allOffSuitConnectors = preflopRange.getOffSuitConnectors(4);

        //all offsuit onegappers lowest 6
        //x
        Map<Integer, Set<Card>> allOffSuitOneGappers = preflopRange.getOffSuitOneGappers(6);

        //twogappers lowest 8
        //x
        Map<Integer, Set<Card>> allOffSuitTwoGappers = preflopRange.getOffSuitTwoGappers(8);

        //threegappers lowest 8
        //x
        Map<Integer, Set<Card>> allOffSuitThreeGappers = preflopRange.getOffSuitThreeGappers(8);

        //all A high
        //all K high
        //all Q high
        //x
        Map<Integer, Set<Card>> allOffSuitHighCards = preflopRange.getOffSuitHoleCards(12, 2);

        //nu gaan we preflop removen
//        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
//            loop: for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
//                Set<Card> setFromAllSortedCombos = it.next();
//                //alles wat niet daarin zit, moet uit de hoofdlijst
//
//                for (Map.Entry<Integer, Set<Card>> entryAllPocketPairs : allPocketPairs.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllPocketPairs.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllSuitedHoleCards : allSuitedHoleCards.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllSuitedHoleCards.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllOffSuitConnectors : allOffSuitConnectors.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllOffSuitConnectors.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllOffSuitOneGappers : allOffSuitOneGappers.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllOffSuitOneGappers.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllOffSuitTwoGappers : allOffSuitTwoGappers.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllOffSuitTwoGappers.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllOffSuitThreeGappers : allOffSuitThreeGappers.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllOffSuitThreeGappers.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                for (Map.Entry<Integer, Set<Card>> entryAllOffSuitHighCards : allOffSuitHighCards.entrySet()) {
//                    if(setFromAllSortedCombos.equals(entryAllOffSuitHighCards.getValue())) {
//                        //continue met next 'entrySet'
//                        continue loop;
//                    }
//                }
//
//                it.remove();
//            }
//        }



        //alle handen boven 50% op flop
        //x
        Map<Integer, Set<Set<Card>>> sortedCombosAboveLevel = boardEvaluator.getSortedCombosAboveDesignatedStrengthLevel(0.52, board);

        //alle strong en medium straight draws op flop
        //x
        Map<Integer, Set<Card>> strongOosdDraws = straightDrawEvaluator.getStrongOosdCombos(board);
        Map<Integer, Set<Card>> mediumOosdDraws = straightDrawEvaluator.getMediumOosdCombos(board);
        Map<Integer, Set<Card>> strongGutshotDraws = straightDrawEvaluator.getStrongGutshotCombos(board);
        Map<Integer, Set<Card>> mediumGutshotDraws = straightDrawEvaluator.getMediumGutshotCombos(board);

        //nog toevoegen, backdoor combos?
        //Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos(board);

        //alle strong en medium flushdraws op flop
        //x
        Map<Integer, Set<Card>> strongFlushDraws = flushDrawEvaluator.getStrongFlushDrawCombos(board);
        Map<Integer, Set<Card>> mediumFlushDraws = flushDrawEvaluator.getMediumFlushDrawCombos(board);

        //nog toevoegen, strong backdoor combos?
        //Map<Integer, List<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombosAsMapList(board);

        //alle strong en medium overcarddraws op flop
        //x
        Map<Integer, Set<Card>> strongHighCardDraws = highCardDrawEvaluator.getStrongTwoOvercards(board);
        Map<Integer, Set<Card>> mediumHighCardDraws = highCardDrawEvaluator.getMediumTwoOvercards(board);




        Map<Integer, Map<Integer, Set<Card>>> preflopRange = new HashMap<>();
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        preflopRange.put(preflopRange.size(), allPocketPairs);
        preflopRange.put(preflopRange.size(), allSuitedHoleCards);
        preflopRange.put(preflopRange.size(), allOffSuitConnectors);
        preflopRange.put(preflopRange.size(), allOffSuitOneGappers);
        preflopRange.put(preflopRange.size(), allOffSuitTwoGappers);
        preflopRange.put(preflopRange.size(), allOffSuitThreeGappers);
        preflopRange.put(preflopRange.size(), allOffSuitHighCards);

        //combosToBeRemoved.put(combosToBeRemoved.size(), sortedCombosAboveLevel);
        flopRange.put(flopRange.size(), strongOosdDraws);
        flopRange.put(flopRange.size(), strongGutshotDraws);
        flopRange.put(flopRange.size(), mediumGutshotDraws);
        flopRange.put(flopRange.size(), strongFlushDraws);
        flopRange.put(flopRange.size(), mediumFlushDraws);
        flopRange.put(flopRange.size(), strongHighCardDraws);
        flopRange.put(flopRange.size(), mediumHighCardDraws);

        createRange(preflopRange, flopRange, sortedCombosAboveLevel, board);


        //nu gaan we postflop removen:
//            for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
//                loop: for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
//                    Set<Card> setFromAllSortedCombos = it.next();
//                    //alles wat niet daarin zit, moet uit de hoofdlijst
//
//                    //we beginnen met de hoge combos
//                    for (Map.Entry<Integer, Set<Set<Card>>> combosAboveLevel : sortedCombosAboveLevel.entrySet()) {
//                        for(Set<Card> s : combosAboveLevel.getValue()) {
//                            if(setFromAllSortedCombos.equals(s)) {
//                                //continue met next 'entrySet'
//                                continue loop;
//                            }
//                        }
//                    }
//
//                    //we beginnen met strongOosdDraws:
//                    for (Map.Entry<Integer, Set<Card>> entryStrongOosd : strongOosdDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(entryStrongOosd.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
//                    //de anderen
//                    for (Map.Entry<Integer, Set<Card>> entryMediumOosd : mediumOosdDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(entryMediumOosd.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
//                    for (Map.Entry<Integer, Set<Card>> entryStrongGutshot : strongGutshotDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(entryStrongGutshot.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
//                    for (Map.Entry<Integer, Set<Card>> entryMediumGutshot : mediumGutshotDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(entryMediumGutshot.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
////                    for (Map.Entry<Integer, Set<Card>> entryStrongBackDoorStraight : strongBackDoorStraightDraws.entrySet()) {
////                        if(setFromAllSortedCombos.equals(entryStrongBackDoorStraight.getValue())) {
////                            //continue met next 'entrySet'
////                            continue loop;
////                        }
////                    }
//
//                    for (Map.Entry<Integer, Set<Card>> entryStrongFlushDraw : strongFlushDraws.entrySet()) {
//                        if(setFromAllSortedCombos.containsAll(entryStrongFlushDraw.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
//                    for (Map.Entry<Integer, Set<Card>> entryMediumFlushDraw : mediumFlushDraws.entrySet()) {
//                        if(setFromAllSortedCombos.containsAll(entryMediumFlushDraw.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
////                    for (Map.Entry<Integer, List<Card>> entryStrongBackDoorFlush : strongBackDoorFlushDraws.entrySet()) {
////                        if(setFromAllSortedCombos.containsAll(entryStrongBackDoorFlush.getValue())) {
////                            //continue met next 'entrySet'
////                            continue loop;
////                        }
////                    }
//
//                    for (Map.Entry<Integer, Set<Card>> strongHighCardDraw : strongHighCardDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(strongHighCardDraw.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//
//                    for (Map.Entry<Integer, Set<Card>> mediumHighCardDraw : mediumHighCardDraws.entrySet()) {
//                        if(setFromAllSortedCombos.equals(mediumHighCardDraw.getValue())) {
//                            //continue met next 'entrySet'
//                            continue loop;
//                        }
//                    }
//                    it.remove();
//                }
//            }
        double x = new HandEvaluator().getHandStrengthAgainstRange(hand, allSortedCombosClearedForRange);
        return allSortedCombosClearedForRange;
//        return null;
        }
////        double x = new HandEvaluator().getHandStrengthAgainstRange(hand, allSortedCombosClearedForRange);
//        return allSortedCombosClearedForRange;
////        return null;
//    }

    //je wilt een methode creeeren die allPossibleStarthands cleared for preflop en postflop ranges. Dit houdt in dat je
    //een variabel aantal maps mee moet kunnen geven aan de methode en dat ie daarvoor gaat clearen.

    public Map<Integer, Set<Set<Card>>> createRange(Map<Integer, Map<Integer, Set<Card>>> preflopRange,
                                                    Map<Integer, Map<Integer, Set<Card>>> flopRange,
                                                    Map<Integer, Set<Set<Card>>> flopValueRangeToRetain, List<Card> board) {
        //het moet worden met argumenten: preflopRange, flopRange, turnRange

        //maak kopie
        Map<Integer, Set<Set<Card>>> allSortedCombos = boardEvaluator.getSortedCombos(board);

        Map<Integer, Set<Set<Card>>> allSortedCombosClearedForRange = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            allSortedCombosClearedForRange.put(allSortedCombosClearedForRange.size(), new HashSet<>());
            allSortedCombosClearedForRange.get(allSortedCombosClearedForRange.size()-1).addAll(entry.getValue());
        }


        //ff
        int countertjeAll1 = 0;
        int countertjeLess1 = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                countertjeAll1++;
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                countertjeLess1++;
            }
        }
        //ff


        //ga door
        //eigenlijk moet het in twee iteraties: eerst clear je voor preflop range, daarna voor postlfop...
        //eerst retain voor preflop
        //dan retain voor flop, gegeven lijst van preflop.. dan turn.. etc

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> setFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Map<Integer, Set<Card>>> eije : preflopRange.entrySet()) {
                    if(!eije.getValue().isEmpty()) {
                        for (Map.Entry<Integer, Set<Card>> eijeSet : eije.getValue().entrySet()) {
                            if (setFromAllSortedCombos.equals(eijeSet.getValue())) {
                                //continue met next 'entrySet'
                                continue loop;
                            }
                        }
                    }
                }
                it.remove();
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> setFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Map<Integer, Set<Card>>> eije : flopRange.entrySet()) {
                    if(!eije.getValue().isEmpty()) {
                        for (Map.Entry<Integer, Set<Card>> eijeSet : eije.getValue().entrySet()) {
                            if (setFromAllSortedCombos.equals(eijeSet.getValue())) {
                                //continue met next 'entrySet'
                                continue loop;
                            }
                        }
                    }
                }

                for (Map.Entry<Integer, Set<Set<Card>>> combosAboveLevel : flopValueRangeToRetain.entrySet()) {
                    for(Set<Card> s : combosAboveLevel.getValue()) {
                        if(setFromAllSortedCombos.equals(s)) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }
                }
                it.remove();
            }
        }

        //ff
        int countertjeAll2 = 0;
        int countertjeLess2 = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                countertjeAll2++;
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                countertjeLess2++;
            }
        }
        //ff

        return allSortedCombosClearedForRange;
    }
}