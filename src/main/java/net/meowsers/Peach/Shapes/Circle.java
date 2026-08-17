package net.meowsers.Peach.Shapes;

import org.joml.Vector2f;

public class Circle {
    Vector2f center;
    int radius;

    public Vector2f getCenter() {
        return center;
    }

    public void setCenter(Vector2f center) {
        this.center = center;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Circle(Vector2f center, int radius) {
        this.center = center;
        this.radius = radius;
    }
}
