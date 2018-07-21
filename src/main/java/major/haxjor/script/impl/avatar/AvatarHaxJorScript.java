package major.haxjor.script.impl.avatar;

import com.moandjiezana.toml.Toml;
import major.haxjor.HaxJor;
import major.haxjor.jnative.keyboard.KeyboardInputListener;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;

import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import static major.haxjor.HaxJor.LOGGER;

/**
 * Avatar script for avatar effects.
 *
 * @author Major
 */
public class AvatarHaxJorScript implements HaxJorScript, KeyboardJNativeListener {

    /**
     * The char(s) for our default avatar. {max chars = 2}
     */
    static final char[] DEFAULT_AVATAR = {'M', 'j'};

    /**
     * Temporary default constructor TODO load this through settings file
     */
    public AvatarHaxJorScript() {
        //some default avatar.
        combination = AvatarCombination.SMILES;
        effect = AvatarEffect.CHAIN;
        speed = AvatarSpeed.FAST;
    }

    private AvatarCombination combination;
    private AvatarEffect effect;
    private AvatarSpeed speed;

    //should we set our avatar to default when effect is completed?
    private boolean restoreToDefault = true;

    public final AvatarCombination getAvatarCombination() {
        return combination;
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

    //the clipboard context we've had before overwriting it.
    private StringSelection previousClipboard;

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
    public final void execute() {
        if (isRunning) {
            LOGGER.info("Already running. Action not queued.");
            return;
        }
        if (effect == null) {
            LOGGER.info("No effect selected.");
            //OR: use default effect
            return;
        }

        //currently testing jnativekeyboard reaction, thus we do not really need the whole functionality of this method but rather
        //some sort of response that we reached this state
//        if (1 == 1) {
//            System.out.println("Running script.");
//            return false;
//        }
        isRunning = true;
        try {
            HaxJor.SCRIPT_EXECUTOR.submit(() -> {
                effect.start(this);
                effect.onEffect(this);
                effect.finish(this);
            }).get();//#get should block this thread until completion to avoid concurrent operations.
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("script blocking has been interrupted.");
            e.printStackTrace();
        }
        System.out.println("this called once the task done. " + HaxJor.firingEvents);
    }

    @Override
    public final boolean enabled() {
        return true;
    }

    final void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public final boolean isRestoreToDefault() {
        return restoreToDefault;
    }
}
