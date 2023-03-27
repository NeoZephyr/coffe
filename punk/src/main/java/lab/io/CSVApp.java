package lab.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CSVApp {
    public static void main(String[] args) {
        parseByCommon();
    }

    private static void parseByCommon() {
        String filepath = "/Users/meilb/Documents/self/coffe/punk/src/main/resources/csv/failedFile.csv";
        try (
                FileInputStream inputStream = new FileInputStream(filepath);
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader().withAllowMissingColumnNames()
                    // .withSkipHeaderRecord(true)
                    // .withHeader()
                    .withDelimiter(',')
                    .withQuote('"')
                    .withEscape('\\')
                    .withRecordSeparator('\n');

            CSVParser parser = csvFormat.parse(reader);

            // BOM
            System.out.println(parser.getHeaderMap().containsKey("rowId"));
            System.out.println(parser.getHeaderMap().containsKey("\uFEFF" + "rowId"));

            for (CSVRecord next : parser) {
                System.out.printf("%s, %s, %s%n",
                        next.get("\uFEFF" + "rowId"),
                        next.get("identityType"),
                        next.get("failedReason"));

                System.out.println(next.get("\uFEFF" + "rowId").length());
                System.out.println(("\uFEFF" + "rowId").length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
