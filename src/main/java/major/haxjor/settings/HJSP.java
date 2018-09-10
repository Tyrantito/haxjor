package major.haxjor.settings;

import major.haxjor.HaxJorSettings;
import major.haxjor.script.impl.avatar.AvatarSpeed;
import major.haxjor.settings.exception.HJSPFigureException;
import major.haxjor.settings.exception.HJSPNonInitializedFieldException;
import major.haxjor.settings.exception.HJSPSyntaxException;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static major.haxjor.HaxJorUtility.debug;

/**
 * TODO Atm fields are non-case sensitive.
 * Custom settings system that parses fields completely reflectively.
 * Syntax goes as easy as this:
 * <p>
 * fieldName = someValue
 * </p>
 * <p>
 * #A single commented out line
 * fieldName = someValue #this also work
 * ## a comment block...
 * lets explain some stuff about the next line
 * this is lovely!
 * ##
 * anotherFieldName = anotherSomeValue
 * </p>
 *
 * <p>
 * Furthermore, tables are now supported, so you can have as many tables as you want with the same namings in one file.
 * </p>
 * <p>
 * <keyboard>
 * <write>
 * <evenMore> #yup, a table inside a table inside a table :)
 * someString = "else" #A string
 * someChar = 'a' #char
 * someInt = 100 #simply a number.
 * someLong = 5000000000000L #L defines long
 * someDouble = 5.55D #D defines double
 * someFloat =  0.00004F #F defines float
 * someBoolean = false #true or false
 * </evenMore>
 * toggle = false; #is write function toggled?
 * </write>
 * <read>
 * toggle = true; #is read function toggled?
 * </read>
 * toggle=true #is keyboard toggled?
 * </keyboard>
 * </p>
 *
 * <p>
 * Currently supported types: All primitives (except short & byte, don't see it any necessary but simple to add) & Enums.
 * </p>
 * <p>
 * <p>
 * TODO docs. bwahh
 *
 * @author Major
 */
@SuppressWarnings("unchecked")
public final class HJSP {

    /**
     * The file extension for a setting file.
     */
    public static final String FILE_EXTENSION = ".haxjor";

    /**
     * The path where all script setting files are saved to.
     */
    public static final Path SCRIPT_BASE_PATH = Paths.get(".", "data", "script_settings");

