package boids;

public class Vector {
  private final double myX;
  private final double myY;

  public Vector(double x, double y) {
    myX = x;
    myY = y;
  }

  public double getX() {
    return myX;
  }

  public double getY() {
    return myY;
  }

  public double distance(Vector other) {
    return Math.sqrt(Math.pow(other.getY() - myY,2) + Math.pow(other.getX() - myX, 2));
  }

  public Vector plus(Vector other) {
    return new Vector(this.myX + other.getX(), this.getY() + other.getY());
  }

  public Vector scale(double scalar) {
    return new Vector(myX * scalar, myY * scalar);
  }

  public Vector minus(Vector other) {
    return this.plus(other.scale(-1));
  }

  public double magnitude() {
    return Math.sqrt((myX * myX) + (myY * myY));
  }

  public Vector normalize() {
    if (magnitude() == 0) {
      return this;
    }
    return this.scale(1 / this.magnitude());
  }
}
