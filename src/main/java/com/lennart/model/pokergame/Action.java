package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.RangeBuilder;

/**
 * Created by LPO10346 on 10/12/2016.
 */
public class Action {
    //Future class to determine your action

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
