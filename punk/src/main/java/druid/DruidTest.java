package druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;

public class DruidTest {
    public static void main(String[] args) {
        SQLExpr expr = SQLUtils.toSQLExpr("id=3", JdbcConstants.MYSQL);
        System.out.println(expr);
    }
}
