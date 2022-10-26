package pattern.visitor.demo;

import java.util.ArrayList;
import java.util.List;

public class Car implements CarElem {
    List<CarElem> elems = new ArrayList<>();

    public Car() {
        this.elems.add(new CarBody());
        this.elems.add(new CarEngine());
    }

    /**
     * The Visitor class doesn’t necessarily need to know anything about how to navigate the node data structure
     */
    @Override
    public void accept(CarElemVisitor visitor) {
        for (CarElem elem : elems) {
            elem.accept(visitor);
        }
        visitor.visit(this);
    }
}
