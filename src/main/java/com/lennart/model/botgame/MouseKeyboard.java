package com.lennart.model.botgame;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Created by LPO21630 on 20-3-2017.
 */
public class MouseKeyboard {

    public static void click(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void enterText(String text) {
        char[] charArray = text.toCharArray();

        try {
            Robot bot = new Robot();

            for(char c : charArray) {
                bot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
                bot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
