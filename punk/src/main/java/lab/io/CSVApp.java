package lab.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;

public class CSVApp {
    public static void main(String[] args) {
        parseByCommon();
    }

    private static void parseByCommon() {
        String filepath = "/Users/meilb/Documents/self/coffe/punk/src/main/resources/csv/multiline.csv";
        try (FileReader reader = new FileReader(filepath)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    // .withSkipHeaderRecord(true)
                    // .withHeader()
                    .withDelimiter(',')
                    .withQuote('"')
                    .withEscape('\\')
                    .withRecordSeparator('\n');
            CSVParser parser = csvFormat.parse(reader);

            for (CSVRecord next : parser) {
                System.out.printf("%s, %s, %s%n",
                        next.get("原始行ID"),
                        next.get("身份类型"),
                        next.get("失败原因"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
