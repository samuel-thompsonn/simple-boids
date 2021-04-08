package boids;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class BoidVisualizer implements BoidListener {

  private Group myGroup;
  private Polygon myTriangle;
  private Circle myFlockCircle;
  private Circle myCollisionCircle;

  public BoidVisualizer(Boid boid) {
    myTriangle = initTriangle(boid);
    myFlockCircle = new Circle(boid.getFlockDistance());
    myFlockCircle.setFill(Color.TRANSPARENT);
    myFlockCircle.setStrokeWidth(1);
    myFlockCircle.setStroke(Color.color(0.0,1.0,0.0,0.2));
    myCollisionCircle = new Circle(boid.getCollisionDistance() / 2);
    myCollisionCircle.setFill(Color.TRANSPARENT);
    myCollisionCircle.setStrokeWidth(1);
    myCollisionCircle.setStroke(Color.color(1.0,0.0,0.0,0.2));
    myGroup = new Group();
    myGroup.getChildren().add(myTriangle);
    myGroup.getChildren().add(myFlockCircle);
    myGroup.getChildren().add(myCollisionCircle);
    boid.subscribe(this);
  }

  public Group getGroup() {
    return myGroup;
  }

  @Override
  public void reactToPosition(Vector position, Vector velocity) {
    myTriangle.setTranslateX(position.getX());
    myTriangle.setTranslateY(position.getY());
    if(velocity.getX() == 0) {
      myTriangle.setRotate((velocity.getY() < 0)? 90 : 270);
    }
    double angle = Math.toDegrees(Math.atan(velocity.getY() / velocity.getX()));
    myTriangle.setRotate((velocity.getX() < 0)? angle + 180 : angle);
    if (velocity.magnitude() < 10.0) {
      myTriangle.setRotate(90);
    }
    myFlockCircle.setCenterX(position.getX());
    myFlockCircle.setCenterY(position.getY());
    myCollisionCircle.setCenterX(position.getX());
    myCollisionCircle.setCenterY(position.getY());
  }

  private Polygon initTriangle(Boid boid) {
    double[] firstPoint = { 0, 0 };
    double[] secondPoint = { 0, 5 };
    double[] thirdPoint = { 10, 0 };
    double[] fourthPoint = { 0, -5 };
    Polygon tri = new Polygon(firstPoint[0],firstPoint[1],secondPoint[0],secondPoint[1],thirdPoint[0],thirdPoint[1],
            fourthPoint[0], fourthPoint[1]);
    return tri;
  }
}
