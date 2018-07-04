package major.haxjor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.moandjiezana.toml.Toml;
import major.haxjor.jnative.keyboard.KeyboardInputListener;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.reflections.Reflections;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * HaxJor is a HaxBall specialized program that offers various of useful utilities
 * that may lead to an unfair advantage over the average user.
 * Some notable features that differs the program from the rest are:
 * Avatar script, Reliable Auto-clicker & Marcos and more...
 * While all of the features are completely modifiable, the program ensures to be lightweight and consume close
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
    public static final Logger JNATIVE_LOGGER = Logger.getLogger(GlobalScreen.class.getPackage().getName());

    /**
     * Some static settings for the system.
     */
    private static final Toml TOML_PARSER = new Toml().read(new File("./settings.toml"));

    //native listeners that are global for the program (these are the native listeners and not the script listeners!)
    private static final KeyboardInputListener KEYBOARD_INPUT_LISTENER = new KeyboardInputListener();

    /**
     * The set of scripts. //TODO doesn't have to be multiset
     */
    public static final Multiset<HaxJorScript> SCRIPTS = HashMultiset.create();

    /**
     * A cached map of scripts that implement a keyboard listener {@link KeyboardJNativeListener} and
     * that reacts to an unique character.
     */
    public static final Map<Character, HaxJorScript> KEYBOARD_SCRIPTS = new HashMap<>();

    /**
     * The path for scripts
     * TODO {@link java.nio.file.Path}
     */
    private static final String SCRIPTS_IMPL_DIRECTORY = "major.haxjor.script.impl";

    /**
     * The executor to concurrently handle scripts.
     */
    public static final ScheduledExecutorService SCRIPT_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    /**
     * Infinity.
     */
    public static void main(String... args) throws NativeHookException {
        LOGGER.info("HaxJor by Major.");
        LOGGER.info("The usage is up to your own responsibility!\n");

        LOGGER.info("Preparing JNativeHook");
        ConsoleHandler var5 = new ConsoleHandler();
        var5.setFormatter(new LogFormatter());
        var5.setLevel(Level.OFF);
        JNATIVE_LOGGER.addHandler(var5);
        JNATIVE_LOGGER.setUseParentHandlers(false);

        LOGGER.info("Registering native hook...");
        GlobalScreen.registerNativeHook();

        LOGGER.info("Hooking native listeners...");
        GlobalScreen.addNativeKeyListener(new KeyboardInputListener());

        LOGGER.info("Hooking shutdown event...");
        final Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //the abnormal behavior when the program is shutdown.
            shutdown(false);

            try {
                //wait for the working threads to finalize (finish!)
                currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        LOGGER.info("Loading utilities...");
        loadSettings();
        loadScripts();


        //command system
        final Scanner input = new Scanner(System.in);
        //TODO a command parser to allow complicated commands
        while (input.hasNextLine()) {
            String next = input.nextLine();
            if (!next.startsWith("!")) {
                LOGGER.info("Please enter a command. Do !help for a list of commands.");
            } else {
                System.out.println("Command entered: " + next);
            }
        }
    }

    public static void shutdown(boolean natural) {
        try {
            //what happens when the program unnaturally shutdowns (i.e exception)
            if (!natural) {

            }
            //TODO save all necessary data

            //handle jnative unregistering.
            GlobalScreen.removeNativeKeyListener(KEYBOARD_INPUT_LISTENER);
            GlobalScreen.unregisterNativeHook();

        } catch (Exception e) {
            //write to file.
            e.printStackTrace();
        }
    }

    private HaxJor() {
        throw new UnsupportedOperationException("Class may not be instanced.");
    }

    //TODO introduce settings
    private static void loadSettings() {

    }

    //load scripts using reflections.
    private static void loadScripts() {
        Set<Class<? extends HaxJorScript>> scripts = new Reflections(SCRIPTS_IMPL_DIRECTORY).getSubTypesOf(HaxJorScript.class);//.getSubTypesOf(HaxJorScript.class);
        LOGGER.info("" + scripts.size() + " are about to be loaded...");
        int success = 0;
        for (Class<? extends HaxJorScript> script : scripts) {
            try {
                //create a new instance of the script
                HaxJorScript haxJorScript = script.getDeclaredConstructor().newInstance();
                //initialize the script
                haxJorScript.initialize();
                //cache the script
                SCRIPTS.add(haxJorScript);
                //count successful scripts loaded
                success++;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Successfully loaded " + success + "/" + scripts.size() + " scripts.");
    }

    //imported: NativeHookDemo.class
    private static final class LogFormatter extends Formatter {
        private LogFormatter() {
        }

        public String format(LogRecord var1) {
            StringBuilder var2 = new StringBuilder();
            var2.append(new Date(var1.getMillis())).append(" ").append(var1.getLevel().getLocalizedName()).append(":\t").append(this.formatMessage(var1));
            if (var1.getThrown() != null) {
                try {
                    StringWriter var3 = new StringWriter();
                    PrintWriter var4 = new PrintWriter(var3);
                    var1.getThrown().printStackTrace(var4);
                    var4.close();
                    var2.append(var3.toString());
                    var3.close();
                } catch (Exception var5) {
                }
            }

            return var2.toString();
        }
    }

}
