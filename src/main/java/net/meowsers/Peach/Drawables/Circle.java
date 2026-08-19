package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

public class Circle {
    Vector2f center;
    int radius;

    public Vector2f getCenter() {
        return LiveVector2f.resolve(center);
    }

    public void setCenter(Vector2f center) {
        this.center.set(center);
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
