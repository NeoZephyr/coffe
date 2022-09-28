package com.pain.rock.antlr.json;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class XMLEmitter extends JSONBaseListener {

    ParseTreeProperty<String> xml = new ParseTreeProperty<>();

    String getXML(ParseTree ctx) {
        return xml.get(ctx);
    }

    void setXML(ParseTree ctx, String value) {
        xml.put(ctx, value);
    }

    @Override
    public void exitAtom(JSONParser.AtomContext ctx) {
        setXML(ctx, ctx.getText());
    }

    @Override
    public void exitString(JSONParser.StringContext ctx) {
        setXML(ctx, stripQuotes(ctx.getText()));
    }

    @Override
    public void exitObjectValue(JSONParser.ObjectValueContext ctx) {
        setXML(ctx, getXML(ctx.object()));
    }

    @Override
    public void exitArrayValue(JSONParser.ArrayValueContext ctx) {
        setXML(ctx, getXML(ctx.array()));
    }

    @Override
    public void exitPair(JSONParser.PairContext ctx) {
        String tag = stripQuotes(ctx.STRING().getText());
        JSONParser.ValueContext valueCtx = ctx.value();
        String x = String.format("<%s>%s</%s>\n", tag, getXML(valueCtx), tag);
        setXML(ctx, x);
    }

    @Override
    public void exitEmptyArray(JSONParser.EmptyArrayContext ctx) {
        setXML(ctx, "");
    }

    @Override
    public void exitArrayOfValues(JSONParser.ArrayOfValuesContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for (JSONParser.ValueContext vctx : ctx.value()) {
            buf.append("<element>");
            buf.append(getXML(vctx));
            buf.append("</element>");
            buf.append("\n");
        }
        setXML(ctx, buf.toString());
    }

    @Override
    public void exitEmptyObject(JSONParser.EmptyObjectContext ctx) {
        setXML(ctx, "");
    }

    @Override
    public void exitAnObject(JSONParser.AnObjectContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for (JSONParser.PairContext pctx : ctx.pair()) {
            buf.append(getXML(pctx));
        }
        setXML(ctx, buf.toString());
    }

    @Override
    public void exitJson(JSONParser.JsonContext ctx) {
        setXML(ctx, getXML(ctx.getChild(0)));
    }

    public static String stripQuotes(String s) {
        if (s == null || s.charAt(0) != '"') return s;
        return s.substring(1, s.length() - 1);
    }
}
