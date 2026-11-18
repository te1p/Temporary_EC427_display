package edu.bu.demo;

import edu.bu.demo.composition.CircleWithComposition;
import edu.bu.demo.composition.Colorizer;
import edu.bu.demo.composition.RectangleWithComposition;
import edu.bu.demo.inheritance.Circle;
import edu.bu.demo.inheritance.Rectangle;
import org.junit.jupiter.api.Test;

class ShapeTest {

  @Test
  public void probe_composition() {
    Shape circle = new CircleWithComposition(new Colorizer("red"), 3);
    Shape rectangle = new RectangleWithComposition(new Colorizer("green"), 3, 4);

    System.out.println(circle.describeWithColor() + " " + circle.area());
    System.out.println(rectangle.describeWithColor() + " " + rectangle.area());
  }

  @Test
  public void probe_inheritance() {
    Shape circle = new Circle("red", 3);
    Shape rectangle = new Rectangle("green", 3, 4);

    System.out.println(circle.describeWithColor() + " " + circle.area());
    System.out.println(rectangle.describeWithColor() + " " + rectangle.area());
  }
}
