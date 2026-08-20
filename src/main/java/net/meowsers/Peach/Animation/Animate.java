package net.meowsers.Peach.Animation;

import net.meowsers.Peach.Drawables.Circle;
import net.meowsers.Peach.Drawables.Curve;
import net.meowsers.Peach.Drawables.Group;
import net.meowsers.Peach.Drawables.Line;
import net.meowsers.Peach.Drawables.Rectangle;
import net.meowsers.Peach.Drawables.Text;
import net.meowsers.Peach.Utils.Enums.Ease;
import org.joml.Vector2f;

public class Animate extends TimedAnimation {

    @FunctionalInterface
    public interface ProgressAction {
        void apply(float progress);
    }

    @FunctionalInterface
    public interface BeginAction {
        void begin();
    }

    @FunctionalInterface
    public interface FloatSetter {
        void set(float value);
    }

    @FunctionalInterface
    public interface Vector2Setter {
        void set(Vector2f value);
    }

    private final BeginAction beginAction;
    private final ProgressAction progressAction;

    private Animate(float duration, Ease ease, ProgressAction progressAction) {
        this(duration, ease, null, progressAction);
    }

    private Animate(float duration, Ease ease, BeginAction beginAction, ProgressAction progressAction) {
        super(duration, ease);

        if (progressAction == null) {
            throw new IllegalArgumentException("Progress action can't be null.");
        }

        this.beginAction = beginAction;
        this.progressAction = progressAction;
    }

    public static Animate custom(float duration, ProgressAction progressAction) {
        return custom(duration, Ease.InOut, progressAction);
    }

    public static Animate custom(float duration, Ease ease, ProgressAction progressAction) {
        return custom(duration, ease, null, progressAction);
    }

    public static Animate custom(float duration, Ease ease, BeginAction beginAction, ProgressAction progressAction) {
        return AnimationTimeline.record(new Animate(duration, ease, beginAction, progressAction));
    }

    public static Animate floatValue(float from, float to, float duration, FloatSetter setter) {
        return floatValue(from, to, duration, Ease.InOut, setter);
    }

    public static Animate floatValue(float from, float to, float duration, Ease ease, FloatSetter setter) {
        if (setter == null) {
            throw new IllegalArgumentException("Setter can't be null.");
        }

        return custom(duration, ease, (progress) -> setter.set(lerp(from, to, progress)));
    }

    public static Animate vector2(Vector2f from, Vector2f to, float duration, Vector2Setter setter) {
        return vector2(from, to, duration, Ease.InOut, setter);
    }

