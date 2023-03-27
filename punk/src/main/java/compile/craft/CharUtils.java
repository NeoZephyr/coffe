package compile.craft;

public class CharUtils {

    private final static boolean[] blankFlags = new boolean[256];
    private final static boolean[] firstIdFlags = new boolean[256];
    private final static boolean[] restIdFlags = new boolean[256];

    public final static byte EOF = 0x1A;

    static {
        for (int i = 0; i <= 32; ++i) {
            blankFlags[i] = true;
        }

        blankFlags[EOF] = false;

        for (int i = 0x7F; i <= 0xA0; ++i) {
            blankFlags[i] = true;
        }
    }

    static {
        for (int i = 0; i < firstIdFlags.length; ++i) {
            if (i >= 'A' && i <= 'Z') {
                firstIdFlags[i] = true;
            } else if (i >= 'a' && i <= 'z') {
                firstIdFlags[i] = true;
            }
        }

        firstIdFlags['_'] = true;
    }

    static {
        for (int i = 0; i < restIdFlags.length; ++i) {
            if (i >= 'A' && i <= 'Z') {
                restIdFlags[i] = true;
            } else if (i >= 'a' && i <= 'z') {
                restIdFlags[i] = true;
            } else if (i >= '0' && i <= '9') {
                restIdFlags[i] = true;
            }
        }

        restIdFlags['$'] = true;
        restIdFlags['_'] = true;
    }

    // isAlphaNumeric
    public static boolean isBlank(char c) {
        return (c <= blankFlags.length && blankFlags[c]) || c == '　'; // Chinese space
    }

    public static boolean isFirstIdChar(char c) {
        if (c <= firstIdFlags.length) {
            return firstIdFlags[c];
        }

        return false;
    }

    public static boolean isIdChar(char c) {
        if (c <= restIdFlags.length) {
            return restIdFlags[c];
        }

        return false;
    }

    public static boolean isHex(char c) {
        return ((c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f') || (c >= '0' && c <= '9'));
    }

    public static boolean isBit(char c) {
        return c == '0' || c == '1';
    }
}
