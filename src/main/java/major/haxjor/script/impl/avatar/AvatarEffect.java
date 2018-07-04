package major.haxjor.script.impl.avatar;

import major.haxjor.jnative.keyboard.Keyboard;

import java.util.Arrays;
import java.util.Random;

import static major.haxjor.jnative.keyboard.Keyboard.HAXBALL_SPACE;

/**
 * The display effect for this avatar.
 */
public enum AvatarEffect {

    /**
     * This effect will pass each character on the charset for this avatar per tick.
     */
    SOLO() {
        @Override
        public final void onEffect(AvatarHaxJorScript script) {
            final char[] chars = random_avatars[new Random().nextInt(random_avatars.length)].toCharArray().clone();

            for (char c : chars) {
                avatar(c);
                pend(script.getSpeed().getMilliseconds());
            }
        }
    },

    /**
     * This will do the same effect as {@link #SOLO} except at the end it will reverse.
     */
    SOLO_REVERSAL {
        @Override
        public final void onEffect(AvatarHaxJorScript script) {
            //TODO temp.
            final char[] chars = random_avatars[new Random().nextInt(random_avatars.length)].toCharArray().clone();

            //write forward
            for (int i = 0; i < chars.length; i++) {
                avatar(chars[i]);
                pend(script.getSpeed().getMilliseconds());
            }

            //write backward
            for (int i = chars.length - 1; i >= 0; i--) {
                avatar(chars[i]);
                pend(script.getSpeed().getMilliseconds());
            }
        }
    },

    /**
     * This effect will make a scrolling effect for the charset.
     */
    CHAIN {
        @Override
        public final void onEffect(AvatarHaxJorScript script) {
            //get the copy of chars to write [should it be a clone?] (a random avatar)
            final char[] chars = random_avatars[new Random().nextInt(random_avatars.length)].toCharArray().clone();

            char prevChar = 0; //id of char
            char nextChar; //id of char

            for (int index = 0; index < chars.length; index++) {
                if (index == 0) {
                    prevChar = chars[index];
                    avatar(prevChar);
                    pend(script.getSpeed().getMilliseconds());
                } else {
                    nextChar = chars[index];
                    avatar(prevChar, nextChar);
                    pend(script.getSpeed().getMilliseconds());
                    prevChar = nextChar;
                }
            }
        }
    },

    /**
     * A static 1-100 - 100-1 counter.
     */
    COUNTER {
        @Override
        public void onEffect(AvatarHaxJorScript script) {
            try {
                //lol some shit way (made for loomani :D)
                for (int i = 0; i < 100; i++) {
                    if (i < 10)
                        avatar(Integer.toString(i).charAt(0));
                    else
                        avatar(Integer.toString(i).charAt(0), Integer.toString(i).charAt(1));
                    pend(script.getSpeed().getMilliseconds());
                }
                for (int i = 100; i > 0; i--) {
                    if (i < 10)
                        avatar(Integer.toString(i).charAt(0));
                    else
                        avatar(Integer.toString(i).charAt(0), Integer.toString(i).charAt(1));
                    pend(script.getSpeed().getMilliseconds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //a mutable array of random avatar combinations. (this isn't apart of the design but rather a demonstration of avatar tests).
    private static final String[] random_avatars = {
            "☺☻☺☻☺☻☺☻☺☻☺☻",
            "OoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOo",
            "GO0O0O0O0O0O0O0O0OAL!!Mj",
            "_eZe_Hj",
            " I" + HAXBALL_SPACE + "am" + HAXBALL_SPACE + "a" + HAXBALL_SPACE + "FU*KING" + HAXBALL_SPACE + "BEAST" + HAXBALL_SPACE + "Mj",
    };

    /**
     * The random instance
     */
    private static final Random RANDOM = new Random();

    /**
     * What happens when the effect is called.
     *
     * @param script the {@link AvatarHaxJorScript} that executed this effect.
     */
    public abstract void onEffect(AvatarHaxJorScript script);

    /**
     * Perform the avatar typing. Must be called synchronously.
     *
     * @param chars the characters to write on this avatar.
     */
    synchronized void avatar(char... chars) {
//        System.out.println("We do avatar: " + Arrays.toString(chars));
        //starts by 'tabbing'
        Keyboard.type('\t');
        //type avatar.
        Keyboard.type("/avatar ");
        //now copypaste the chars for this avatar
        Keyboard.copyPaste(chars);
        //enter as we finish this avatar
        Keyboard.enter();
    }

    /**
     * What happens before the effect starts.
     */
    void start(AvatarHaxJorScript script) {
        //backup the current clipboard
//            try {
//                script.previousClipboard = new StringSelection(((String) Toolkit.getDefaultToolkit()
//                        .getSystemClipboard().getData(DataFlavor.stringFlavor)));
//            } catch (UnsupportedFlavorException | IOException e) {
//                e.printStackTrace();
//            }
    }

    /**
     * What happens when this avatar script is finished.
     */
    void finish(AvatarHaxJorScript script) {
        //restore the previous clipboard.
//            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//            clipboard.setContents(script.previousClipboard, script.previousClipboard);

        //indicate that we finished running this script.
        script.setRunning(false);
    }

    /**
     * Pends the thread for a given period in milliseconds.
     * This method must be carefully called since misuse might lead to
     * desynchronization issues, as this method trust that the thread its being called on
     * is the thread it needs to {@link Thread#sleep(long)} for, thus, if its being called
     * directly from an inappropriate thread, deadlocks might occur.
     *
     * @param ms the milliseconds to 'delay' for.
     */
    synchronized void pend(int ms) {
        try {
//            System.out.println("Pending for: " + ms + "ms");
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
