package com.pain.rock.easy.parser;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

public class EasyParserTest {

    @Test
    public void parse() throws Exception {
        EasyParser parser = new EasyParser();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("input/script.txt"));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            System.out.printf("=== parse line: %s\n", line);

            try {
                ASTNode node = parser.parse(line);
                parser.dump(node, "");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("");
        }
    }
}