package foundation.pattern.visitor.demo;

public class CarElemOutputVisitor implements CarElemVisitor {
    @Override
    public void visit(CarBody body) {
        System.out.println("body");
    }

    @Override
    public void visit(CarEngine engine) {
        System.out.println("engine");
    }

    @Override
    public void visit(Car car) {
        System.out.println("car");
    }
}
