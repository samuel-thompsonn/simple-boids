package boids;

public interface BoidListener {
  public void reactToPosition(Vector position, Vector velocity);
}
