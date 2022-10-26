package pattern.visitor.demo;

public class CarEngine implements CarElem {
    @Override
    public void accept(CarElemVisitor visitor) {
        visitor.visit(this);
    }
}
