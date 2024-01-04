package compile.antlr.script.memory;

import compile.antlr.script.symbol.Klass;

public class KlassObject extends ActivationRecord {

    public static KlassObject NULL_OBJECT = new KlassObject();

    public Klass klass = null;
}
