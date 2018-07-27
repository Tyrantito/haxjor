package major.haxjor.script.impl.avatar;

/**
 * Avatar combination definitions.
 *
 * @author Major
 */
public enum AvatarCombination {
    SMILES(150,0,'☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻','☺', '☻'),
    EZPZ("_eZpZ_"),
    A("A");
    //the chars combination of this avatar
    private final char[] avatarCombination;
    //overwrites the default delay between char (depending on effect).
    private final int milliseconds;
    //how many ADDITIONAL times we should repeat this avatar for (we always run it once, and then *repeat times)
    private final int repeat;

    /**
     * Creates a default combination with no custom specifications.
     */
    AvatarCombination(char... avatarCombination) {
        this(0, avatarCombination);
    }

    /**
     * Convert string to chars array.
     */
    AvatarCombination(String avatarCombination) {
        this(avatarCombination.toCharArray());
    }

    AvatarCombination(int repeat, char... avatarCombination) {
        this(0, repeat, avatarCombination);
    }

    AvatarCombination(int milliseconds, int repeat, char... avatarCombination) {
        this.milliseconds = milliseconds;
        this.avatarCombination = avatarCombination;
        this.repeat = repeat;
    }

    public final char[] getChars() {
        return avatarCombination;
    }

    public final int getRepeat() {
        return repeat;
    }

    public final int getMilliseconds() {
        return milliseconds;
    }
}