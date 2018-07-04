package major.haxjor.jnative.keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

/**
 * Controls keyboard pressing actions only.
 *
 * @author Major (Compatibility adjustments)
 * @author Style (C&P suggestion & function)
 *
 * @see <a href="https://stackoverflow.com/a/11779211">Credits for original author</a>
 */
public class Keyboard {

    private static final Robot ROBOT = create();

    /**
     * The character that acts as a space-bar in Haxball avatar. (Alt + 255)
     */
    public static final char HAXBALL_SPACE = ' ';

    /**
     * Static method to create a {@link Robot} instance while
     * the {@link AWTException} is being manually handled.
     */
    private static Robot create() {
        try {
            return new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Robot couldn't be initialized.");
    }

    /**
     * Type a sequence of characters.
     */
    public static void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    /**
     * Copy paste the chars.
     * <p>
     * Note: this keeps a backup of your current clipboard, overwrite it, and then reverse it.
     *
     * @param chars
     */
    public static synchronized void copyPaste(char... chars) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(new String(chars));
        clipboard.setContents(stringSelection, stringSelection);
        ROBOT.keyPress(KeyEvent.VK_CONTROL);
        ROBOT.keyPress(KeyEvent.VK_V);
        ROBOT.keyRelease(KeyEvent.VK_V);
        ROBOT.keyRelease(KeyEvent.VK_CONTROL);

    }

    //shortcut to 'enter' key press
    public static void enter() {
        type('\n');
    }

