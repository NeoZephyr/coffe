package compile.craft.frontend;

public class CharUtils {

    public final static byte EOF = 0x1A;

    public static boolean isBlank(char c) {
        if (c == EOF) {
            return false;
        }

        // Chinese space
        if (c == '　') {
            return true;
        }

        // 控制字符 + 空格字符
        if (c <= 32) {
            return true;
        }

        // 控制字符和不可打印字符
        return c >= 0x7F && c <= 0xA0;
    }

    public static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_') ||
                (c >= 128);
    }

    public static boolean isIdentifierChar(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9') ||
                (c == '_') ||
                (c == '$') ||
                (c >= 128);
    }

    public static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean isHex(char c) {
        return ((c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f') || (c >= '0' && c <= '9'));
    }

    public static boolean isOct(char c) {
        return (c >= '0' && c <= '7');
    }

    public static boolean isBit(char c) {
        return c == '0' || c == '1';
    }

    public static boolean isSingleQuotes(char c) {
        return c == '\'';
    }

    public static boolean isQuotes(char c) {
        return c == '"';
    }
}
