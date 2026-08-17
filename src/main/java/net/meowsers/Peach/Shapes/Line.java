package net.meowsers.Peach.Shapes;

import org.joml.Vector2f;

public class Line {
    Vector2f startPos, endPos;

    public Vector2f getStartPos() {
        return startPos;
    }

    public void setStartPos(Vector2f startPos) {
        this.startPos = startPos;
    }

    public Vector2f getEndPos() {
        return endPos;
    }

    public void setEndPos(Vector2f endPos) {
        this.endPos = endPos;
    }

    public Line(Vector2f startPos, Vector2f endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
}
