package foundation.pattern.visitor.demo;

public class CarBody implements CarElem {
    @Override
    public void accept(CarElemVisitor visitor) {
        visitor.visit(this);
    }
}
