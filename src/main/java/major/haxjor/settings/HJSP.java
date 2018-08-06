package major.haxjor.settings;

import major.haxjor.HaxJorSettings;
import major.haxjor.HaxJorUtility;
import major.haxjor.settings.exception.HJSPFigureException;
import major.haxjor.settings.exception.HJSPSyntaxException;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static major.haxjor.HaxJorUtility.debug;

/**
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
 * <p>
 * Furthermore, tables are now supported, so you can have as many tables as you want with the same namings in one file.
 *
 * <p>
 * <keyboard>
 *      <write>
 *          <evenMore> #yup, a table inside a table inside a table :)
 *              someString = "else" #A string
 *              someChar = 'a' #char
 *              someInt = 100 #simply a number.
 *              someLong = 5000000000000L #L defines long
 *              someDouble = 5.55D #D defines double
 *              someFloat =  0.00004F #F defines float
 *              someBoolean = false #true or false
 *          </evenMore>
 *          toggle = false; #is write function toggled?
 *      </write>
 *      <read>
 *          toggle = true; #is read function toggled?
 *      </read>
 *      toggle=true #is keyboard toggled?
 * </keyboard>
 * </p>
 *
 * <p>
 * Currently supported types: All primitives (except short & byte, don't see it any necessary but simple to add) & Enums.
 * </p>
 *
 * @author Major
 */
@SuppressWarnings("unchecked")
public final class HJSP {

    private static final String FILE_TYPE = ".haxjor";

    private static final Path SCRIPT_BASE_PATH = Paths.get(".", "data", "script_settings");

    public static void main(String[] args) {
        long start = System.nanoTime();
        HJSP hjsp = build("settings").acceptEmptyValues().parse();

        System.out.println("Took to build: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
        start = System.nanoTime();
        HJSP table = hjsp.getTable("keyboard.write.evenMore");

        //now all fields are called from the table "keyboard.write.evenMore"
        int i = table.getInteger("someint");
        long l = table.getLong("somelong");
        double d = table.getDouble("somedouble");
        float f = table.getFloat("somefloat");
        boolean b = table.getBoolean("someboolean");
//
        System.out.printf("i=%d, l=%d, d=" + d + ", f=" + f + ", b=%s\n", i, l, b);
        //take in mind that sout operations take  time too so benchmark might not be as accurate. (rather faster)
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    /**
     * Cache of settings
     */
    private static final Map<String, HJSP> CACHED_SETTINGS = new HashMap<>();

    /**
     * Build a setting and cache it.
     */
    public static HJSP build(String fileName) {
        if (CACHED_SETTINGS.containsKey(fileName)) {
            return CACHED_SETTINGS.get(fileName);
        }
        HJSP hjsp = new HJSP(fileName);
        CACHED_SETTINGS.put(fileName, hjsp);
        return hjsp;
    }

    /**
     * Get the settings of a file.
     */
    public static HJSP settings(String fileName) {
        if (!CACHED_SETTINGS.containsKey(fileName)) {
            return build(fileName);
        }
        return CACHED_SETTINGS.get(fileName);
    }


    private HJSP(String fileName) {
        this(fileName, new HJSPTable("root"));
    }

    private HJSP(String fileName, HJSPTable root) {
        this.root = root;
        this.fileName = fileName;
    }

    //the root table. doesn't mean that it can not have a parent table!
    private HJSPTable root;
    //keep the data of the fields
    private final String fileName;
    //can values be empty on initialization?
    private boolean acceptEmptyValues;

    /**
     * Should we allow initializing with empty values? this might cause issues when attempting to call #get methods
     */
    public final HJSP acceptEmptyValues() {
        this.acceptEmptyValues = true;
        return this;
    }

    public <T> T get(String field, Class type) {
        HJSPTable workingTable = figureFieldInTable(field);

        if (workingTable.fields.isEmpty()) {
            throw new HJSPFigureException("Table (" + workingTable.name + ") has no fields");
        }
        if (!workingTable.fields.get(field).init) {
            figureAndCache(field, type);
        }
        return (T) workingTable.fields.get(field).get();
    }

    //since we already premade primitive types, this function doesn't need to try re-parse them again and use existing values.
    private <T> T get(String field) {
        HJSPTable workingTable = figureFieldInTable(field);
        String[] tables = field.split("\\.");
        final String targetField = tables[tables.length - 1];
        if (workingTable.fields.containsKey(targetField)) {
            return (T) workingTable.fields.get(targetField).get();
        }
        throw new NullPointerException("No such field \"" + targetField + "\" on table " + workingTable.name + ".");
    }

    //here we create the object and cache. (TODO can be void? // should probably not change since we might add support for instancing classes)
    private <T> T figureAndCache(String fieldName, Class type) {
        //we get the hjspobject by the field
        HJSPObject hjspObject = root.fields.get(fieldName);
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
        HJSP newCopy = new HJSP(fileName, root);
        if (acceptEmptyValues) {
            newCopy.acceptEmptyValues();
        }
        return newCopy;
    }

    /**
     * Parse the file and check for syntax issues.
     */
    public HJSP parse() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCRIPT_BASE_PATH.resolve(fileName + FILE_TYPE).toFile()))) {
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
                    //# ENDS THE LINE!
                    if (isLongComment) {
                        if (line.endsWith("##")) {
                            isLongComment = false; //end of comment
                            //if there's still more chars after the end of comment then keep on reading the line.
                            if (!line.substring(line.lastIndexOf("#") + 1).isEmpty()) {
                                continue;
                            } else {
                                //as the line continues after the comment we can keep on reading it
                                line = line.substring(line.lastIndexOf("#") + 1);
                            }
                            //if we're already commenting out but we reach the end in the mid of the line
                        } else if (line.contains("##")) {
                            line = line.substring(line.lastIndexOf("#") + 1);
                            isLongComment = false;
                        } else {
                            continue; // we will skip this line as its apart of the comment
                        }
                    }
                    if (line.startsWith("##")) { // long comment that we start.
                        isLongComment = true;
                        continue;
                    } else if (line.startsWith("#")) { //comment
                        continue;
                    }
                    if (line.contains("#")) {
                        //if line has a comment, read until the part of the line
                        line = line.substring(0, line.indexOf("#"));
                    } else if (line.contains("##")) {
                        line = line.substring(0, line.indexOf("#"));
                        isLongComment = true;
                    }
                    if (isLongComment) {
                        if (line.endsWith("##")) {
                            isLongComment = false; //end of comment
                            //if there's still more chars after the end of comment then keep on reading the line.
                            if (!line.substring(line.lastIndexOf("#") + 1).isEmpty()) {
                                continue;
                            } else {
                                //as the line continues after the comment we can keep on reading it
                                line = line.substring(line.lastIndexOf("#") + 1);
                            }
                        } else {
                            continue; // we will skip this line as its apart of the comment
                        }
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
                } else if (line.replaceAll(" ", "").startsWith("<") && line.contains(">")) {
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
                    System.out.println();
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
                            throw new HJSPSyntaxException("Excepted numeric value but found \"" + value + "\" instead.", currentLine);
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