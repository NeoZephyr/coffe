package compile;

import java.io.IOException;

public class DruidTest {
    // char c = ' ';
    public static void main(String[] args) throws IOException {
        // SQLExpr expr = SQLUtils.toSQLExpr("id=3", JdbcConstants.MYSQL);
        // System.out.println(expr);

        // CHAR_LITERAL:       '\'' (~['\\\r\n] | EscapeSequence) '\'';
        // EscapeSequence: '\\' 'u005c'? [btnfr"'\\]
        // EscapeSequence: '\\' 'u005c'? ([0-3]? [0-7])? [0-7]
        // EscapeSequence: '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit

        char c = '\b';
        char cc = '\uFFFF';

        int i = c;
        int ii = cc;

        String s = String.valueOf(c);
        System.out.println(c == cc);

        System.out.println(c);
        System.out.println(cc);
        System.out.println(ii);
        System.out.println(i);
        System.out.println(s);

        System.out.println('\u005c6');
        System.out.println('\uuuuuuuuuu1111');
        System.out.println('\uuu1111');
        System.out.println('\uuuuuuuuuuuuuuuuu1111');

        // '\\' 'u005c'? [btnfr"'\\]
    }
}
