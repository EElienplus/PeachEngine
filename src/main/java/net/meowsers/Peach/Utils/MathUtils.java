package net.meowsers.Peach.Utils;


import org.joml.Vector2f;

public class MathUtils {

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Vector2f sub(Vector2f a, Vector2f b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Vectors can't be null.");
        }
        return new Vector2f(a.x - b.x, a.y - b.y);
    }

    public static Vector2f add(Vector2f a, Vector2f b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Vectors can't be null.");
        }
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

}
