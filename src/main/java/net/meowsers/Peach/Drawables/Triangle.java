package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

public class Triangle {
    Vector2f pointA, pointB, pointC;

    public Vector2f getPointA() {
        return LiveVector2f.resolve(pointA);
    }

    public void setPointA(Vector2f pointA) {
        this.pointA.set(pointA);
    }

    public Vector2f getPointB() {
        return LiveVector2f.resolve(pointB);
    }

    public void setPointB(Vector2f pointB) {
        this.pointB.set(pointB);
    }

    public Vector2f getPointC() {
        return LiveVector2f.resolve(pointC);
    }

    public void setPointC(Vector2f pointC) {
        this.pointC.set(pointC);
    }

    public Triangle(Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
    }
}
