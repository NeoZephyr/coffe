package pattern.visitor.demo;

public interface CarElem {
    void accept(CarElemVisitor visitor);
}
