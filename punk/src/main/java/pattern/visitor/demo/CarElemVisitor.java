package pattern.visitor.demo;

public interface CarElemVisitor {
    void visit(CarBody body);
    void visit(CarEngine engine);
    void visit(Car car);
}