    public static void type(char character) {
        switch (character) {
            case 'a':
                doType(VK_A);
                break;
            case 'b':
                doType(VK_B);
                break;
            case 'c':
                doType(VK_C);
                break;
            case 'd':
                doType(VK_D);
                break;
            case 'e':
                doType(VK_E);
                break;
            case 'f':
                doType(VK_F);
                break;
            case 'g':
                doType(VK_G);
                break;
            case 'h':
                doType(VK_H);
                break;
            case 'i':
                doType(VK_I);
                break;
            case 'j':
                doType(VK_J);
                break;
            case 'k':
                doType(VK_K);
                break;
            case 'l':
                doType(VK_L);
                break;
            case 'm':
                doType(VK_M);
                break;
            case 'n':
                doType(VK_N);
                break;
            case 'o':
                doType(VK_O);
                break;
            case 'p':
                doType(VK_P);
                break;
            case 'q':
                doType(VK_Q);
                break;
            case 'r':
                doType(VK_R);
                break;
            case 's':
                doType(VK_S);
                break;
            case 't':
                doType(VK_T);
                break;
            case 'u':
                doType(VK_U);
                break;
            case 'v':
                doType(VK_V);
                break;
            case 'w':
                doType(VK_W);
                break;
            case 'x':
                doType(VK_X);
                break;
            case 'y':
                doType(VK_Y);
                break;
            case 'z':
                doType(VK_Z);
                break;
            case 'A':
                doType(VK_SHIFT, VK_A);
                break;
            case 'B':
                doType(VK_SHIFT, VK_B);
                break;
            case 'C':
                doType(VK_SHIFT, VK_C);
                break;
            case 'D':
                doType(VK_SHIFT, VK_D);
                break;
            case 'E':
                doType(VK_SHIFT, VK_E);
                break;
            case 'F':
                doType(VK_SHIFT, VK_F);
                break;
            case 'G':
                doType(VK_SHIFT, VK_G);
                break;
            case 'H':
                doType(VK_SHIFT, VK_H);
                break;
            case 'I':
                doType(VK_SHIFT, VK_I);
                break;
            case 'J':
                doType(VK_SHIFT, VK_J);
                break;
            case 'K':
                doType(VK_SHIFT, VK_K);
                break;
            case 'L':
                doType(VK_SHIFT, VK_L);
                break;
            case 'M':
                doType(VK_SHIFT, VK_M);
                break;
            case 'N':
                doType(VK_SHIFT, VK_N);
                break;
            case 'O':
                doType(VK_SHIFT, VK_O);
                break;
            case 'P':
                doType(VK_SHIFT, VK_P);
                break;
            case 'Q':
                doType(VK_SHIFT, VK_Q);
                break;
            case 'R':
                doType(VK_SHIFT, VK_R);
                break;
            case 'S':
                doType(VK_SHIFT, VK_S);
                break;
            case 'T':
                doType(VK_SHIFT, VK_T);
                break;
            case 'U':
                doType(VK_SHIFT, VK_U);
                break;
            case 'V':
                doType(VK_SHIFT, VK_V);
                break;
            case 'W':
                doType(VK_SHIFT, VK_W);
                break;
            case 'X':
                doType(VK_SHIFT, VK_X);
                break;
            case 'Y':
                doType(VK_SHIFT, VK_Y);
                break;
            case 'Z':
                doType(VK_SHIFT, VK_Z);
                break;
            case '`':
                doType(VK_BACK_QUOTE);
                break;
            case '0':
                doType(VK_0);
                break;
            case '1':
                doType(VK_1);
                break;
            case '2':
                doType(VK_2);
                break;
            case '3':
                doType(VK_3);
                break;
            case '4':
                doType(VK_4);
                break;
            case '5':
                doType(VK_5);
                break;
            case '6':
                doType(VK_6);
                break;
            case '7':
                doType(VK_7);
                break;
            case '8':
                doType(VK_8);
                break;
            case '9':
                doType(VK_9);
                break;
            case '-':
                doType(VK_MINUS);
                break;
            case '=':
                doType(VK_EQUALS);
                break;
            case '~':
                doType(VK_BACK_QUOTE);
                break;
            case '!':
                doType(VK_SHIFT, VK_1);
                break;
            case '@':
                doType(VK_SHIFT, VK_2);
                break;
            case '#':
                doType(VK_SHIFT, VK_3);
                break;
            case '$':
                doType(VK_SHIFT, VK_4);
                break;
            case '%':
                doType(VK_SHIFT, VK_5);
                break;
            case '^':
                doType(VK_SHIFT, VK_6);
                break;
            case '&':
                doType(VK_SHIFT, VK_7);
                break;
            case '*':
                doType(VK_SHIFT, VK_8);
                break;
            case '(':
                doType(VK_SHIFT, VK_9);
                break;
            case ')':
                doType(VK_SHIFT, VK_0);
                break;
            case '_':
                try {
                    doType(VK_SHIFT, VK_UNDERSCORE);
                } catch (Exception e) {
                    doType(VK_SHIFT, VK_MINUS);
                    //occur because some keyboards identify '_' by SHIFT - and not underscore.
                }
                break;
            case '+':
                doType(VK_SHIFT, VK_PLUS);
                break;
            case '\t':
                doType(VK_TAB);
                break;
            case '\n':
                doType(VK_ENTER);
                break;
            case '[':
                doType(VK_OPEN_BRACKET);
                break;
            case ']':
                doType(VK_CLOSE_BRACKET);
                break;
            case '\\':
                doType(VK_BACK_SLASH);
                break;
            case '{':
                doType(VK_SHIFT, VK_OPEN_BRACKET);
                break;
            case '}':
                doType(VK_SHIFT, VK_CLOSE_BRACKET);
                break;
            case '|':
                doType(VK_SHIFT, VK_BACK_SLASH);
                break;
            case ';':
                doType(VK_SEMICOLON);
                break;
            case ':':
                doType(VK_SHIFT, VK_SEMICOLON);
                break;
            case '\'':
                doType(VK_QUOTE);
                break;
            case '"':
                doType(VK_SHIFT, VK_QUOTEDBL);
                break;
            case ',':
                doType(VK_COMMA);
                break;
            case '<':
                doType(VK_SHIFT, VK_COMMA);
                break;
            case '.':
                doType(VK_PERIOD);
                break;
            case '>':
                doType(VK_SHIFT, VK_PERIOD);
                break;
            case '/':
                doType(VK_SLASH);
                break;
            case '?':
                doType(VK_SHIFT, VK_SLASH);
                break;
            case ' ':
                doType(VK_SPACE);
                break;
            case '\b':
                doType(VK_BACK_SPACE);
                break;
            default:
                throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private static void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    //synchronized blocks the whole class due to static reference. which is good because this can't run concurrently
    //since we only have 1 keyboard
    private static synchronized void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }
        ROBOT.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        ROBOT.keyRelease(keyCodes[offset]);
    }

}