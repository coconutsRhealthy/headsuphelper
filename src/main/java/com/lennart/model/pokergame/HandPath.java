package com.lennart.model.pokergame;

/**
 * Created by LPO10346 on 9/8/2016.
 */
public class HandPath {
    //Future class to keep track of the path (line) in a hand. Should be send back and forth between server and client to
    //keep track.

    private String handPath;

    public String getHandPath(Action action) {
        return "2bet";
    }

}
