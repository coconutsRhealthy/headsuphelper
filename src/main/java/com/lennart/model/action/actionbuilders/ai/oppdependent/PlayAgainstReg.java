package com.lennart.model.action.actionbuilders.ai.oppdependent;

import java.util.*;

public class PlayAgainstReg {

    //freaky
        //pre
            //-bluffraises
        //post:
            //-donkets
            //-bluffraises

    private List<String> getPlayStyles() {
        return Arrays.asList("normal", "passive", "onlyValue", "freaky");
    }

    private List<String> getSizingStylesPreflopIp() {
        return Arrays.asList("normal", "bigWithAirAndValue", "randomEverything");
    }

    private List<String> getSizingStylesPreflopOop() {
        return Arrays.asList("normal", "bigWithAirAndValue", "randomEverything", "smallEverything", "smallWithAirAndValue");
    }

    private List<String> getSizingStylesPostflop() {
        return Arrays.asList("normal, bigWithAirAndValue", "smallWithAirAndValue", "smallEverything", "randomNormalPercentage");
    }

    private List<String> getPosition() {
        return Arrays.asList("IP", "OOP");
    }

    private List<String> getBlinds() {
        return Arrays.asList("20", "30", "40", "50", "60", "80", "100");
    }

    private List<String> getPreflopPostflop() {
        return Arrays.asList("Pre", "Post");
    }

    private Map<String, String> getStrategyPlan() {
        List<String> strategyDivisors = getAllStrategyDivisors();
        Map<String, String> strategyPlan = new LinkedHashMap<>();

        for(String stratDivisor : strategyDivisors) {
           boolean preflop = stratDivisor.contains("Pre");
           boolean position = stratDivisor.contains("IP");

           String strategy = "";

           if(preflop) {
               strategy = strategy + "Style: " + getRandomElement(getPlayStyles()) + " || ";

               if(position) {
                   strategy = strategy + "Sizing: " + getRandomElement(getSizingStylesPreflopIp());
               } else {
                   strategy = strategy + "Sizing: " + getRandomElement(getSizingStylesPreflopOop());
               }
           } else {
               strategy = strategy + "Style: " + getRandomElement(getPlayStyles()) + " || ";
               strategy = strategy + "Sizing: " + getRandomElement(getSizingStylesPostflop());
           }

           strategyPlan.put(stratDivisor, strategy);
        }

        return strategyPlan;
    }

    public static void main(String[] args) {
        new PlayAgainstReg().getStrategyPlan();
    }

    private Map<String, String> initializeStrategyMap() {
        Map<String, String> strategyMap = new LinkedHashMap<>();

        List<String> blinds = getBlinds();
        List<String> preflopPostflop = getPreflopPostflop();
        List<String> positions = getPosition();

        for(String blind : blinds) {
            for(String prepost : preflopPostflop) {
                for(String position : positions) {
                    strategyMap.put(blind + "_" + prepost + "_" + position, "toFill");
                }
            }
        }

        return strategyMap;
    }

    private List<String> getAllStrategyDivisors() {
        List<String> strategyDivisors = new ArrayList<>();

        List<String> blinds = getBlinds();
        List<String> preflopPostflop = getPreflopPostflop();
        List<String> positions = getPosition();

        for(String blind : blinds) {
            for(String prepost : preflopPostflop) {
                for(String position : positions) {
                    strategyDivisors.add(blind + "_" + prepost + "_" + position);
                }
            }
        }

        return strategyDivisors;
    }

    private String getRandomElement(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }





    //ideas
        //river: dont shove against river raise unless nuts type hand (so no 95%)




    //




    //mixup categories
        //only value shoves (80% plus)
        //complete nit (no raises at all)
        //normal

    


}
