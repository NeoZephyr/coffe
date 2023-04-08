package compile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DruidTest {
    // char c = ' ';
    public static void main(String[] args) throws IOException {
        // SQLExpr expr = SQLUtils.toSQLExpr("id=3", JdbcConstants.MYSQL);
        // System.out.println(expr);

        FileReader reader = new FileReader("/Users/meilb/Documents/self/coffe/punk/test.txt");
        BufferedReader bufReader = new BufferedReader(reader);
        char[] buf = new char[100];
        int count = bufReader.read(buf);
        System.out.println(count);

        for (int i = 0; i < 20; ++i) {
            int d = buf[i];
            System.out.printf("int format: %d, char format: %c，%b%n", d, buf[i], buf[i] == '\n');
        }
    }
}
