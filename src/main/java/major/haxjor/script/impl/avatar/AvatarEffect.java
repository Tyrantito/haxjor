package major.haxjor.script.impl.avatar;

import major.haxjor.jnative.keyboard.Keyboard;

/**
 * The display effect for this avatar.
 *
 * @author Major
 */
public enum AvatarEffect {

    /**
     * This effect will pass each character on the charset for this avatar per tick.
     */
    SOLO() {
        @Override
        public final void onEffect(AvatarHaxJorScript script) {
            final char[] chars = script.getAvatarCombination().getChars().clone();

            final int delay = script.getAvatarCombination().getMilliseconds() != 0 ? script.getAvatarCombination().getMilliseconds() : script.getSpeed().getMilliseconds();

            for (int repeat = 0; repeat < 1 + script.getAvatarCombination().getRepeat(); repeat++) {
                for (char c : chars) {
                    avatar(c);
                    script.pend(delay);
                }
            }
        }
    },

    /**
     * This will do the same effect as {@link #SOLO} except at the end it will reverse.
     */
    SOLO_REVERSAL {
        @Override
        public final void onEffect(AvatarHaxJorScript script) {
            final char[] chars = script.getAvatarCombination().getChars().clone();
            final int delay = script.getAvatarCombination().getMilliseconds() != 0 ? script.getAvatarCombination().getMilliseconds() : script.getSpeed().getMilliseconds();

            for (int repeat = 0; repeat < 1 + script.getAvatarCombination().getRepeat(); repeat++) {
                //write forward
                for (char c : chars) {
                    avatar(c);
                    script.pend(delay);
                }

                //write backward
                for (int i = chars.length - 1; i >= 0; i--) {
                    avatar(chars[i]);
                    script.pend(delay);
                }
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
            final char[] chars = script.getAvatarCombination().getChars().clone();
            final int delay = script.getAvatarCombination().getMilliseconds() != 0 ? script.getAvatarCombination().getMilliseconds() : script.getSpeed().getMilliseconds();

            char prevChar = 0; //id of char
            char nextChar; //id of char

            for (int repeat = 0; repeat < 1 + script.getAvatarCombination().getRepeat(); repeat++) {
                for (int index = 0; index < chars.length; index++) {
                    if (index == 0) {
                        prevChar = chars[index];
                        avatar(prevChar);
                        script.pend(delay);
                    } else {
                        nextChar = chars[index];
                        avatar(prevChar, nextChar);
                        script.pend(delay);
                        prevChar = nextChar;
                    }
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
            final int delay = script.getAvatarCombination().getMilliseconds() != 0 ? script.getAvatarCombination().getMilliseconds() : script.getSpeed().getMilliseconds();

            //lol some shit way (made for loomani :D)
            for (int i = 0; i < 100; i++) {
                if (i < 10)
                    avatar(Integer.toString(i).charAt(0));
                else
                    avatar(Integer.toString(i).charAt(0), Integer.toString(i).charAt(1));
                script.pend(delay);
            }
            for (int i = 100; i > 0; i--) {
                if (i < 10)
                    avatar(Integer.toString(i).charAt(0));
                else
                    avatar(Integer.toString(i).charAt(0), Integer.toString(i).charAt(1));
                script.pend(delay);
            }
        }
    };

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
    protected final synchronized void avatar(char... chars) {
//        System.out.println("We do avatar: " + Arrays.toString(chars));
        //starts by 'tabbing'
        Keyboard.type('\t');
        //type avatar.
        Keyboard.type("/avatar ");
        //now copypaste the chars for this avatar
        Keyboard.copyPaste(chars);
        //enter as we finish this avatar
        Keyboard.enter();

        System.out.println("this avatar just finished....");
    }

    /**
     * What happens before the effect starts.
     */
    void start(AvatarHaxJorScript script) {
        if (script.isRememberClipboard()) {
            Keyboard.backupClipboard();
        }
    }

    /**
     * What happens when this avatar script is finished.
     */
    void finish(AvatarHaxJorScript script) {
        //should we restore avatar to default on end?
        if (script.isRestoreToDefault()) {
            avatar(AvatarHaxJorScript.DEFAULT_AVATAR);
        }

        //restore the previous clipboard.
        if (script.isRememberClipboard()) {
            Keyboard.restoreClipboard();
        }

        //indicate that we've finished running this script.
        script.setRunning(false);
    }

}
