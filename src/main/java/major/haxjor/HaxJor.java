package major.haxjor;

import major.haxjor.jnative.keyboard.Keyboard;
import major.haxjor.jnative.keyboard.KeyboardInputListener;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;
import major.haxjor.script.HaxJorScriptSettings;
import major.haxjor.script.impl.ToggleKeyboardHaxJorScript;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static major.haxjor.HaxJorSettings.*;
import static major.haxjor.HaxJorUtility.debug;
import static major.haxjor.HaxJorUtility.elapsedMs;

/**
 * HaxJor is a HaxBall specialized program that offers various of useful utilities
 * that may lead to an unfair advantage over the average user.
 * Some notable features that differs the program from the rest are:
 * Avatar script, Reliable Auto-clicker & Marcos and more...
 * While all of the features are completely modifiable, the program also ensures to be lightweight and consume close
 * to no system effort and thus ensure that your Haxball experience remains as smooth as possible.
 * <p>
 *
 * <b>Note:</b>I do not take any responsibilities for your machine or any punishment that might occur from using this software.
 * This software was created for learning purposes and community share.
 *
 * <b>Note:</b>Please also be careful that due to this program being open-sourced some may fork and misuse this program
 * to create different kind of key loggers and various native traces using jnative. Please act in cautious.
 *
 * @author Major - A.K.A Guy
 * @version 1.2
 * @since 24-June-2018
 */
public final class HaxJor {

    /**
     * The logger that will stream information to the console.
     */
    public static final Logger LOGGER = Logger.getLogger(HaxJor.class.getSimpleName());

    /**
     * The logger for global screen jnative
     */
    private static final Logger JNATIVE_LOGGER = Logger.getLogger(GlobalScreen.class.getPackage().getName());

    /**
     * Native listeners that are global for the program (these are the native listeners and not the script listeners!)
     **/
    private static final KeyboardInputListener KEYBOARD_INPUT_LISTENER = new KeyboardInputListener();

    /**
     * The set of scripts. //TODO doesn't have to be multiset
     */
    private static final Set<HaxJorScript> SCRIPTS = new HashSet<>();

    /**
     * The path for scripts
     */
    private static final Path SCRIPTS_IMPL_DIRECTORY = Paths.get("major.haxjor.script.impl");

    /**
     * The executor to concurrently but synchronously handle scripts.
     */
    public static final ScheduledExecutorService SCRIPT_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    /**
     * We always shutdown unnaturally unless when its meant to happen we change this to true.
     */
    private static boolean shutdownNaturally = false;

    /**
     * Private, non-instanceable class.
     */
    private HaxJor() {
        throw new UnsupportedOperationException("Class may not be instanced.");
    }

