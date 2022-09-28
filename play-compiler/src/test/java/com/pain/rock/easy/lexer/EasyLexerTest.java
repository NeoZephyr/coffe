package com.pain.rock.easy.lexer;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EasyLexerTest {

    @Test
    public void tokenize() throws IOException {
        EasyLexer lexer = new EasyLexer();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("input/script.txt"));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            TokenReader tokenReader = lexer.tokenize(line);
            Token token;

            System.out.printf("line: %s\n", line);
            System.out.println("=====");
            while ((token = tokenReader.read()) != null) {
                System.out.printf("%s\t\t%s\n", token.getType(), token.getText());
            }
            System.out.println();
        }
    }
}