    public static void main(String[] args) {
        long start = System.nanoTime();

        HJSP hjsp = build("./settings.haxjor").acceptEmptyValues().parse();


        System.out.println("Took to build: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
        System.out.println(hjsp.get("general.authors") + " authors.");
        System.out.println(hjsp.get("config.avatarspeed", AvatarSpeed.class) + " blablabla speed");
        start = System.nanoTime();
//        HJSP table = hjsp.getTable("keyboard.write.evenMore");

        //now all fields are called from the table "keyboard.write.evenMore"
//        int i = table.getInteger("someint");
//        long l = table.getLong("somelong");
//        double d = table.getDouble("somedouble");
//        float f = table.getFloat("somefloat");
//        boolean b = table.getBoolean("someboolean");

//        System.out.printf("i=%d, l=%d, d=" + d + ", f=" + f + ", b=%s\n", i, l, b);
        //take in mind that sout operations take  time too so benchmark might not be as accurate. (rather faster)
//        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    /**
     * The cache of all settings instances,
     * whereas the file path returns the applicable {@link HJSP} file.
     */
    private static final Map<String, HJSP> CACHED_SETTINGS = new ConcurrentHashMap<>();

    /**
     * Build a new instance by the setting file path and cache it.
     * If an <code>settingPath</code> already exists, but either not linked to a setting parser or rather have a null value,
     * we will create a new instance and return it.
     *
     * @param settingPath the setting file path, also used as the key for grabbing cached values.
     * @return the new instance of this setting parser.
     * @see Map#computeIfAbsent(Object, Function)   for relocating the instance
     */
    public static HJSP build(String settingPath) {
        if (!settingPath.endsWith(FILE_EXTENSION)) {
            throw new IllegalArgumentException("Setting path must be a \"" + FILE_EXTENSION + "\" file-type.");
        }
        File settingFile = new File(settingPath);
        if (CACHED_SETTINGS.containsKey(settingPath)) {
            return CACHED_SETTINGS.computeIfAbsent(settingPath, ignore -> new HJSP(settingFile));
        }
        HJSP hjsp = new HJSP(settingFile);
        CACHED_SETTINGS.put(settingPath, hjsp);
        return hjsp;
    }

    /**
     * Build and then instantly parse the file.
     * Note: One shouldn't use this if they aim to use {@link #acceptEmptyValues()} or etceteras,
     * since these do only take effect before using {@link #parse()}
     *
     * @param settingPath the setting file path, also used as the key for grabbing cached values.
     * @return the new instance of this setting parser.
     */
    public static HJSP buildAndParse(String settingPath) {
        return build(settingPath).parse();
    }

    /**
     * Attempts to peek into the {@link #CACHED_SETTINGS} in order to find an existing instance
     * of a {@link HJSP}. If absent, create a new instance and cache it.
     */
    public static HJSP of(String fileName) {
        return CACHED_SETTINGS.computeIfAbsent(fileName, k -> new HJSP(new File(fileName)));
    }

    /**
     * Create a new instance of a setting parser, with a default {@link HJSPTable}.
     * Associates the setting file with the instance.
     *
     * @param settingFile the file that the settings are stored into.
     */
    private HJSP(File settingFile) {
        this(settingFile, new HJSPTable("root"));
    }

    /**
     * Creates a new instance of a setting parser, with a given {@link HJSPTable}.
     * This is originally used when grabbing a table of an existing settting parser,
     * whereas the original instance is being {@link #copy(HJSPTable)} but the table is overridden with one of its child tables (In most cases).
     * This practice allows a linked-node system design. Example:
     *
     * <p>
     * //the main, first table.
     * HJSP root = new HJSP("./settings.haxjor");
     * <p>
     * //a child table inside #root
     * HJSP child = root.getTable("child");
     * <p>
     * //the child's first parent (the very first parent table, not the previous parent!)
     * HJSP childsRoot = child.root.getFirstParent();
     * <p>
     * //the child's root is obviously root.
     * assert childsRoot.equals(root);
     * </p>
     *
     * @param settingFile the setting file instance, would help us with {@link #parse()} the file
     * @param root        the root table for this instance. Usually, by default, we're given "root" as the root table.
     */
    private HJSP(File settingFile, HJSPTable root) {
        this.root = root;
        this.settingFile = settingFile;
    }

    /**
     * The root parent of this instance.
     * This does not mean that it is the main root (the first parent) of all tables,
     * but rather the root of the specific instance, which makes sense as {@link #getTable(String)} is used
     * as an inherited child-table off the root, which makes it become the root table, allowing shorten calls.
     */
    private HJSPTable root;

    /**
     * The setting file, which is used to {@link #parse()} the data of settings.
     */
    private final File settingFile;

    //can values be empty on initialization?
    /**
     * Indicates whether values of fields can be empty upon initialization, and assigned later.
     * However, do <b>NOT</b> call {@link HJSPObject#get()} when value isn't initialized since
     * you will receive a {@link HJSPNonInitializedFieldException} saying "Field has no value yet".
     */
    private boolean acceptEmptyValues;

    /**
     * TODO
     * Indicates that the values of fields are immutable and can not be changed after being initialized.
     */
    private boolean immutableValues;

    /**
     * Should we allow returning null values, i.e when field is not found, or rather throw an exception?
     */
    private boolean returnNullValues;


    public final HJSP acceptEmptyValues() {
        this.acceptEmptyValues = true;
        return this;
    }

    public final HJSP returnNullValues() {
        this.immutableValues = true;
        return this;
    }

    public final HJSP immutableValues() {
        this.returnNullValues = true;
        return this;
    }
    /**
     * Grabs the value of the given <code>field</code>.
     * This method knows how to figure out the path to the table throughout the given
     * routine, however, <code>field</code> could be passed as solely the field without
     * any tables to look into, which the method knows how to resolve and find it accordingly.
     * <p>
     * Once the appropriate table is found, we continue to find out if the <code>field</code>
     * exists in the {@link HJSPTable#fields}, and if it is, but not {@link HJSPObject#init)},
     * it will initialize it, cache the value and then return the value of the given field.
     *
     * @param field the path to the field. I.E "keyboard.write.toggle" whereas "toggle" is the field, and "keyboard.write" is the path.
     * @param type  the class type that the value is suppose to be created as.
     * @param <T>   the generic class type that the value is returned as.
     * @return the value of the field
     * @throws HJSPFigureException the table has no fields.
     */
    public <T> T get(String field, Class type) {
        HJSPTable workingTable = figureFieldInTable(field);
        if (workingTable.fields.isEmpty()) {
            throw new HJSPFigureException("Table (" + workingTable.name + ") has no fields");
        }
        final String targetField = getTargetField(field);
        HJSPObject targetObject = workingTable.fields.get(targetField);

        //should throw exception or null?
        if (targetObject == null) {
            throw new HJSPFigureException("Field: " + targetField + " couldn't be found in file: " + settingFile.getName());
        }

        if (!targetObject.init) {
            figureAndCache(workingTable, targetField, type);
        }
        return (T) targetObject.get();
    }


    /**
     * Grabs the value of the given <code>field</code>.
     * However, as this method might seem to be similar to {@link #get(String, Class)},
     * the major difference is that this method do not use {@link #figureAndCache(String, Class)}
     * so it doesn't try to parse the field if no value assigned to it, the reason for that
     * is that we use pre-parsing for primitive fields, such as <code>String</code> or a <code>Integer</code>,
     * since we can parse it easily, this process saves us time on the future so we can use this method
     * to easily grab these kind of field's values. Despite that, we can not use this method to call
     * for non-primitive objects, such as an Enum or a Class, the reason for that is since we use reflective
     * we need to know what kind of object we are calling to, which isn't provided here but rather through
     * the method {@link #get(String, Class)}, whom allows you to get field's values that are reflective based.
     * If you'll use this method to try getting fields that are reflective based, you'll most likely receive
     * an unresponsive exception such as {@link HJSPNonInitializedFieldException} exception, which as it might
     * seem to be unnatural exception, the field is actually never initialized because we wouldn't know how to parse it.
     *
     * @param field the path to the field. I.E "keyboard.write.toggle" whereas "toggle" is the field, and "keyboard.write" is the path.
     * @param <T>   the generic class type that the value is returned as.
     * @return the value of the field
     * @throws NullPointerException the table has no fields.
     */
    private <T> T get(String field) {
        HJSPTable workingTable = figureFieldInTable(field);
        final String targetField = getTargetField(field);
        if (workingTable.fields.containsKey(targetField)) {
            return (T) workingTable.fields.get(targetField).get();
        }
        throw new NullPointerException("No such field \"" + targetField + "\" on table " + workingTable.name + ".");
    }

    /**
     * Returns the accepted target field given by the path.
     * Basically, it returns the last argument of the path.
     *
     * @param field the path to the field
     * @return the target field
     */
    private String getTargetField(String field) {
        final String[] tables = field.split("\\.");
        return tables[tables.length - 1].toLowerCase();//all fields are non-case sensitive
    }

    //here we create the object and cache. (TODO can be void? // should probably not change since we might add support for instancing classes)
    private <T> T figureAndCache(String fieldName, Class type) {
        return figureAndCache(root, fieldName, type);
    }

    private <T> T figureAndCache(HJSPTable table, String fieldName, Class type) {
        //we get the hjspobject by the field
        HJSPObject hjspObject = table.fields.get(fieldName);
        try {
            if (type.isEnum()) {
                Field field = type.getField(hjspObject.data);
                hjspObject.set(Enum.valueOf((Class<Enum>) field.getType(), hjspObject.data));
                return (T) hjspObject.get();
            } else {
                throw new HJSPFigureException("Type is not supported. Please suggest or create your own.");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //probably won't happen
        throw new NullPointerException("No such field found.");
    }

    //if table = false then look for table that contains the field
    private final HJSPTable figureTable(String field, boolean table) {
        //if we're not trying to get a table but rather a field
        if (!field.contains(".")) {
            return root;
        } else {
            HJSPTable workingTable = root;
            String[] tables = field.split("\\.");
            final String targetTable = tables[tables.length - (table ? 1 : 2)];
            //find the path to the targetTable by the tables.
            return findTable(workingTable, 0, targetTable, tables);
        }
    }

    private final HJSPTable findTable(HJSPTable workingTable, int workingIndex, final String targetTable, final String[] tables) {
        for (HJSPTable wtChild : workingTable.getChilds()) {
            //keep getting the next root
            if (wtChild.name.equalsIgnoreCase(targetTable)) {
                //we found it.
                workingTable = wtChild;
                return workingTable;
            } else if (wtChild.name.equalsIgnoreCase(tables[workingIndex])) {
                workingIndex++;
                workingTable = wtChild; //set the working table into the next path
                return findTable(workingTable, workingIndex, targetTable, tables);
            }
        }
        throw new HJSPFigureException("No such table found: " + Arrays.toString(Arrays.stream(tables).limit(workingIndex + 1).collect(Collectors.toList()).toArray(new String[workingIndex])));
    }


    private final HJSPTable figureFieldInTable(String field) {
        return figureTable(field, false);
    }

    private final HJSPTable figureTable(String field) {
        return figureTable(field, true);
    }

    //copy with a new root
    public final HJSP getTable(String table) {
        return copy(figureTable(table));
    }

    public final Boolean getBoolean(String field) {
        return get(field);
    }

    public final String getString(String field) {
        return get(field);
    }

    public final int getInteger(String field) {
        return get(field);
    }

    public final double getDouble(String field) {
        return get(field);
    }

    public final long getLong(String field) {
        return get(field);
    }

    public final float getFloat(String field) {
        return get(field);
    }

    public final char getChar(String field) {
        return get(field);
    }

    /**
     * Copy with a new root
     */
    public final HJSP copy(HJSPTable root) {
        HJSP newCopy = new HJSP(settingFile, root);
        if (acceptEmptyValues) {
            newCopy.acceptEmptyValues();
        }
        return newCopy;
    }

    /**
     * Parse the file and check for syntax issues.
     */
    public HJSP parse() {
        try (BufferedReader reader = new BufferedReader(new FileReader(settingFile))) {
            StringBuilder whatWeParsed = new StringBuilder();
            String line;
            boolean isLongComment = false; // if we do "##" we have a multiple line of comment.
            int currentLine = 0;//current line used for debugging errors
            HJSPTable workingTable = root; //what is the table we're dealing with at the moment
            while ((line = reader.readLine()) != null) {
                currentLine++;
                //remove spaces
                line = line.replaceAll(" ", "");
                //just a space line
                if (line.isEmpty()) {
                    continue;
                }
                { //comment handling
                    //this line is already apart of the block comment
                    if (isLongComment) {
                        if (line.endsWith("##")) {
                            isLongComment = false; //end of comment
                            //if there's still more chars after the end of comment then keep on reading the line.
                            if (line.substring(line.lastIndexOf("##") + 2).isEmpty()) {
                                continue;
                            } else {
                                //as the line continues after the comment we can keep on reading it
                                line = line.substring(line.lastIndexOf("##") + 2);
                            }
                            //if we're already commenting out but we reach the end in the mid of the line
                        } else if (line.contains("##")) {
                            line = line.substring(line.lastIndexOf("##") + 2);
                            isLongComment = false;
                        } else {
                            continue; // we will skip this line as its apart of the comment
                        }
                    }
                    if (line.startsWith("##")) { // long comment that we start.
                        //what if we also end it on the same line?
                        isLongComment = true;
                        if (!line.endsWith("##")) {
                            if (line.contains("##")) {
                                isLongComment = false; //end of comment
                                //if there's still more chars after the end of comment then keep on reading the line.
                                if (line.substring(line.lastIndexOf("##") + 2).isEmpty()) {
                                    continue;
                                } else {
                                    //as the line continues after the comment we can keep on reading it
                                    line = line.substring(line.lastIndexOf("##") + 2);
                                }
                            } else {
                                continue; // we will skip this line as its apart of the comment
                            }
                        } else {
                            isLongComment = false;
                            continue;
                        }
                    } else if (line.startsWith("#")) { //comment
                        continue;
                    }
                    //comment starts from mid
                    if (line.contains("#")) {
                        //if line has a comment, read until the part of the line
                        line = line.substring(0, line.indexOf("#"));
                    } else if (line.contains("##")) {
                        //TODO this might be bugged line. indexOf("##")
                        line = line.substring(0, line.indexOf("##"));
                        isLongComment = true;
                    }
                }
                //we make a relation. this means that fields can be linked to a relation table, and thus allow multi settings on a single file
                if (line.contains("</")) {
                    final String tableName = line.substring(line.indexOf("/") + 1, line.lastIndexOf(">"));
                    if (tableName.contains("<") || tableName.contains(">") || tableName.contains("/")) {
                        throw new HJSPSyntaxException("End-Table can not have \"<, >, /\" in it's name.", currentLine);
                    }
                    //we must close the working table before closing any other table
                    if (!tableName.equalsIgnoreCase(workingTable.name)) {
                        //we're closing the wrong one
                        throw new HJSPSyntaxException("Wrong table closing. (Attempt: " + tableName + ", Found:  " + workingTable.name + ")", currentLine);
                    }
                    //now we return to work on the prev table
                    workingTable = workingTable.parent;
                    //we finished ending this table.
                    continue;
                } else if (line.startsWith("<") && line.contains(">")) {
                    final String tableName = line.substring(line.indexOf("<") + 1, line.lastIndexOf(">"));
                    if (tableName.contains("<") || tableName.contains(">")) {
                        throw new HJSPSyntaxException("Table can not have <, > in it's name.", currentLine);
                    }
                    //this is a new table, we need to read its fields and its childs.
                    HJSPTable newTable = new HJSPTable(tableName);
                    //link this table to its parent
                    newTable.setParent(workingTable);
                    //here we link the new table to its parent table
                    workingTable.addChild(newTable);
                    //the table we're writing to is the new one now
                    workingTable = newTable;
                    //we made a table and linked to it, so from now on write to the table.
                    continue;
                    //end of table
                } else {
                    //no childs. just create the fields normally
                    root.fields = new HashMap<>();
                }
                //line must contain an equal mark
                if (!line.contains("=")) {
                    throw new HJSPSyntaxException("Missing \"=\" mark", currentLine);
                }
                //line has no value. we can decide whether to allow this or not.
                if (!acceptEmptyValues && line.substring(line.indexOf("=") + 1).isEmpty()) {
                    throw new HJSPSyntaxException("No value to field (Optional #acceptEmptyValues)", currentLine);
                }
                //cache the field name.
                final String field = line.substring(0, line.indexOf("=")).toLowerCase();

                if (field.contains(".")) {
                    throw new HJSPSyntaxException("Dots '.' are forbidden in fields!", currentLine);
                }
                String value = line.substring(line.indexOf("=") + 1);
                //proper string
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    //reg string
                    value = value.substring(value.indexOf("\""), value.lastIndexOf("\"") + 1);
                } else if (value.startsWith("\"") && !value.endsWith("\"")) {
                    //broken string
                    throw new HJSPSyntaxException("Excepted end of String but found \"" + value.indexOf(value.length() - 1) + "\" instead.", currentLine);
                } else if (value.startsWith("'") && value.endsWith("'")) {
                    //reg char
                    value = value.substring(value.indexOf("'"), value.lastIndexOf("'") + 1);
                } else if (value.startsWith("'") && !value.endsWith("'")) {
                    //broken char
                    throw new HJSPSyntaxException("Excepted end of Character but found \"" + value.indexOf(value.length() - 1) + "\" instead.", currentLine);
                    //if its not a boolean
                } else if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    //any object that isn't a numeric value starts with $
                    if (!(value.startsWith("$"))) {
                        //non numberic
                        if (!NumberUtils.isCreatable(value)) {
                            //illegal char. TODO find out how to tell which char is the mismatching one.
                            if (!value.matches("[a-zA-Z0-9$]*")) {
                                throw new HJSPSyntaxException("Illegal syntax found: \"" + value + "\".", currentLine);
                            }
                            throw new HJSPSyntaxException("Excepted numeric value but found \"" + value + "\" instead\nHint: If you're excepting an object, put \"$\" beforehand.", currentLine);
                        }
                    } else {
                        //true or false does not require $ at the start
                        if (value.substring(value.indexOf("$") + 1).toLowerCase().contains("true") || value.substring(value.indexOf("$") + 1).toLowerCase().contains("false")) {
                            throw new HJSPSyntaxException("True / False statements should not have a \"$\" sign beforehand.", currentLine);
                        }
                    }
                }
                //add to the working table
                workingTable.fields.put(field, new HJSPObject(currentLine, value));

                //the only critical time we use the direct setting to check if on debug mode and to append the string.
                if (HaxJorSettings.DEBUG_MESSAGES) {
                    whatWeParsed.append(workingTable.name).append(".").append(line).append("\n");
                }
            }
            debug("We parsed:");
            debug(whatWeParsed.toString());
        } catch (FileNotFoundException f) {
            throw new IllegalStateException("No file found to parse.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}