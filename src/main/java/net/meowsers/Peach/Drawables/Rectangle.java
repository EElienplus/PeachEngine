package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

public class Rectangle {
    private final Vector2f position;
    private final Vector2f size;

    public float getX() {
        return getPosition().x;
    }

    public void setX(float x) {
        position.x = x;
    }

    public float getY() {
        return getPosition().y;
    }

    public void setY(float y) {
        position.y = y;
    }

    public float getW() {
        return getSize().x;
    }

    public void setW(float w) {
        size.x = w;
    }

    public float getH() {
        return getSize().y;
    }

    public void setH(float h) {
        size.y = h;
    }

    public Vector2f getPosition() {
        return LiveVector2f.resolve(position);
    }

    public Vector2f getSize() {
        return LiveVector2f.resolve(size);
    }

    public Rectangle(float x, float y, float w, float h) {
        this(new Vector2f(x, y), new Vector2f(w, h));
    }

    public Rectangle(Vector2f position, float w, float h) {
        this(position, new Vector2f(w, h));
    }

    public Rectangle(Vector2f position, Vector2f size) {
        if (position == null || size == null) {
            throw new IllegalArgumentException("Rectangle position and size can't be null.");
        }

        this.position = position;
        this.size = size;
    }
}
