package boids;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

  private boolean mouseDown;

  @Override
  public void start(Stage primaryStage) throws Exception {

    Group root = new Group();
    Scene scene = new Scene(root, 500, 500);

    BoidModel model = new BoidModel();
    model.getBoids().forEach(boid -> {
            BoidVisualizer visualizer = new BoidVisualizer(boid);
            root.getChildren().add(visualizer.getGroup());
    });

    mouseDown = false;
    scene.setOnMousePressed(event -> {
      if (event.getButton().equals(MouseButton.PRIMARY)) {
        mouseDown = true;
        System.out.println("mouseDown = " + mouseDown);
      }
    });

    scene.setOnMouseDragged(event -> {
      if (mouseDown) {
        model.setGoal(new Vector(event.getX(), event.getY()));
      }
    });
    scene.setOnMouseReleased(event -> {
      model.setGoal(null);
      mouseDown = false;
      System.out.println("mouseDown = " + mouseDown);
    });

    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1/60.), event -> {
      model.update(1/60.);
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    primaryStage.setScene(scene);
    primaryStage.setTitle("Boids");
    primaryStage.show();
  }
}
