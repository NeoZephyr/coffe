package compile.antlr.script.memory;

public class ReturnObject {

    public Object value = null;

    public ReturnObject(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Return";
    }
}