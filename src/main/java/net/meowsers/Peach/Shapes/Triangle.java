package net.meowsers.Peach.Shapes;

import org.joml.Vector2f;

public class Triangle {
    Vector2f pointA, pointB, pointC;

    public Vector2f getPointA() {
        return pointA;
    }

    public void setPointA(Vector2f pointA) {
        this.pointA = pointA;
    }

    public Vector2f getPointB() {
        return pointB;
    }

    public void setPointB(Vector2f pointB) {
        this.pointB = pointB;
    }

    public Vector2f getPointC() {
        return pointC;
    }

    public void setPointC(Vector2f pointC) {
        this.pointC = pointC;
    }

    public Triangle(Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
    }
}