    /**
     * Infinity.
     */
    public static void main(String... args) throws NativeHookException {
        //debug initialization period.
        long startNano = System.nanoTime();

        LOGGER.info("HaxJor by Major.");
        LOGGER.info("The usage of this software is on your own responsibility!\n");

        LOGGER.info("Preparing JNativeHook...");
        ConsoleHandler var5 = new ConsoleHandler();
        var5.setFormatter(new HaxJorUtility.LogFormatter());
        var5.setLevel(Level.OFF);
        JNATIVE_LOGGER.addHandler(var5);
        JNATIVE_LOGGER.setUseParentHandlers(false);

        LOGGER.info("Registering native hook...");
        GlobalScreen.registerNativeHook();

        LOGGER.info("Hooking native listeners...");
        GlobalScreen.addNativeKeyListener(new KeyboardInputListener());

        LOGGER.info("Loading scripts...");
        loadScripts();

        LOGGER.info("Hooking shutdown event...");
        final Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //what happens on shutdown
            shutdown(shutdownNaturally);
            try {
                //wait for the working threads to finalize (finish!)
                currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        //clear up some memory
        System.gc();

        //denote that the script has finished initializing.
        LOGGER.info("HaxJor is now running (Took: " + elapsedMs(startNano) + "ms). Use !help for a list of commands.");

        //command system
        final Scanner input = new Scanner(System.in);
        //TODO a command parser to allow complicated commands
        while (input.hasNextLine()) {
            command(input.nextLine());
        }
    }

    private static void command(String input) {
        if (!input.startsWith("!")) {
            LOGGER.info("Please enter a command. Do !help for a list of commands.");
        } else {
            String command = input.substring(1);
            switch (command.toLowerCase()) {
                case "exit":
                case "close":
                case "stop":
                case "shutdown":
                    shutdownNaturally = true;
                    System.exit(0);
                    break;
                case "gc":
                    System.gc();
                    break;
                case "help":
                case "?":
                    System.out.println("Commands: exit (or shutdown, close, stop). gc. help (or ?)");
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }

    //are we currently running any script?
    public volatile static boolean firingEvents;

    //queue of pending scripts.
    private static final Queue<HaxJorScript> scriptQueue = new ArrayDeque<>();

    //attempt to run the script, and then handle the queue.
    //this must run synchronously, since we might reach a deadlock due to using recursive calling.
    public synchronized static void runScript(HaxJorScript script) {
        //are we running a script atm?
        if (script == null)
            throw new NullPointerException("script is null.");

        //are we currently doing any script-related action?
        if (firingEvents) {
            if (SCRIPT_QUEUEING) {
                debug("Queued action instead: " + script.getClass().getSimpleName());
                scriptQueue.add(script);
            }
            return;
        }

        //is the script enabled?
        if (!script.enabled()) {
            LOGGER.info("Script is disabled.");
            return;
        }

        //is this a keyboard script? and if so, are they disabled?
        if (script instanceof KeyboardJNativeListener) {
            if (!ToggleKeyboardHaxJorScript.toggle.get()) {
                debug("Keyboard scripts are disabled.");
                return;
            }
        }

        //denote that we're executing a script
        firingEvents = true;

        //execute the script
        script.execute();

        //denote that the action is done
        firingEvents = false;

        //proceed the script queue if enabled.
        if (SCRIPT_QUEUEING && !scriptQueue.isEmpty()) {
            runScript(scriptQueue.poll());
            debug("Running script from queue...");
        }
    }

    /**
     * The necessary actions to ensure safe shutdown of the program.
     * Must unregister all native hooks.
     *
     * @param natural did the program shutdown in a natural way or due to a system failure.
     */
    public static void shutdown(boolean natural) {
        try {
            LOGGER.info("Shutting down Haxjor... (behavior: " + (natural ? "natural" : "unnatural") + ")");

            //what happens when the program unnaturally shutdowns (i.e exception)
            if (!natural) {

            }
            //TODO save all necessary data

            //handle jnative unregistering.
            GlobalScreen.removeNativeKeyListener(KEYBOARD_INPUT_LISTENER);
            GlobalScreen.unregisterNativeHook();

            System.exit(0);
        } catch (Exception e) {
            //write to file.
            e.printStackTrace();
        }
    }

    //load scripts using reflections.
    private static void loadScripts() {
        final Reflections reflections = new Reflections(SCRIPTS_IMPL_DIRECTORY.toString());
        //the scripts
        final Set<Class<? extends HaxJorScript>> scripts = reflections.getSubTypesOf(HaxJorScript.class);
        //scripts that has settings
        final Set<Class<?>> scriptSettings = reflections.getTypesAnnotatedWith(HaxJorScriptSettings.SettingsFile.class);

        LOGGER.info("" + scripts.size() + " scripts are about to be loaded...");

        int success = 0;
        long startNano = System.nanoTime();
        for (Class<? extends HaxJorScript> script : scripts) {
            try {
                //create a new instance of the script
                HaxJorScript haxJorScript = script.getDeclaredConstructor().newInstance();
                //build settings if required
                if (scriptSettings.contains(script)) {
                    ((HaxJorScriptSettings) haxJorScript).build(script.getAnnotation(HaxJorScriptSettings.SettingsFile.class).file());
                }
                //initialize the script
                haxJorScript.initialize();
                //if a keyboard script, cache the indicator linked to this script.
                if (haxJorScript instanceof KeyboardJNativeListener) {
                    //whats null means that we don't want to use it atm.
                    if (((KeyboardJNativeListener) haxJorScript).indicator() != null) {
                        Keyboard.KEYBOARD_SCRIPTS.put(((KeyboardJNativeListener) haxJorScript).indicator(), haxJorScript);
                    }
                }
                //cache the script
                SCRIPTS.add(haxJorScript);
                //count successful scripts loaded
                success++;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Successfully loaded " + success + "/" + scripts.size() + " scripts in " + elapsedMs(startNano) + "ms.");
    }

}
