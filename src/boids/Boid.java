package boids;

import java.util.ArrayList;
import java.util.List;

public class Boid {

  public static final double FRICTION_FACTOR = 0.2;
  public static final double MAX_SPEED = 500 / 3.;
  private Vector myPosition;
  private Vector myVelocity;
  private double mySpeed;
  private double myFlockDistance;
  private double myCollisionDistance;
  private List<BoidListener> myListeners;

  public Boid(double startX, double startY, Vector startVel, double speed, double flockDistance, double collisionDistance) {
    myPosition = new Vector(startX, startY);
    myVelocity = startVel;
    mySpeed = speed;
    myFlockDistance = flockDistance;
    myCollisionDistance = collisionDistance;
    myListeners = new ArrayList<>();
  }

  public Vector getPosition() {
    return myPosition;
  }

  public void update(double elapsedTime, List<Boid> allBoids, Vector goal) {
    executeMovement(elapsedTime);
//    executeFriction(elapsedTime);

    //accelerate toward local center of mass

    Vector towardCenter = accelerationTowardsPoint(findCenterOfMass(myFlockDistance,allBoids),1);
    Vector averageVelocity = findAverageVelocity(myFlockDistance,allBoids);
    Vector towardNearbyVelocity = averageVelocity.minus(myVelocity).normalize();

    Vector avoidingCollisions = collisionAvoidanceAccleration(myCollisionDistance, allBoids).normalize();

    Vector towardGoal = (goal != null)? accelerationTowardsPoint(goal, 1) : new Vector(0,0);
    towardGoal = towardGoal.normalize().scale(-1);

    Vector avoidingObstacles = avoidObstacleAccel(myCollisionDistance, 500, 500).normalize();

    Vector accelerationDirection = towardNearbyVelocity.scale(0.2).plus(avoidingCollisions.scale(0.1)).plus(avoidingObstacles.scale(0.3).plus(towardGoal.scale(0.6)));
    accelerateInDirection(accelerationDirection, mySpeed, elapsedTime);
  }

  private void executeFriction(double elapsedTime) {
    Vector frictionVector = myVelocity.scale(-1);
    accelerateInDirection(frictionVector, myVelocity.magnitude() * FRICTION_FACTOR, elapsedTime);
  }

  private void executeMovement(double elapsedTime) {
    double bounceScale = 0.5;
    Vector newPosition = myPosition.plus(myVelocity.scale(elapsedTime));
    if (newPosition.getX() < 0) {
      newPosition = new Vector(0, newPosition.getY());
      myVelocity = myVelocity.minus(new Vector(myVelocity.getX(), 0).scale(1+bounceScale));
    }
    if (newPosition.getX() > 500) {
      newPosition = new Vector(500, newPosition.getY());
      myVelocity = myVelocity.minus(new Vector(myVelocity.getX(), 0).scale(1+bounceScale));
    }
    if (newPosition.getY() < 0) {
      newPosition = new Vector(newPosition.getX(), 0);
      myVelocity = myVelocity.minus(new Vector(0, myVelocity.getY()).scale(1+bounceScale));
    }
    if (newPosition.getY() > 500) {
      newPosition = new Vector(newPosition.getX(), 500);
      myVelocity = myVelocity.minus(new Vector(0, myVelocity.getY()).scale(1+bounceScale));
    }
    myPosition = newPosition;
    myListeners.forEach(boidListener -> boidListener.reactToPosition(myPosition, myVelocity));
  }

  private void accelerateInDirection(Vector direction, double amount, double elapsedTime) {
    myVelocity = myVelocity.plus(direction.normalize().scale(amount * elapsedTime));
  }

  private Vector collisionAvoidanceAccleration(double maxDistance, List<Boid> boids) {
    int count = 0;
    double sumX = 0;
    double sumY = 0;
    for (Boid boid : boids) {
      if (boid == this) {
        continue;
      }
      count ++;
      Vector accel = individualAvoidanceAccel(maxDistance, boid);
      sumX += accel.getX();
      sumY += accel.getY();
    }
    double averageX = sumX / count;
    double averageY = sumY / count;
    return new Vector(averageX, averageY);
  }

  private Vector individualAvoidanceAccel(double maxDistance, Boid boid) {
    if (boid.getPosition().distance(myPosition) > maxDistance) {
      return new Vector(0,0);
    }
    return accelerationTowardsPoint(boid.getPosition(), -1);
  }

  private Vector accelerationTowardsPoint(Vector point, double weight) {
    if (point.distance(myPosition) < myFlockDistance) {
      return point.minus(myPosition).normalize().scale(weight);
    }
    return new Vector(0,0);
  }

  private Vector findCenterOfMass(double maxDistance, List<Boid> boids) {
    int count = 0;
    double sumX = 0;
    double sumY = 0;
    for (Boid boid : boids) {
      if (boid.getPosition().distance(this.getPosition()) < maxDistance) {
        count++;
        sumX += boid.getPosition().getX();
        sumY += boid.getPosition().getY();
      }
    }
    double averageX = sumX / count;
    double averageY = sumY / count;
    return new Vector(averageX, averageY);
  }

  private Vector avoidObstacleAccel(double distance, double width, double height) {
    Vector retVec = new Vector(0,0);
    if (myPosition.getX() < distance) {
      retVec = retVec.plus(new Vector(1.0,0.0));
    }
    if (myPosition.getY() < distance) {
      retVec = retVec.plus(new Vector(0.0,1.0));
    }
    if (myPosition.getX() > width - distance) {
      retVec = retVec.plus(new Vector(-1.0,0.0));
    }
    if (myPosition.getY() > height - distance) {
      retVec = retVec.plus(new Vector(0.0,-1.0));
    }
    return retVec;
  }

  private Vector findAverageVelocity(double maxDistance, List<Boid> boids) {
    int count = 0;
    double sumX = 0;
    double sumY = 0;
    for (Boid boid : boids) {
      if (boid.getPosition().distance(this.getPosition()) < maxDistance) {
        count++;
        sumX += boid.getVelocity().getX();
        sumY += boid.getVelocity().getY();
      }
    }
    double averageX = sumX / count;
    double averageY = sumY / count;
    return new Vector(averageX, averageY);
  }

  public double getFlockDistance() {
    return myFlockDistance;
  }

  public double getCollisionDistance() {
    return myCollisionDistance;
  }

  public Vector getVelocity() {
    return myVelocity;
  }

  public void subscribe(BoidListener listener) {
    myListeners.add(listener);
  }
}
