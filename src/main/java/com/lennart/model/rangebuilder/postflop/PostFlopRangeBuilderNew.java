package com.lennart.model.rangebuilder.postflop;

/**
 * Created by LennartMac on 11/01/17.
 */
public class PostFlopRangeBuilderNew {

    //Class will build postflop range based on preflop range and postflop potsize

    private void getFlopRange() {
        //check hoe groot pot is

            // < 7 bb
                //dan gewoon gehele preflop range

            // 7 - 16bb
                //most likely single raised with cbet

                    //opponent total betsize relative to pot
                        // < 20%
                            //entire preflop range

                        // 20 - 50%
                            //all handstrength 40% + hands
                            //all draws
                            //a lot of air

                        // > 50%
                            //all handstrength 50% + hands
                            //all draws
                            //a lot of air but bit less then 20-50


            // 16 - 33bb
                //bijv single raised met cbet flop en cbet turn.
                    //opponent total betsize relative to pot
                        // < 20%
                            //entire preflop range

                        // 20 - 50%
                            //value range: all handstrength 40% + hands
                            //draws: all regular draws and all medium and strong backdoor draws
                            //air: a lot of air

                        // > 50%
                            //value range: all handstrength 60% + hands
                            //draws: medium+ regular draws, strong backdoors


            // 30 - 70bb
                //bijv single raised met triple barrel
                    //opponent total betsize relative to pot
                        // < 20%
                            //entire preflop range

                        // 20 - 50%
                            //value range: 65%+ hands
                            //draws: medium+ regular draws
                            //quite some air (25%?)

                        // > 50%
                            //value range: 75%+ hands
                            //draws: strong regular draws
                            //a bit of air

            // 70bb >
                //pot commited
                    //opponent total betsize relative to pot
                        // < 20%
                            //entire preflop range

                        // 20 - 50%
                            //value range: 70%+ hands
                            //draws: medium+ regular draws
                            //some air (20%?)

                        // > 50%
                            //value range: 80%+ hands
                            //draws: strong regular draws
                            //a bit of air

    }



    private void getFlopRange22() {
        //get the range resulting from preflop action

            //determine how big is pot

                //below 7bb

                    //see how much pot has grown since preflop

                        //less than 20% of potsize preflop
                            //range: all preflop range

                        //between 20% and 50% of potsize preflop
                            //range: all draws
                            //all handstrength above 50%
                            //quite some air

                        //between 50% and 75% of potsize preflop
                            //range:

                //between 7 and 16bb

                    //same as above

                //


                    //below 7 bb

                    //between 7 and 16 bb

                    //


        //determine if last action of opponent was call, bet or raise.
        //If so, then you have new information about his range
    }





}
