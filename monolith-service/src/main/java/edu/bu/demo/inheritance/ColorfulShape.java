package edu.bu.demo.inheritance;

import edu.bu.demo.Shape;

/** Abstract class that implements aspects of the Shape interface that deal with color. */
public abstract class ColorfulShape implements Shape {
  final String color;

  public ColorfulShape(String color) {
    this.color = color;
  }

  @Override
  public String describeWithColor() {
    return describe() + " that is " + color;
  }
}
