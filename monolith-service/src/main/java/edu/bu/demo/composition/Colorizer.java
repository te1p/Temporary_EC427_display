package edu.bu.demo.composition;

import edu.bu.demo.Shape;

/** Component that adds colorization to shapes. */
public class Colorizer {
  final String color;

  public Colorizer(String color) {
    this.color = color;
  }

  public String describeWithColor(Shape shape) {
    return shape.describe() + " that is " + color;
  }
}
