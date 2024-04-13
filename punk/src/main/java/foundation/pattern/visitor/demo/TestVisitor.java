package foundation.pattern.visitor.demo;

public class TestVisitor {
    public static void main(String[] args) {
        Car car = new Car();
        CarElemOutputVisitor visitor = new CarElemOutputVisitor();
        visitor.visit(car);
        // car.accept(visitor);
    }
}
