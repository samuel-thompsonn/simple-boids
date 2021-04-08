package boids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoidModel {

  private List<Boid> myBoids;
  private Vector myGoal;

  public BoidModel() {
    myBoids = new ArrayList<>();
    double flockDistance = 45;
    double collisionDistance = 25;
    double speed = 96;
    double numBoids = 40;
    for (int i = 0; i < numBoids; i ++) {
      double x = (new Random().nextDouble() * 100) + 100;
      double y = (new Random().nextDouble() * 100) + 100;
      double xVel = (new Random().nextDouble() * 100) + -40;
      double yVel = (new Random().nextDouble() * 100) + -40;
      myBoids.add(new Boid(x, y, new Vector(xVel, yVel), speed, flockDistance, collisionDistance));
    }
  }

  public void setGoal(Vector goal) {
    myGoal = goal;
  }

  public List<Boid> getBoids() {
    return myBoids;
  }

  public void update(double elapsedTime) {
    for (Boid boid : myBoids) {
      boid.update(elapsedTime, myBoids, myGoal);
    }
  }

}
