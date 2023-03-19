package compile.antlr.script;

public class ByteCodeGenerator {
    private AnnotatedTree tree;

    public ByteCodeGenerator(AnnotatedTree tree) {
        this.tree = tree;
    }

    public byte[] generate() {
        return new byte[10];
    }
}
