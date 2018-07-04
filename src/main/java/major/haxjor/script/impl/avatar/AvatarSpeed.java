package major.haxjor.script.impl.avatar;

/**
 * Avatar changing speed in milliseconds
 * @author Major
 */
public enum AvatarSpeed {
    SUPER_FAST(5),
    FAST(125),
    MEUM(15),
    SLOW(20),
    VERY_SLOW(50);

    AvatarSpeed(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    private final int milliseconds;

    public final int getMilliseconds() {
        return milliseconds;
    }
}