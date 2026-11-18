package edu.bu.demo.composition;

import edu.bu.demo.Shape;

/**
 * Rectangle implementation that uses composition to support colorization. Note that the reason this
 * class has a long, verbose name is to distinguish from the Rectangle class in an adjacent package.
 * Normally, it would be called just Rectangle.
 */
public class RectangleWithComposition implements Shape {

  final double height;
  final double width;
  final Colorizer colorizer;

  public RectangleWithComposition(Colorizer colorizer, double height, double width) {
    this.width = width;
    this.height = height;
    this.colorizer = colorizer;
  }

  @Override
  public double area() {
    return height * width;
  }

  @Override
  public String describe() {
    return "I am a rectangle";
  }

  @Override
  public String describeWithColor() {
    return colorizer.describeWithColor(this);
  }
}
