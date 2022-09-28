package com.pain.rock.antlr.json;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class JsonTest {
    public static void main(String[] args) throws IOException {
        CharStream inputStream = CharStreams.fromFileName("input/antlr/test.json");
        JSONLexer lexer = new JSONLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokens);
        JSONParser.JsonContext context = parser.json();

        ParseTreeWalker walker = new ParseTreeWalker();

        // listener
        XMLEmitter emitter = new XMLEmitter();
        walker.walk(emitter, context);
        String xml = emitter.getXML(context);
        System.out.println(xml);
    }
}
