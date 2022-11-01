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
    }

    enum A {
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
}
