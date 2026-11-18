package edu.bu.demo.inheritance;

/** Basic rectangle. */
public class Rectangle extends ColorfulShape {
  final double height;
  final double width;

  public Rectangle(String color, double height, double width) {
    super(color);
    this.height = height;
    this.width = width;
  }

  @Override
  public double area() {
    return height * width;
  }

  @Override
  public String describe() {
    return "I am a rectangle";
  }
}