    public static Animate vector2(Vector2f from, Vector2f to, float duration, Ease ease, Vector2Setter setter) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Vector points can't be null.");
        }
        if (setter == null) {
            throw new IllegalArgumentException("Setter can't be null.");
        }

        Vector2f fromCopy = new Vector2f(from);
        Vector2f toCopy = new Vector2f(to);

        return custom(duration, ease, (progress) -> setter.set(lerp(fromCopy, toCopy, progress)));
    }

    public static Animate lineEnd(Line line, Vector2f targetEndPos, float duration) {
        return lineEnd(line, targetEndPos, duration, Ease.InOut);
    }

    public static Animate lineEnd(Line line, Vector2f targetEndPos, float duration, Ease ease) {
        if (line == null || targetEndPos == null) {
            throw new IllegalArgumentException("Line and target end position can't be null.");
        }

        Vector2f startEndPos = new Vector2f();
        Vector2f targetEndPosCopy = new Vector2f(targetEndPos);

        return custom(duration, ease, () -> {
            startEndPos.set(line.getEndPos());
        }, (progress) -> {
            line.setEndPos(lerp(startEndPos, targetEndPosCopy, progress));
        });
    }

    public static Animate lineStart(Line line, Vector2f targetStartPos, float duration) {
        return lineStart(line, targetStartPos, duration, Ease.InOut);
    }

    public static Animate lineStart(Line line, Vector2f targetStartPos, float duration, Ease ease) {
        if (line == null || targetStartPos == null) {
            throw new IllegalArgumentException("Line and target start position can't be null.");
        }

        Vector2f startStartPos = new Vector2f();
        Vector2f targetStartPosCopy = new Vector2f(targetStartPos);

        return custom(duration, ease, () -> {
            startStartPos.set(line.getStartPos());
        }, (progress) -> {
            line.setStartPos(lerp(startStartPos, targetStartPosCopy, progress));
        });
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float duration) {
        return rectangle(rectangle, targetX, targetY, duration, Ease.InOut);
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float duration, Ease ease) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }

        float[] start = new float[2];

        return custom(duration, ease, () -> {
            start[0] = rectangle.getX();
            start[1] = rectangle.getY();
        }, (progress) -> {
            rectangle.setX(lerp(start[0], targetX, progress));
            rectangle.setY(lerp(start[1], targetY, progress));
        });
    }

    public static Animate rectangle(Rectangle rectangle, Vector2f targetPosition, float duration) {
        return rectangle(rectangle, targetPosition, duration, Ease.InOut);
    }

    public static Animate rectangle(Rectangle rectangle, Vector2f targetPosition, float duration, Ease ease) {
        if (targetPosition == null) {
            throw new IllegalArgumentException("Target position can't be null.");
        }
        return rectangle(rectangle, targetPosition.x, targetPosition.y, duration, ease);
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float targetW, float targetH, float duration) {
        return rectangle(rectangle, targetX, targetY, targetW, targetH, duration, Ease.InOut);
    }

    public static Animate rectangle(Rectangle rectangle, float targetX, float targetY, float targetW, float targetH,
                                    float duration, Ease ease) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }

        float[] start = new float[4];

        return custom(duration, ease, () -> {
            start[0] = rectangle.getX();
            start[1] = rectangle.getY();
            start[2] = rectangle.getW();
            start[3] = rectangle.getH();
        }, (progress) -> {
            rectangle.setX(lerp(start[0], targetX, progress));
            rectangle.setY(lerp(start[1], targetY, progress));
            rectangle.setW(lerp(start[2], targetW, progress));
            rectangle.setH(lerp(start[3], targetH, progress));
        });
    }

    public static Animate rectangleMove(Rectangle rectangle, float deltaX, float deltaY, float duration) {
        return rectangleMove(rectangle, deltaX, deltaY, duration, Ease.InOut);
    }

    public static Animate rectangleMove(Rectangle rectangle, float deltaX, float deltaY, float duration, Ease ease) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }

        float[] start = new float[2];

        return custom(duration, ease, () -> {
            start[0] = rectangle.getX();
            start[1] = rectangle.getY();
        }, (progress) -> {
            rectangle.setX(lerp(start[0], start[0] + deltaX, progress));
            rectangle.setY(lerp(start[1], start[1] + deltaY, progress));
        });
    }

    public static Animate rectangleMove(Rectangle rectangle, Vector2f delta, float duration) {
        return rectangleMove(rectangle, delta, duration, Ease.InOut);
    }

    public static Animate rectangleMove(Rectangle rectangle, Vector2f delta, float duration, Ease ease) {
        if (delta == null) {
            throw new IllegalArgumentException("Delta can't be null.");
        }
        return rectangleMove(rectangle, delta.x, delta.y, duration, ease);
    }

    public static Animate circle(Circle circle, Vector2f targetCenter, int targetRadius, float duration) {
        return circle(circle, targetCenter, targetRadius, duration, Ease.InOut);
    }

    public static Animate circle(Circle circle, Vector2f targetCenter, int targetRadius, float duration, Ease ease) {
        if (circle == null || targetCenter == null) {
            throw new IllegalArgumentException("Circle and target center can't be null.");
        }

        Vector2f startCenter = new Vector2f();
        Vector2f targetCenterCopy = new Vector2f(targetCenter);
        int[] startRadius = new int[1];

        return custom(duration, ease, () -> {
            startCenter.set(circle.getCenter());
            startRadius[0] = circle.getRadius();
        }, (progress) -> {
            circle.setCenter(lerp(startCenter, targetCenterCopy, progress));
            circle.setRadius(Math.round(lerp(startRadius[0], targetRadius, progress)));
        });
    }

    public static Animate curve(Curve curve, Vector2f targetP0, Vector2f targetP1, Vector2f targetP2, Vector2f targetP3, float duration) {
        return curve(curve, targetP0, targetP1, targetP2, targetP3, duration, Ease.InOut);
    }

    public static Animate curve(Curve curve, Vector2f targetP0, Vector2f targetP1, Vector2f targetP2, Vector2f targetP3, float duration, Ease ease) {
        if (curve == null) {
            throw new IllegalArgumentException("Curve can't be null.");
        }

        Vector2f startP0 = new Vector2f();
        Vector2f startP1 = new Vector2f();
        Vector2f startP2 = new Vector2f();
        Vector2f startP3 = new Vector2f();

        Vector2f targetP0Copy = targetP0 != null ? new Vector2f(targetP0) : null;
        Vector2f targetP1Copy = targetP1 != null ? new Vector2f(targetP1) : null;
        Vector2f targetP2Copy = targetP2 != null ? new Vector2f(targetP2) : null;
        Vector2f targetP3Copy = targetP3 != null ? new Vector2f(targetP3) : null;

        return custom(duration, ease, () -> {
            startP0.set(curve.getP0());
            startP1.set(curve.getP1());
            startP2.set(curve.getP2());
            startP3.set(curve.getP3());
        }, (progress) -> {
            if (targetP0Copy != null) curve.setP0(lerp(startP0, targetP0Copy, progress));
            if (targetP1Copy != null) curve.setP1(lerp(startP1, targetP1Copy, progress));
            if (targetP2Copy != null) curve.setP2(lerp(startP2, targetP2Copy, progress));
            if (targetP3Copy != null) curve.setP3(lerp(startP3, targetP3Copy, progress));
        });
    }

    public static Animate curveP0(Curve curve, Vector2f targetP0, float duration) {
        return curveP0(curve, targetP0, duration, Ease.InOut);
    }

    public static Animate curveP0(Curve curve, Vector2f targetP0, float duration, Ease ease) {
        if (curve == null || targetP0 == null) {
            throw new IllegalArgumentException("Curve and target point can't be null.");
        }
        Vector2f start = new Vector2f();
        Vector2f targetCopy = new Vector2f(targetP0);
        return custom(duration, ease, () -> {
            start.set(curve.getP0());
        }, (progress) -> {
            curve.setP0(lerp(start, targetCopy, progress));
        });
    }

    public static Animate curveP0(Curve curve, float targetX, float targetY, float duration) {
        return curveP0(curve, new Vector2f(targetX, targetY), duration, Ease.InOut);
    }

    public static Animate curveP0(Curve curve, float targetX, float targetY, float duration, Ease ease) {
        return curveP0(curve, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate curveP1(Curve curve, Vector2f targetP1, float duration) {
        return curveP1(curve, targetP1, duration, Ease.InOut);
    }

    public static Animate curveP1(Curve curve, Vector2f targetP1, float duration, Ease ease) {
        if (curve == null || targetP1 == null) {
            throw new IllegalArgumentException("Curve and target point can't be null.");
        }
        Vector2f start = new Vector2f();
        Vector2f targetCopy = new Vector2f(targetP1);
        return custom(duration, ease, () -> {
            start.set(curve.getP1());
        }, (progress) -> {
            curve.setP1(lerp(start, targetCopy, progress));
        });
    }

    public static Animate curveP1(Curve curve, float targetX, float targetY, float duration) {
        return curveP1(curve, new Vector2f(targetX, targetY), duration, Ease.InOut);
    }

    public static Animate curveP1(Curve curve, float targetX, float targetY, float duration, Ease ease) {
        return curveP1(curve, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate curveP2(Curve curve, Vector2f targetP2, float duration) {
        return curveP2(curve, targetP2, duration, Ease.InOut);
    }

    public static Animate curveP2(Curve curve, Vector2f targetP2, float duration, Ease ease) {
        if (curve == null || targetP2 == null) {
            throw new IllegalArgumentException("Curve and target point can't be null.");
        }
        Vector2f start = new Vector2f();
        Vector2f targetCopy = new Vector2f(targetP2);
        return custom(duration, ease, () -> {
            start.set(curve.getP2());
        }, (progress) -> {
            curve.setP2(lerp(start, targetCopy, progress));
        });
    }

    public static Animate curveP2(Curve curve, float targetX, float targetY, float duration) {
        return curveP2(curve, new Vector2f(targetX, targetY), duration, Ease.InOut);
    }

    public static Animate curveP2(Curve curve, float targetX, float targetY, float duration, Ease ease) {
        return curveP2(curve, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate curveP3(Curve curve, Vector2f targetP3, float duration) {
        return curveP3(curve, targetP3, duration, Ease.InOut);
    }

    public static Animate curveP3(Curve curve, Vector2f targetP3, float duration, Ease ease) {
        if (curve == null || targetP3 == null) {
            throw new IllegalArgumentException("Curve and target point can't be null.");
        }
        Vector2f start = new Vector2f();
        Vector2f targetCopy = new Vector2f(targetP3);
        return custom(duration, ease, () -> {
            start.set(curve.getP3());
        }, (progress) -> {
            curve.setP3(lerp(start, targetCopy, progress));
        });
    }

    public static Animate curveP3(Curve curve, float targetX, float targetY, float duration) {
        return curveP3(curve, new Vector2f(targetX, targetY), duration, Ease.InOut);
    }

    public static Animate curveP3(Curve curve, float targetX, float targetY, float duration, Ease ease) {
        return curveP3(curve, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate curveMove(Curve curve, float deltaX, float deltaY, float duration) {
        return curveMove(curve, deltaX, deltaY, duration, Ease.InOut);
    }

    public static Animate curveMove(Curve curve, float deltaX, float deltaY, float duration, Ease ease) {
        if (curve == null) {
            throw new IllegalArgumentException("Curve can't be null.");
        }

        Vector2f startP0 = new Vector2f();
        Vector2f startP1 = new Vector2f();
        Vector2f startP2 = new Vector2f();
        Vector2f startP3 = new Vector2f();

        return custom(duration, ease, () -> {
            startP0.set(curve.getP0());
            startP1.set(curve.getP1());
            startP2.set(curve.getP2());
            startP3.set(curve.getP3());
        }, (progress) -> {
            curve.setP0(lerp(startP0, new Vector2f(startP0.x + deltaX, startP0.y + deltaY), progress));
            curve.setP1(lerp(startP1, new Vector2f(startP1.x + deltaX, startP1.y + deltaY), progress));
            curve.setP2(lerp(startP2, new Vector2f(startP2.x + deltaX, startP2.y + deltaY), progress));
            curve.setP3(lerp(startP3, new Vector2f(startP3.x + deltaX, startP3.y + deltaY), progress));
        });
    }

    public static Animate curveMove(Curve curve, Vector2f delta, float duration) {
        return curveMove(curve, delta, duration, Ease.InOut);
    }

    public static Animate curveMove(Curve curve, Vector2f delta, float duration, Ease ease) {
        if (delta == null) {
            throw new IllegalArgumentException("Delta can't be null.");
        }
        return curveMove(curve, delta.x, delta.y, duration, ease);
    }

    public static Animate textString(Text text, String targetString, float duration) {
        return textString(text, targetString, duration, Ease.InOut);
    }

    public static Animate textString(Text text, String targetString, float duration, Ease ease) {
        if (text == null || targetString == null) {
            throw new IllegalArgumentException("Text and target string can't be null.");
        }

        String target = targetString;

        return custom(duration, ease, (progress) -> {
            int visibleChars = Math.max(0, Math.min(target.length(), (int) Math.floor(target.length() * progress)));

            if (progress >= 1.0f) {
                text.setString(target);
                return;
            }

            text.setString(target.substring(0, visibleChars));
        });
    }

    public static Animate textPosition(Text text, Vector2f targetPosition, float duration) {
        return textPosition(text, targetPosition, duration, Ease.InOut);
    }

    public static Animate textPosition(Text text, Vector2f targetPosition, float duration, Ease ease) {
        if (text == null || targetPosition == null) {
            throw new IllegalArgumentException("Text and target position can't be null.");
        }

        Vector2f startPosition = new Vector2f();
        Vector2f targetPositionCopy = new Vector2f(targetPosition);

        return custom(duration, ease, () -> {
            startPosition.set(text.getPosition());
        }, (progress) -> {
            text.setPosition(lerp(startPosition, targetPositionCopy, progress));
        });
    }

    public static Animate textPosition(Text text, float targetX, float targetY, float duration) {
        return textPosition(text, new Vector2f(targetX, targetY), duration);
    }

    public static Animate textPosition(Text text, float targetX, float targetY, float duration, Ease ease) {
        return textPosition(text, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate group(Group group, float targetX, float targetY, float duration) {
        return group(group, new Vector2f(targetX, targetY), duration, Ease.InOut);
    }

    public static Animate group(Group group, float targetX, float targetY, float duration, Ease ease) {
        return group(group, new Vector2f(targetX, targetY), duration, ease);
    }

    public static Animate group(Group group, Vector2f targetPosition, float duration) {
        return group(group, targetPosition, duration, Ease.InOut);
    }

    public static Animate group(Group group, Vector2f targetPosition, float duration, Ease ease) {
        if (group == null || targetPosition == null) {
            throw new IllegalArgumentException("Group and target position can't be null.");
        }

        Vector2f startPosition = new Vector2f();
        Vector2f targetPositionCopy = new Vector2f(targetPosition);

        return custom(duration, ease, () -> {
            startPosition.set(group.getPosition());
        }, (progress) -> {
            Vector2f currentTarget = lerp(startPosition, targetPositionCopy, progress);
            float dx = currentTarget.x - group.getX();
            float dy = currentTarget.y - group.getY();
            group.move(dx, dy);
        });
    }

    public static Animate groupMove(Group group, float deltaX, float deltaY, float duration) {
        return groupMove(group, new Vector2f(deltaX, deltaY), duration, Ease.InOut);
    }

    public static Animate groupMove(Group group, float deltaX, float deltaY, float duration, Ease ease) {
        return groupMove(group, new Vector2f(deltaX, deltaY), duration, ease);
    }

    public static Animate groupMove(Group group, Vector2f delta, float duration) {
        return groupMove(group, delta, duration, Ease.InOut);
    }

    public static Animate groupMove(Group group, Vector2f delta, float duration, Ease ease) {
        if (group == null || delta == null) {
            throw new IllegalArgumentException("Group and delta can't be null.");
        }

        Vector2f startPosition = new Vector2f();
        Vector2f targetPositionCopy = new Vector2f();

        return custom(duration, ease, () -> {
            startPosition.set(group.getPosition());
            targetPositionCopy.set(startPosition.x + delta.x, startPosition.y + delta.y);
        }, (progress) -> {
            Vector2f currentTarget = lerp(startPosition, targetPositionCopy, progress);
            float dx = currentTarget.x - group.getX();
            float dy = currentTarget.y - group.getY();
            group.move(dx, dy);
        });
    }

    public static Animate move(Object drawable, float deltaX, float deltaY, float duration) {
        return move(drawable, new Vector2f(deltaX, deltaY), duration, Ease.InOut);
    }

    public static Animate move(Object drawable, float deltaX, float deltaY, float duration, Ease ease) {
        return move(drawable, new Vector2f(deltaX, deltaY), duration, ease);
    }

    public static Animate move(Object drawable, Vector2f delta, float duration) {
        return move(drawable, delta, duration, Ease.InOut);
    }

    public static Animate move(Object drawable, Vector2f delta, float duration, Ease ease) {
        if (drawable == null || delta == null) {
            throw new IllegalArgumentException("Drawable and delta can't be null.");
        }
        if (drawable instanceof Group) {
            return groupMove((Group) drawable, delta, duration, ease);
        }
        if (drawable instanceof Curve) {
            return curveMove((Curve) drawable, delta, duration, ease);
        }

        float[] lastProgress = new float[1];
        return custom(duration, ease, () -> {
            lastProgress[0] = 0.0f;
        }, (progress) -> {
            float stepProgress = progress - lastProgress[0];
            lastProgress[0] = progress;
            Group.moveDrawable(drawable, delta.x * stepProgress, delta.y * stepProgress);
        });
    }

    @Override
    public void begin() {
        super.begin();
        if (beginAction != null) {
            beginAction.begin();
        }
    }

    @Override
    protected void apply(float progress) {
        progressAction.apply(progress);
    }

    private static float lerp(float from, float to, float progress) {
        return from + ((to - from) * progress);
    }

    private static Vector2f lerp(Vector2f from, Vector2f to, float progress) {
        return new Vector2f(
                lerp(from.x, to.x, progress),
                lerp(from.y, to.y, progress)
        );
    }
}
