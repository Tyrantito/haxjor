package major.haxjor.script.impl.avatar;

import major.haxjor.HaxJor;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;
import major.haxjor.script.HaxJorScriptSettings;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static major.haxjor.HaxJor.LOGGER;
import static major.haxjor.HaxJor.SCRIPT_EXECUTOR;
import static major.haxjor.HaxJorUtility.debug;

/**
 *
 * DISCONTINUED: Since I found a better way doing this, through reversing the original avatar function
 * and thus allowing you to modify avatar without any game interaction, this seems as a useless method to do so,
 * though it will be still available, but limited to its current functioning. You can still produce awesome avatars.
 * Avatar script for avatar effects.
 *
 * @author Major
 */
@HaxJorScriptSettings.SettingsFile(file = "avatar_script.toml")
public class AvatarHaxJorScript extends HaxJorScriptSettings implements HaxJorScript, KeyboardJNativeListener {

    /**
     * The char(s) for our default avatar. {max chars = 2}
     */
    static final char[] DEFAULT_AVATAR = {'M', 'j'};

    /**
     * Temporary default constructor TODO load this through settings file
     */
    public AvatarHaxJorScript() {
        //some default avatar.
        combination = AvatarCombination.A;
        effect = AvatarEffect.CHAIN;
        speed = AvatarSpeed.FAST;
    }

    /*
     * Settings
     */
    private AvatarCombination combination;
    private AvatarEffect effect;
    private AvatarSpeed speed;
    private boolean rememberClipboard;

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
    public final String indicator() {
        return "Q";
    }

    //use the parent path with this file path.
//    @Override
//    public Path settings() {
//        return HaxJorScript.SCRIPT_BASE_PATH.resolve(Paths.get("avatar_script.toml"));
//    }

    @Override
    public final void initialize() {
        rememberClipboard = hjsp.getBoolean("rememberClipboard");
        speed = hjsp.get("speed", AvatarSpeed.class);
        effect = hjsp.get("effect", AvatarEffect.class);
        combination = hjsp.get("combination", AvatarCombination.class);

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
            //proper sequence
            CompletableFuture
                    .runAsync(() -> effect.start(this), SCRIPT_EXECUTOR)
                    .thenRun(() -> effect.onEffect(this))
                    .thenRun(() -> effect.finish(this))
                    .get();//#get should block this thread until completion to avoid concurrent operations.
        } catch (InterruptedException | ExecutionException e) {
            debug("script blocking has been interrupted.");
            e.printStackTrace();
        }
        debug("this called once the task done. " + HaxJor.firingEvents);
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

    public final boolean isRememberClipboard() {
        return rememberClipboard;
    }
}
