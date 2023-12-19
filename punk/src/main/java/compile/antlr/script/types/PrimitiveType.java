package compile.antlr.script.types;

import compile.antlr.script.symbol.Scope;

public class PrimitiveType implements Type {

    private String name = null;

    public static PrimitiveType Integer = new PrimitiveType("Integer");
    public static PrimitiveType Long = new PrimitiveType("Long");
    public static PrimitiveType Float = new PrimitiveType("Float");
    public static PrimitiveType Double = new PrimitiveType("Double");
    public static PrimitiveType Boolean = new PrimitiveType("Boolean");
    public static PrimitiveType Byte = new PrimitiveType("Byte");
    public static PrimitiveType Char = new PrimitiveType("Char");
    public static PrimitiveType Short = new PrimitiveType("Short");

    public static PrimitiveType String = new PrimitiveType("Short");

    public static PrimitiveType Null = new PrimitiveType("null");

    private PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public boolean isType(Type type) {
        return this == type;
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static boolean isNumeric(Type type) {
        return type == PrimitiveType.Byte ||
                type == PrimitiveType.Short ||
                type == PrimitiveType.Integer ||
                type == PrimitiveType.Long ||
                type == PrimitiveType.Float ||
                type == PrimitiveType.Double;
    }

    public static PrimitiveType upperType(Type type1, Type type2) {
        PrimitiveType type = null;

        if ((type1 == PrimitiveType.String) || (type2 == PrimitiveType.String)) {
            type = PrimitiveType.String;
        } else if ((type1 == PrimitiveType.Double) || (type2 == PrimitiveType.Double)) {
            type = PrimitiveType.Double;
        } else if ((type1 == PrimitiveType.Float) || (type2 == PrimitiveType.Float)) {
            type = PrimitiveType.Float;
        } else if ((type1 == PrimitiveType.Long) || (type2 == PrimitiveType.Long)) {
            type = PrimitiveType.Long;
        } else if ((type1 == PrimitiveType.Integer) || (type2 == PrimitiveType.Integer)) {
            type = PrimitiveType.Integer;
        } else if ((type1 == PrimitiveType.Short) || (type2 == PrimitiveType.Short)) {
            type = PrimitiveType.Short;
        } else {
            type = PrimitiveType.Byte;
        }

        return type;
    }
}