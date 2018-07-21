package major.haxjor.script.impl.avatar;

/**
 * Avatar changing speed in milliseconds
 * @author Major
 */
public enum AvatarSpeed {
    SUPER_FAST(25),
    FAST(50),
    MEDIUM(100),
    SLOW(200),
    VERY_SLOW(350);

    AvatarSpeed(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    private final int milliseconds;

    public final int getMilliseconds() {
        return milliseconds;
    }
}