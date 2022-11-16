package druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class DruidTest {
    public static void main(String[] args) {
        SQLExpr expr = SQLUtils.toSQLExpr("id=3", JdbcConstants.MYSQL);
        System.out.println(expr);

        System.out.println(A.B);
        System.out.println(A.name);

        for (A value : A.values()) {
            System.out.println("=== age: " + value.age + ", ===: " + value.ordinal() + ", ===: " + value.name());
        }

        char c = '\u200B';
        int a = c;
        System.out.println("c: = " + c);
        c = 0x1A;
        System.out.println(c);
        c = 26;
        System.out.println(c);
        System.out.println(0x7F);
        System.out.println(0xA0);
        System.out.println(0x1F);

        // HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;
        // System.out.println(0xF.1234Ep0);
    }

    enum A {
        C("ccc"),
        B;

        static String name = "xxx";

        String age;

        A(){
            this(null);
        }

        A(String age){
            this.age = age;
        }
    }

//    public Lexer(String input, boolean skipComment){
//    }
}
