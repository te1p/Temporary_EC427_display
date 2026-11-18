package edu.bu.demo.composition;

import edu.bu.demo.Shape;

/**
 * Circle implementation that uses composition to add colorization. Note that the reason this class
 * has a long, verbose name is to distinguish from the Circle class in an adjacent package.
 * Normally, it would be called just Circle.
 */
public class CircleWithComposition implements Shape {

  final double radius;
  final Colorizer colorizer;

  public CircleWithComposition(Colorizer colorizer, double radius) {
    this.radius = radius;
    this.colorizer = colorizer;
  }

  @Override
  public double area() {
    return radius * radius * Math.PI;
  }

  @Override
  public String describe() {
    return "I am a circle";
  }

  @Override
  public String describeWithColor() {
    return colorizer.describeWithColor(this);
  }
}
