package major.haxjor.script.impl.avatar;

import com.moandjiezana.toml.Toml;
import major.haxjor.HaxJor;
import major.haxjor.jnative.keyboard.Keyboard;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static major.haxjor.HaxJor.LOGGER;

/**
 * Avatar script for avatar effects.
 *
 * @author Major
 */
public class AvatarHaxJorScript implements HaxJorScript, KeyboardJNativeListener {

    //a mutable array of random avatar combinations. (this isn't apart of the design but rather a demonstration of avatar tests).
    private static final String[] random_avatars = {
            "OoOoOoOoOoO",
            "_eZe_Hj",
            "I am a FU*KING BEAST",
    };

    public AvatarHaxJorScript() {
        //some default avatar.
        avatars = "oOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOo".toCharArray();
        effect = AvatarEffect.CHAIN;
        speed = AvatarSpeed.FAST;
    }


    private char[] avatars;
    private AvatarEffect effect;
    private AvatarSpeed speed;

    public final char[] getAvatars() {
        return avatars;
    }

    public final AvatarSpeed getSpeed() {
        return speed;
    }

    public final AvatarEffect getEffect() {
        return effect;
    }

    //to avoid concurrent running of this script
    private boolean isRunning;

    @Override
    public final char indicator() {
        return 'Q';
    }

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
                    pend(script.speed.milliseconds);
                }
                script.isRunning = false;
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
                for (int i = 0; i < chars.length; i++)  {
                    avatar(chars[i]);
                    pend(script.speed.milliseconds);
                }

                //write backward
                for (int i = chars.length - 1; i >= 0; i--) {
                    avatar(chars[i]);
                    pend(script.speed.milliseconds);
                }
                script.isRunning = false;
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
                        pend(script.speed.milliseconds);
                    } else {
                        nextChar = chars[index];
                        avatar(prevChar, nextChar);
                        pend(script.speed.milliseconds);
                        prevChar = nextChar;
                    }
                }
                script.isRunning = false;
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
                        pend(script.speed.milliseconds);
                    }
                    for (int i = 100; i > 0; i--) {
                        if (i < 10)
                            avatar(Integer.toString(i).charAt(0));
                        else
                            avatar(Integer.toString(i).charAt(0), Integer.toString(i).charAt(1));
                        pend(script.speed.milliseconds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //this naturally only being called on end
                script.isRunning = false;
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
        synchronized void avatar(char... chars) {
            System.out.println("We do avatar: " + Arrays.toString(chars));
            //starts by 'tabbing'
            Keyboard.type('\t');
            //then write avatar and a space
            Keyboard.type("/avatar ");
            //now type the chars (TODO benchmark if its actually necessary to do the lazy check)
            if (chars.length == 1)
                Keyboard.type(chars[0]);
            else
                Keyboard.type(new String(chars));
            //enter as we finish this avatar
            Keyboard.enter();
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
                System.out.println("Pending for: " + ms + "ms");
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * The speed in milliseconds for this avatar.
     *
     * TODO find appropriate delays
     */
    private enum AvatarSpeed {
        SUPER_FAST(5),
        FAST(75),
        MEUM(15),
        SLOW(20),
        VERY_SLOW(50);

        AvatarSpeed(int milliseconds) {
            this.milliseconds = milliseconds;
        }

        final int milliseconds;

    }

    //use the parent path with this file path.
    @Override
    public Path settings() {
        return HaxJorScript.SCRIPT_BASE_PATH.resolve(Paths.get("avatar_script.toml"));
    }

    @Override
    public final void initialize() {
        Toml toml = new Toml().read(settings().toFile());

        //test for TOML
        LOGGER.info(toml.getString("test"));

        HaxJor.KEYBOARD_SCRIPTS.put(indicator(), this);
        System.out.println("added: " + indicator() + " " + this + " to keyboard scripts");

        //initialize through
    }

    @Override
    public final boolean execute() {
        if (!enabled()) {
            LOGGER.info("Script is disabled.");
            return false;
        }
        if (isRunning) {
            LOGGER.info("Already running. Action not queued.");
            return false;
        }
        if (effect == null) {
            LOGGER.info("No effect selected.");
            //OR: use default effect
            return false;
        }

        //currently testing jnativekeyboard reaction, thus we do not really need the whole functionality of this method but rather
        //some sort of response that we reached this state
//        if (1 == 1) {
//            System.out.println("Running script.");
//            return false;
//        }
        isRunning = true;
        try {
            HaxJor.SCRIPT_EXECUTOR.submit(() -> effect.onEffect(this)).get();//#get should block this thread until completion to avoid concurrent operations.
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("should be blocking until its done.");
            e.printStackTrace();
        }
        System.out.println("this called once the task done.");
        return true;
    }

    @Override
    public final boolean enabled() {
        return true;
    }

}
