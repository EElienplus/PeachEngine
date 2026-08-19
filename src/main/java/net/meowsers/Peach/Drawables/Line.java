package net.meowsers.Peach.Drawables;

import net.meowsers.Peach.Utils.LiveVector2f;
import org.joml.Vector2f;

public class Line {
    Vector2f startPos, endPos;

    public Vector2f getStartPos() {
        return LiveVector2f.resolve(startPos);
    }

    public void setStartPos(Vector2f startPos) {
        this.startPos.set(startPos);
    }

    public Vector2f getEndPos() {
        return LiveVector2f.resolve(endPos);
    }

    public void setEndPos(Vector2f endPos) {
        this.endPos.set(endPos);
    }

    public Line(Vector2f startPos, Vector2f endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
}
